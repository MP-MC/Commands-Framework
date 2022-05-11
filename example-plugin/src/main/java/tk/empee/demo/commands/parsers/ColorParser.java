package tk.empee.demo.commands.parsers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import tk.empee.commandManager.parsers.ParameterParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColorParser extends ParameterParser<ChatColor> {

    public static final ColorParser DEFAULT = new ColorParser("", "");
    private static final List<String> COLORS;

    static  {
        COLORS = Collections.unmodifiableList(
                Arrays.stream(ChatColor.values())
                .map(Enum::name).collect(Collectors.toList())
        );
    }

    public ColorParser(String label, String defaultValue) {
        super(label, defaultValue);
    }

    @Override
    public ChatColor parse(int offset, String... strings) {
        try {
            return ChatColor.valueOf(strings[offset].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandException(" &4&l> &cThe color &e" + strings[offset] + "&c isn't valid");
        }
    }

    @Override
    public List<String> getSuggestions(CommandSender source, String arg) {
        return COLORS;
    }

}
