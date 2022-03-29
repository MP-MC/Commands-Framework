package tk.empee.commandManager;

import lombok.Getter;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tk.empee.commandManager.command.Command;
import tk.empee.commandManager.command.parameters.ParameterManager;
import tk.empee.commandManager.command.parameters.parsers.*;
import tk.empee.commandManager.command.parameters.parsers.annotations.*;
import tk.empee.commandManager.command.parameters.parsers.annotations.greedy.GreedyStringParam;
import tk.empee.commandManager.command.parameters.parsers.greedy.GreedyStringParser;
import tk.empee.commandManager.helpers.CommandMap;
import tk.empee.commandManager.services.CompletionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public final class CommandManager implements Listener {

    private final ArrayList<Command> commands = new ArrayList<>();
    private final JavaPlugin plugin;
    private final Logger logger;

    private CompletionService completionService = null;
    @Getter private final ParameterManager parameterManager;


    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        this.parameterManager = new ParameterManager();

        registerDefaultParameters();
        setupCompletionService();
    }

    private void registerDefaultParameters() {
        parameterManager.registerParameter(IntegerParam.class, IntegerParser.class);
        parameterManager.registerParameter(FloatParam.class, FloatParser.class);
        parameterManager.registerParameter(DoubleParam.class, DoubleParser.class);
        parameterManager.registerParameter(LongParam.class, LongParser.class);
        parameterManager.registerParameter(BoolParam.class, BoolParser.class);
        parameterManager.registerParameter(PlayerParam.class, PlayerParser.class);
        parameterManager.registerParameter(StringParam.class, StringParser.class);
        parameterManager.registerParameter(GreedyStringParam.class, GreedyStringParser.class);
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
        PluginCommand pluginCommand = command.build(plugin, parameterManager);
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
