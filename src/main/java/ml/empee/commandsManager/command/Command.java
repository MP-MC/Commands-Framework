package ml.empee.commandsManager.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import ml.empee.commandsManager.CommandManager;
import ml.empee.commandsManager.command.annotations.CmdNode;
import ml.empee.commandsManager.command.annotations.CmdRoot;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.services.generators.HelpMenu;
import ml.empee.commandsManager.services.generators.IntractableHelpMenu;
import ml.empee.commandsManager.utils.CommandMapUtils;
import ml.empee.commandsManager.utils.PluginCommandUtils;
import ml.empee.commandsManager.utils.helpers.Tuple;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Command implements CommandExecutor {

  @Setter
  private static String prefix = "&4&l > ";
  protected static String malformedCommandMSG = "The command is missing arguments, check the help menu";
  protected static String missingPermissionsMSG = "You haven't enough permissions";
  protected static String runtimeErrorMSG = "Error while executing the command";
  protected static String invalidSenderMSG = "You aren't an allowed sender type of this command";

  private final HashMap<CommandSender, CommandContext> contexts = new HashMap<>();
  private ArrayList<Listener> listeners = new ArrayList<>();
  @Getter
  private PluginCommand pluginCommand;
  @Getter
  private CommandNode rootNode;
  @Getter
  private HelpMenu helpMenu;
  private Logger logger;

  public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
    try {
      parseParametersAndExecuteNode(new CommandContext(sender), rootNode, args, 0);
    } catch (CommandException exception) {
      String message = exception.getMessage().replace("&r", "&c");
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&c" + message));

      Throwable cause = exception.getCause();
      if (cause != null) {
        if (cause instanceof InvocationTargetException) {
          cause = cause.getCause();
        }

        logger.log(Level.SEVERE, "Error while executing the command {0} \n\t - Arguments: {1}",
            new Object[] {command.getName(), Arrays.toString(args)}
        );

        logger.log(Level.SEVERE, "Stacktrace:", cause);
      }
    }

    return true;
  }

  private void parseParametersAndExecuteNode(
      CommandContext context, CommandNode node, String[] args, int offset
  ) throws CommandException {
    if (node == null) {
      throw new CommandException(malformedCommandMSG);
    } else {
      if (!context.getSource().hasPermission(node.getPermission())) {
        throw new CommandException(missingPermissionsMSG);
      }

      ParameterParser<?>[] parsers = node.getParameterParsers();
      List<Tuple<String, Object>> arguments = parseArguments(parsers, args, offset);
      executeNode(node, context, arguments);
      context.addArguments(arguments);
      offset += parsers.length;

      findAndExecuteChild(context, node, args, offset);
    }
  }

  private void findAndExecuteChild(CommandContext context, CommandNode node, String[] args, int offset) throws CommandException {
    if (node.getChildren().length == 0) {
      if (!node.isExecutable()) {
        throw new CommandException(malformedCommandMSG);
      }
    } else {
      CommandNode nextNode = node.findNextNode(args, offset);
      if (nextNode == null && !node.isExecutable()) {
        throw new CommandException(malformedCommandMSG);
      } else if (nextNode != null) {
        parseParametersAndExecuteNode(context, nextNode, args,
            offset + nextNode.getLabel().split(" ").length);
      }
    }
  }

  private void executeNode(CommandNode node, CommandContext context, List<Tuple<String, Object>> arguments) throws CommandException {
    Object[] args = new Object[arguments.size() + 1];
    args[0] = context.getSource();
    if (!node.getSenderType().isInstance(args[0])) {
      throw new CommandException(invalidSenderMSG);
    }

    int i = 1;
    for (Tuple<String, Object> arg : arguments) {
      args[i] = arg.getSecond();
      i += 1;
    }

    try {
      if (node.executor != null) {
        contexts.put(context.getSource(), context);
        node.executor.invoke(this, args);
        contexts.remove(context.getSource());
      }
    } catch (Exception e) {
      if (e.getCause() instanceof CommandException) {
        throw (CommandException) e.getCause();
      }

      throw new CommandException(runtimeErrorMSG, e);
    }
  }

  private List<Tuple<String, Object>> parseArguments(ParameterParser<?>[] parsers, String[] args, int offset) {
    List<Tuple<String, Object>> arguments = new ArrayList<>();

    for (ParameterParser<?> parser : parsers) {
      if (offset >= args.length) {
        if (parser.isOptional()) {
          arguments.add(Tuple.of(parser.getLabel(), parser.getDefaultValue()));
        } else {
          throw new CommandException(malformedCommandMSG);
        }
      } else {
        arguments.add(Tuple.of(parser.getLabel(), parser.parse(offset, args)));
      }
      offset += 1;
    }

    return arguments;
  }

  public final PluginCommand build(CommandManager commandManager) {
    CmdRoot rootAnnotation = getClass().getAnnotation(CmdRoot.class);
    if (rootAnnotation == null) {
      throw new IllegalStateException("The class " + getClass().getName() + " is not annotated with @CommandRoot");
    }

    logger = commandManager.getPlugin().getLogger();

    Method rootMethod = getRootMethod(rootAnnotation);
    if (rootMethod != null) {
      rootMethod.setAccessible(true);
      rootNode = new CommandNode(rootMethod, getClass(), commandManager.getParserManager());
    } else {
      rootNode = new CommandNode(rootAnnotation, getClass(), commandManager.getParserManager());
    }

    pluginCommand = PluginCommandUtils.buildFromCommandRoot(rootAnnotation, commandManager.getPlugin());
    pluginCommand.setExecutor(this);

    helpMenu = buildHelpMenu();

    return pluginCommand;
  }

  protected HelpMenu buildHelpMenu() {
    return new IntractableHelpMenu(pluginCommand.getPlugin().getName(), rootNode);
  }

  private Method getRootMethod(CmdRoot cmdRoot) {
    for (Method method : getClass().getDeclaredMethods()) {

      CmdNode cmdNode = method.getAnnotation(
          CmdNode.class
      );

      if (cmdNode != null && cmdNode.parent().isEmpty() && cmdRoot.label().equals(cmdNode.label())) {
        return method;
      }

    }

    return null;
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

    CommandMapUtils.unregisterCommand(pluginCommand);
  }

  protected final CommandContext getContext(CommandSender sender) {
    return contexts.get(sender);
  }
}
