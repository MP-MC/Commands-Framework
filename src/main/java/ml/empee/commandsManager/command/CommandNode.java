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

  /**
   * If the node is marked as an exit node it means that that node should be executed only if
   * there isn't any subcommand to execute.
   */
  boolean exitNode() default true;

}
