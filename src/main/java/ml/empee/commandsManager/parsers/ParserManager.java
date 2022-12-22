package ml.empee.commandsManager.parsers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import ml.empee.commandsManager.parsers.types.EnumParser;
import org.jetbrains.annotations.Nullable;

public final class ParserManager {

  private final Map<Class<? extends Annotation>, ParameterParser<?>> parsersIdentifiers = new HashMap<>();
  private final Map<Class<?>, ParameterParser<?>> defaultParsers = new HashMap<>();
  private final HashSet<ParameterParser<?>> parsersCache = new HashSet<>();

  public void registerParser(ParameterParser<?> parser, @Nullable Class<? extends Annotation> identifier, Class<?>... defaultTypes) {
    parsersCache.add(parser);

    if(identifier != null) {
      parsersIdentifiers.put(identifier, parser);
    }

    for(Class<?> clazz : defaultTypes) {
      defaultParsers.put(clazz, parser);
    }
  }

  @Nullable
  public ParameterParser<?> getParameterParser(Parameter parameter) {
    Annotation identifier = findIdentifier(parameter.getAnnotations());
    ParameterParser<?> parser;
    if(identifier != null) {
      parser = buildParser(identifier);
    } else if(parameter.getType().isEnum()) {
      parser = EnumParser.builder().label("values").build();
    } else {
      parser = defaultParsers.get(parameter.getType());
      if(parser == null) {
        return null;
      } else {
        parser = parser.copyParser();
      }
    }

    if(parameter.isNamePresent()) {
      parser.setLabel(parameter.getName());
    }

    if(parser instanceof EnumParser) {
      ((EnumParser) parser).setEnumType(parameter.getType());
    }

    return cacheParser(parser);
  }

  public ParameterParser<?> cacheParser(ParameterParser<?> parser) {
    if(parsersCache.add(parser)) {
      return parser;
    } else {
      return parsersCache.stream().filter(p -> p.equals(parser)).findFirst().orElse(parser);
    }
  }

  @SneakyThrows
  public ParameterParser<?> buildParser(Annotation annotation) {
    Class<? extends Annotation> annotationClazz = annotation.annotationType();
    ParameterParser<?> originalParser = parsersIdentifiers.get(annotationClazz);
    Objects.requireNonNull(
        originalParser,
        "The annotation " + annotationClazz.getName() + " isn't an parser identifier"
    );

    ParameterParser<?> clonedParser = originalParser.copyParser();
    List<Method> annotationFields = Arrays.asList(annotationClazz.getMethods());
    List<Method> parserMethods = getAllMethods(clonedParser.getClass()).stream()
        .filter(m -> m.getReturnType() == void.class)
        .filter(m -> m.getParameterCount() == 1)
        .filter(m -> annotationFields.stream().anyMatch(
            f -> m.getName().equalsIgnoreCase("set" + f.getName()) && m.getParameters()[0].getType() == f.getReturnType()
        )).collect(Collectors.toList());

    for(Method parserMethod : parserMethods) {
      for(Method annotationField : annotationFields) {
        if(parserMethod.getName().equalsIgnoreCase("set" + annotationField.getName())) {
          parserMethod.setAccessible(true);
          parserMethod.invoke(clonedParser, annotationField.invoke(annotation));
        }
      }
    }

    return clonedParser;
  }

  private Annotation findIdentifier(Annotation... annotations) {
    for(Annotation annotation : annotations) {
      if(parsersIdentifiers.containsKey(annotation.annotationType())) {
        return annotation;
      }
    }

    return null;
  }

  private static List<Method> getAllMethods(Class<?> clazz) {
    List<Method> methods = new ArrayList<>();
    while (clazz != null) {
      methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
      clazz = clazz.getSuperclass();
    }

    return methods;
  }

}
