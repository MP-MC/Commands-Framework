package ml.empee.commandsManager.parsers.types;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter @SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlayerParser extends ParameterParser<OfflinePlayer> {

  @Setter
  private boolean onlyOnline;

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("player",
        "This parameter can only contain a player's name or his UUID",
        Tuple.of("Requires online: ", onlyOnline ? "yes" : "no"),
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().getName()))
    );
  }

  @Override
  public OfflinePlayer parse(int offset, String... args) {

    OfflinePlayer player = Bukkit.getPlayer(args[offset]);
    if (player == null) {
      if (onlyOnline) {
        throw new CommandException("The player &e" + args[offset] + "&r isn't online");
      }

      try {
        player = Bukkit.getOfflinePlayer(UUID.fromString(args[offset]));
      } catch (IllegalArgumentException e) {
        throw new CommandException("The value &e" + args[offset] + "&r must be an UUID");
      }
    }

    return player;
  }

  @Override
  public List<String> buildSuggestions(CommandSender source, String arg) {
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
    PlayerParser parser = new PlayerParser();
    parser.label = label;
    parser.defaultValue = defaultValue;
    parser.onlyOnline = onlyOnline;
    return parser;
  }
}
