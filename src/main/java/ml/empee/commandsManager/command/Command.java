package ml.empee.commandsManager.command;

import lombok.Getter;
import ml.empee.commandsManager.CommandManager;
import ml.empee.commandsManager.command.annotations.CommandRoot;
import ml.empee.commandsManager.helpers.PluginCommand;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.types.IntegerParser;
import ml.empee.commandsManager.services.helpMenu.AdventureHelpMenu;
import ml.empee.commandsManager.services.helpMenu.HelpMenuGenerator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

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

public abstract class Command implements CommandExecutor, TabCompleter {

    private static Logger log = JavaPlugin.getProvidingPlugin(Command.class).getLogger();
    private static String PREFIX = "&4&l > &c";
    private static String MALFORMED_COMMAND = "The command is missing arguments, check the help menu";
    private static String MISSING_PERMISSIONS = "You haven't enough permissions";
    private static String RUNTIME_ERROR = "Error while executing the command";

    @Getter
    private org.bukkit.command.PluginCommand pluginCommand;
    @Getter
    private CommandNode rootNode;

    private HelpMenuGenerator helpMenuGenerator;

    public static void setPrefix(String prefix) {
        PREFIX = prefix;
    }

    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        try {
            if(args.length > 0 && sender.hasPermission(rootNode.getPermission())) {
                if(args[0].equalsIgnoreCase("help")) { //Handling of default commands
                    if(args.length > 1) {
                        helpMenuGenerator.sendHelpMenu(sender, IntegerParser.DEFAULT.parse(args[1]));
                    } else {
                        helpMenuGenerator.sendHelpMenu(sender, 0);
                    }

                    return true;
                }
            }

            executeNode(new CommandContext(sender), rootNode, args, 0);
        } catch (CommandException exception) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + exception.getMessage()));

            Throwable cause = exception.getCause();
            if(cause != null) {
                if(cause instanceof InvocationTargetException) {
                    cause = cause.getCause();
                }

                log.log(
                    Level.SEVERE,
                    "Error while executing the command " + command.getName() +
                    "\n\t - Arguments: " + Arrays.toString(args) +
                    "\n\t - Cause: " + cause.getMessage(),
                    cause
                );
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
                    List<String> suggestions = parameterParser.getSuggestions(sender, offset-1, args);
                    if(parameterParser.isOptional()) {
                        suggestions.add("[" + parameterParser.getLabel() + "]");
                    } else {
                        suggestions.add("<" + parameterParser.getLabel() + ">");
                    }

                    return suggestions;
                }

            }

            node = findNextNode(node, args, offset);
            if(node == null) {
                break;
            }

            offset += node.getLabel().split(" ").length;
        };

        return Collections.emptyList();
    }


    private void executeNode(CommandContext context, CommandNode node, String[] args, int offset) throws CommandException {
        if(node == null) {
            throw new CommandException(MALFORMED_COMMAND);
        } else {
            if(!context.getSource(CommandSender.class).hasPermission(node.getPermission())) {
                throw new CommandException(MISSING_PERMISSIONS);
            }

            ParameterParser<?>[] parsers = node.getParameterParsers();
            Map<String, Object> arguments = parseArguments(parsers, args, offset);
            performNodeActions(node, context, arguments.values());
            context.addArguments(arguments);
            offset += parsers.length;

            findAndExecuteChild(context, node, args, offset);
        }
    }
    private void findAndExecuteChild(CommandContext context, CommandNode node, String[] args, int offset) throws CommandException {
        if(node.getChildren().length == 0) {
            if(!node.isExecutable()) {
                throw new CommandException(MALFORMED_COMMAND);
            }
        } else {
            CommandNode nextNode = findNextNode(node, args, offset);
            if(nextNode == null && !node.isExecutable()) {
                throw new CommandException(MALFORMED_COMMAND);
            } else if(nextNode != null) {
                executeNode(context, nextNode, args, offset + nextNode.getLabel().split(" ").length);
            }
        }
    }
    private void performNodeActions(CommandNode node, CommandContext context, Collection<Object> arguments) throws CommandException {
        Object[] args = new Object[arguments.size() + 1];
        args[0] = context;

        int i = 0;
        for(Object arg : arguments) {
            args[++i] = arg;
        }

        try {
            node.executor.invoke(this, args);
        } catch (Exception e) {
            if(e.getCause() instanceof CommandException) {
                throw (CommandException) e.getCause();
            }

            throw new CommandException(RUNTIME_ERROR, e);
        }
    }

    private Map<String, Object> parseArguments(ParameterParser<?>[] parsers, String[] args, int offset) {
        LinkedHashMap<String, Object> arguments = new LinkedHashMap<>();

        for(int i=0; i<parsers.length; i++) {
            if (offset >= args.length) {
                if (parsers[i].isOptional()) {
                    arguments.put(parsers[i].getLabel(), parsers[i].parseDefaultValue());
                } else {
                    throw new CommandException(MALFORMED_COMMAND);
                }
            } else {
                arguments.put( parsers[i].getLabel(), parsers[i].parse(offset, args) );
            }
            offset += 1;
        }

        return arguments;
    }
    private CommandNode findNextNode(CommandNode node, String[] args, int offset) {
        if(offset < args.length) {
            for (CommandNode child : node.getChildren()) {
                String[] labels = child.getLabel().split(" ");
                boolean matchAllLabels = true;
                for(int i=0; i<labels.length; i++) {
                    if (offset+i >= args.length || !labels[i].equalsIgnoreCase(args[offset+i])) {
                        matchAllLabels = false;
                        break;
                    }
                }

                if(matchAllLabels) {
                    return child;
                }
            }
        }

        return null;
    }

    public final org.bukkit.command.PluginCommand build(CommandManager commandManager) {
        Method rootMethod = getRootMethod();
        rootMethod.setAccessible(true);

        rootNode = new CommandNode(rootMethod, getClass(), commandManager.getParserManager());

        pluginCommand = PluginCommand.buildFromCommandRoot(getClass().getAnnotation(CommandRoot.class), rootMethod.getAnnotation(ml.empee.commandsManager.command.annotations.CommandNode.class), commandManager.getPlugin());
        pluginCommand.setExecutor(this);

        if(commandManager.getAdventure() != null) {
            helpMenuGenerator = new AdventureHelpMenu(commandManager.getAdventure(), rootNode);
        }

        return pluginCommand;
    }
    private Method getRootMethod() {
        CommandRoot commandRoot = getClass().getAnnotation(CommandRoot.class);
        if(commandRoot == null) {
            throw new IllegalStateException("The class " + getClass().getName() + " is not annotated with @CommandRoot");
        }

        for(Method method : getClass().getDeclaredMethods()) {

            ml.empee.commandsManager.command.annotations.CommandNode commandNode = method.getAnnotation(ml.empee.commandsManager.command.annotations.CommandNode.class);
            if(commandNode != null && commandNode.parent().isEmpty() && commandRoot.value().equals(commandNode.label())) {
                return method;
            }

        }

        throw new IllegalStateException("Can't find the root node of " + getClass().getName());
    }

}
