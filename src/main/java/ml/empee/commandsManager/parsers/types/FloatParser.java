package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserDescription;
import org.bukkit.command.CommandException;

@Getter
@EqualsAndHashCode(callSuper = true)
public class FloatParser extends ParameterParser<Float> {

    public static final FloatParser DEFAULT = new FloatParser("", "", -Float.MAX_VALUE, Float.MAX_VALUE);

    @Getter private final float min;
    @Getter private final float max;

    public FloatParser(String label, String defaultValue, Float min, Float max) {
        super(label, defaultValue);

        this.min = min;
        this.max = max;

        descriptor = new ParserDescription("float", "This parameter can only contain a decimal number", new String[]{
                "Min: ", (min != -Float.MAX_VALUE ? min+"" : "-∞"),
                "Max: ", (max != Float.MAX_VALUE ? max+"" : "+∞"),
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
    }

    protected FloatParser(FloatParser parser) {
        super(parser);
        this.min = parser.min;
        this.max = parser.max;
    }

    @Override
    public Float parse(int offset, String... args) {
        try {
            float result = Float.parseFloat(args[offset]);

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
    public ParameterParser<Float> clone() {
        return new FloatParser(this);
    }

}
