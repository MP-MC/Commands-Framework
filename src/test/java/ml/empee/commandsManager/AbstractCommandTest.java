package ml.empee.commandsManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public abstract class AbstractCommandTest {

  protected Logger log = Logger.getLogger("MockedServer");
  protected JavaPlugin plugin = Mockito.mock(JavaPlugin.class);
  protected CommandManager commandManager;
  protected Queue<String> senderReceivedMessage = new LinkedList<>();
  protected CommandSender sender = Mockito.mock(Player.class);
  protected CommandSender consoleSender = Mockito.mock(CommandSender.class);

  @BeforeEach
  public void setUp() {
    senderReceivedMessage.clear();
    commandManager = new CommandManager(plugin, log);

    sender = Mockito.mock(Player.class);
    when(sender.getName()).thenReturn("MockedPlayer");
    when(sender.hasPermission(Mockito.anyString())).thenReturn(true);
    when(consoleSender.hasPermission(Mockito.anyString())).thenReturn(true);

    doAnswer((invocation) -> {
      log.info(invocation.getArguments()[0].toString());
      return senderReceivedMessage.add(invocation.getArguments()[0].toString());
    }).when(sender).sendMessage(Mockito.anyString());

    doAnswer((invocation) -> {
      log.info(invocation.getArguments()[0].toString());
      return senderReceivedMessage.add(invocation.getArguments()[0].toString());
    }).when(consoleSender).sendMessage(Mockito.anyString());
  }

}
