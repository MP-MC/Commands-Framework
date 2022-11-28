package ml.empee.commandsManager.parsers.types;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;

@Getter
@EqualsAndHashCode(callSuper = true)
public class PlayerParser extends ParameterParser<OfflinePlayer> {

  public static final PlayerParser DEFAULT = new PlayerParser("target", true, "");

  private final boolean onlyOnline;

  public PlayerParser(String label, Boolean onlyOnline, String defaultValue) {
    super(label, defaultValue);

    this.onlyOnline = onlyOnline;
    descriptionBuilder = new DescriptionBuilder("player", "This parameter can only contain a player's name or his UUID",
        new String[] {
            "Requires online: ", onlyOnline.toString(),
            "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
  }

  protected PlayerParser(PlayerParser parser) {
    super(parser);

    this.onlyOnline = parser.onlyOnline;
  }

  @Override
  public OfflinePlayer parse(int offset, String... args) {

    OfflinePlayer player = Bukkit.getPlayer(args[offset]);
    if (player == null) {
      if (onlyOnline) {
        throw new CommandException("The player &e" + args[offset] + "&c isn't online");
      }

      try {
        player = Bukkit.getOfflinePlayer(UUID.fromString(args[offset]));
      } catch (IllegalArgumentException e) {
        throw new CommandException("The value &e" + args[offset] + "&c must be an UUID");
      }
    }

    return player;
  }

  @Override
  public List<String> getSuggestions(CommandSender source, String arg) {
    List<String> suggestions;

    if (source instanceof Player) {
      suggestions = new ArrayList<>();
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (((Player) source).canSee(player)) {
          suggestions.add(player.getName());
        }
      }
    } else {
      suggestions = Bukkit.getOnlinePlayers().stream()
          .map(Player::getName)
          .collect(Collectors.toList());
    }

    return suggestions;
  }

  @Override
  public ParameterParser<OfflinePlayer> copyParser() {
    return new PlayerParser(this);
  }

}
