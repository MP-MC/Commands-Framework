package ml.empee.commandsManager.parsers.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import lombok.EqualsAndHashCode;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserDescription;
import net.md_5.bungee.api.ChatColor;

@EqualsAndHashCode(callSuper = true)
public class ColorParser extends ParameterParser<ChatColor> {

    public static final ColorParser DEFAULT = new ColorParser("color", "");
    private static final List<String> COLORS;

    static  {
        COLORS = Collections.unmodifiableList(Arrays.asList(
                "BLACK",
                "DARK_BLUE",
                "DARK_GREEN",
                "DARK_AQUA",
                "DARK_RED",
                "DARK_PURPLE",
                "GOLD",
                "GRAY",
                "DARK_GRAY",
                "BLUE",
                "GREEN",
                "AQUA",
                "RED",
                "LIGHT_PURPLE",
                "YELLOW",
                "WHITE"
        ));
    }

    public ColorParser(String label, String defaultValue) {
        super(label, defaultValue);

        descriptor = new ParserDescription("color", "This parameter can only contain a valid color", new String[]{
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
    }

    protected ColorParser(ColorParser parser) {
        super(parser);
    }

    @Override
    public ChatColor parse(int offset, String... strings) {
        try {
            strings[offset] = strings[offset].toUpperCase();
            if(!COLORS.contains(strings[offset])) {
                if(strings[offset].length() == 6) {
                    strings[offset] = "#" + strings[offset];
                } else {
                    throw new CommandException("The color &e" + strings[offset] + "&c isn't valid");
                }
            }

            return ChatColor.of(strings[offset]);
        } catch (IllegalArgumentException e) {
            throw new CommandException("The color &e" + strings[offset] + "&c isn't valid");
        }
    }

    @Override
    public List<String> getSuggestions(CommandSender source, String arg) {
        return COLORS;
    }

    @Override
    public ParameterParser<ChatColor> clone() {
        return new ColorParser(this);
    }

}
