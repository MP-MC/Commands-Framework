package ml.empee.commandsManager.parsers;

import lombok.Getter;
import ml.empee.commandsManager.utils.Tuple;
import org.bukkit.ChatColor;

/**
 * The description of a parser <br><br>
 * <p>
 * The {@link #fallbackLabel} is used when the parser label is empty <br>
 * The {@link #description} is shown when a user goes hover a parser identifier
 */
public class DescriptionBuilder {
  @Getter
  protected String fallbackLabel;
  @Getter
  protected String description;

  @SafeVarargs
  public DescriptionBuilder(String fallbackLabel, String rawDesc, Tuple<String, String>... requirements) {
    this.fallbackLabel = fallbackLabel;

    StringBuilder description = new StringBuilder("\n&3" + rawDesc + "\n");

    if (requirements != null) {
      description.append("\n");

      for (Tuple<String, String> requirement : requirements) {
        description.append("&e").append(requirement.getFirst()).append("&d")
            .append(requirement.getSecond()).append("\n");
      }
    }

    this.description = ChatColor.translateAlternateColorCodes('&', description.toString());
  }
}
