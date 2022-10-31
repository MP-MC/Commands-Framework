package ml.empee.commandsManager.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ml.empee.commandsManager.exceptions.CommandManagerException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandMap {

  private static final SimpleCommandMap internalCommandMap;
  private static final Field commandMapField;

  static {
    Server server = Bukkit.getServer();

    try {
      internalCommandMap = (SimpleCommandMap) server.getClass().getMethod("getCommandMap").invoke(server);

      commandMapField = SimpleCommandMap.class.getDeclaredField("knownCommands");
      commandMapField.setAccessible(true);
    } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      throw new CommandManagerException("Unable to retrieve the commandMap", e);
    }
  }

  public static boolean register(PluginCommand command) {
    command.register(internalCommandMap);
    return internalCommandMap.register(command.getPlugin().getName().toLowerCase(Locale.ENGLISH).trim(), command);
  }

  @SuppressWarnings("unchecked")
  public static void unregisterCommand(Command command) {
    try {
      Map<String, Command> map = (Map<String, Command>) commandMapField.get(internalCommandMap);
      map.remove(command.getName().toLowerCase(Locale.ENGLISH).trim());

      for(String alias : command.getAliases()) {
        map.remove(alias.toLowerCase(Locale.ENGLISH).trim());
      }
    } catch (IllegalAccessException e) {
      throw new CommandManagerException("Error while unregistering the command '" + command.getName() + "'", e);
    }
  }

}
