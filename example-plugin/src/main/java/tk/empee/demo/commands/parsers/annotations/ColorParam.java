package tk.empee.demo.commands.parsers.annotations;

import tk.empee.commandManager.parsers.ParameterParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColorParam {

    @ParameterParser.Property(index = 0)
    String label() default "";
    @ParameterParser.Property(index = 1)
    String defaultValue() default "";

}
