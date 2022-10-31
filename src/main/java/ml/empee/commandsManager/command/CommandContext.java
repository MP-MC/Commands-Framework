package ml.empee.commandsManager.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

@SuppressWarnings("unchecked")
public final class CommandContext {

  private final HashMap<String, Object> arguments = new HashMap<>();
  private final CommandSender source;

  public CommandContext(CommandSender source) {
    this.source = source;
  }

  public void addArgument(String id, Object arg) {
    arguments.put(id, arg);
  }

  public <T> T getArgument(String id) {
    return (T) arguments.get(id);
  }

  void addArguments(Map<String, Object> arguments) {
    for (Map.Entry<String, Object> arg : arguments.entrySet()) {
      String key = arg.getKey();
      if (key != null && !key.isEmpty()) {
        this.arguments.put(key, arg.getValue());
      }
    }
  }

  /**
   * Gets the command source
   */
  public CommandSender getSource() {
    return source;
  }

}
