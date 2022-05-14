package tk.empee.commandsFramework.parsers.types;

import lombok.Getter;
import org.bukkit.command.CommandException;
import tk.empee.commandsFramework.parsers.ParameterParser;
import tk.empee.commandsFramework.parsers.ParserDescription;

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
    public boolean equals(Object o) {
        return super.equals(o) && min == ((FloatParser) o).min && max == ((FloatParser) o).max;
    }

}
