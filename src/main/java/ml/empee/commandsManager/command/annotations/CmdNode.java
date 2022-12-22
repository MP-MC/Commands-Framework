package ml.empee.commandsManager.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CmdNode {

  String parent() default "";

  String label();

  String permission() default "";

  String description() default "";

  boolean executable() default true;

}
