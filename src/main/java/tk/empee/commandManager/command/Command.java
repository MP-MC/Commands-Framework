package tk.empee.commandManager.command;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import tk.empee.commandManager.command.annotations.CommandRoot;
import tk.empee.commandManager.command.parameters.ParameterManager;
import tk.empee.commandManager.command.parameters.parsers.ParameterParser;
import tk.empee.commandManager.command.parameters.parsers.greedy.GreedyParser;
import tk.empee.commandManager.helpers.PluginCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public abstract class Command implements CommandExecutor, TabCompleter {

    @Getter
    private org.bukkit.command.PluginCommand pluginCommand;

    @Getter
    private CommandNode rootNode;

    public final boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        try {
            run(new CommandContext(sender), rootNode, args, 0);
        } catch (CommandException exception) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', exception.getMessage()));
        }

        return true;
    }
    public final List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        CommandNode node = rootNode;
        int offset = 0;
        while (node != null) {
            ParameterParser<?>[] parameters = node.getParameters();
            if (args.length - offset > parameters.length) {
                offset += parameters.length;
                node = findNextNode(node, args, offset);
                offset += 1;
            } else {
                return parameters[args.length-offset-1].getSuggestions(sender);
            }
        }

        return Collections.emptyList();
    }

    private void run(CommandContext context, CommandNode node, String[] args, int offset) {

        if(node == null) {
            throw new CommandException("&4&l > &cThe command is missing arguments, check the help menu");
        } else {

            if(!context.getSource(CommandSender.class).hasPermission(node.getPermission())) {
                throw new CommandException("&4&l > &cYou haven't enough permissions");
            }

            ParameterParser<?>[] parameters = node.getParameters();
            executeNode(context, node, parameters, args, offset);
            offset += parameters.length;

            if(node.getChildren().length == 0) {
                if(!node.isExecutable()) {
                    throw new CommandException("&4&l > &cThe command is missing arguments, check the help menu");
                }
            } else {
                CommandNode nextNode = findNextNode(node, args, offset);
                if(nextNode == null && !node.isExecutable()) {
                    throw new CommandException("&4&l > &cThe command is missing arguments, check the help menu");
                } else if(nextNode != null) {
                    run(context, nextNode, args, offset+1);
                }
            }
        }

    }
    private void executeNode(CommandContext context, CommandNode node, ParameterParser<?>[] parameters, String[] args, int offset) {
        Object[] arguments = parseArguments(context, parameters, args, offset);
        if(node.isExecutable()) {
            try {
                node.getExecutor().invoke(this, arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e.getCause()); //TODO Better handling
            }
        }
    }
    /**
     * Parse the arguments and updates correspondingly the command context
     */
    private Object[] parseArguments(CommandContext context, ParameterParser<?>[] parameters, String[] args, int offset) {
        Object[] arguments = new Object[parameters.length+1];
        arguments[0] = context;

        for(int i=0; i<parameters.length; i++) {
            if(offset >= args.length) {
                if(parameters[i].isOptional()) {
                    arguments[i+1] = parameters[i].parseDefaultValue();
                } else {
                    throw new CommandException("&4&l > &cThe command is missing arguments, check the help menu");
                }
            } else {
                arguments[i+1] = parseArgument(context, parameters[i], args, offset);
            }

            offset += 1;
        }

        return arguments;
    }
    private Object parseArgument(CommandContext context, ParameterParser<?> parameter, String[] args, int offset) {
        Object parsedArg;
        if(parameter instanceof GreedyParser) {
            parsedArg = parameter.parse(offset, args);
        } else {
            parsedArg = parameter.parse(args[offset]);
        }

        String parameterLabel = parameter.getLabel();
        if(!parameterLabel.isEmpty()) {
            context.addArgument(parameterLabel, parsedArg);
        }

        return parsedArg;
    }
    private CommandNode findNextNode(CommandNode node, String[] args, int offset) {
        if(offset < args.length) {
            for (CommandNode child : node.getChildren()) {
                if (child.getLabel().equalsIgnoreCase(args[offset])) {
                    return child;
                }
            }
        }

        return null;
    }

    protected final void sendHelp(CommandContext context) {
        //TODO Implements adventure support, (Checks for already implemented services)
        throw new UnsupportedOperationException("This is a work in progress");
    }

    public final org.bukkit.command.PluginCommand build(JavaPlugin plugin, ParameterManager parameterManager) {
        Method rootMethod = getRootMethod();
        rootMethod.setAccessible(true);

        rootNode = new CommandNode(rootMethod, getClass(), parameterManager);

        pluginCommand = PluginCommand.createInstance(rootMethod.getAnnotation(CommandRoot.class), rootMethod.getAnnotation(tk.empee.commandManager.command.annotations.CommandNode.class), plugin);
        pluginCommand.setExecutor(this);

        return pluginCommand;
    }
    private Method getRootMethod() {
        for(Method method : getClass().getDeclaredMethods()) {

            if(method.getAnnotation(CommandRoot.class) != null) {
                return method;
            }

        }

        throw new IllegalStateException("Can't find the root node of " + getClass().getName());
    }
}
