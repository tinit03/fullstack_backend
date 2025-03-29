package org.ntnu.idi.idatt2105.fant.org.fantorg.exception;

import lombok.extern.slf4j.Slf4j;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.chat.ChatRoomNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.item.ItemNotFoundException;
import org.ntnu.idi.idatt2105.fant.org.fantorg.exception.user.UserNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.MessagingException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler to handle different types of exceptions across the application. It provides centralized
 * exception handling for various types of exceptions that may occur during the execution of the application.
 *
 * @author Harry Xu
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Default constructor
   */
  public GlobalExceptionHandler() {}

  /**
   * Log the error to the log.
   *
   * @param ex
   *            The exception to be logged.
   */
  private void logError(Exception ex) {
    log.error("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
  }

  private String createErrorResponseMsg(Exception ex) {
    return ex.getMessage();
  }

  /**
   * Handle exceptions related to existing objects.
   *
   * @param ex
   *            The exception indicating that an object already exists.
   * @return ResponseEntity with an appropriate HTTP status code and error message.
   */
  @ExceptionHandler(
      value = {
      })
  public ResponseEntity<String> handleObjectAlreadyExistException(Exception ex) {
    logError(ex);
    String msg = createErrorResponseMsg(ex);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
  }

  /**
   * Handle exceptions related to non-existing objects.
   *
   * @param ex
   *            The exception indicating that an object does not exist.
   * @return ResponseEntity with an appropriate HTTP status code and error message.
   */
  @ExceptionHandler(
      value = {
          UserNotFoundException.class,
          ChatRoomNotFoundException.class,
          ItemNotFoundException.class
      })
  public ResponseEntity<String> handleObjectNotFoundException(Exception ex) {
    logError(ex);
    String msg = createErrorResponseMsg(ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
  }

  /**
   * Handle exceptions related to bad input or invalid requests.
   *
   * @param ex
   *            The exception indicating bad input or invalid request.
   * @return ResponseEntity with an appropriate HTTP status code and error message.
   */
  @ExceptionHandler(
      value = {
          IllegalArgumentException.class,
          HttpMessageNotReadableException.class,
          NullPointerException.class,
          MissingServletRequestParameterException.class,
          HttpRequestMethodNotSupportedException.class,
          MessagingException.class,
          MethodArgumentNotValidException.class,
          DataIntegrityViolationException.class,
      })
  public ResponseEntity<String> handleBadInputException(Exception ex) {
    logError(ex);
    String msg = createErrorResponseMsg(ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
  }

    /*
    /**
     * Handle any remaining exceptions that are not explicitly handled.
     *
     * @param ex
     *            The exception that is not explicitly handled.
     * @return ResponseEntity with an appropriate HTTP status code and error message.

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<String> handleRemainderExceptions(Exception ex) {
      logError(ex);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getClass().getSimpleName());
    }
    */
}
