package tk.empee.commandManager.services;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.lucko.commodore.Commodore;
import org.bukkit.command.PluginCommand;
import tk.empee.commandManager.command.Command;
import tk.empee.commandManager.command.CommandNode;
import tk.empee.commandManager.command.parameters.parsers.*;
import tk.empee.commandManager.command.parameters.parsers.greedy.GreedyStringParser;

@SuppressWarnings("unchecked")
public final class CompletionService {

    private final Commodore commodore;

    public CompletionService(Commodore commodore) {
        this.commodore = commodore;
    }

    public void registerCompletions(Command command) {
        PluginCommand pluginCommand = command.getPluginCommand();

        pluginCommand.setTabCompleter(command);
        commodore.register(pluginCommand, convertNodeToBrigadier(command.getRootNode()));
    }

    private LiteralArgumentBuilder<?> convertNodeToBrigadier(CommandNode node) {

        LiteralArgumentBuilder<Object> rootNode = LiteralArgumentBuilder.literal(node.getLabel());
        ParameterParser<?>[] parameters = node.getParameters();
        ArgumentBuilder<Object, ?> lastArg;

        if(parameters.length > 0) {
            lastArg = RequiredArgumentBuilder.argument(parameters[parameters.length-1].getLabel(), findArgType(parameters[parameters.length-1]));
        } else {
            lastArg = rootNode;
        }

        for(CommandNode child : node.getChildren()) {
            lastArg.then((ArgumentBuilder<Object, ?>) convertNodeToBrigadier(child));
        }

        for(int i=parameters.length-2; i>=0; i--) {
            ArgumentBuilder<Object, ?> arg = RequiredArgumentBuilder.argument(parameters[i].getLabel(), findArgType(parameters[i]));
            arg.then(lastArg);
            lastArg = arg;
        }

        if(lastArg != rootNode) {
            rootNode.then(lastArg);
        }

        return rootNode;
    }
    private ArgumentType<?> findArgType(ParameterParser<?> rawType) {

        if(rawType instanceof GreedyStringParser) {
            return StringArgumentType.greedyString();
        } else if(rawType instanceof IntegerParser) {
            return IntegerArgumentType.integer(((IntegerParser) rawType).getMin(), ((IntegerParser) rawType).getMax());
        } else if(rawType instanceof FloatParser) {
            return FloatArgumentType.floatArg(((FloatParser) rawType).getMin(), ((FloatParser) rawType).getMax());
        } else if(rawType instanceof DoubleParser) {
            return DoubleArgumentType.doubleArg(((DoubleParser) rawType).getMin(), ((DoubleParser) rawType).getMax());
        } else if(rawType instanceof LongParser) {
            return LongArgumentType.longArg(((LongParser) rawType).getMin(), ((LongParser) rawType).getMax());
        } else if(rawType instanceof BoolParser) {
            return BoolArgumentType.bool();
        } else {
            return StringArgumentType.word();
        }

    }

}
