package ml.empee.commandsManager.parsers.types.annotations;

import ml.empee.commandsManager.parsers.ParameterParser;

public @interface MaterialParam {

  @ParameterParser.Property(index = 0)
  String label() default "";

  @ParameterParser.Property(index = 1)
  String defaultValue() default "";

}
