package ml.empee.commandsManager.parsers.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ml.empee.commandsManager.parsers.ParameterParser;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegerParam {

  @ParameterParser.Property(index = 0)
  String label() default "";

  @ParameterParser.Property(index = 1)
  String defaultValue() default "";

  @ParameterParser.Property(index = 2)
  int min() default Integer.MIN_VALUE;

  @ParameterParser.Property(index = 3)
  int max() default Integer.MAX_VALUE;

}
