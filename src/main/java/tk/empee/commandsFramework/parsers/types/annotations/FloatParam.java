package tk.empee.commandsFramework.parsers.types.annotations;

import tk.empee.commandsFramework.parsers.ParameterParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FloatParam {

    @ParameterParser.Property(index = 0)
    String label() default "";
    @ParameterParser.Property(index = 1)
    String defaultValue() default "";
    @ParameterParser.Property(index = 2)
    float min() default -Float.MAX_VALUE;
    @ParameterParser.Property(index = 3)
    float max() default Float.MAX_VALUE;

}
