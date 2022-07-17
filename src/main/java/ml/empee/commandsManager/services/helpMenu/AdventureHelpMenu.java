package ml.empee.commandsManager.services.helpMenu;

import ml.empee.commandsManager.command.CommandNode;
import ml.empee.commandsManager.parsers.ParameterParser;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Comparator;

public final class AdventureHelpMenu implements HelpMenuGenerator {

    private final CommandNode command;
    private final int helpPages;

    private final BukkitAudiences audiences;
    private final TextComponent header;

    private final Component[] helpMenu;

    public AdventureHelpMenu(BukkitAudiences audiences, CommandNode command) {
        this.audiences = audiences;
        this.command = command;

        this.header = Component.newline()
                .append(Component.text("   Help Menu").color(NamedTextColor.YELLOW))
                .append(Component.text("    -    ").color(NamedTextColor.GRAY))
                .append(Component.text(command.getLabel()).color(NamedTextColor.GOLD))
                .append(Component.newline());

        ArrayList<TextComponent> menu = getHelpMenu();
        menu.sort(Comparator.comparing(TextComponent::content));

        this.helpMenu = menu.toArray(new Component[0]);
        this.helpPages = (int) Math.ceil(helpMenu.length / (float) HELP_PAGE_ROWS);
    }
    private ArrayList<TextComponent> getHelpMenu() {
        return getHelpMenu(new ArrayList<>(), command,
                Component.text("   ")
                        .append(Component.text("? ").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
                        .append(Component.text("/").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(command.getLabel() + " ").color(NamedTextColor.GRAY))
        );
    }
    private ArrayList<TextComponent> getHelpMenu(ArrayList<TextComponent> menu, CommandNode node, TextComponent parent) {
        for(CommandNode child : node.getChildren()) {

            TextComponent helpRow = parent.append(Component.text(child.getLabel() + " ").color(NamedTextColor.GRAY));
            for(ParameterParser<?> parameter : child.getParameterParsers()) {
                String label = parameter.getLabel();
                if(label.isEmpty()) {
                    label = parameter.getDescriptor().getFallbackLabel();
                }

                helpRow = helpRow.append(
                        Component.text("<" + label + "> ")
                                .color(NamedTextColor.RED)
                                .hoverEvent(HoverEvent.showText(parameter.getDescriptor().getDescription()))
                );
            }

            StringBuilder rawCommand = new StringBuilder();
            for(Component component : helpRow.children()) {
                if(component instanceof TextComponent) {
                    rawCommand.append(((TextComponent) component).content());
                }
            }

            helpRow = helpRow.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(child.getDescription())
                    ))
                    .clickEvent(ClickEvent.suggestCommand(rawCommand.substring(2, rawCommand.length()-1 )));

            if(child.isExecutable()) {
                menu.add(helpRow);
            }

            getHelpMenu(menu, child, helpRow);
        }

        return menu;
    }


    public void sendHelpMenu(CommandSender target, Integer page) {
        if(page >= helpPages || page < 0) {
            throw new CommandException(INVALID_PAGE_ERROR);
        }

        Audience audience;

        //If it is running paper use the target as the audience
        if(target instanceof Audience) {
            audience = (Audience) target;
        } else {
            audience = audiences.sender(target);
        }

        audience.sendMessage(header);

        for(int i = page* HELP_PAGE_ROWS; i<(page+1)* HELP_PAGE_ROWS && i<helpMenu.length; i++) {
            audience.sendMessage(helpMenu[i]);
        }

        Component footer = Component.newline().append(Component.text("   "));
        for(int i=0; i<helpPages; i++) {
            Component pageNumber = Component.text(i + " ");
            if(i == page) {
                pageNumber = pageNumber.color(NamedTextColor.RED);
            } else {
                pageNumber = pageNumber.color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.runCommand("/" + command.getLabel() + " help " + i));
            }

            footer = footer.append(pageNumber);
        }
        audience.sendMessage(footer.append(Component.newline()));
    }


}
