package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserDescription;
import org.bukkit.command.CommandException;

@Getter
@EqualsAndHashCode(callSuper = true)
public class DoubleParser extends ParameterParser<Double> {

    public static final DoubleParser DEFAULT = new DoubleParser("", "", -Double.MAX_VALUE, Double.MAX_VALUE);

    @Getter private final double min;
    @Getter private final double max;

    public DoubleParser(String label, String defaultValue, Double min, Double max) {
        super(label, defaultValue);

        this.min = min;
        this.max = max;

        descriptor = new ParserDescription("double", "This parameter can only contain a decimal number", new String[]{
                "Min: ", (min != -Double.MAX_VALUE ? min+"" : "-∞"),
                "Max: ", (max != Double.MAX_VALUE ? max+"" : "+∞"),
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
    }

    protected DoubleParser(DoubleParser parser) {
        super(parser);
        this.min = parser.min;
        this.max = parser.max;
    }

    @Override
    public Double parse(int offset, String... args) {
        try {
            double result = Double.parseDouble(args[offset]);

            if(result < min) {
                throw new CommandException("&4&l > &cThe value must be higher then &e" + min + "&c but it's value is &e" + result);
            } else if(result > max) {
                throw new CommandException("&4&l > &cThe value must be lower then &e" + max + "&c but it's value is &e" + result);
            }

            return result;
        } catch (NumberFormatException e) {
            throw new CommandException(" &4&l>&c The number &e" + args[offset] + "&c isn't valid");
        }
    }

    @Override
    public ParameterParser<Double> clone() {
        return new DoubleParser(this);
    }

}
