package ml.empee.commandsManager.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import ml.empee.commandsManager.CommandManager;
import ml.empee.commandsManager.command.annotations.CommandRoot;
import ml.empee.commandsManager.helpers.PluginCommand;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.types.IntegerParser;
import ml.empee.commandsManager.services.helpMenu.AdventureHelpMenu;
import ml.empee.commandsManager.services.helpMenu.HelpMenuGenerator;

public abstract class Command implements CommandExecutor, TabCompleter {

  private static final Logger logger = JavaPlugin.getProvidingPlugin(Command.class).getLogger();
  private static String prefix = "&4&l > &c";
  protected static String malformedCommandMSG = "The command is missing arguments, check the help menu";
  protected static String missingPermissionsMSG = "You haven't enough permissions";
  protected static String runtimeErrorMSG = "Error while executing the command";

  @Getter
  private org.bukkit.command.PluginCommand pluginCommand;
  @Getter
  private CommandNode rootNode;

  private HelpMenuGenerator helpMenuGenerator;

  public static void setPrefix(String prefix) {
    Command.prefix = prefix;
  }

  protected boolean executeDefaultCommands(String[] args, CommandSender sender) {
    if (args.length > 0 && sender.hasPermission(rootNode.getPermission())) {

      if ("help".equalsIgnoreCase(args[0])) {
        if (args.length > 1) {
          helpMenuGenerator.sendHelpMenu(sender, IntegerParser.DEFAULT.parse(args[1]));
        } else {
          helpMenuGenerator.sendHelpMenu(sender, 0);
        }
        return true;
      }

    }

    return false;
  }

  public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
    try {
      if (!executeDefaultCommands(args, sender)) {
        parseParametersAndExecuteNode(new CommandContext(sender), rootNode, args, 0);
      }
    } catch (CommandException exception) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + exception.getMessage()));

      Throwable cause = exception.getCause();
      if (cause != null) {
        if (cause instanceof InvocationTargetException) {
          cause = cause.getCause();
        }

        logger.log(Level.SEVERE, "Error while executing the command {0} \n\t - Arguments: {1}",
            new Object[] { command.getName(), Arrays.toString(args) }
        );

        logger.log(Level.SEVERE, "Stacktrace:", cause);
      }
    }

    return true;
  }

  public final List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

    int offset = 0;
    CommandNode node = rootNode;
    while (true) {
      ParameterParser<?>[] parameterParsers = node.getParameterParsers();
      for (ParameterParser<?> parameterParser : parameterParsers) {

        offset += 1;
        if (offset == args.length) {
          return getSuggestions(sender, args, offset - 1, parameterParser);
        }

      }

      node = findNextNode(node, args, offset);
      if (node == null) {
        break;
      }

      offset += node.getLabel().split(" ").length;
    }

    return Collections.emptyList();
  }

  private static List<String> getSuggestions(CommandSender sender, String[] args, int offset, ParameterParser<?> parameterParser) {
    List<String> suggestions = parameterParser.getSuggestions(sender, offset, args);

    if ( suggestions.isEmpty() && (args[args.length - 1] == null || args[args.length - 1].isEmpty())) {
      if (parameterParser.isOptional()) {
        suggestions.add("[" + parameterParser.getLabel() + "]");
      } else {
        suggestions.add("<" + parameterParser.getLabel() + ">");
      }
    }
    return suggestions;
  }

  private void parseParametersAndExecuteNode(CommandContext context, CommandNode node, String[] args, int offset) throws CommandException {
    if (node == null) {
      throw new CommandException(malformedCommandMSG);
    } else {
      if (!context.getSource(CommandSender.class).hasPermission(node.getPermission())) {
        throw new CommandException(missingPermissionsMSG);
      }

      ParameterParser<?>[] parsers = node.getParameterParsers();
      Map<String, Object> arguments = parseArguments(parsers, args, offset);
      executeNode(node, context, arguments.values());
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
      CommandNode nextNode = findNextNode(node, args, offset);
      if (nextNode == null && !node.isExecutable()) {
        throw new CommandException(malformedCommandMSG);
      } else if (nextNode != null) {
        parseParametersAndExecuteNode(context, nextNode, args, offset + nextNode.getLabel().split(" ").length);
      }
    }
  }

  private void executeNode(CommandNode node, CommandContext context, Collection<Object> arguments) throws CommandException {
    Object[] args = new Object[arguments.size() + 1];
    args[0] = context;

    int i = 1;
    for (Object arg : arguments) {
      args[i] = arg;
      i += 1;
    }

    try {
      if(node.executor != null) {
        node.executor.invoke(this, args);
      }
    } catch (Exception e) {
      if (e.getCause() instanceof CommandException) {
        throw (CommandException) e.getCause();
      }

      throw new CommandException(runtimeErrorMSG, e);
    }
  }

  private Map<String, Object> parseArguments(ParameterParser<?>[] parsers, String[] args, int offset) {
    LinkedHashMap<String, Object> arguments = new LinkedHashMap<>();

    for (int i = 0; i < parsers.length; i++) {
      if (offset >= args.length) {
        if (parsers[i].isOptional()) {
          arguments.put(parsers[i].getLabel(), parsers[i].parseDefaultValue());
        } else {
          throw new CommandException(malformedCommandMSG);
        }
      } else {
        arguments.put(parsers[i].getLabel(), parsers[i].parse(offset, args));
      }
      offset += 1;
    }

    return arguments;
  }

  private CommandNode findNextNode(CommandNode node, String[] args, int offset) {
    if (offset >= args.length) {
      return null;
    }

    for (CommandNode child : node.getChildren()) {
      String[] labels = child.getLabel().split(" ");
      boolean matchAllLabels = true;
      for (int i = 0; i < labels.length; i++) {
        if (offset + i >= args.length || !labels[i].equalsIgnoreCase(args[offset + i])) {
          matchAllLabels = false;
          break;
        }
      }

      if (matchAllLabels) {
        return child;
      }
    }

    return null;
  }

  public final org.bukkit.command.PluginCommand build(CommandManager commandManager) {
    CommandRoot rootAnnotation = getClass().getAnnotation(CommandRoot.class);
    if (rootAnnotation == null) {
      throw new IllegalStateException("The class " + getClass().getName() + " is not annotated with @CommandRoot");
    }

    Method rootMethod = getRootMethod(rootAnnotation);
    if (rootMethod != null) {
      rootMethod.setAccessible(true);
      rootNode = new CommandNode(rootMethod, getClass(), commandManager.getParserManager());
    } else {
      rootNode = new CommandNode(rootAnnotation, getClass(), commandManager.getParserManager());
    }

    pluginCommand = PluginCommand.buildFromCommandRoot(rootAnnotation, commandManager.getPlugin());
    pluginCommand.setExecutor(this);

    if (commandManager.getAdventure() != null) {
      helpMenuGenerator = new AdventureHelpMenu(commandManager.getAdventure(), rootNode);
    }

    return pluginCommand;
  }

  private Method getRootMethod(CommandRoot commandRoot) {
    for (Method method : getClass().getDeclaredMethods()) {

      ml.empee.commandsManager.command.annotations.CommandNode commandNode = method.getAnnotation(
          ml.empee.commandsManager.command.annotations.CommandNode.class);
      if (commandNode != null && commandNode.parent().isEmpty() && commandRoot.label().equals(commandNode.label())) {
        return method;
      }

    }

    return null;
  }

}
