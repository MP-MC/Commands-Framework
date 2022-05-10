package tk.empee.commandManager.command;

import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import tk.empee.commandManager.CommandManager;
import tk.empee.commandManager.command.annotations.CommandRoot;
import tk.empee.commandManager.helpers.PluginCommand;
import tk.empee.commandManager.parsers.ParameterParser;
import tk.empee.commandManager.parsers.types.IntegerParser;
import tk.empee.commandManager.parsers.types.greedy.GreedyParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Command implements CommandExecutor, TabCompleter {

    private static final String MALFORMED_COMMAND = "&4&l > &cThe command is missing arguments, check the help menu";
    private static final String MISSING_PERMISSIONS = "&4&l > &cYou haven't enough permissions";
    private static final String RUNTIME_ERROR = "&4&l > &cError while executing the command";
    private static final String INVALID_PAGE = "&4&l > &cError the page number is invalid";
    private static final int HELP_PAGE_ROWS = 5;

    private BukkitAudiences adventure;
    private int helpPages;
    protected Component[] helpMenu;
    protected TextComponent header;

    @Getter
    private org.bukkit.command.PluginCommand pluginCommand;
    @Getter
    private CommandNode rootNode;

    public final boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {
        try {

            if(args.length > 0 && sender.hasPermission(rootNode.getPermission())) {
                //Handling of default commands
                if(args[0].equalsIgnoreCase("help")) {
                    if(args.length > 1) {
                        sendHelp(sender, IntegerParser.DEFAULT.parse(args[1]));
                    } else {
                        sendHelp(sender, 0);
                    }

                    return true;
                }
            }

            run(new CommandContext(sender), rootNode, args, 0);
        } catch (CommandException exception) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', exception.getMessage()));

            Throwable cause = exception.getCause();
            if(cause != null) {
                cause.printStackTrace();
            }
        }

        return true;
    }
    public final List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, String[] args) {

        int offset = 0;
        CommandNode node = rootNode;
        do {

            ParameterParser<?>[] parameterParsers = node.getParameterParsers();
            for (ParameterParser<?> parameterParser : parameterParsers) {

                offset += 1;
                if (offset == args.length) {
                    return parameterParser.getSuggestions(sender, offset-1, args);
                }

            }

            node = findNextNode(node, args, offset);
            offset += 1;

        } while (node != null);

        return Collections.emptyList();
    }

    private void run(CommandContext context, CommandNode node, String[] args, int offset) {

        if(node == null) {
            throw new CommandException(MALFORMED_COMMAND);
        } else {

            if(!context.getSource(CommandSender.class).hasPermission(node.getPermission())) {
                throw new CommandException(MISSING_PERMISSIONS);
            }

            ParameterParser<?>[] parsers = node.getParameterParsers();
            executeNode(context, node, parsers, args, offset);
            offset += parsers.length;

            if(node.getChildren().length == 0) {
                if(!node.isExecutable()) {
                    throw new CommandException(MALFORMED_COMMAND);
                }
            } else {
                CommandNode nextNode = findNextNode(node, args, offset);
                if(nextNode == null && !node.isExecutable()) {
                    throw new CommandException(MALFORMED_COMMAND);
                } else if(nextNode != null) {
                    run(context, nextNode, args, offset+1);
                }
            }
        }

    }
    private void executeNode(CommandContext context, CommandNode node, ParameterParser<?>[] parsers, String[] args, int offset) {
        Object[] arguments = parseArguments(context, parsers, args, offset);
        if(node.isExecutable()) {
            try {
                node.getExecutor().invoke(this, arguments);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                if(e.getCause() instanceof CommandException) {
                    throw (CommandException) e.getCause();
                }

                throw new CommandException(RUNTIME_ERROR, e.getCause());
            }
        }
    }
    /**
     * Parse the arguments and put them inside the command context if needed
     */
    private Object[] parseArguments(CommandContext context, ParameterParser<?>[] parsers, String[] args, int offset) {
        Object[] arguments = new Object[parsers.length+1];
        arguments[0] = context;

        for(int i=0; i<parsers.length; i++) {
            if(offset >= args.length) {
                if(parsers[i].isOptional()) {
                    arguments[i+1] = parsers[i].parseDefaultValue();
                } else {
                    throw new CommandException(MALFORMED_COMMAND);
                }
            } else {
                arguments[i+1] = parseArgument(context, parsers[i], args, offset);
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

    private void sendHelp(CommandSender target, Integer page) {
        if(page >= helpPages || page < 0) {
            throw new CommandException(INVALID_PAGE);
        }

        Audience audience;

        //If it is running paper use the target as the audience
        if(target instanceof Audience) {
            audience = (Audience) target;
        } else {
            audience = adventure.sender(target);
        }

        audience.sendMessage(header);

        for(int i = page* HELP_PAGE_ROWS; i<(page+1)* HELP_PAGE_ROWS && i<helpMenu.length; i++) {
            audience.sendMessage(helpMenu[i]);
        }

        Component footer = Component.newline().append(Component.text("   "));
        for(int i=0; i<helpPages; i++) {
            Component pageNumber = Component.text(i + " ");
            if(i == page) {
                pageNumber = pageNumber.color(NamedTextColor.RED);
            } else {
                pageNumber = pageNumber.color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/" + rootNode.getLabel() + " help " + i));
            }

            footer = footer.append(pageNumber);
        }
        audience.sendMessage(footer.append(Component.newline()));
    }

    private void buildHelpMessage() {
        ArrayList<TextComponent> menu = buildHelpMenu();
        menu.sort(Comparator.comparing(TextComponent::content));
        helpMenu = menu.toArray(new Component[0]);

        helpPages = (int) Math.ceil(helpMenu.length / (float) HELP_PAGE_ROWS);
        header = Component.newline()
                .append(Component.text("   Help Menu").color(NamedTextColor.YELLOW))
                .append(Component.text("    -    ").color(NamedTextColor.GRAY))
                .append(Component.text(pluginCommand.getPlugin().getName()).color(NamedTextColor.GOLD))
                .append(Component.newline());
    }

    private ArrayList<TextComponent> buildHelpMenu() {
        return buildHelpMenu(new ArrayList<>(), rootNode,
                Component.text("   ")
                .append(Component.text("? ").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                .append(Component.text("/").color(NamedTextColor.DARK_GRAY))
                .append(Component.text(rootNode.getLabel() + " ").color(NamedTextColor.GRAY))
        );
    }
    private ArrayList<TextComponent> buildHelpMenu(ArrayList<TextComponent> menu, CommandNode node, TextComponent parent) {
        for(CommandNode child : node.getChildren()) {

            TextComponent helpRow = parent.append(Component.text(child.getLabel() + " ").color(NamedTextColor.GRAY));
            for(ParameterParser<?> parameter : child.getParameterParsers()) {
                String label = parameter.getLabel();
                if(label.isEmpty()) {
                    label = parameter.getDescriptor().getFallbackLabel();
                }

                helpRow = helpRow.append(
                        Component.text("<" + label + "> ")
                        .color(NamedTextColor.RED)
                        .hoverEvent(HoverEvent.showText(parameter.getDescriptor().getDescription()))
                );
            }

            StringBuilder rawCommand = new StringBuilder();
            for(Component component : helpRow.children()) {
                if(component instanceof TextComponent) {
                    rawCommand.append(((TextComponent) component).content());
                }
            }

            helpRow = helpRow.hoverEvent(HoverEvent.showText(child.getDescription()))
                    .clickEvent(ClickEvent.suggestCommand(rawCommand.substring(2, rawCommand.length()-1 )));

            if(child.isExecutable()) {
                menu.add(helpRow);
            }

            buildHelpMenu(menu, child, helpRow);
        }

        return menu;
    }

    public final org.bukkit.command.PluginCommand build(CommandManager commandManager) {
        Method rootMethod = getRootMethod();
        rootMethod.setAccessible(true);

        rootNode = new CommandNode(rootMethod, getClass(), commandManager.getParserManager());

        pluginCommand = PluginCommand.createInstance(rootMethod.getAnnotation(CommandRoot.class), rootMethod.getAnnotation(tk.empee.commandManager.command.annotations.CommandNode.class), commandManager.getPlugin());
        pluginCommand.setExecutor(this);

        adventure = commandManager.getAdventure();
        buildHelpMessage();

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

    /**
     * UTILITIES
     */

    protected void sendMessage(CommandSender sender, String... messages) {
        for(String message : messages) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

}
