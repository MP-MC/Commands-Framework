package ml.empee.commandsManager.helpers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.bukkit.plugin.Plugin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ml.empee.commandsManager.command.annotations.CommandRoot;
import ml.empee.commandsManager.exceptions.CommandManagerException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PluginCommand {

  private static final Constructor<org.bukkit.command.PluginCommand> pluginCommandConstructor;

  static {
    try {
      Class<org.bukkit.command.PluginCommand> pluginCommandClazz = org.bukkit.command.PluginCommand.class;
      pluginCommandConstructor = pluginCommandClazz.getDeclaredConstructor(String.class, Plugin.class);
      pluginCommandConstructor.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new CommandManagerException("Error while retrieving the PluginCommand constructor", e);
    }
  }

  public static org.bukkit.command.PluginCommand buildFromCommandRoot(CommandRoot rootNode, Plugin plugin) {
    try {
      org.bukkit.command.PluginCommand command = pluginCommandConstructor.newInstance(rootNode.label(), plugin);

      command.setAliases(Arrays.asList(rootNode.aliases()));
      command.setDescription(rootNode.description());
      command.setPermission(rootNode.permission());

      return command;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new CommandManagerException("Error while wrapping a command inside a PluginCommand", e);
    }
  }

}
