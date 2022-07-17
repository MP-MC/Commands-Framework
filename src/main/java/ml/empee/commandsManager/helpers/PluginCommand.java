package ml.empee.commandsManager.helpers;

import ml.empee.commandsManager.command.annotations.CommandNode;
import ml.empee.commandsManager.command.annotations.CommandRoot;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public final class PluginCommand {

    private static final Constructor<org.bukkit.command.PluginCommand> pluginCommandConstructor;

    static {
        try {
            Class<org.bukkit.command.PluginCommand> pluginCommandClazz = org.bukkit.command.PluginCommand.class;
            pluginCommandConstructor = pluginCommandClazz.getDeclaredConstructor(String.class, Plugin.class);
            pluginCommandConstructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Error while retrieving the PluginCommand constructor", e);
        }
    }

    public static org.bukkit.command.PluginCommand buildFromCommandRoot(CommandRoot rootInfo, CommandNode nodeInfo, Plugin plugin) {
        try {
            org.bukkit.command.PluginCommand command = pluginCommandConstructor.newInstance(nodeInfo.label(), plugin);

            command.setAliases(Arrays.asList(rootInfo.aliases()));
            command.setDescription(nodeInfo.description());
            command.setPermission(nodeInfo.permission());

            return command;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error while wrapping a command inside a PluginCommand", e);
        }
    }

}
