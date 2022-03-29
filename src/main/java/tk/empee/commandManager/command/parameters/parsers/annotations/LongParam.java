package tk.empee.commandManager.command.parameters.parsers.annotations;

import tk.empee.commandManager.command.parameters.parsers.ParameterParser;

public @interface LongParam {

    @ParameterParser.Property(index = 0)
    String label();
    @ParameterParser.Property(index = 1)
    String defaultValue();
    @ParameterParser.Property(index = 2)
    long min() default Long.MIN_VALUE;
    @ParameterParser.Property(index = 3)
    long max() default Long.MAX_VALUE;

}
