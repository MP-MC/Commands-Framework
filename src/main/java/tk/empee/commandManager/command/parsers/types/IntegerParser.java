package tk.empee.commandManager.command.parsers.types;

import lombok.Getter;
import org.bukkit.command.CommandException;
import tk.empee.commandManager.command.parsers.types.annotations.IntegerParam;

public class IntegerParser extends ParameterParser<Integer> {

    @Getter private final int min;
    @Getter private final int max;

    public IntegerParser(String label, String defaultValue, Integer min, Integer max) {
        super(IntegerParam.class, label, defaultValue);

        this.min = min;
        this.max = max;
    }

    @Override
    public Integer parse(int offset, String... args) {
        try {
            int result = Integer.parseInt(args[offset]);

            if(result < min) {
                throw new CommandException("&4&l > &cThe value must be higher then &e" + min + "&c but it's value is &e" + result);
            } else if(result > max) {
                throw new CommandException("&4&l > &cThe value must be lower then &e" + max + "&c but it's value is &e" + result);
            }

            return result;
        } catch (NumberFormatException e) {
            throw new CommandException("&4&l > &cThe value &e" + args[offset] + "&c must be an integer");
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && ((IntegerParser) o).min == min && ((IntegerParser) o).max == max;
    }


}
