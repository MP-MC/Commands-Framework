package ml.empee.commandsManager.parsers.types;

import lombok.Getter;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserDescription;
import org.bukkit.command.CommandException;

public class LongParser extends ParameterParser<Long> {

    public static final LongParser DEFAULT = new LongParser("", "", Long.MIN_VALUE, Long.MAX_VALUE);

    @Getter private final long min;
    @Getter private final long max;

    public LongParser(String label, String defaultValue, Long min, Long max) {
        super(label, defaultValue);

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
