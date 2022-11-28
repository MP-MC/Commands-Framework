package ml.empee.commandsManager.services.generators;

import org.bukkit.command.CommandSender;

public interface HelpMenu {
  String INVALID_PAGE_ERROR = "The page number is invalid";
  int HELP_PAGE_ROWS = 5;

  void sendHelpMenu(CommandSender target, Integer page);

}
