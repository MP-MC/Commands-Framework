package ml.empee.commandsManager.parsers.types;

import org.bukkit.command.CommandException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;

@Getter
@EqualsAndHashCode(callSuper = true)
public class IntegerParser extends ParameterParser<Integer> {

  public static final IntegerParser DEFAULT = new IntegerParser("", "", Integer.MIN_VALUE, Integer.MAX_VALUE);

  @Getter
  private final int min;
  @Getter
  private final int max;

  public IntegerParser(String label, String defaultValue, Integer min, Integer max) {
    super(label, defaultValue);

    this.min = min;
    this.max = max;

    descriptionBuilder = new DescriptionBuilder("integer", "This parameter can only contain an integer", new String[] {
        "Min: ", (min != Integer.MIN_VALUE ? min + "" : "-∞"),
        "Max: ", (max != Integer.MAX_VALUE ? max + "" : "+∞"),
        "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
    });
  }

  protected IntegerParser(IntegerParser parser) {
    super(parser);
    this.min = parser.min;
    this.max = parser.max;
  }

  @Override
  public Integer parse(int offset, String... args) {
    try {
      int result = Integer.parseInt(args[offset]);

      if (result < min) {
        throw new CommandException("The value must be higher then &e" + min + "&c but it's value is &e" + result);
      } else if (result > max) {
        throw new CommandException("The value must be lower then &e" + max + "&c but it's value is &e" + result);
      }

      return result;
    } catch (NumberFormatException e) {
      throw new CommandException("The value &e" + args[offset] + "&c must be an integer");
    }
  }

  @Override
  public ParameterParser<Integer> copyParser() {
    return new IntegerParser(this);
  }

}
