package tk.empee.commandManager.command.parsers.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import tk.empee.commandManager.command.parsers.ParserDescription;
import tk.empee.commandManager.command.parsers.types.annotations.PlayerParam;

import java.util.List;
import java.util.UUID;

public class PlayerParser extends ParameterParser<OfflinePlayer> {

    private final boolean onlyOnline;

    public PlayerParser(String label, Boolean onlyOnline, String defaultValue) {
        super(PlayerParam.class, label, defaultValue);

        this.onlyOnline = onlyOnline;
        descriptor = new ParserDescription("player", "This parameter can only contain a player's name or his UUID", new String[]{
                "Requires online: ", onlyOnline.toString(),
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
    }

    @Override
    public OfflinePlayer parse(int offset, String... args) {

        OfflinePlayer player = Bukkit.getPlayer(args[offset]);
        if(player == null) {
            if(onlyOnline) {
                throw new CommandException("&4&l > &cThe player &e" + args[offset] + "&c isn't online");
            }

            try {
                player = Bukkit.getOfflinePlayer(UUID.fromString(args[offset]));
            } catch (IllegalArgumentException e) {
                throw new CommandException("&4&l > &cThe value &e" + args[offset] + "&c must be an UUID");
            }
        }

        return player;
    }

    @Override
    public List<String> getSuggestions(CommandSender source, String arg) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && ((PlayerParser) o).onlyOnline == onlyOnline;
    }

}
