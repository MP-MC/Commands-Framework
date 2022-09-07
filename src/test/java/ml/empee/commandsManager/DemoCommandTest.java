package ml.empee.commandsManager;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import ml.empee.commandsManager.command.Command;
import ml.empee.commandsManager.command.CommandContext;
import ml.empee.commandsManager.command.annotations.CommandNode;
import ml.empee.commandsManager.command.annotations.CommandRoot;
import ml.empee.commandsManager.parsers.types.annotations.ColorParam;
import ml.empee.commandsManager.parsers.types.annotations.DoubleParam;
import ml.empee.commandsManager.parsers.types.annotations.IntegerParam;
import ml.empee.commandsManager.parsers.types.annotations.greedy.MsgParam;
import net.md_5.bungee.api.ChatColor;

class DemoCommandTest {
    
    @Test
    void testCommandParsing() {
        JavaPlugin plugin = Mockito.mock(JavaPlugin.class);
        when(plugin.getLogger()).thenReturn(
            Logger.getLogger("MockedServer")
        );

        CommandManager commandManager = new CommandManager(plugin);
        DemoCommand demoCommand = new DemoCommand();
        PluginCommand builtCommand = demoCommand.build(commandManager);

        CommandSender sender = Mockito.mock(Player.class);
        when(sender.getName()).thenReturn("MockedPlayer");
        when(sender.hasPermission(Mockito.anyString())).thenReturn(true);

        Queue<String> queue = new LinkedList<>();
        doAnswer( (invocation) -> {
            return queue.add(invocation.getArguments()[0].toString());
        }).when(sender).sendMessage(Mockito.anyString());

        demoCommand.onCommand(sender, builtCommand, "demo", new String[] {"hello"});
        assert queue.poll().equals(" World! ");
    }

    @Test
    void testConcatenatedCommands() {
        JavaPlugin plugin = Mockito.mock(JavaPlugin.class);
        when(plugin.getLogger()).thenReturn(
            Logger.getLogger("MockedServer")
        );

        CommandManager commandManager = new CommandManager(plugin);
        DemoCommand demoCommand = new DemoCommand();
        PluginCommand builtCommand = demoCommand.build(commandManager);

        CommandSender sender = Mockito.mock(Player.class);
        when(sender.getName()).thenReturn("MockedPlayer");
        when(sender.hasPermission(Mockito.anyString())).thenReturn(true);

        Queue<String> queue = new LinkedList<>();
        doAnswer( (invocation) -> {
            return queue.add(invocation.getArguments()[0].toString());
        }).when(sender).sendMessage(Mockito.anyString());

        demoCommand.onCommand(sender, builtCommand, "demo", new String[] {"player", "10", "BLACK", "8ball"});
        queue.poll();
        assert queue.poll().equals("Yay, you are positive");

        demoCommand.onCommand(sender, builtCommand, "demo", new String[] {"player", "-10", "BLACK", "8ball"});
        queue.poll();
        assert queue.poll().equals("Mh, you should be more positive");
    }

    @Test
    void testDefaultArguments() {
        JavaPlugin plugin = Mockito.mock(JavaPlugin.class);
        when(plugin.getLogger()).thenReturn(
            Logger.getLogger("MockedServer")
        );

        CommandManager commandManager = new CommandManager(plugin);
        DemoCommand demoCommand = new DemoCommand();
        PluginCommand builtCommand = demoCommand.build(commandManager);

        CommandSender sender = Mockito.mock(Player.class);
        when(sender.getName()).thenReturn("MockedPlayer");
        when(sender.hasPermission(Mockito.anyString())).thenReturn(true);

        Queue<String> queue = new LinkedList<>();
        doAnswer( (invocation) -> {
            return queue.add(invocation.getArguments()[0].toString());
        }).when(sender).sendMessage(Mockito.anyString());

        demoCommand.onCommand(sender, builtCommand, "demo", new String[] {"player", "-10", "BLACK", "echo"});
        queue.poll();
        assert queue.poll().equals("§0this is a default message");

        demoCommand.onCommand(sender, builtCommand, "demo", new String[] {"player", "-10", "BLACK", "echo", "this is a custom message"});
        queue.poll();
        assert queue.poll().equals("§0this is a custom message");
    }

    @Test
    void testArgumentsConstraints() {
        JavaPlugin plugin = Mockito.mock(JavaPlugin.class);
        when(plugin.getLogger()).thenReturn(
            Logger.getLogger("MockedServer")
        );

        CommandManager commandManager = new CommandManager(plugin);
        DemoCommand demoCommand = new DemoCommand();
        PluginCommand builtCommand = demoCommand.build(commandManager);

        CommandSender sender = Mockito.mock(Player.class);
        when(sender.getName()).thenReturn("MockedPlayer");
        when(sender.hasPermission(Mockito.anyString())).thenReturn(true);

        Queue<String> queue = new LinkedList<>();
        doAnswer( (invocation) -> {
            return queue.add(invocation.getArguments()[0].toString());
        }).when(sender).sendMessage(Mockito.anyString());

        demoCommand.onCommand(sender, builtCommand, "demo", new String[] {"teleport", "10", "-10", "10"});
        assert queue.poll().equals("§4§l > §cThe value must be higher then §e0.0§c but it's value is §e-10.0");

        demoCommand.onCommand(sender, builtCommand, "demo", new String[] {"teleport", "10", "260", "10"});
        assert queue.poll().equals("§4§l > §cThe value must be lower then §e255.0§c but it's value is §e260.0");


    }

    @CommandRoot("demo")
    private final class DemoCommand extends Command {

        @CommandNode(
                label = "demo",
                executable = false
        )
        private void root(CommandContext c) { }
    
        @CommandNode(
                parent = "demo",
                label = "hello",
                description = "Greets the world",
                permission = "demo.hello"
        )
        private void greetsWorld(CommandContext c) {
            CommandSender sender = c.getSource(CommandSender.class);
            sender.sendMessage(" World! ");
        }
    
        @CommandNode(
                parent = "demo",
                label = "teleport",
                description = "Teleports you to the given coordinates"
        )
        private void teleport(CommandContext c, double x, @DoubleParam(min = 0, max = 255) double y, double z) {
            Player sender = c.getSource(Player.class);
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
                CommandContext c,
    
                @IntegerParam(label = "magicV")
                int magicValue,
    
                @ColorParam(label = "color")
                ChatColor color
        ) {
            sendMessage(c.getSource(Player.class), "&cCompound effect! :D");
        }
    
        @CommandNode(parent = "player", label = "8ball")
        private void ask8ball(CommandContext c) {
            CommandSender sender = c.getSource(CommandSender.class);
    
            int value = c.getArgument("magicV");
            if(value > 0) {
                sender.sendMessage("Yay, you are positive");
            } else {
                sender.sendMessage("Mh, you should be more positive");
            }
    
        }
    
        @CommandNode(parent="player", label = "echo")
        private void makeEcho(CommandContext c, @MsgParam(defaultValue = "this is a default message") String message) {
            CommandSender sender = c.getSource(CommandSender.class);
            sendMessage(sender, c.getArgument("color") + message);
        }

    }
}
