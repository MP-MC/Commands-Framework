package ml.empee.demo;

import ml.empee.commandsManager.CommandManager;
import ml.empee.commandsManager.parsers.ParserManager;
import ml.empee.demo.commands.DemoCommand;
import ml.empee.demo.commands.parsers.ColorParser;
import ml.empee.demo.commands.parsers.annotations.ColorParam;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
public class DemoPlugin extends JavaPlugin {

    private void registerParsers(CommandManager commandManager) {
        ParserManager parserManager = commandManager.getParserManager();

        parserManager.registerParser(ColorParam.class, ColorParser.class);
        parserManager.registerDefaultParser(ChatColor.class, ColorParser.DEFAULT);
    }

    private void registerCommands() {
        CommandManager commandManager = new CommandManager(this);

        registerParsers(commandManager);
        commandManager.registerCommand(new DemoCommand());
    }

    @Override
    public void onEnable() {
        registerCommands();
    }

}
