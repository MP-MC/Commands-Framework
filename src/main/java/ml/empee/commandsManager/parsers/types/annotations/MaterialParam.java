package ml.empee.commandsManager.parsers.types.annotations;

public @interface MaterialParam {
  String label() default "";
  String defaultValue() default "";
}
