package tk.empee.commandManager.parsers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public final class ParserManager {

    private final HashMap<Class<?>, ParameterParser<?>> defaultParsers = new HashMap<>();
    private final HashMap<Class<? extends Annotation>, Class<? extends ParameterParser<?>>> registeredParsers = new HashMap<>();
    private final ArrayList<ParameterParser<?>> parsersCache = new ArrayList<>();

    public void registerParser(Class<? extends Annotation> identifier, Class<? extends ParameterParser<?>> parser) {
        registeredParsers.put(identifier, parser);
    }

    public void registerDefaultParser(Class<?> targetType, ParameterParser<?> parser) {
        defaultParsers.put(targetType, getCachedParser(parser));
    }

    public ParameterParser<?> getDefaultParser(Class<?> targetType) {
        return defaultParsers.get(targetType);
    }

    public boolean isRegistered(Class<? extends Annotation> identifier) {
        return registeredParsers.get(identifier) != null;
    }

    /**
     * Extract the fields from the annotation and build the parser
     */
    public ParameterParser<?> registerParser(Annotation annotation) {
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

            return getCachedParser(buildParser(annotation.annotationType(), params.toArray()));
        }

        return null;

    }
    private ParameterParser<?> buildParser(Class<? extends Annotation> identifier, Object[] params) {

        try {
            Class<?>[] paramsType = new Class<?>[params.length];
            for(int i=0; i<paramsType.length; i++) {
                paramsType[i] = params[i].getClass();
            }

            Constructor<? extends ParameterParser<?>> constructor = getParser(identifier).getConstructor(paramsType);

            return constructor.newInstance(params);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("The parameter " + identifier.getName() + " is missing the default constructor", e);
        }

    }
    private Class<? extends ParameterParser<?>> getParser(Class<? extends Annotation> identifier) {

        Class<? extends ParameterParser<?>> parameterClazz = registeredParsers.get(identifier);

        if(parameterClazz == null) {
            throw new IllegalArgumentException("The parser linked to " + identifier.getName() + " isn't registered");
        }

        return parameterClazz;

    }
    private ParameterParser<?> getCachedParser(ParameterParser<?> parser) {
        for(ParameterParser<?> p : parsersCache) {

            if(p.equals(parser)) {
                return p;
            }

        }

        parsersCache.add(parser);
        return parser;
    }

}
