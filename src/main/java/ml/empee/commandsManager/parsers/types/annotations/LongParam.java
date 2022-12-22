package ml.empee.commandsManager.parsers.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LongParam {
  String label() default "";
  String defaultValue() default "";
  long min() default Long.MIN_VALUE;
  long max() default Long.MAX_VALUE;
}
