package tk.empee.commandManager.command.parameters.parsers.annotations;

import tk.empee.commandManager.command.parameters.parsers.ParameterParser;

public @interface DoubleParam {

    @ParameterParser.Property(index = 0)
    String label();
    @ParameterParser.Property(index = 1)
    String defaultValue();
    @ParameterParser.Property(index = 2)
    double min() default Double.MIN_VALUE;
    @ParameterParser.Property(index = 3)
    double max() default Double.MAX_VALUE;

}
