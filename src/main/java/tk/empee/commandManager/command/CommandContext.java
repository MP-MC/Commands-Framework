package tk.empee.commandManager.command;

import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public final class CommandContext {

    private final HashMap<String, Object> arguments = new HashMap<>();
    private final CommandSender source;

    public CommandContext(CommandSender source) {
        this.source = source;
    }


    public void addArgument(String id, Object arg) {
        arguments.put(id, arg);
    }
    public <T> T getArgument(String id) {
        return (T) arguments.get(id);
    }


    public <T> T getSource(Class<? extends T> sourceClazz) {
        if(sourceClazz.isInstance(source)) {
            return (T) source;
        }
        throw new CommandException("&4&l > &cOnly a &e" + sourceClazz.getSimpleName() + "&c can use this command");
    }

}
