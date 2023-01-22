package ml.empee.commandsManager;

import lombok.Getter;
import lombok.NonNull;
import ml.empee.commandsManager.command.CommandExecutor;
import ml.empee.commandsManager.parsers.ParserManager;
import ml.empee.commandsManager.parsers.types.*;
import ml.empee.commandsManager.parsers.types.annotations.*;
import ml.empee.commandsManager.parsers.types.annotations.greedy.MsgParam;
import ml.empee.commandsManager.parsers.types.greedy.MsgParser;
import ml.empee.commandsManager.services.CompletionService;
import ml.empee.commandsManager.utils.CommandMapUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides an entry point for accessing the framework
 */
public final class CommandManager {

  @Getter
  final JavaPlugin plugin;
  private final ArrayList<CommandExecutor> registeredCommands = new ArrayList<>();
  private final Logger logger;
  @Getter
  private final ParserManager parserManager;
  private CompletionService completionService;

  public CommandManager(@NonNull JavaPlugin plugin, Logger logger) {
    this.plugin = plugin;
    this.logger = logger;

    this.parserManager = new ParserManager();
    registerDefaultParsers();

    completionService = new CompletionService();
  }

  public CommandManager(@NonNull JavaPlugin plugin) {
    this(plugin, plugin.getLogger());
  }

  private void registerDefaultParsers() {
    parserManager.registerParser(
            IntegerParser.builder().label("number").min(-Integer.MAX_VALUE).max(Integer.MAX_VALUE)
                    .build(),
            IntegerParam.class, int.class, Integer.class
    );

    parserManager.registerParser(
            DoubleParser.builder().label("number").min(-Double.MAX_VALUE).max(Double.MAX_VALUE).build(),
            DoubleParam.class, double.class, Double.class
    );

    parserManager.registerParser(
            LongParser.builder().label("number").min(-Long.MAX_VALUE).max(Long.MAX_VALUE).build(),
            LongParam.class, long.class, Long.class
    );

    parserManager.registerParser(
            BoolParser.builder().label("bool").build(),
            BoolParam.class, boolean.class, Boolean.class
    );

    parserManager.registerParser(
            PlayerParser.builder().label("player").onlyOnline(true).build(),
            PlayerParam.class, Player.class
    );

    parserManager.registerParser(
            PlayerParser.builder().label("offlinePlayer").onlyOnline(false).build(),
            PlayerParam.class, OfflinePlayer.class
    );

    parserManager.registerParser(
            StringParser.builder().label("string").build(),
            StringParam.class, String.class
    );

    parserManager.registerParser(
            MsgParser.builder().label("message").build(), MsgParam.class
    );

    parserManager.registerParser(
            ColorParser.builder().label("color").build(),
            ColorParam.class, ChatColor.class
    );

    parserManager.registerParser(
            EnumParser.builder().label("values").build(), EnumParam.class
    );

    parserManager.registerParser(
            MaterialParser.builder().label("material").build(),
            MaterialParam.class, Material.class
    );
  }

  public void registerCommand(@NonNull CommandExecutor command) {
    PluginCommand pluginCommand = command.build(this);
    if(!CommandMapUtils.register(pluginCommand)) {
      logger.log(
              Level.WARNING,
              () -> "It already exists a command '" + pluginCommand.getName() +
                      "' Use /" + pluginCommand.getPlugin().getName().toLowerCase(Locale.ENGLISH) +
                      ":" + pluginCommand.getName() + " instead"
      );
    }

    registeredCommands.add(command);

    if(completionService != null) {
      completionService.registerCompletions(command);
    }
  }

  public void unregisterCommands() {
    for(CommandExecutor command : registeredCommands) {
      command.unregister();
    }
  }

}
