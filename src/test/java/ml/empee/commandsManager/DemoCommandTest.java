package ml.empee.commandsManager;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ml.empee.commandsManager.command.Command;
import ml.empee.commandsManager.command.annotations.CommandNode;
import ml.empee.commandsManager.command.annotations.CommandRoot;
import ml.empee.commandsManager.parsers.types.annotations.ColorParam;
import ml.empee.commandsManager.parsers.types.annotations.DoubleParam;
import ml.empee.commandsManager.parsers.types.annotations.IntegerParam;
import ml.empee.commandsManager.parsers.types.annotations.greedy.MsgParam;
import net.md_5.bungee.api.ChatColor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DemoCommandTest extends AbstractCommandTest {

  private DemoCommand demoCommand;
  private PluginCommand pluginCommand;

  @BeforeEach
  public void setUp() {
    super.setUp();

    demoCommand = new DemoCommand();
    pluginCommand = demoCommand.build(commandManager);
  }

  private void executeCommand(String... args) {
    demoCommand.onCommand(sender, pluginCommand, "demo", args);
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
        "§4§l > §cThe value must be higher then §e0.0§c but it's value is §e-10.0",
        senderReceivedMessage.poll()
    );

    executeCommand("teleport", "10", "260", "10" );
    assertEquals(
        "§4§l > §cThe value must be lower then §e255.0§c but it's value is §e260.0",
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

  @CommandRoot(label = "demo")
  private final class DemoCommand extends Command {

    @CommandNode(
        parent = "demo",
        label = "world label1"
    )
    private void spaceLabel(CommandSender sender) {
      sender.sendMessage("First space label");
    }

    @CommandNode(
        parent = "demo",
        label = "world label2"
    )
    private void spaceLabel2(CommandSender sender, String arg) {
      sender.sendMessage("Second space label, with arg: " + arg);
    }

    @CommandNode(
        parent = "demo",
        label = "hello",
        description = "Greets the world",
        permission = "demo.hello"
    )
    private void greetsWorld(CommandSender sender) {
      sender.sendMessage(" World! ");
    }

    @CommandNode(
        parent = "demo",
        label = "teleport",
        description = "Teleports you to the given coordinates"
    )
    private void teleport(Player sender, double x, @DoubleParam(min = 0, max = 255) double y, double z) {
      sender.teleport(new Location(sender.getWorld(), x, y, z));
      sendMessage(sender, "&bWoosh!");
    }

    @CommandNode(
        parent = "demo",
        label = "player",
        executable = false,
        permission = "demo.admin"
    )
    private void compound(
        Player sender,

        @IntegerParam(label = "magicV")
        int magicValue,

        @ColorParam(label = "color")
        ChatColor color
    ) {
      sendMessage(sender, "&cCompound effect! :D");
    }

    @CommandNode(parent = "player", label = "8ball")
    private void ask8ball(CommandSender sender) {

      int value = getContext(sender).getArgument("magicV");
      if (value > 0) {
        sender.sendMessage("Yay, you are positive");
      } else {
        sender.sendMessage("Mh, you should be more positive");
      }

    }

    @CommandNode(parent = "player", label = "echo")
    private void makeEcho(CommandSender sender, @MsgParam(defaultValue = "this is a default message") String message) {
      sendMessage(sender, getContext(sender).getArgument("color") + message);
    }

    private void sendMessage(CommandSender sender, String message) {
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

  }
}
