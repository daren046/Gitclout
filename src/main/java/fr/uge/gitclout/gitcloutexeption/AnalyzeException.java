package fr.uge.gitclout.gitcloutexeption;

import java.io.IOException;
import java.io.Serial;

/**
 * This class represents an exception that occurs during analysis of a Git repository.
 * @author Tagnan Tremellat
 * @version 1.0
 */
public class AnalyzeException extends IOException {
  @Serial
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructs a new AnalyzeException with the specified detail message and cause.
   * @param message the detail message.
   * @param cause the cause.
   */
  public AnalyzeException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
   * Constructs a new AnalyzeException with the specified detail message.
   * @param message the detail message.
   */
  public AnalyzeException(String message) {
    super(message);
  }
  
  /**
   *  Constructs a new AnalyzeException with the specified cause.
   * @param cause  the cause.
   */
  public AnalyzeException(Throwable cause) {
    super(cause);
  }
}