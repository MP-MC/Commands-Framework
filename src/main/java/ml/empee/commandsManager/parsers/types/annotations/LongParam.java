package ml.empee.commandsManager.parsers.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ml.empee.commandsManager.parsers.ParameterParser;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LongParam {

  @ParameterParser.Property(index = 0)
  String label() default "";

  @ParameterParser.Property(index = 1)
  String defaultValue() default "";

  @ParameterParser.Property(index = 2)
  long min() default Long.MIN_VALUE;

  @ParameterParser.Property(index = 3)
  long max() default Long.MAX_VALUE;

}
