package ml.empee.commandsManager.parsers.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.Tuple;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ColorParser extends ParameterParser<ChatColor> {
  private static final List<String> COLORS;

  static {
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

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder(
        "color", "This parameter can only contain a valid color",
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().getName()))
    );
  }

  @Override
  public ChatColor parse(int offset, String... strings) {
    try {
      strings[offset] = strings[offset].toUpperCase(Locale.ENGLISH);
      if (!COLORS.contains(strings[offset])) {
        if (strings[offset].length() == 6) {
          strings[offset] = "#" + strings[offset];
        } else {
          throw new CommandException("The color &e" + strings[offset] + "&r isn't valid");
        }
      }

      return ChatColor.valueOf(strings[offset]);
    } catch (IllegalArgumentException e) {
      throw new CommandException("The color &e" + strings[offset] + "&r isn't valid");
    }
  }

  @Override
  public List<String> buildSuggestions(CommandSender source, String arg) {
    return COLORS;
  }

  @Override
  public ParameterParser<ChatColor> copyParser() {
    ColorParser parser = new ColorParser();
    parser.label = label;
    parser.defaultValue = defaultValue;
    return parser;
  }

}
