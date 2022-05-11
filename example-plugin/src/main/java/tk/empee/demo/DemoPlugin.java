package tk.empee.demo;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import tk.empee.commandManager.CommandManager;
import tk.empee.commandManager.parsers.ParserManager;
import tk.empee.demo.commands.DemoCommand;
import tk.empee.demo.commands.parsers.ColorParser;
import tk.empee.demo.commands.parsers.annotations.ColorParam;

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
