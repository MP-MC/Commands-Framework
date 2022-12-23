package ml.empee.commandsManager.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CommandNode {

  String parent() default "";

  String label();

  String[] aliases() default {};

  String permission() default "";

  String description() default "";

  boolean executable() default true;

}
