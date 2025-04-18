package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemSearchFilter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemSearchResponse;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.ItemStatusUpdate;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ItemMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ItemService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.specification.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing items.
 *
 * <p>Provides endpoints to search, retrieve, create, update, and delete items. It also allows
 * updating item statuses as well as fetching items belonging to the authenticated user.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

  private final ItemService itemService;

  /**
   * Constructs an ItemController with the specified ItemService.
   *
   * @param itemService the service for handling item operations
   */
  @Autowired
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  /**
   * Searches for items using the provided search filter and pagination parameters.
   *
   * @param filter the search filter criteria
   * @param sortField the field to sort the results by (default is "publishedAt")
   * @param sortDir the sort direction ("asc" or "desc", default is "desc")
   * @param page the page number for pagination (default is 0)
   * @param size the number of items per page (default is 10)
   * @param user the authenticated user performing the search
   * @return an ItemSearchResponse containing the search results
   */
  @Operation(
      summary = "Search Items",
      description =
          "Searches for items based on filter criteria, sorting, and pagination. "
              + "The endpoint returns a paginated response matching the search filter.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ItemSearchResponse.class))
            }),
      })
  @GetMapping("/search")
  public ItemSearchResponse searchItems(
      @Parameter(description = "Filter of item") @ModelAttribute ItemSearchFilter filter,
      @Parameter(description = "Sorting value") @RequestParam(defaultValue = "publishedAt")
          String sortField,
      @Parameter(description = "Sortingdirection") @RequestParam(defaultValue = "desc")
          String sortDir,
      @Parameter(description = "Page number") @RequestParam(value = "page", defaultValue = "0")
          int page,
      @Parameter(description = "page size") @RequestParam(value = "size", defaultValue = "10")
          int size,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user) {

    Sort sort = SortUtil.buildSort(sortField, sortDir);
    Pageable pageable = PageRequest.of(page, size, sort);
    return itemService.searchItems(filter, pageable, user);
  }

  /**
   * Retrieves all items with pagination, sorting, and filtering by status.
   *
   * @param page the page number for pagination (default is 0)
   * @param size the number of items per page (default is 10)
   * @param sortField the field to sort the items by (default is "publishedAt")
   * @param sortDir the sort direction ("asc" or "desc", default is "desc")
   * @param status the status filter for the items (default is "ACTIVE")
   * @param user the authenticated user
   * @return a paginated list of ItemDto objects
   */
  @Operation(
      summary = "Get All Items",
      description =
          "Retrieves all items with pagination and filtering by status. "
              + "Results are sorted by the specified field and direction.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Items retrieved successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ItemDto.class))
            }),
      })
  @GetMapping
  public Page<ItemDto> getAllItems(
      @Parameter(description = "Page num") @RequestParam(value = "page", defaultValue = "0")
          int page,
      @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "10")
          int size,
      @Parameter(description = "Sorting value") @RequestParam(defaultValue = "publishedAt")
          String sortField,
      @Parameter(description = "Sorting direction") @RequestParam(defaultValue = "desc")
          String sortDir,
      @Parameter(description = "Item status") @RequestParam(defaultValue = "ACTIVE") Status status,
      @Parameter(description = "Auth user") @AuthenticationPrincipal User user) {
    Sort sort = SortUtil.buildSort(sortField, sortDir);
    Pageable pageable = PageRequest.of(page, size, sort);
    return itemService.getAllItems(pageable, status, user);
  }

  /**
   * Retrieves detailed information for a single item.
   *
   * @param id the ID of the item to retrieve
   * @param user the authenticated user requesting the item details
   * @return a ResponseEntity containing the ItemDto with detailed information
   */
  @Operation(
      summary = "Get Item Detail",
      description =
          "Retrieves detailed information for a single item identified by its ID. "
              + "The endpoint includes bookmark status information for the authenticated user.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Item details retrieved successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ItemDto.class))
            }),
      })
  @GetMapping("/{id}")
  public ResponseEntity<ItemDto> getItemDetail(
      @Parameter(description = "Identificator of item") @PathVariable Long id,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user) {
    ItemDto dto = itemService.getItemByIdBookmarked(id, user);
    return ResponseEntity.ok(dto);
  }

  /**
   * Creates a new item.
   *
   * @param dto the DTO containing item creation data
   * @param user the authenticated user creating the item
   * @return a ResponseEntity containing the created ItemDto with HTTP status 201 (Created)
   */
  @Operation(
      summary = "Create Item",
      description =
          "Creates a new item using the provided item data. "
              + "Returns the created item details with HTTP status 201.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Item created successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ItemDto.class))
            }),
      })
  @PostMapping
  public ResponseEntity<ItemDto> createItem(
      @Parameter(description = "Item creating info") @Valid @RequestBody ItemCreateDto dto,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user) {
    Item saved = itemService.createItem(dto, user);
    return ResponseEntity.status(HttpStatus.CREATED).body(ItemMapper.toItemDto(saved));
  }

  /**
   * Deletes an item.
   *
   * @param itemId the ID of the item to delete
   * @param user the authenticated user performing the deletion
   * @return a ResponseEntity with HTTP status 204 (No Content) if deletion is successful
   */
  @Operation(
      summary = "Delete Item",
      description = "Deletes the item identified by the provided ID for the authenticated user.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "204",
            description = "Item deleted successfully",
            content = @Content)
      })
  @DeleteMapping("/{itemId}")
  public ResponseEntity<Void> deleteItem(
      @Parameter(description = "Item identificator") @PathVariable Long itemId,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user) {
    itemService.deleteItem(itemId, user);
    return ResponseEntity.noContent().build();
  }

  /**
   * Updates an existing item with new data.
   *
   * @param itemId the ID of the item to update
   * @param dto the DTO containing updated item data
   * @param user the authenticated user performing the update
   * @return a ResponseEntity containing the updated ItemDto
   */
  @Operation(
      summary = "Update Item",
      description = "Updates the item identified by the provided ID with new information.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Item updated successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ItemDto.class))
            }),
      })
  @PutMapping("/{itemId}")
  public ResponseEntity<ItemDto> updateItem(
      @Parameter(description = "item identificator") @PathVariable Long itemId,
      @Parameter(description = "Item editing info") @Valid @RequestBody ItemEditDto dto,
      @Parameter(description = "authenticated user") @AuthenticationPrincipal User user) {
    Item updated = itemService.updateItem(itemId, dto, user);
    ItemDto updatedDto = ItemMapper.toItemDto(updated);
    return ResponseEntity.ok(updatedDto);
  }

  /**
   * Updates the status of an existing item.
   *
   * @param itemId the ID of the item whose status is to be updated
   * @param status the DTO containing the new status value
   * @param user the authenticated user performing the status update
   * @return a ResponseEntity containing the updated ItemDto
   */
  @Operation(
      summary = "Update Item Status",
      description =
          "Changes the status of the item identified by the provided ID using the specified status update data.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Item status updated successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ItemDto.class))
            }),
      })
  @PutMapping("/{itemId}/status")
  public ResponseEntity<ItemDto> updateStatus(
      @Parameter(description = "Item identificator") @PathVariable Long itemId,
      @Parameter(description = "Item status") @Valid @RequestBody ItemStatusUpdate status,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user) {
    Item updated = itemService.changeStatus(itemId, status.getStatus(), user);
    ItemDto updatedDto = ItemMapper.toItemDto(updated);
    return ResponseEntity.ok(updatedDto);
  }

  /**
   * Retrieves the items created by the authenticated user.
   *
   * @param page the page number for pagination (default is 0)
   * @param size the number of items per page (default is 10)
   * @param sortField the field to sort the items by (default is "publishedAt")
   * @param sortDir the sort direction ("asc" or "desc", default is "desc")
   * @param status an optional status filter for the items
   * @param user the authenticated user
   * @return a paginated list of ItemDto objects representing the user's own items
   */
  @Operation(
      summary = "Get Own Items",
      description =
          "Retrieves a paginated list of items created by the authenticated user. "
              + "An optional status filter can be applied.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User's own items retrieved successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ItemDto.class))
            }),
      })
  @GetMapping("/me")
  public Page<ItemDto> getOwnItems(
      @Parameter(description = "page number") @RequestParam(value = "page", defaultValue = "0")
          int page,
      @Parameter(description = "page size") @RequestParam(value = "size", defaultValue = "10")
          int size,
      @Parameter(description = "sorting value")
          @RequestParam(value = "sortField", defaultValue = "publishedAt")
          String sortField,
      @Parameter(description = "sorting direction")
          @RequestParam(value = "sortDir", defaultValue = "desc")
          String sortDir,
      @Parameter(description = "Item status") @RequestParam(value = "status", required = false)
          Status status,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User user) {
    Sort sort = SortUtil.buildSort(sortField, sortDir);
    Pageable pageable = PageRequest.of(page, size, sort);
    return itemService.getItemsBySeller(user, status, pageable);
  }
}
