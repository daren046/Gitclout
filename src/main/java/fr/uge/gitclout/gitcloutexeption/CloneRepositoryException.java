package fr.uge.gitclout.gitcloutexeption;


import java.io.IOException;
import java.io.Serial;

/**
 * This class represents an exception that occurs during cloning of a Git repository.
 * @author Tagnan Tremellat
 * @version 1.0
 */
public class CloneRepositoryException extends IOException {
  @Serial
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructs a new CloneRepositoryException with the specified detail message and cause.
   * @param message the detail message.
   */
  public CloneRepositoryException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new CloneRepositoryException with the specified detail message and cause.
   * @param message the detail message.
   * @param cause the cause.
   */
  public CloneRepositoryException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
    * Constructs a new CloneRepositoryException with the specified cause.
    * @param cause  the cause.
   */
  public CloneRepositoryException(Throwable cause) {
    super(cause);
  }
  
}