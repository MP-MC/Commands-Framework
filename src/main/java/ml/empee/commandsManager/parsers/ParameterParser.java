package ml.empee.commandsManager.parsers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
public abstract class ParameterParser<T> {

  protected T defaultValue;

  protected String label;

  @Setter
  protected boolean optional;

  public void setLabel(String label) {
    if(label.trim().isEmpty()) {
      return;
    }

    this.label = label;
  }

  public void setDefaultValue(String value) {
    if(value.trim().isEmpty()) {
      return;
    }

    defaultValue = parse(value);
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

    if(suggestions != null && !args[offset].isEmpty() && !suggestions.isEmpty()) {
      String arg = args[offset].toUpperCase(Locale.ROOT);
      List<String> matchedSuggestions = new ArrayList<>();
      for(String suggestion : suggestions) {
        if(suggestion.toUpperCase().startsWith(arg)) {
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
    return optional || defaultValue != null;
  }

  /**
   * @return a parser deep copy
   */
  public abstract ParameterParser<T> copyParser();

  protected <K extends ParameterParser<T>> K copyParser(K parser) {
    parser.label = label;
    parser.defaultValue = defaultValue;
    parser.optional = optional;
    return parser;
  }

}
