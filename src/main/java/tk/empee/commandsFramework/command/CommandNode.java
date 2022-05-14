package tk.empee.commandsFramework.command;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import tk.empee.commandsFramework.parsers.ParameterParser;
import tk.empee.commandsFramework.parsers.ParserManager;
import tk.empee.commandsFramework.parsers.types.greedy.GreedyParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

public final class CommandNode {

    @Getter private final String label;

    @Getter private final String permission;
    @Getter private Component description;

    @Getter private final ParameterParser<?>[] parameterParsers;
    @Getter private final CommandNode[] children;

    @Getter private final Method executor;
    @Getter private final boolean executable;

    CommandNode(Method executor, Class<? extends Command> target, ParserManager parserManager) {
        this.executor = executor;

        tk.empee.commandsFramework.command.annotations.CommandNode annotation = executor.getAnnotation(tk.empee.commandsFramework.command.annotations.CommandNode.class);
        Objects.requireNonNull(annotation, "Can't find the commandNode annotation of " + executor.getName());

        label = annotation.label();
        permission = annotation.permission();

        description = Component.newline();
        if(!annotation.description().isEmpty()) {
            description = description
                    .append(Component.text(annotation.description()).color(NamedTextColor.DARK_AQUA))
                    .append(Component.newline()).append(Component.newline());
        }

        description = description
                .append(Component.text("Permission: ").color(NamedTextColor.YELLOW))
                .append(Component.text(permission.isEmpty() ? "none" : permission).color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.newline());

        executable = annotation.executable();

        parameterParsers = getParameterParsers(parserManager);
        children = getChildren(annotation.childNodes(), target, parserManager);

        if(children.length > 0 && parameterParsers.length > 0) {
            ParameterParser<?> lastParser = parameterParsers[parameterParsers.length-1];

            if(lastParser instanceof GreedyParser) {
                throw new IllegalArgumentException("You can't have children inside the node " + label + ", his last parameter is a greedy one!");
            } else if(lastParser.isOptional()) {
                throw new IllegalArgumentException("You can't have a children after a optional argument inside the node " + label);
            }
        }

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

    private CommandNode[] getChildren(String[] labels, Class<? extends Command> target, ParserManager parserManager) {

        CommandNode[] children = new CommandNode[labels.length];

        int matches = 0;
        while (target != null) {
            Method[] methods = target.getDeclaredMethods();
            for(int i=0; i<labels.length; i++) {

                if(children[i] == null) {
                    for (Method m : methods) {
                        tk.empee.commandsFramework.command.annotations.CommandNode annotation = m.getAnnotation(tk.empee.commandsFramework.command.annotations.CommandNode.class);
                        if (annotation != null && ((annotation.id().isEmpty() && annotation.label().equals(labels[i])) || !annotation.id().isEmpty() && annotation.id().equals(labels[i])) ) {
                            m.setAccessible(true);
                            children[i] = new CommandNode(m, target, parserManager);
                            matches += 1;
                        }
                    }
                }

            }

            target = (Class<? extends Command>) target.getSuperclass();
        }

        if(matches != labels.length) {
            throw new IllegalArgumentException("Can't find all sub-commands of " + label);
        }

        return children;

    }

}
