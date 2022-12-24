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
public class IntegerParser extends ParameterParser<Integer> {

  @Setter
  private int min;
  @Setter
  private int max;

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("integer", "This parameter can only contain an integer",
        Tuple.of("Min: ", (min != Integer.MIN_VALUE ? min + "" : "-∞")),
        Tuple.of("Max: ", (max != Integer.MAX_VALUE ? max + "" : "+∞")),
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().toString()))
    );
  }

  @Override
  public Integer parse(int offset, String... args) {
    try {
      int result = Integer.parseInt(args[offset]);

      if (result < min) {
        throw new CommandException("&e" + result + "&r must be equal or greater then &e" + min);
      } else if (result > max) {
        throw new CommandException("&e" + result + "&r must be equal or lower then &e" + min);
      }

      return result;
    } catch (NumberFormatException e) {
      throw new CommandException("The value &e" + args[offset] + "&r must be an integer");
    }
  }

  @Override
  public ParameterParser<Integer> copyParser() {
    IntegerParser parser = copyParser(new IntegerParser());
    parser.min = min;
    parser.max = max;
    return parser;
  }
}
