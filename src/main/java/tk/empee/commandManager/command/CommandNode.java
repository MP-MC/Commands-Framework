package tk.empee.commandManager.command;

import lombok.Getter;
import tk.empee.commandManager.command.parameters.ParameterManager;
import tk.empee.commandManager.command.parameters.parsers.ParameterParser;
import tk.empee.commandManager.command.parameters.parsers.greedy.GreedyParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public final class CommandNode {

    @Getter private final String label;

    @Getter private final String permission;
    @Getter private final String description;

    @Getter private final ParameterParser<?>[] parameters;
    @Getter private final CommandNode[] children;

    @Getter private final Method executor;
    @Getter private final boolean executable;

    CommandNode(Method executor, Class<? extends Command> target, ParameterManager parameterManager) {
        this.executor = executor;

        tk.empee.commandManager.command.annotations.CommandNode annotation = executor.getAnnotation(tk.empee.commandManager.command.annotations.CommandNode.class);
        Objects.requireNonNull(annotation, "Can't find the commandNode annotation of " + executor.getName());

        label = annotation.label();
        permission = annotation.permission();
        description = annotation.description();
        executable = annotation.executable();

        parameters = getParameters(parameterManager);
        children = getChildren(annotation.childNodes(), target, parameterManager);

        if(children.length > 0 && parameters[parameters.length-1] instanceof GreedyParser) {
            throw new IllegalArgumentException("You can't have children inside the node " + label + ", his last parameter is a greedy one!");
        }

        if(children.length > 0 && parameters[parameters.length-1].isOptional()) {
            throw new IllegalArgumentException("You can't have a children after a optional argument inside the node " + label);
        }

    }

    private ParameterParser<?>[] getParameters(ParameterManager parameterManager) {
        java.lang.reflect.Parameter[] rawParameters = executor.getParameters();
        if(rawParameters.length == 0 || rawParameters[0].getType() != CommandContext.class) {
            throw new IllegalArgumentException("Missing command context parameter from " + label);
        }

        ParameterParser<?>[] parameters = new ParameterParser<?>[rawParameters.length-1];

        for(int i=1; i<rawParameters.length; i++) {
            ParameterParser<?> type = convertParameter(rawParameters[i], parameterManager);
            Objects.requireNonNull(type, "The parameter of " + label + " linked to " + rawParameters[i].getName() + " isn't registered");

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
    private ParameterParser<?> convertParameter(java.lang.reflect.Parameter parameter, ParameterManager parameterManager) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (parameterManager.isRegistered(annotation.annotationType())) {

                ArrayList<Object> params = new ArrayList<>();
                for (Method method : annotation.annotationType().getMethods()) {
                    ParameterParser.Property property = method.getAnnotation(ParameterParser.Property.class);
                    if (property != null) {

                        int index = property.index();

                        //Fill ArrayList to prevent OutOfBoundsException
                        for(int i=0; i<=index; i++) {
                            if(params.size() <= i) {
                                params.add(null);
                            }
                        }

                        try {
                            params.set(index, method.invoke(annotation));
                        } catch (IllegalAccessException | InvocationTargetException ignored) {}
                    }
                }

                return parameterManager.getParameter(annotation.annotationType(), params.toArray());
            }
        }

        return null;
    }

    private CommandNode[] getChildren(String[] labels, Class<? extends Command> target, ParameterManager parameterManager) {

        CommandNode[] children = new CommandNode[labels.length];
        Method[] methods = target.getDeclaredMethods();
        int matches = 0;

        for(int i=0; i<labels.length; i++) {

            for(Method m : methods) {
                tk.empee.commandManager.command.annotations.CommandNode annotation = m.getAnnotation(tk.empee.commandManager.command.annotations.CommandNode.class);
                if(annotation != null && annotation.label().equals(labels[i])) {
                    m.setAccessible(true);
                    children[i] = new CommandNode(m, target, parameterManager);
                    matches += 1;
                }
            }

        }

        if(matches != labels.length) {
            throw new IllegalArgumentException("Can't find all sub-commands of " + label);
        }

        return children;

    }

}
