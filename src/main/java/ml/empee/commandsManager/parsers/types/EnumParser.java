package ml.empee.commandsManager.parsers.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;

@Getter
@EqualsAndHashCode(callSuper = true)
public class EnumParser<T extends Enum<T>> extends ParameterParser<T> {

  private final Class<T> enumType;
  private final List<String> suggestions;

  //TODO Annotation constructor
  public EnumParser(Class<T> enumType) {
    super("", "");

    this.enumType = enumType;

    int i = 0;
    suggestions = new ArrayList<>();
    String[] values = new String[enumType.getEnumConstants().length * 2];
    for(T value : enumType.getEnumConstants()) {
      values[i] = "- ";
      values[i+1] = value.name();
      suggestions.add(value.name());
      i+=2;
    }

    descriptionBuilder = new DescriptionBuilder("enum", "This parameter can only contains these values", values);
  }

  public EnumParser(EnumParser<T> parser) {
    super(parser);

    this.suggestions = parser.suggestions;
    this.enumType = parser.enumType;
  }

  @Override
  public List<String> getSuggestions(CommandSender source, String arg) {
    return suggestions;
  }


  @Override
  public T parse(int offset, String... args) {
    try {
      return Enum.valueOf(enumType, args[offset].toUpperCase(Locale.ENGLISH));
    } catch (IllegalArgumentException e) {
      throw new CommandException("The value &e" + args[offset] + "&c isn't valid");
    }
  }

  @Override
  public ParameterParser<T> copyParser() {
    return new EnumParser<>(this);
  }
}
