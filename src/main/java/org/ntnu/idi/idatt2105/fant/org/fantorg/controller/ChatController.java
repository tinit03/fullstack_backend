package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatMessageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatNotification;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.chat.ChatProfileDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ChatRoomRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ChatMessageServiceImpl;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.ChatRoomServiceImpl;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.UserServiceImpl;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.SortUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for chat-related operations.
 * <p>
 * Provides endpoints for retrieving user chats, fetching messages for a specific chat, and obtaining chat profile details.
 * Also contains a WebSocket endpoint to process and route chat messages.
 *
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("*")
public class ChatController {

  private final ChatMessageServiceImpl chatMessageService;
  private final SimpMessagingTemplate messagingTemplate;
  private final ChatRoomRepository chatRoomRepository;
  private final UserServiceImpl userServiceImpl;
  private final ChatRoomServiceImpl chatRoomService;

  /**
   * Retrieves a paginated list of chats for the authenticated user.
   *
   * @param user the authenticated user
   * @param page the page number to retrieve (default is 0)
   * @param size the number of chats per page (default is 10)
   * @param sortField the field by which to sort the chats (default is "lastEntry")
   * @param sortDir the sort direction ("asc" or "desc", default is "desc")
   * @return a ResponseEntity containing a page of ChatDto objects
   */
  @Operation(summary = "Get User Chats",
      description = "Retrieves a paginated list of chat conversations for the authenticated user, sorted by the specified field and direction.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Chats retrieved successfully",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ChatDto.class))
          }),
  })
  @GetMapping("/chats")
  public ResponseEntity<Page<ChatDto>> getUserChats(
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "page size") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "sorting field") @RequestParam(defaultValue = "lastEntry") String sortField,
      @Parameter(description = "sorting direction") @RequestParam(defaultValue = "desc") String sortDir
  ) {
    log.info("Received GET request for /chats/{}", user.getEmail());
    Sort sort = SortUtil.buildSort(sortField, sortDir);
    Pageable pageable = PageRequest.of(page, size, sort);
    Page<ChatDto> chats = chatRoomService.getChats(user.getEmail(), pageable);
    log.info("Returning result for /chats/{}", user.getEmail());
    return ResponseEntity.ok(chats);
  }

  /**
   * Retrieves a paginated list of chat messages for a specific chat conversation.
   *
   * @param itemId the ID of the item associated with the chat
   * @param user the authenticated user requesting the messages
   * @param recipientId the identifier of the other chat participant
   * @param page the page number to retrieve (default is 0)
   * @param size the number of messages per page (default is 50)
   * @return a ResponseEntity containing a page of ChatMessageDto objects
   */
  @Operation(summary = "Find Chat Messages",
      description = "Retrieves a paginated list of chat messages between the authenticated user and the specified recipient for a given item.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Chat messages retrieved successfully",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ChatMessageDto.class))
          }),
  })
  @GetMapping("/messages/{itemId}/{recipientId}")
  public ResponseEntity<Page<ChatMessageDto>> findChatMessages(
     @Parameter(description = "Identificator of item") @PathVariable("itemId") Long itemId,
     @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user,
     @Parameter(description = "Recipient email") @PathVariable("recipientId") String recipientId,
     @Parameter(description = "Page num") @RequestParam(defaultValue = "0") int page,
     @Parameter(description = "Page size") @RequestParam(defaultValue = "50") int size
  ) {
    log.info("Received GET request for /messages/{}/{}/{}", itemId, user.getEmail(), recipientId);
    Pageable pageable = PageRequest.of(page, size);
    Page<ChatMessageDto> messages = chatMessageService.findChatMessages(user.getEmail(), recipientId, itemId, pageable);
    return ResponseEntity.ok(messages);
  }

  /**
   * Processes incoming chat messages sent via WebSocket.
   * <p>
   * This method receives a chat message, persists it, and then sends a chat notification to the recipient.
   * Note: This endpoint is not exposed as a REST API but rather as a WebSocket message handler.
   *
   * @param messageDto the chat message creation DTO containing the message details
   */
  @MessageMapping("/chat")
  public void processMessage(@Payload ChatMessageCreateDto messageDto, @AuthenticationPrincipal User user) {
    System.out.println(user.getEmail());
    if (!Objects.equals(messageDto.getSenderId(), user.getEmail())) {
      System.out.println("bad");
      return;
    }
    log.info("Received message for /chat");
    log.info("Received chat msg: '{}'", messageDto.getContent());
    log.info("Saving chat msg: '{}'...", messageDto.getContent());
    ChatMessageDto savedMsgDto = chatMessageService.save(messageDto);
    log.info("Saved chat msg: '{}'", savedMsgDto);
    log.info("Sending message to /{}/queue/messages", savedMsgDto.getRecipientId());
    messagingTemplate.convertAndSendToUser(savedMsgDto.getRecipientId(), "/queue/messages",
        ChatNotification.builder()
            .senderId(savedMsgDto.getSenderId())
            .recipientId(savedMsgDto.getRecipientId())
            .itemId(savedMsgDto.getItemId())
            .content(savedMsgDto.getContent())
            .timestamp(LocalDateTime.now())
            .build()
    );
    log.info("Sent to recipient");
  }

  /**
   * Retrieves the chat profile information for a specified recipient.
   *
   * @param recipientId the identifier of the recipient
   * @param user the authenticated user (unused in this method, but provided for security context)
   * @return a ChatProfileDto containing the recipient's chat profile details
   */
  @Operation(summary = "Get Recipient Info",
      description = "Retrieves the chat profile information for a specified recipient.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Chat profile retrieved successfully",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ChatProfileDto.class))
          }),
  })
  @GetMapping("/chat/recipient/{recipientId}")
  public ChatProfileDto getRecipientInfo(
      @Parameter(description = "Recipient mail") @PathVariable String recipientId, @AuthenticationPrincipal User user) {
    return userServiceImpl.findChatProfile(recipientId);
  }

  /**
   * Sends a message to the seller.
   * @param chatMessageCreateDto Message data.
   * @param user The authenticated user from JWT-token.
   * @return Response entity of the sent chat.
   */
  @Operation(
      summary = "Send chat",
      description = "Sends a chat to a seller, creating a new room if it does not exist, else uses the existing chat room"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Chat sent successfully",
          content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ChatMessageDto.class))
          }),
  })
  @PostMapping("/chat")
  public ResponseEntity<ChatMessageDto> contactSeller(
      @Parameter(description = "Chat message info") @RequestBody ChatMessageCreateDto chatMessageCreateDto,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user) {
    chatMessageCreateDto.setSenderId(user.getEmail());
    ChatMessageDto savedMsgDto = chatMessageService.save(chatMessageCreateDto);
    messagingTemplate.convertAndSendToUser(savedMsgDto.getRecipientId(), "/queue/messages",
        ChatNotification.builder()
            .senderId(savedMsgDto.getSenderId())
            .recipientId(savedMsgDto.getRecipientId())
            .itemId(savedMsgDto.getItemId())
            .content(savedMsgDto.getContent())
            .timestamp(LocalDateTime.now())
            .build()
    );
    return ResponseEntity.ok(savedMsgDto);
}
}