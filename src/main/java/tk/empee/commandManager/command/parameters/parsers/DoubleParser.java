package tk.empee.commandManager.command.parameters.parsers;

import lombok.Getter;
import org.bukkit.command.CommandException;
import tk.empee.commandManager.command.parameters.parsers.annotations.DoubleParam;

public class DoubleParser extends ParameterParser<Double> {

    @Getter private final double min;
    @Getter private final double max;

    protected DoubleParser(String label, String defaultValue, Double min, Double max) {
        super(DoubleParam.class, label, defaultValue);

        this.min = min;
        this.max = max;
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
}
