package ml.empee.commandsManager.parsers.types.greedy;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.helpers.Tuple;

@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MsgParser extends ParameterParser<String> implements GreedyParser {

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return  new DescriptionBuilder("message", "This parameter can only contain a string value with spaces",
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue()))
    );
  }

  @Override
  public String parse(int offset, String... args) {
    StringBuilder string = new StringBuilder(args[offset]);
    for (int i = offset + 1; i < args.length; i++) {
      string.append(' ').append(args[i]);
    }

    return string.toString();
  }

  @Override
  public ParameterParser<String> copyParser() {
    MsgParser parser = new MsgParser();
    parser.label = label;
    parser.defaultValue = defaultValue;
    return parser;
  }
}
