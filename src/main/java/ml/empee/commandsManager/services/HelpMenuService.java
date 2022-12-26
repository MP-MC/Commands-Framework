package ml.empee.commandsManager.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ml.empee.commandsManager.command.Node;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpMenuService {

  public static final String INVALID_PAGE_ERROR = "The page number is invalid";
  public static final int HELP_PAGE_ROWS = 5;

  private final int totalPages;
  private final BaseComponent[] header;
  private final String legacyHeader;
  private final BaseComponent[] body;
  private final String[] legacyBody;

  private final String footerTemplate;


  private static BaseComponent[] fromLegacy(String legacy) {
    return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', legacy));
  }

  private static String toLegacy(BaseComponent... components) {
    return ChatColor.stripColor(BaseComponent.toLegacyText(components));
  }

  public HelpMenuService(String title, Node root) {
    header = fromLegacy(" &eInteractive Menu  &7-  &6" + title + "\n");
    legacyHeader = toLegacy(header);

    body = buildNodeEntries(root);
    totalPages = (int) Math.ceil((double) body.length / HELP_PAGE_ROWS);

    legacyBody = new String[body.length];
    for (int i = 0; i < body.length; i++) {
      legacyBody[i] = toLegacy(body[i]);
    }

    footerTemplate = ChatColor.translateAlternateColorCodes('&', "\n &7Page &e%page_number% &7of &e" + totalPages);
  }

  private BaseComponent[] buildNodeEntries(Node root) {
    ArrayList<BaseComponent> entries = new ArrayList<>();

    TextComponent baseEntry = new TextComponent(" /");
    baseEntry.setColor(ChatColor.DARK_GRAY);

    buildNodeEntries(entries, baseEntry, root);

    entries.sort(Comparator.comparing(a -> a.toPlainText()));

    return entries.toArray(new BaseComponent[0]);
  }
  private void buildNodeEntries(List<BaseComponent> entries, BaseComponent entry, Node node) {

    TextComponent nodeLabel = new TextComponent(node.getData().label() + " ");
    nodeLabel.setColor(ChatColor.GRAY);
    entry.addExtra(nodeLabel);

    addParameters(entry, node);
    Node[] children = node.getChildren();
    if(children.length == 0) {
      if(!node.getData().exitNode()) {
        return;
      }

      entry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fromLegacy(node.getDescription())));
      entry.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(entry.toPlainText().trim())));
      entries.add(entry);
    } else {
      for(Node child : children) {
        buildNodeEntries(entries, entry.duplicate(), child);
      }
    }

  }

  private void addParameters(BaseComponent entry, Node node) {
    for (ParameterParser<?> parameterParser : node.getParameterParsers()) {
      String parameterLabel = parameterParser.getLabel();
      DescriptionBuilder descriptionBuilder = parameterParser.getDescriptionBuilder();

      if (parameterLabel.isEmpty()) {
        parameterLabel = descriptionBuilder.getFallbackLabel();
      }

      TextComponent parameterLabelComponent = new TextComponent("<" + parameterLabel + "> ");
      parameterLabelComponent.setColor(ChatColor.RED);
      parameterLabelComponent.setHoverEvent(
          new HoverEvent(
              HoverEvent.Action.SHOW_TEXT,
              fromLegacy(descriptionBuilder.getDescription())
          )
      );

      entry.addExtra(parameterLabelComponent);
    }
  }

  public void sendHelpMenu(CommandSender target, Integer page) {
    if(page < 1 || page > totalPages) {
      throw new CommandException(INVALID_PAGE_ERROR);
    }

    if (target instanceof Player) {
      Player player = (Player) target;
      player.spigot().sendMessage(header);

      for(int i = (page - 1) * HELP_PAGE_ROWS; i < page * HELP_PAGE_ROWS && i < body.length; i++) {
        player.spigot().sendMessage(body[i]);
      }

      player.spigot().sendMessage(fromLegacy(footerTemplate.replace("%page_number%", page.toString())));
    } else {
      target.sendMessage(legacyHeader);

      for(String entry : legacyBody) {
        target.sendMessage(entry);
      }
    }
  }
}
