package ml.empee.commandsManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.NonNull;
import me.lucko.commodore.CommodoreProvider;
import ml.empee.commandsManager.command.Command;
import ml.empee.commandsManager.helpers.CommandMap;
import ml.empee.commandsManager.parsers.ParserManager;
import ml.empee.commandsManager.parsers.types.BoolParser;
import ml.empee.commandsManager.parsers.types.ColorParser;
import ml.empee.commandsManager.parsers.types.DoubleParser;
import ml.empee.commandsManager.parsers.types.FloatParser;
import ml.empee.commandsManager.parsers.types.IntegerParser;
import ml.empee.commandsManager.parsers.types.LongParser;
import ml.empee.commandsManager.parsers.types.MaterialParser;
import ml.empee.commandsManager.parsers.types.PlayerParser;
import ml.empee.commandsManager.parsers.types.StringParser;
import ml.empee.commandsManager.parsers.types.annotations.BoolParam;
import ml.empee.commandsManager.parsers.types.annotations.ColorParam;
import ml.empee.commandsManager.parsers.types.annotations.DoubleParam;
import ml.empee.commandsManager.parsers.types.annotations.FloatParam;
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
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

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
  @Getter
  private BukkitAudiences adventure;

  public CommandManager(@NonNull JavaPlugin plugin) {
    this.plugin = plugin;
    this.logger = plugin.getLogger();

    this.parserManager = new ParserManager();
    registerDefaultParsers();

    setupCompletionService();
  }

  public CommandManager(@NonNull JavaPlugin plugin, BukkitAudiences bukkitAudiences) {
    this(plugin);
    this.adventure = bukkitAudiences;
  }

  private void registerDefaultParsers() {
    parserManager.registerParser(IntegerParam.class, IntegerParser.class);
    parserManager.setDefaultParserForType(int.class, IntegerParser.DEFAULT);
    parserManager.setDefaultParserForType(Integer.class, IntegerParser.DEFAULT);

    parserManager.registerParser(FloatParam.class, FloatParser.class);
    parserManager.setDefaultParserForType(float.class, FloatParser.DEFAULT);
    parserManager.setDefaultParserForType(Float.class, FloatParser.DEFAULT);

    parserManager.registerParser(DoubleParam.class, DoubleParser.class);
    parserManager.setDefaultParserForType(double.class, DoubleParser.DEFAULT);
    parserManager.setDefaultParserForType(Double.class, DoubleParser.DEFAULT);

    parserManager.registerParser(LongParam.class, LongParser.class);
    parserManager.setDefaultParserForType(long.class, LongParser.DEFAULT);
    parserManager.setDefaultParserForType(Long.class, LongParser.DEFAULT);

    parserManager.registerParser(BoolParam.class, BoolParser.class);
    parserManager.setDefaultParserForType(boolean.class, BoolParser.DEFAULT);
    parserManager.setDefaultParserForType(Boolean.class, BoolParser.DEFAULT);

    parserManager.registerParser(PlayerParam.class, PlayerParser.class);
    parserManager.setDefaultParserForType(Player.class, PlayerParser.DEFAULT);
    parserManager.setDefaultParserForType(OfflinePlayer.class, new PlayerParser(
        "target", false, ""
    ));

    parserManager.registerParser(StringParam.class, StringParser.class);
    parserManager.setDefaultParserForType(String.class, StringParser.DEFAULT);

    parserManager.registerParser(MsgParam.class, MsgParser.class);

    parserManager.registerParser(ColorParam.class, ColorParser.class);
    parserManager.setDefaultParserForType(ChatColor.class, ColorParser.DEFAULT);

    parserManager.registerParser(MaterialParam.class, MaterialParser.class);
    parserManager.setDefaultParserForType(Material.class, MaterialParser.DEFAULT);
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
