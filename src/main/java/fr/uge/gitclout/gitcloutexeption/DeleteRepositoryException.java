package fr.uge.gitclout.gitcloutexeption;

import java.io.IOException;
import java.io.Serial;

public class DeleteRepositoryException extends IOException {
  @Serial
  private static final long serialVersionUID = 1L;
  public DeleteRepositoryException(String message, Throwable cause) {
    super(message, cause);
  }
  public DeleteRepositoryException(String message) {
    super(message);
  }
  public DeleteRepositoryException(Throwable cause) {
    super(cause);
  }
}