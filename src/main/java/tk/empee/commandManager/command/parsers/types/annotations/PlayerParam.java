package tk.empee.commandManager.command.parsers.types.annotations;

import tk.empee.commandManager.command.parsers.types.ParameterParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PlayerParam {

    @ParameterParser.Property(index = 0)
    String label() default "";
    @ParameterParser.Property(index = 1)
    boolean online() default true;
    @ParameterParser.Property(index = 2)
    String defaultValue() default "";

}
