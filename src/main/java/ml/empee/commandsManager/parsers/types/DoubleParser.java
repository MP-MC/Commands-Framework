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
public class DoubleParser extends ParameterParser<Double> {
  @Setter
  private double min;
  @Setter
  private double max;

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("double", "This parameter can only contain a decimal number",
        Tuple.of("Min: ", (min <= -Double.MAX_VALUE ? min + "" : "-∞")),
        Tuple.of("Max: ", (max >= Double.MAX_VALUE ? max + "" : "+∞")),
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().toString()))
    );
  }

  @Override
  public Double parse(int offset, String... args) {
    try {
      double result = Double.parseDouble(args[offset]);

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
  public ParameterParser<Double> copyParser() {
    DoubleParser parser = copyParser(new DoubleParser());
    parser.min = min;
    parser.max = max;
    return parser;
  }

}
