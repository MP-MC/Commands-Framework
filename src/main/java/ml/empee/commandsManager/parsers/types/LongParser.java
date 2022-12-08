package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.Tuple;
import org.bukkit.command.CommandException;

@Getter
@EqualsAndHashCode(callSuper = true)
public class LongParser extends ParameterParser<Long> {

  public static final LongParser DEFAULT = new LongParser("", "", Long.MIN_VALUE, Long.MAX_VALUE);

  private final long min;
  private final long max;

  public LongParser(String label, String defaultValue, Long min, Long max) {
    super(label, defaultValue);

    this.min = min;
    this.max = max;
  }

  protected LongParser(LongParser parser) {
    super(parser);
    this.min = parser.min;
    this.max = parser.max;
  }

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
  public ParameterParser<Long> copyParser() {
    return new LongParser(this);
  }
}
