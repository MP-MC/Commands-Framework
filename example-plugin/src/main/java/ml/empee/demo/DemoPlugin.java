package ml.empee.demo;

import ml.empee.commandsManager.CommandManager;
import ml.empee.demo.commands.DemoCommand;
import org.bukkit.plugin.java.JavaPlugin;
public class DemoPlugin extends JavaPlugin {

    private void registerCommands() {
        CommandManager commandManager = new CommandManager(this);
        commandManager.registerCommand(new DemoCommand());
    }

    @Override
    public void onEnable() {
        registerCommands();
    }

}
