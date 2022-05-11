package tk.empee.demo.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.empee.commandManager.command.Command;
import tk.empee.commandManager.command.CommandContext;
import tk.empee.commandManager.command.annotations.CommandNode;
import tk.empee.commandManager.command.annotations.CommandRoot;
import tk.empee.commandManager.parsers.types.annotations.DoubleParam;
import tk.empee.commandManager.parsers.types.annotations.IntegerParam;
import tk.empee.commandManager.parsers.types.annotations.greedy.MsgParam;

@SuppressWarnings("unused")
public final class DemoCommand extends Command {

    @CommandRoot
    @CommandNode(
            label = "tk/empee/demo",
            childNodes = {"world", "teleport", "player"},
            executable = false
    )
    private void root(CommandContext c) { }

    @CommandNode(
            id = "world",
            label = "hello",
            description = "Greets the world",
            permission = "demo.hello"
    )
    private void greetsWorld(CommandContext c) {
        CommandSender sender = c.getSource(CommandSender.class);
        sender.sendMessage(" World! ");
    }

    @CommandNode(
            label = "teleport",
            description = "Teleports you to the given coordinates"
    )
    private void teleport(CommandContext c, double x, @DoubleParam(min = 0, max = 255) double y, double z) {
        Player sender = c.getSource(Player.class);
        sender.teleport(new Location(sender.getWorld(), x, y, z));
        sendMessage(sender, "&bWoosh!");
    }

    @CommandNode(
            label = "player",
            executable = false,
            permission = "demo.admin",
            childNodes = {"8ball", "echo"}
    )
    private void compound(
            CommandContext c,

            @IntegerParam(label = "magicV")
            int magicValue,

            ChatColor color
    ) {}

    @CommandNode(label = "8ball")
    private void ask8ball(CommandContext c) {
        CommandSender sender = c.getSource(CommandSender.class);

        int value = c.getArgument("magicV");
        if(value > 0) {
            sender.sendMessage("Yay, you are positive");
        } else {
            sender.sendMessage("Mh, you should be more positive");
        }

    }

    @CommandNode(label = "echo")
    private void makeEcho(CommandContext c, @MsgParam(defaultValue = "this is a default message") String message) {
        CommandSender sender = c.getSource(CommandSender.class);
        sendMessage(sender,message);
    }

}
