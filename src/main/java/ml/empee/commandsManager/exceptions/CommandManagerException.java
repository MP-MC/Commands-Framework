package ml.empee.commandsManager.exceptions;

public class CommandManagerException extends RuntimeException {

  public CommandManagerException(String message) {
    super(message);
  }

  public CommandManagerException(String message, Throwable cause) {
    super(message, cause);
  }

  public CommandManagerException(Throwable cause) {
    super(cause);
  }

}
