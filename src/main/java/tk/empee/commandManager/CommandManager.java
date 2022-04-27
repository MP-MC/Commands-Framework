package tk.empee.commandManager;

import lombok.Getter;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tk.empee.commandManager.command.Command;
import tk.empee.commandManager.command.parsers.ParserManager;
import tk.empee.commandManager.command.parsers.types.*;
import tk.empee.commandManager.command.parsers.types.annotations.*;
import tk.empee.commandManager.command.parsers.types.annotations.greedy.GreedyStringParam;
import tk.empee.commandManager.command.parsers.types.greedy.GreedyStringParser;
import tk.empee.commandManager.helpers.CommandMap;
import tk.empee.commandManager.services.CompletionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public final class CommandManager implements Listener {

    private final ArrayList<Command> commands = new ArrayList<>();
    private final Logger logger;
    private CompletionService completionService = null;

    @Getter final JavaPlugin plugin;
    @Getter private final ParserManager parserManager;
    @Getter private final BukkitAudiences adventure;

    public CommandManager(JavaPlugin plugin) {
        this(plugin, null);
    }

    public CommandManager(JavaPlugin plugin, BukkitAudiences adventure) {
        this.plugin = plugin;
        this.adventure = adventure;
        this.logger = plugin.getLogger();

        this.parserManager = new ParserManager();
        registerDefaultParsers();

        setupCompletionService();
    }

    private void registerDefaultParsers() {
        parserManager.registerParser(IntegerParam.class, IntegerParser.class);
        parserManager.registerParser(FloatParam.class, FloatParser.class);
        parserManager.registerParser(DoubleParam.class, DoubleParser.class);
        parserManager.registerParser(LongParam.class, LongParser.class);
        parserManager.registerParser(BoolParam.class, BoolParser.class);
        parserManager.registerParser(PlayerParam.class, PlayerParser.class);
        parserManager.registerParser(StringParam.class, StringParser.class);
        parserManager.registerParser(GreedyStringParam.class, GreedyStringParser.class);
    }
    private void setupCompletionService() {
        if(CommodoreProvider.isSupported()) {
            completionService = new CompletionService(CommodoreProvider.getCommodore(plugin));
            logger.info("Hooked to brigadier NMS");
        } else {
            logger.warning("Your server doesn't support the command completion");
        }
    }

    public void registerCommand(Command command) {
        PluginCommand pluginCommand = command.build(this);
        if(!CommandMap.register(pluginCommand)) {
            logger.warning("It already exists the command " + pluginCommand.getName() + ". Use /" + pluginCommand.getPlugin().getName().toLowerCase(Locale.ROOT) + ":" + pluginCommand.getName() + " instead");
        }

        commands.add(command);
        logger.info("The command '" + pluginCommand.getName() + "' has been registered");

        if(completionService != null) {
            completionService.registerCompletions(command);
            logger.info("Command completions for '" + pluginCommand.getName() + "' have been registered");
        }
    }
    public void clearCommands() {
        for(Command command : commands) {
            PluginCommand pluginCommand = command.getPluginCommand();

            CommandMap.unregisterCommand(pluginCommand);
            logger.info("The command '" + pluginCommand.getName() + "' has been unregistered");
        }
    }

    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

}
