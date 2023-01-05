package ml.empee.commandsManager.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.bukkit.command.CommandSender;

public abstract class Controller {
  private static final HashMap<CommandSender, CommandContext> contexts = new HashMap<>();
  private final List<Controller> subControllers = new ArrayList<>();

  public final void addSubController(Controller controller) {
    subControllers.add(controller);
  }

  public final List<Controller> getSubControllers() {
    return Collections.unmodifiableList(subControllers);
  }

  protected static CommandContext getContext(CommandSender sender) {
    return contexts.get(sender);
  }

  protected static void executeNode(
      CommandContext context, Node node, Object... args
  ) throws InvocationTargetException, IllegalAccessException {
    contexts.put(context.getSource(), context);
    node.executeNode(args);
    contexts.remove(context.getSource());
  }

}
