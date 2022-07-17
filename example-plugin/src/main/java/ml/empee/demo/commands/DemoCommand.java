package ml.empee.demo.commands;

import ml.empee.commandsManager.command.Command;
import ml.empee.commandsManager.command.CommandContext;
import ml.empee.commandsManager.command.annotations.CommandNode;
import ml.empee.commandsManager.command.annotations.CommandRoot;
import ml.empee.commandsManager.parsers.types.annotations.DoubleParam;
import ml.empee.commandsManager.parsers.types.annotations.IntegerParam;
import ml.empee.commandsManager.parsers.types.annotations.greedy.MsgParam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public final class DemoCommand extends Command {

    @CommandRoot
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
        sendMessage(sender,message);
    }

}
