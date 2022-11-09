package ml.empee.commandsManager.command;

import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;

import ml.empee.commandsManager.utils.Tuple;

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

  void addArguments(List<Tuple<String, Object>> arguments) {
    for (Tuple<String, Object> arg : arguments) {
      String key = arg.getFirst();
      if (key != null && !key.isEmpty()) {
        this.arguments.put(key, arg.getSecond());
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
