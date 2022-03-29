package tk.empee.commandManager.command.parameters.parsers.annotations;

import tk.empee.commandManager.command.parameters.parsers.ParameterParser;

public @interface BoolParam {

    @ParameterParser.Property(index = 0)
    String label() default "";
    @ParameterParser.Property(index = 1)
    String defaultValue() default "";
}
