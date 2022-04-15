package tk.empee.commandManager.command.parsers.types;

import lombok.Getter;
import org.bukkit.command.CommandException;
import tk.empee.commandManager.command.parsers.types.annotations.FloatParam;

public class FloatParser extends ParameterParser<Float> {

    @Getter private final float min;
    @Getter private final float max;

    protected FloatParser(String label, String defaultValue, Float min, Float max) {
        super(FloatParam.class, label, defaultValue);

        this.min = min;
        this.max = max;
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
