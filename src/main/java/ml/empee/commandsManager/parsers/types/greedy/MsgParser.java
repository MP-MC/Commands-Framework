package ml.empee.commandsManager.parsers.types.greedy;

import lombok.EqualsAndHashCode;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;

@EqualsAndHashCode(callSuper = true)
public class MsgParser extends ParameterParser<String> implements GreedyParser {

  public MsgParser(String label, String defaultValue) {
    super(label, defaultValue);

    descriptionBuilder = new DescriptionBuilder("message", "This parameter can only contain a string value with spaces",
        new String[] {
            "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
  }

  protected MsgParser(MsgParser parser) {
    super(parser);
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
    return new MsgParser(this);
  }

}
