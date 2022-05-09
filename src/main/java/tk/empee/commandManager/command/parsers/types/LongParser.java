package tk.empee.commandManager.command.parsers.types;

import lombok.Getter;
import org.bukkit.command.CommandException;
import tk.empee.commandManager.command.parsers.ParserDescription;
import tk.empee.commandManager.command.parsers.types.annotations.LongParam;

public class LongParser extends ParameterParser<Long> {
    @Getter private final long min;
    @Getter private final long max;

    public LongParser(String label, String defaultValue, Long min, Long max) {
        super(LongParam.class, label, defaultValue);

        this.min = min;
        this.max = max;

        descriptor = new ParserDescription("long", "This parameter can only contain an integer", new String[]{
                "Min: ", (min != Long.MIN_VALUE ? min+"" : "-∞"),
                "Max: ", (max != Long.MAX_VALUE ? max+"" : "+∞"),
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
    }

    @Override
    public Long parse(int offset, String... args) {
        try {
            long result = Long.parseLong(args[offset]);

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
        return super.equals(o) && min == ((LongParser) o).min && max == ((LongParser) o).max;
    }
}
