package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.Tuple;

@EqualsAndHashCode(callSuper = true)
public class StringParser extends ParameterParser<String> {

  public static final StringParser DEFAULT = new StringParser("", "");

  public StringParser(String label, String defaultValue) {
    super(label, defaultValue);
  }

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("string", "This parameter can only contain string value",
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue()))
    );
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
