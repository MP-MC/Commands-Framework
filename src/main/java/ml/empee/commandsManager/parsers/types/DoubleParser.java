package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.Tuple;
import org.bukkit.command.CommandException;

@Getter
@EqualsAndHashCode(callSuper = true)
public class DoubleParser extends ParameterParser<Double> {

  public static final DoubleParser DEFAULT = new DoubleParser("", "", -Double.MAX_VALUE, Double.MAX_VALUE);

  @Getter
  private final double min;
  @Getter
  private final double max;

  public DoubleParser(String label, String defaultValue, Double min, Double max) {
    super(label, defaultValue);

    this.min = min;
    this.max = max;
  }

  protected DoubleParser(DoubleParser parser) {
    super(parser);
    this.min = parser.min;
    this.max = parser.max;
  }

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
        throw new CommandException("The value must be higher then &e" + min + "&c but it's value is &e" + result);
      } else if (result > max) {
        throw new CommandException("The value must be lower then &e" + max + "&c but it's value is &e" + result);
      }

      return result;
    } catch (NumberFormatException e) {
      throw new CommandException("The number &e" + args[offset] + "&c isn't valid");
    }
  }

  @Override
  public ParameterParser<Double> copyParser() {
    return new DoubleParser(this);
  }

}
