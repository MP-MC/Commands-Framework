package ml.empee.commandsManager.parsers;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;

public abstract class ParameterParser<T> {

    @Getter protected ParserDescription descriptor = new ParserDescription("value", "This is a default description message", null);
    @Getter private final String label;
    @Getter private final T defaultValue;

    protected ParameterParser(String label, String defaultValue) {
        this.label = label;
        if(defaultValue == null || defaultValue.isEmpty()) {
            this.defaultValue = null;
        } else {
            this.defaultValue = parse(defaultValue);
        }
    }

    public T parse(String... args) {
        return parse(0, args);
    }
    public abstract T parse(int offset, String... args);

    public List<String> getSuggestions(CommandSender source, int offset, String[] args) {
        List<String> suggestions = getSuggestions(source, args[offset]);

        if(suggestions != null && !args[offset].isEmpty() && !suggestions.isEmpty()) {
            String arg = args[offset].toUpperCase(Locale.ROOT);
            List<String> matchedSuggestions = new ArrayList<>();
            for (String suggestion : suggestions) {
                if(suggestion.toUpperCase(Locale.ROOT).startsWith(arg)) {
                    matchedSuggestions.add(suggestion);
                }
            }

            return matchedSuggestions;
        }

        return suggestions;
    }

    public List<String> getSuggestions(CommandSender source, String arg) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass() == getClass() && ((ParameterParser<?>) o).label.equals(label) && Objects.equals( ((ParameterParser<?>) o).defaultValue, defaultValue );
    }

    public final T parseDefaultValue() {
        return defaultValue;
    }

    public final boolean isOptional() {
        return defaultValue != null;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Property {
        int index();
    }

}
