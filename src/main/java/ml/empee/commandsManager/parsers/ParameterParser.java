package ml.empee.commandsManager.parsers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public abstract class ParameterParser<T> {

  protected DescriptionBuilder descriptionBuilder = new DescriptionBuilder("value", "This is a default description message", null);
  private String label;
  private T defaultValue;

  protected ParameterParser(String label, String defaultValue) {
    this.label = label;
    if (defaultValue == null || defaultValue.isEmpty()) {
      this.defaultValue = null;
    } else {
      this.defaultValue = parse(defaultValue);
    }
  }

  protected ParameterParser(ParameterParser<T> parser) {
    this.descriptionBuilder = parser.descriptionBuilder;
    this.label = parser.label;
    this.defaultValue = parser.defaultValue;
  }

  public T parse(String... args) {
    return parse(0, args);
  }

  public abstract T parse(int offset, String... args);

  public List<String> getSuggestions(CommandSender source, int offset, String[] args) {
    List<String> suggestions = getSuggestions(source, args[offset]);

    if (suggestions != null && !args[offset].isEmpty() && !suggestions.isEmpty()) {
      String arg = args[offset].toUpperCase(Locale.ROOT);
      List<String> matchedSuggestions = new ArrayList<>();
      for (String suggestion : suggestions) {
        if (suggestion.toUpperCase(Locale.ROOT).startsWith(arg)) {
          matchedSuggestions.add(suggestion);
        }
      }

      return matchedSuggestions;
    }

    return suggestions;
  }

  public List<String> getSuggestions(CommandSender source, String arg) {
    return new ArrayList<>();
  }

  public final T parseDefaultValue() {
    return defaultValue;
  }

  public final boolean isOptional() {
    return defaultValue != null;
  }

  /**
   * @return an parser deep copy
   */
  public abstract ParameterParser<T> copyParser();

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Property {
    int index();
  }

}
