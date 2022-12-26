package ml.empee.commandsManager.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Controller {
  private static final HashMap<CommandSender, CommandContext> contexts = new HashMap<>();
  private ArrayList<Listener> listeners = new ArrayList<>();
  private final List<Controller> subControllers = new ArrayList<>();

  protected final void addSubController(Controller controller) {
    subControllers.add(controller);
  }

  protected final List<Controller> getSubControllers() {
    return Collections.unmodifiableList(subControllers);
  }

  protected final void registerListeners(Listener... listeners) {
    this.listeners.addAll(Arrays.asList(listeners));

    JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
    for (Listener listener : listeners) {
      plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }
  }

  public void unregister() {
    for (Listener listener : listeners) {
      HandlerList.unregisterAll(listener);
    }
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
