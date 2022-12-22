package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.helpers.Tuple;
import org.bukkit.command.CommandException;

@SuperBuilder
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LongParser extends ParameterParser<Long> {

  @Setter
  private long min;
  @Setter
  private long max;

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("long", "This parameter can only contain an integer",
        Tuple.of("Min: ", (min != Long.MIN_VALUE ? min + "" : "-∞")),
        Tuple.of("Max: ", (max != Long.MAX_VALUE ? max + "" : "+∞")),
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().toString()))
    );
  }

  @Override
  public Long parse(int offset, String... args) {
    try {
      long result = Long.parseLong(args[offset]);

      if (result < min) {
        throw new CommandException("&e" + result + "&r must be equal or greater then &e" + min);
      } else if (result > max) {
        throw new CommandException("&e" + result + "&r must be equal or lower then &e" + min);
      }

      return result;
    } catch (NumberFormatException e) {
      throw new CommandException("The number &e" + args[offset] + "&r isn't valid");
    }
  }

  @Override
  public ParameterParser<Long> copyParser() {
    LongParser parser = new LongParser();
    parser.label = label;
    parser.defaultValue = defaultValue;
    parser.min = min;
    parser.max = max;
    return parser;
  }
}
