package ml.empee.commandsManager.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class Controller {
  private final HashMap<CommandSender, CommandContext> contexts = new HashMap<>();
  private final List<Controller> subControllers = new ArrayList<>();

  public final void addSubController(Controller controller) {
    subControllers.add(controller);
  }

  public final List<Controller> getSubControllers() {
    return Collections.unmodifiableList(subControllers);
  }

  protected final CommandContext getContext(CommandSender sender) {
    return contexts.get(sender);
  }

  final void addContext(CommandSender sender, CommandContext context) {
    contexts.put(sender, context);
  }

  final void removeContext(CommandSender sender) {
    contexts.remove(sender);
  }

}
