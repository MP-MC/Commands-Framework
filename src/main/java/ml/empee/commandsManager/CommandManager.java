package ml.empee.commandsManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NonNull;
import me.lucko.commodore.CommodoreProvider;
import ml.empee.commandsManager.command.Command;
import ml.empee.commandsManager.helpers.CommandMap;
import ml.empee.commandsManager.parsers.ParserManager;
import ml.empee.commandsManager.parsers.types.BoolParser;
import ml.empee.commandsManager.parsers.types.ColorParser;
import ml.empee.commandsManager.parsers.types.DoubleParser;
import ml.empee.commandsManager.parsers.types.EnumParser;
import ml.empee.commandsManager.parsers.types.IntegerParser;
import ml.empee.commandsManager.parsers.types.LongParser;
import ml.empee.commandsManager.parsers.types.MaterialParser;
import ml.empee.commandsManager.parsers.types.PlayerParser;
import ml.empee.commandsManager.parsers.types.StringParser;
import ml.empee.commandsManager.parsers.types.annotations.BoolParam;
import ml.empee.commandsManager.parsers.types.annotations.ColorParam;
import ml.empee.commandsManager.parsers.types.annotations.DoubleParam;
import ml.empee.commandsManager.parsers.types.annotations.EnumParam;
import ml.empee.commandsManager.parsers.types.annotations.IntegerParam;
import ml.empee.commandsManager.parsers.types.annotations.LongParam;
import ml.empee.commandsManager.parsers.types.annotations.MaterialParam;
import ml.empee.commandsManager.parsers.types.annotations.PlayerParam;
import ml.empee.commandsManager.parsers.types.annotations.StringParam;
import ml.empee.commandsManager.parsers.types.annotations.greedy.MsgParam;
import ml.empee.commandsManager.parsers.types.greedy.MsgParser;
import ml.empee.commandsManager.services.completion.CommodoreCompletionService;
import ml.empee.commandsManager.services.completion.CompletionService;
import ml.empee.commandsManager.services.completion.DefaultCompletionService;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class provides an entry point for accessing the framework
 */
public final class CommandManager {

  private final ArrayList<Command> registeredCommands = new ArrayList<>();
  private final Logger logger;
  private CompletionService completionService;

  @Getter
  final JavaPlugin plugin;
  @Getter
  private final ParserManager parserManager;

  public CommandManager(@NonNull JavaPlugin plugin, Logger logger) {
    this.plugin = plugin;
    this.logger = logger;

    this.parserManager = new ParserManager();
    registerDefaultParsers();

    setupCompletionService();
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

  private void setupCompletionService() {
    if (CommodoreProvider.isSupported()) {
      completionService = new CommodoreCompletionService(CommodoreProvider.getCommodore(plugin));
      logger.info("Hooked to brigadier NMS");
    } else {
      completionService = new DefaultCompletionService();
      logger.info("Brigadier NMS not found, using default completion service");
    }
  }

  public void registerCommand(@NonNull Command command) {
    PluginCommand pluginCommand = command.build(this);
    if (!CommandMap.register(pluginCommand)) {
      logger.log(Level.WARNING,
          () -> "It already exists a command '" + pluginCommand.getName() +
              "' Use /" + pluginCommand.getPlugin().getName().toLowerCase(Locale.ENGLISH) +
              ":" + pluginCommand.getName() + " instead"
      );
    }

    registeredCommands.add(command);

    if (completionService != null) {
      completionService.registerCompletions(command);
    }
  }

  public void unregisterCommands() {
    for (Command command : registeredCommands) {
      command.unregister();
    }
  }

}
