package ml.empee.commandsManager.parsers;

import org.bukkit.ChatColor;

import lombok.Getter;

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

  public DescriptionBuilder(String fallbackLabel, String rawDesc, String[] requirements) {
    this.fallbackLabel = fallbackLabel;

    StringBuilder description = new StringBuilder("\n&3" + rawDesc + "\n");

    if (requirements != null) {
      description.append("\n");
      if (requirements.length % 2 != 0) {
        throw new IllegalArgumentException("The requirements array must be even");
      }

      for (int i = 0; i < requirements.length; i += 2) {
        description.append("&e").append(requirements[i]).append("&d").append(requirements[i + 1]).append("\n");
      }
    }

    this.description = ChatColor.translateAlternateColorCodes('&', description.toString());
  }
}
