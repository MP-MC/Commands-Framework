package ml.empee.commandsManager.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ml.empee.commandsManager.command.annotations.CmdRoot;
import ml.empee.commandsManager.exceptions.CommandManagerException;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PluginCommandUtils {

  private static final Constructor<PluginCommand> pluginCommandConstructor;

  static {
    try {
      Class<PluginCommand> pluginCommandClazz = PluginCommand.class;
      pluginCommandConstructor = pluginCommandClazz.getDeclaredConstructor(String.class, Plugin.class);
      pluginCommandConstructor.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new CommandManagerException("Error while retrieving the PluginCommand constructor", e);
    }
  }

  public static PluginCommand buildFromCommandRoot(CmdRoot rootNode, Plugin plugin) {
    try {
      PluginCommand command = pluginCommandConstructor.newInstance(rootNode.label(), plugin);

      command.setAliases(Arrays.asList(rootNode.aliases()));
      command.setDescription(rootNode.description());
      command.setPermission(rootNode.permission());

      return command;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new CommandManagerException("Error while wrapping a command inside a PluginCommand", e);
    }
  }

}
