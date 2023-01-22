package ml.empee.commandsManager.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ml.empee.commandsManager.command.annotations.CommandNode;
import ml.empee.commandsManager.exceptions.CommandManagerException;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PluginCommandUtils {

  private static final Constructor<PluginCommand> pluginCommandConstructor;

  static {
    try {
      Class<PluginCommand> pluginCommandClazz = PluginCommand.class;
      pluginCommandConstructor = pluginCommandClazz.getDeclaredConstructor(String.class, Plugin.class);
      pluginCommandConstructor.setAccessible(true);
    } catch(NoSuchMethodException e) {
      throw new CommandManagerException("Error while retrieving the PluginCommand constructor", e);
    }
  }

  public static PluginCommand of(CommandNode cmdRoot) {
    return of(cmdRoot, JavaPlugin.getProvidingPlugin(PluginCommandUtils.class));
  }

  public static PluginCommand of(CommandNode cmdRoot, JavaPlugin plugin) {
    try {
      PluginCommand command = pluginCommandConstructor.newInstance(cmdRoot.label(), plugin);

      command.setAliases(Arrays.asList(cmdRoot.aliases()));
      command.setDescription(cmdRoot.description());
      command.setPermission(cmdRoot.permission());

      return command;
    } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new CommandManagerException("Error while wrapping a command inside a PluginCommand", e);
    }
  }

}
