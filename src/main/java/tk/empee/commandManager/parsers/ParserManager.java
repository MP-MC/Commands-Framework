package tk.empee.commandManager.parsers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public final class ParserManager {

    private final HashMap<Class<? extends Annotation>, Class<? extends ParameterParser<?>>> registeredParameters = new HashMap<>();
    private final ArrayList<ParameterParser<?>> parameterParsers = new ArrayList<>();

    public void registerParser(Class<? extends Annotation> identifier, Class<? extends ParameterParser<?>> parameter) {
        registeredParameters.put(identifier, parameter);
    }

    public boolean isRegistered(Class<? extends Annotation> identifier) {
        return registeredParameters.get(identifier) != null;
    }

    /**
     * Extract the fields from the annotation and build the parser using {@link #buildParser(Class, Object...)}
     */
    public ParameterParser<?> buildParser(Annotation annotation) {
        if (isRegistered(annotation.annotationType())) {
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

            return buildParser(annotation.annotationType(), params.toArray());
        }

        return null;

    }
    /**
     * Build a parser with the given parameters, if an equal parser already exists return the instance of the cached one
     */
    public ParameterParser<?> buildParser(Class<? extends Annotation> identifier, Object... params) {

        ParameterParser<?> parameter = createParser(identifier, params);
        for(ParameterParser<?> p : parameterParsers) {

            if(p.getIdentifier() == identifier) {
                if(p.equals(parameter)) {
                    return p;
                }
            }

        }

        parameterParsers.add(parameter);
        return parameter;
    }
    private ParameterParser<?> createParser(Class<? extends Annotation> identifier, Object[] params) {

        try {
            Class<?>[] paramsType = new Class<?>[params.length];
            for(int i=0; i<paramsType.length; i++) {
                paramsType[i] = params[i].getClass();
            }

            Constructor<? extends ParameterParser<?>> constructor = findParser(identifier).getConstructor(paramsType);

            return constructor.newInstance(params);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("The parameter " + identifier.getName() + " is missing the default constructor", e);
        }

    }
    private Class<? extends ParameterParser<?>> findParser(Class<? extends Annotation> identifier) {

        Class<? extends ParameterParser<?>> parameterClazz = registeredParameters.get(identifier);

        if(parameterClazz == null) {
            throw new IllegalArgumentException("The parameter linked to " + identifier.getName() + " isn't registered");
        }

        return parameterClazz;

    }

}
