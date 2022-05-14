package tk.empee.commandsFramework.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;

public final class CommandMap {

    private final static SimpleCommandMap commandMap;
    private static final Field commandMapField;

    static {
        Server server = Bukkit.getServer();

        try {
            commandMap = (SimpleCommandMap) server.getClass().getMethod("getCommandMap").invoke(server);

            commandMapField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            commandMapField.setAccessible(true);
        } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to retrieve the commandMap", e);
        }
    }

    public static boolean register(PluginCommand command) {
        command.register(commandMap);
        return commandMap.register(command.getPlugin().getName().toLowerCase().trim(), command);
    }

    public static void unregisterCommand(Command command) {
        try {
            Map<String, Command> map = (Map<String, Command>) commandMapField.get(commandMap);
            map.remove(command.getName().toLowerCase(Locale.ENGLISH).trim());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while unregistering the command '" + command.getName() + "'", e);
        }
    }

}
