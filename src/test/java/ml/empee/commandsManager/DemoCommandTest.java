package ml.empee.commandsManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;
import ml.empee.commandsManager.command.CommandExecutor;
import ml.empee.commandsManager.command.CommandNode;
import ml.empee.commandsManager.command.Node;
import ml.empee.commandsManager.parsers.types.annotations.ColorParam;
import ml.empee.commandsManager.parsers.types.annotations.DoubleParam;
import ml.empee.commandsManager.parsers.types.annotations.IntegerParam;
import ml.empee.commandsManager.parsers.types.annotations.greedy.MsgParam;
import ml.empee.commandsManager.services.HelpMenuService;
import ml.empee.commandsManager.utils.PluginCommandUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DemoCommandTest extends AbstractCommandTest {

  private DemoCommand demoCommand;
  private PluginCommand pluginCommand;

  @BeforeEach
  public void setUp() {
    super.setUp();

    demoCommand = new DemoCommand();
    pluginCommand = demoCommand.build(commandManager);
  }

  private void executeCommand(CommandSender sender, String... args) {
    demoCommand.onCommand(sender, pluginCommand, "demo", args);
  }

  private void executeCommand(String... args) {
    executeCommand(sender, args);
  }

  @Test
  void testCommandParsing() {
    executeCommand("hello");
    assertEquals(" World! ", senderReceivedMessage.poll());
  }

  @Test
  void testConcatenatedCommands() {
    executeCommand("player", "10", "BLACK", "8ball");
    senderReceivedMessage.poll();
    assertEquals("Yay, you are positive", senderReceivedMessage.poll());

    executeCommand("player", "-10", "BLACK", "8ball");
    senderReceivedMessage.poll();
    assertEquals("Mh, you should be more positive", senderReceivedMessage.poll());
  }

  @Test
  void testDefaultArguments() {
    executeCommand("player", "-10", "BLACK", "echo");
    senderReceivedMessage.poll();
    assertEquals("§0this is a default message", senderReceivedMessage.poll());

    executeCommand("player", "-10", "BLACK", "echo", "this is a custom message");
    senderReceivedMessage.poll();
    assertEquals("§0this is a custom message", senderReceivedMessage.poll());
  }

  @Test
  void testArgumentsConstraints() {
    executeCommand("teleport", "10", "-10", "10");
    assertEquals(
        "§4§l > §c§e-10.0§c must be equal or greater then §e0.0",
        senderReceivedMessage.poll()
    );

    executeCommand("teleport", "10", "260", "10");
    assertEquals(
        "§4§l > §c§e260.0§c must be equal or lower then §e0.0",
        senderReceivedMessage.poll()
    );

  }

  @Test
  void testLabelWithSpaces() {
    executeCommand("world", "label1");
    assertEquals("First space label", senderReceivedMessage.poll());

    executeCommand("world", "label2", "test");
    assertEquals("Second space label, with arg: test", senderReceivedMessage.poll());
  }

  @Test
  void testHelpMenu() {
    executeCommand(consoleSender, "help", "1");
  }

  public static class TestCommand extends CommandExecutor {
    @Override
    public PluginCommand build(CommandManager commandManager) {
      logger = Logger.getLogger("TestCommand");
      rootNode = Node.buildCommandTree(commandManager, this);
      //No need to check existence of the annotation, it's already done in the CommandNode
      pluginCommand = PluginCommandUtils.of(getClass().getAnnotation(CommandNode.class), null);
      pluginCommand.setExecutor(this);
      helpMenu = new HelpMenuService("TestMenu", rootNode);
      return pluginCommand;
    }
  }

  @CommandNode(label = "demo", description = "Demo command")
  public final class DemoCommand extends TestCommand {

    @CommandNode(label = "demo")
    public void onCommand(CommandSender sender) {

    }

    @CommandNode(
        parent = "demo",
        label = "help"
    )
    public void help(CommandSender sender, @IntegerParam(min = 1, defaultValue = "1") Integer page) {
      getHelpMenu().sendHelpMenu(sender, page);
    }

    @CommandNode(
        parent = "demo",
        label = "world label1"
    )
    public void spaceLabel(CommandSender sender) {
      sender.sendMessage("First space label");
    }

    @CommandNode(
        parent = "demo",
        label = "world label2"
    )
    public void spaceLabel2(CommandSender sender, String arg) {
      sender.sendMessage("Second space label, with arg: " + arg);
    }

    @CommandNode(
        parent = "demo",
        label = "hello",
        description = "Greets the world",
        permission = "demo.hello"
    )
    public void greetsWorld(CommandSender sender) {
      sender.sendMessage(" World! ");
    }

    @CommandNode(
        parent = "demo",
        label = "teleport",
        description = "Teleports you to the given coordinates"
    )
    public void teleport(Player sender, double x, @DoubleParam(min = 0, max = 255) double y, double z) {
      sender.teleport(new Location(sender.getWorld(), x, y, z));
      sendMessage(sender, "&bWoosh!");
    }

    @CommandNode(
        parent = "demo",
        label = "player",
        exitNode = false,
        permission = "demo.admin"
    )
    public void compound(
        Player sender,

        @IntegerParam(label = "magicV")
        int magicValue,

        @ColorParam(label = "color")
        ChatColor color
    ) {
      sendMessage(sender, "&cCompound effect! :D");
    }

    @CommandNode(parent = "player", label = "8ball")
    public void ask8ball(CommandSender sender) {

      int value = getContext(sender).getArgument("magicV");
      if (value > 0) {
        sender.sendMessage("Yay, you are positive");
      } else {
        sender.sendMessage("Mh, you should be more positive");
      }

    }

    @CommandNode(parent = "player", label = "echo")
    public void makeEcho(CommandSender sender, @MsgParam(defaultValue = "this is a default message") String message) {
      sendMessage(sender, getContext(sender).getArgument("color") + message);
    }

    private void sendMessage(CommandSender sender, String message) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

  }
}
