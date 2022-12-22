package ml.empee.commandsManager.parsers.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.Tuple;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

@Getter @SuperBuilder @NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EnumParser<T extends Enum<T>> extends ParameterParser<T> {

  private Class<T> enumType;
  private List<String> suggestions;

  public void setEnumType(Class<T> enumType) {
    this.enumType = enumType;
    this.suggestions = Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
  }

  @Override
  public List<String> buildSuggestions(CommandSender source, String arg) {
    return suggestions;
  }

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    ArrayList<Tuple<String, String>> tuples = new ArrayList<>();
    for(T value : enumType.getEnumConstants()) {
      tuples.add(Tuple.of("- ", value.name()));
    }

    return new DescriptionBuilder(
        "enum", "This parameter can only contains these values", tuples.toArray(new Tuple[0])
    );
  }

  @Override
  public T parse(int offset, String... args) {
    try {
      return Enum.valueOf(enumType, args[offset].toUpperCase(Locale.ENGLISH));
    } catch (IllegalArgumentException e) {
      throw new CommandException("The value &e" + args[offset] + "&r isn't valid");
    }
  }

  @Override
  public ParameterParser<T> copyParser() {
    EnumParser<T> parser = new EnumParser<>();
    parser.label = label;
    parser.defaultValue = defaultValue;
    parser.setEnumType(enumType);
    return parser;
  }
}
