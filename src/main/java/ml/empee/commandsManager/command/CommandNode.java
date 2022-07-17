package ml.empee.commandsManager.command;

import lombok.Getter;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserManager;
import ml.empee.commandsManager.parsers.types.greedy.GreedyParser;
import org.bukkit.ChatColor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public final class CommandNode {

    @Getter private final String id;
    @Getter private final String label;

    @Getter private final String permission;
    @Getter private final String description;
    @Getter private final ParameterParser<?>[] parameterParsers;
    @Getter private final CommandNode[] children;
    @Getter private final boolean executable;

    final Method executor;

    CommandNode(Method executor, Class<? extends Command> target, ParserManager parserManager) {
        this(null, executor, target, parserManager);
    }
    CommandNode(CommandNode parent, Method executor, Class<? extends Command> target, ParserManager parserManager) {
        this.executor = executor;

        ml.empee.commandsManager.command.annotations.CommandNode annotation = executor.getAnnotation(ml.empee.commandsManager.command.annotations.CommandNode.class);
        Objects.requireNonNull(annotation, "Can't find the commandNode annotation of " + executor.getName());

        this.label = annotation.label();
        if(parent == null) {
            this.id = label;
        } else {
            this.id = parent.id + "." + label;
        }

        this.permission = annotation.permission();
        this.description = buildDescription(annotation.description());
        this.executable = annotation.executable();
        this.parameterParsers = getParameterParsers(parserManager);

        this.children = getChildren(target, parserManager);
        if(children.length > 0 && parameterParsers.length > 0) {
            ParameterParser<?> lastParser = parameterParsers[parameterParsers.length-1];

            if(lastParser instanceof GreedyParser) {
                throw new IllegalArgumentException("You can't have children inside the node " + label + ", his last parameter is a greedy one!");
            } else if(lastParser.isOptional()) {
                throw new IllegalArgumentException("You can't have a children after a optional argument inside the node " + label);
            }
        }
    }

    private String buildDescription(String rawDescription) {
        StringBuilder description = new StringBuilder("\n");
        if(!rawDescription.isEmpty()) {
            description
                    .append(ChatColor.DARK_AQUA + rawDescription)
                    .append("\n\n");
        }

        description
                .append(ChatColor.YELLOW + "Permission: ")
                .append(ChatColor.LIGHT_PURPLE + (permission.isEmpty() ? "none" : permission) )
                .append("\n");

        return description.toString();
    }

    private ParameterParser<?>[] getParameterParsers(ParserManager parserManager) {
        java.lang.reflect.Parameter[] rawParameters = executor.getParameters();
        if(rawParameters.length == 0 || rawParameters[0].getType() != CommandContext.class) {
            throw new IllegalArgumentException("Missing command context parameter from " + label);
        }

        ParameterParser<?>[] parameters = new ParameterParser<?>[rawParameters.length-1];

        for(int i=1; i<rawParameters.length; i++) {
            ParameterParser<?> type = getParameterParser(rawParameters[i], parserManager);
            Objects.requireNonNull(type, "Can't find a parser for the parameter type " + rawParameters[i].getType().getName());

            if(i != 1 && parameters[i-2] instanceof GreedyParser) {
                throw new IllegalArgumentException("You can't have a parameter after a greedy parameter inside the node " + label);
            }

            if(i != 1 && !type.isOptional() && parameters[i-2].isOptional()) {
                throw new IllegalArgumentException("You can't have a required argument after a optional one inside the node " + label);
            }

            parameters[i-1] = type;

        }

        return parameters;
    }

    /**
     * Get the parameter parser for the given parameter, if a cached one already exists return that instance. <br><br>
     *
     * If it isn't specified through annotation the parser that should have been picked try picking a default one.<br>
     * The registered default types are:
     * <ul>
     *     <li>Integer</li>
     *     <li>Double</li>
     *     <li>Float</li>
     *     <li>Long</li>
     *     <li>Boolean</li>
     *     <li>String</li>
     *     <li>Player</li>
     *     <li>OfflinePlayer</li>
     * </ul>
     */
    private ParameterParser<?> getParameterParser(java.lang.reflect.Parameter parameter, ParserManager parserManager) {

        ParameterParser<?> parser = null;
        for (Annotation annotation : parameter.getAnnotations()) {
            parser = parserManager.registerParser(annotation);
        }

        if(parser == null) {
            parser = parserManager.getDefaultParser(parameter.getType());
        }

        return parser;
    }

    private CommandNode[] getChildren(Class<? extends Command> target, ParserManager parserManager) {

        ArrayList<CommandNode> children = new ArrayList<>();

        while (target != null) {
            Method[] methods = target.getDeclaredMethods();
            for (Method m : methods) {
                if(m.equals(executor)) continue;

                ml.empee.commandsManager.command.annotations.CommandNode annotation = m.getAnnotation(ml.empee.commandsManager.command.annotations.CommandNode.class);
                if (annotation != null && !annotation.parent().isEmpty() && id.endsWith(annotation.parent()) ) {
                    m.setAccessible(true);
                    children.add(new CommandNode(m, target, parserManager));
                }
            }

            target = (Class<? extends Command>) target.getSuperclass();
        }

        return children.toArray(new CommandNode[0]);
    }

}
