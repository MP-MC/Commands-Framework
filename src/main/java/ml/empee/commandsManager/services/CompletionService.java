package ml.empee.commandsManager.services;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import me.lucko.commodore.Commodore;
import ml.empee.commandsManager.command.Command;
import ml.empee.commandsManager.command.CommandNode;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.types.*;
import ml.empee.commandsManager.parsers.types.greedy.MsgParser;
import org.bukkit.command.PluginCommand;

public final class CompletionService {

    private final Commodore commodore;

    public CompletionService(Commodore commodore) {
        this.commodore = commodore;
    }

    public void registerCompletions(Command command) {
        PluginCommand pluginCommand = command.getPluginCommand();

        pluginCommand.setTabCompleter(command);

        LiteralArgumentBuilder<Object> rootNode = convertNodeToBrigadier(command.getRootNode());
        //Registering completion for default commandNode "help"
        rootNode.then(LiteralArgumentBuilder.literal("help").then(RequiredArgumentBuilder.argument("page", IntegerArgumentType.integer(0))));

        commodore.register(pluginCommand, rootNode);
    }

    private LiteralArgumentBuilder<Object> convertNodeToBrigadier(CommandNode node) {

        LiteralArgumentBuilder<Object> rootNode = LiteralArgumentBuilder.literal(node.getLabel());
        ParameterParser<?>[] parsers = node.getParameterParsers();
        ArgumentBuilder<Object, ?> lastArg;

        if(parsers.length > 0) {
            lastArg = RequiredArgumentBuilder.argument(parsers[parsers.length-1].getLabel(), findArgType(parsers[parsers.length-1]));
        } else {
            lastArg = rootNode;
        }

        for(CommandNode child : node.getChildren()) {
            lastArg.then(convertNodeToBrigadier(child));
        }

        for(int i=parsers.length-2; i>=0; i--) {
            ArgumentBuilder<Object, ?> arg = RequiredArgumentBuilder.argument(parsers[i].getLabel(), findArgType(parsers[i]));
            arg.then(lastArg);
            lastArg = arg;
        }

        if(lastArg != rootNode) {
            rootNode.then(lastArg);
        }

        return rootNode;
    }
    private ArgumentType<?> findArgType(ParameterParser<?> rawType) {

        if(rawType instanceof MsgParser) {
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
