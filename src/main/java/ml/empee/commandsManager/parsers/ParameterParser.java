package ml.empee.commandsManager.parsers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

@Getter
@EqualsAndHashCode
public abstract class ParameterParser<T> {

  private final T defaultValue;

  @Setter
  private String label;

  protected ParameterParser(String label, String defaultValue) {
    this.label = label;
    if (defaultValue == null || defaultValue.isEmpty()) {
      this.defaultValue = null;
    } else {
      this.defaultValue = parse(defaultValue);
    }
  }

  protected ParameterParser(ParameterParser<T> parser) {
    this.label = parser.label;
    this.defaultValue = parser.defaultValue;
  }

  public Class<?>[] getNeededParsers() {
    return new Class[0];
  }

  public abstract DescriptionBuilder getDescriptionBuilder();

  public final T parse(String... args) {
    return parse(0, args);
  }

  public abstract T parse(int offset, String... args);

  public final List<String> getSuggestions(CommandSender source, int offset, String[] args) {
    List<String> suggestions = buildSuggestions(source, offset, args);

    if (suggestions != null && !args[offset].isEmpty() && !suggestions.isEmpty()) {
      String arg = args[offset].toUpperCase(Locale.ROOT);
      List<String> matchedSuggestions = new ArrayList<>();
      for (String suggestion : suggestions) {
        if (suggestion.toUpperCase().startsWith(arg)) {
          matchedSuggestions.add(suggestion);
        }
      }

      return matchedSuggestions;
    }

    return suggestions;
  }

  protected List<String> buildSuggestions(CommandSender source, int offset, String[] args) {
    return buildSuggestions(source, args[offset]);
  }

  protected List<String> buildSuggestions(CommandSender source, String arg) {
    return new ArrayList<>();
  }

  public final boolean isOptional() {
    return defaultValue != null;
  }

  /**
   * @return a parser deep copy
   */
  public abstract ParameterParser<T> copyParser();

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Property {
    int index();
  }

}
