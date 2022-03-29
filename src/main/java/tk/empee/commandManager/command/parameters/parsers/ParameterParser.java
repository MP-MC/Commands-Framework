package tk.empee.commandManager.command.parameters.parsers;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.lang.annotation.*;
import java.util.Collections;
import java.util.List;

public abstract class ParameterParser<T> {

    @Getter private final Class<? extends Annotation> identifier;
    @Getter private final String label;
    @Getter private final String defaultValue;

    protected ParameterParser(Class<? extends Annotation> identifier, String label, String defaultValue) {
        this.identifier = identifier;
        this.label = label;
        this.defaultValue = defaultValue;
    }

    public T parse(String... args) {
        return parse(0, args);
    }
    public abstract T parse(int offset, String... args);

    public List<String> getSuggestions(CommandSender source) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass() == getClass() && ((ParameterParser<?>) o).label.equals(label) && ((ParameterParser<?>) o).defaultValue.equals(defaultValue);
    }

    public final T parseDefaultValue() {
        return parse(defaultValue);
    }

    public final boolean isOptional() {
        return !defaultValue.isEmpty();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Property {
        int index();
    }

    public interface Greedy {
    }
}
