package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserDescription;

@EqualsAndHashCode(callSuper = true)
public class StringParser extends ParameterParser<String> {

  public static final StringParser DEFAULT = new StringParser("", "");

  public StringParser(String label, String defaultValue) {
    super(label, defaultValue);

    descriptor = new ParserDescription("string", "This parameter can only contain string value", new String[] {
        "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
    });
  }

  protected StringParser(StringParser parser) {
    super(parser);
  }

  @Override
  public String parse(int offset, String... args) {
    return args[offset];
  }

  @Override
  public ParameterParser<String> copyParser() {
    return new StringParser(this);
  }
}
