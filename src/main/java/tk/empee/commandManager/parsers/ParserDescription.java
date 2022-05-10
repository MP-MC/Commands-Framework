package tk.empee.commandManager.parsers;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * The description of a parser <br><br>
 *
 * The {@link #fallbackLabel} is used when the parser label is empty <br>
 * The {@link #description} is shown when a user goes hover a parser identifier
 */
public final class ParserDescription {

    @Getter
    private final String fallbackLabel;
    @Getter
    private Component description;

    public ParserDescription(String fallbackLabel, String rawDesc, String[] requirements) {
        this.fallbackLabel = fallbackLabel;

        description = Component.newline()
                .append(Component.text(rawDesc).color(NamedTextColor.DARK_AQUA))
                .append(Component.newline());

        if(requirements != null) {
            description = description.append(Component.newline());
            if(requirements.length%2 != 0) {
                throw new IllegalArgumentException("The requirements array must be even");
            }

            for(int i=0; i<requirements.length; i+=2) {
                description = description
                        .append(Component.text(requirements[i]).color(NamedTextColor.YELLOW))
                        .append(Component.text(requirements[i+1]).color(NamedTextColor.LIGHT_PURPLE))
                        .append(Component.newline());
            }
        }

    }
}
