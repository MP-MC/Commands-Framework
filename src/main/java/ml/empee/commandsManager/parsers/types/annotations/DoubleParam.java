package ml.empee.commandsManager.parsers.types.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoubleParam {
  String label() default "";
  String defaultValue() default "";
  double min() default -Double.MAX_VALUE;
  double max() default Double.MAX_VALUE;
}
