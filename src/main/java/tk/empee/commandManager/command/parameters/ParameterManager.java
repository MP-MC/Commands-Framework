package tk.empee.commandManager.command.parameters;

import tk.empee.commandManager.command.parameters.parsers.ParameterParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public final class ParameterManager {

    private final HashMap<Class<? extends Annotation>, Class<? extends ParameterParser<?>>> registeredParameters = new HashMap<>();
    private final ArrayList<ParameterParser<?>> parameters = new ArrayList<>();

    public void registerParameter(Class<? extends Annotation> identifier, Class<? extends ParameterParser<?>> parameter) {
        registeredParameters.put(identifier, parameter);
    }

    public boolean isRegistered(Class<? extends Annotation> identifier) {
        return registeredParameters.get(identifier) != null;
    }

    public ParameterParser<?> getParameter(Class<? extends Annotation> identifier, Object... params) {

        ParameterParser<?> parameter = createParameter(identifier, params);

        for(ParameterParser<?> p : parameters) {

            if(p.getIdentifier() == identifier) {

                if(p.equals(parameter)) {
                    return p;
                }
            }

        }

        parameters.add(parameter);
        return parameter;
    }
    private ParameterParser<?> createParameter(Class<? extends Annotation> identifier, Object[] params) {

        try {
            Class<?>[] paramsType = new Class<?>[params.length];
            for(int i=0; i<paramsType.length; i++) {
                paramsType[i] = params[i].getClass();
            }

            Constructor<? extends ParameterParser<?>> constructor = findParameter(identifier).getConstructor(paramsType);

            return constructor.newInstance(params);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("The parameter " + identifier.getName() + " is missing the default constructor", e);
        }

    }
    private Class<? extends ParameterParser<?>> findParameter(Class<? extends Annotation> identifier) {

        Class<? extends ParameterParser<?>> parameterClazz = registeredParameters.get(identifier);

        if(parameterClazz == null) {
            throw new IllegalArgumentException("The parameter linked to " + identifier.getName() + " isn't registered");
        }

        return parameterClazz;

    }

}
