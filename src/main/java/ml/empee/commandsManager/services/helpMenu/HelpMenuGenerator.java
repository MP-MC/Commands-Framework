package ml.empee.commandsManager.services.helpMenu;

import org.bukkit.command.CommandSender;

public interface HelpMenuGenerator {
  String INVALID_PAGE_ERROR = "The page number is invalid";
  int HELP_PAGE_ROWS = 5;

  void sendHelpMenu(CommandSender target, Integer page);

}
