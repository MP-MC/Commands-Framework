package ml.empee.commandsManager.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

import org.bukkit.ChatColor;

import lombok.AccessLevel;
import lombok.Getter;
import ml.empee.commandsManager.command.annotations.CommandRoot;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserManager;
import ml.empee.commandsManager.parsers.types.greedy.GreedyParser;

@Getter
public final class CommandNode {

  private final String id;
  private final String label;

  private final String permission;
  private final String description;
  private final ParameterParser<?>[] parameterParsers;
  private final CommandNode[] children;
  private final boolean executable;

  @Getter(AccessLevel.NONE)
  final Method executor;

  CommandNode(Method executor, Class<? extends Command> target, ParserManager parserManager) {
    this(null, executor, target, parserManager);
  }

  CommandNode(CommandRoot commandRoot, Class<? extends Command> target, ParserManager parserManager) {
    this.executor = null;
    this.label = commandRoot.label();
    this.id = commandRoot.label();
    this.permission = commandRoot.permission();
    this.description = commandRoot.description();
    this.executable = false;
    this.parameterParsers = new ParameterParser<?>[0];

    this.children = getChildren(target, parserManager);
    checkForLastParameterValidity();
    checkForChildrenConflicts();
  }

  CommandNode(CommandNode parent, Method executor, Class<? extends Command> target, ParserManager parserManager) {
    this.executor = executor;

    ml.empee.commandsManager.command.annotations.CommandNode annotation = executor.getAnnotation(
        ml.empee.commandsManager.command.annotations.CommandNode.class);
    Objects.requireNonNull(annotation, "Can't find the commandNode annotation of " + executor.getName());

    this.label = annotation.label();
    if (parent == null) {
      this.id = label;
    } else {
      this.id = parent.id + "." + label;
    }

    this.permission = annotation.permission();
    this.description = buildDescription(annotation.description());
    this.executable = annotation.executable();
    this.parameterParsers = getParameterParsers(parserManager);

    this.children = getChildren(target, parserManager);
    checkForLastParameterValidity();
    checkForChildrenConflicts();
  }

  private void checkForLastParameterValidity() {
    if (children.length > 0 && parameterParsers.length > 0) {
      ParameterParser<?> lastParser = parameterParsers[parameterParsers.length - 1];

      if (lastParser instanceof GreedyParser) {
        throw new IllegalArgumentException(
            "You can't have children inside the node " + label + ", his last parameter is a greedy one!");
      } else if (lastParser.isOptional()) {
        throw new IllegalArgumentException(
            "You can't have a children after a optional argument inside the node " + label);
      }
    }
  }

  private void checkForChildrenConflicts() {

    for (CommandNode child : children) {
      String childLabel = child.getLabel().split(" ")[0];
      for (CommandNode comparison : children) {

        if (
            child != comparison
            &&
            ( childLabel.equals(comparison.getLabel()) || child.getLabel().equals(comparison.getLabel()) )
        ) {
          throw new IllegalArgumentException(
              "You can't have two children with the same label inside the node " + this.label);
        }

      }
    }
  }

  private String buildDescription(String rawDescription) {
    StringBuilder result = new StringBuilder("\n");
    if (!rawDescription.isEmpty()) {
      result.append(ChatColor.DARK_AQUA).append(rawDescription).append("\n\n");
    }

    result.append(ChatColor.YELLOW).append("Permission: ").append(ChatColor.LIGHT_PURPLE)
        .append(permission.isEmpty() ? "none" : permission)
        .append("\n");

    return result.toString();
  }

  private ParameterParser<?>[] getParameterParsers(ParserManager parserManager) {
    java.lang.reflect.Parameter[] rawParameters = executor.getParameters();
    if (rawParameters.length == 0 || rawParameters[0].getType() != CommandContext.class) {
      throw new IllegalArgumentException("Missing command context parameter from " + label);
    }

    ParameterParser<?>[] parsers = new ParameterParser<?>[rawParameters.length - 1];

    for (int i = 1; i < rawParameters.length; i++) {
      ParameterParser<?> type = parserManager.getParameterParser(rawParameters[i]);
      Objects.requireNonNull(type,
          "Can't find a parser for the parameter type " + rawParameters[i].getType().getName());

      if (i != 1 && parsers[i - 2] instanceof GreedyParser) {
        throw new IllegalArgumentException(
            "You can't have a parameter after a greedy parameter inside the node " + label);
      }

      if (i != 1 && !type.isOptional() && parsers[i - 2].isOptional()) {
        throw new IllegalArgumentException(
            "You can't have a required argument after a optional one inside the node " + label);
      }

      parsers[i - 1] = type;
    }

    return parsers;
  }

  @SuppressWarnings("unchecked")
  private CommandNode[] getChildren(Class<? extends Command> target, ParserManager parserManager) {
    ArrayList<CommandNode> result = new ArrayList<>();

    while (target != null) {
      Method[] methods = target.getDeclaredMethods();
      for (Method m : methods) {
        if (m.equals(executor))
          continue;

        ml.empee.commandsManager.command.annotations.CommandNode annotation = m.getAnnotation(
            ml.empee.commandsManager.command.annotations.CommandNode.class);
        if (annotation != null && !annotation.parent().isEmpty() && id.endsWith(annotation.parent())) {
          m.setAccessible(true);
          result.add(new CommandNode(m, target, parserManager));
        }
      }

      //The command class doesn't extends another class so I can cast it to Command
      target = (Class<? extends Command>) target.getSuperclass();
    }

    return result.toArray(new CommandNode[0]);
  }

}
