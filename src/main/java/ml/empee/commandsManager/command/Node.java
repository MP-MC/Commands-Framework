package ml.empee.commandsManager.command;

import lombok.AccessLevel;
import lombok.Getter;
import ml.empee.commandsManager.CommandManager;
import ml.empee.commandsManager.command.annotations.CommandNode;
import ml.empee.commandsManager.command.annotations.Context;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.types.greedy.GreedyParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public final class Node {

  private final CommandManager commandManager;
  private final Controller controller;
  private final CommandNode data;
  private final String description;
  private final Class<? extends CommandSender> senderType;
  @Getter(AccessLevel.PRIVATE)
  private final Method executor;
  private final ParameterParser<?>[] parameterParsers;
  private final Parameter[] parameters;
  private Node parent;
  private String id;
  private Node[] children;

  private Node(Controller controller, CommandManager commandManager) {
    this.controller = controller;
    this.commandManager = commandManager;
    this.executor = null;

    data = controller.getClass().getAnnotation(CommandNode.class);
    if(data == null) {
      throw new IllegalStateException("The class " + controller.getClass().getName() + " is not annotated with @CmdNode");
    }

    id = data.label().toLowerCase();
    senderType = CommandSender.class;
    parameterParsers = new ParameterParser[0];
    parameters = new Parameter[0];
    description = buildDescription();
  }

  private Node(Controller controller, CommandManager commandManager, Method executor) {
    this.controller = controller;
    this.commandManager = commandManager;
    this.executor = executor;

    data = executor.getAnnotation(CommandNode.class);
    id = data.label().toLowerCase();
    senderType = buildSenderType();
    parameters = executor.getParameters();
    parameterParsers = buildParameterParsers();
    description = buildDescription();
  }

  public static Node buildCommandTree(CommandManager commandManager, Controller controller) {
    List<Node> nodes = buildCommandNodes(commandManager, controller);
    Node root = findRootNode(nodes).orElse(
            new Node(controller, commandManager)
    );
    linkNodes(root, nodes);
    nodes.forEach(
            Node::validateNode
    );

    return root;
  }

  private static Optional<Node> findRootNode(List<Node> nodes) {
    return nodes.stream()
            .filter(node -> node.getData().parent().isEmpty())
            .findFirst();
  }

  private static void linkNodes(Node root, List<Node> nodes) {
    root.children = nodes.stream()
            .filter(n -> n != root && !n.data.parent().isEmpty())
            .filter(n -> root.id.endsWith(n.data.parent().toLowerCase()))
            .toArray(Node[]::new);

    for(Node node : root.children) {
      node.parent = root;
      node.id = root.id + "." + node.data.label().toLowerCase();
      linkNodes(node, nodes);
    }
  }

  private static List<Node> buildCommandNodes(CommandManager commandManager, Controller controller) {
    List<Node> nodes = new ArrayList<>();

    for(Controller subController : controller.getSubControllers()) {
      nodes.addAll(buildCommandNodes(commandManager, subController));
    }

    nodes.addAll(Arrays.stream(controller.getClass().getDeclaredMethods())
            .filter(m -> m.isAnnotationPresent(CommandNode.class))
            .filter(m -> m.getParameterCount() > 0)
            .filter(m -> CommandSender.class.isAssignableFrom(m.getParameterTypes()[0]))
            .map(m -> new Node(controller, commandManager, m))
            .collect(Collectors.toList()));

    return nodes;
  }

  private String buildDescription() {
    StringBuilder result = new StringBuilder("\n");
    if(!data.description().isEmpty()) {
      result.append(ChatColor.DARK_AQUA).append(data.description()).append("\n\n");
    }

    result.append(ChatColor.YELLOW).append("Permission: ").append(ChatColor.LIGHT_PURPLE)
            .append(data.permission().isEmpty() ? "none" : data.permission())
            .append("\n");

    return result.toString();
  }

  private Class<? extends CommandSender> buildSenderType() {
    return (Class<? extends CommandSender>) executor.getParameters()[0].getType();
  }

  private ParameterParser<?>[] buildParameterParsers() {
    return Arrays.stream(parameters).skip(1)
            .filter(p -> !p.isAnnotationPresent(Context.class))
            .map(p -> commandManager.getParserManager().getParameterParser(p))
            .toArray(ParameterParser[]::new);
  }

  private void validateNode() {
    validateParsersConstrains();
    validateChildren();
  }

  private void validateParsersConstrains() {
    List<ParameterParser<?>> parsers = Arrays.asList(parameterParsers);
    if(parsers.stream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException(
              "Can't find a parser for one of the parameters of the node " + data.label()
      );
    }

    validateGreedyParsers();
    validateOptionalParsers();
    validateRequiredParsers();
  }

  private void validateRequiredParsers() {
    for(int i = 0; i < parameterParsers.length; i++) {
      ParameterParser<?> parser = parameterParsers[i];
      Class<?>[] neededParsers = parser.getNeededParsers();
      if(neededParsers == null || neededParsers.length == 0) {
        continue;
      }

      int j = -1;
      for(Class<?> neededParser : neededParsers) {
        if(i + j < 0 || !parameterParsers[i + j].getClass().equals(neededParser)) {
          throw new IllegalArgumentException(
                  "The parser " + parser.getClass().getSimpleName() + " needs the parser "
                          + neededParser.getSimpleName() + " to be before it"
          );
        }

        j--;
      }
    }
  }

  private void validateGreedyParsers() {
    for(int i = 0; i < parameterParsers.length; i++) {
      if(parameterParsers[i] instanceof GreedyParser) {
        if(i != parameterParsers.length - 1) {
          throw new IllegalArgumentException(
                  "The greedy parser must be the last one inside the node " + data.label()
          );
        } else if(children.length > 0) {
          throw new IllegalArgumentException(
                  "The greedy parser can't be used with children inside the node " + data.label()
          );
        }
      }
    }
  }

  private void validateOptionalParsers() {
    for(int i = 0; i < parameterParsers.length; i++) {
      if(parameterParsers[i].isOptional()) {
        if(i != parameterParsers.length - 1 && !parameterParsers[i + 1].isOptional()) {
          throw new IllegalArgumentException(
                  "Can't have a required parser after an optional one inside the node " + data.label()
          );
        } else if(children.length > 0) {
          throw new IllegalArgumentException(
                  "Can't have optional parsers inside a node with children " + data.label()
          );
        }
      }

    }
  }

  private void validateChildren() {
    for(Node c : children) {
      for(Node k : children) {
        if(c != k && c.data.label().equalsIgnoreCase(k.data.label())) {
          throw new IllegalArgumentException(
                  "Can't have two children with the same label inside the node " + data.label()
          );
        }
      }
    }
  }

  public void executeNode(CommandContext context, Object... args) throws InvocationTargetException, IllegalAccessException {
    if(executor != null) {
      if(parameters.length == args.length) {
        executor.invoke(controller, args);
        return;
      }

      Object[] arguments = new Object[parameters.length];
      int parsedArgIndex = 0;
      for(int i = 0; i < arguments.length; i++) {
        Context contextId = parameters[i].getAnnotation(Context.class);
        if(contextId != null) {
          if(contextId.value().isEmpty()) {
            arguments[i] = context.getArgument(parameters[i].getName());
          } else {
            arguments[i] = context.getArgument(contextId.value());
          }
        } else {
          arguments[i] = args[parsedArgIndex];
          parsedArgIndex++;
        }
      }

      executor.invoke(controller, arguments);
    }
  }

  @Nullable
  public Node findNextNode(String[] args, int offset) {
    if(offset >= args.length) {
      return null;
    }

    for(Node child : children) {
      String[] labels = child.data.label().split(" ");
      boolean matchAllLabels = true;
      for(int i = 0; i < labels.length; i++) {
        if(offset + i >= args.length || !labels[i].equalsIgnoreCase(args[offset + i])) {
          matchAllLabels = false;
          break;
        }
      }

      if(matchAllLabels) {
        return child;
      }
    }

    return null;
  }

}
