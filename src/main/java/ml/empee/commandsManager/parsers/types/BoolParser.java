package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;

@EqualsAndHashCode(callSuper = true)
public class BoolParser extends ParameterParser<Boolean> {

  public static final BoolParser DEFAULT = new BoolParser("", "");

  public BoolParser(String label, String defaultValue) {
    super(label, defaultValue);

    descriptionBuilder = new DescriptionBuilder("bool", "This parameter can only contain a true or false value", new String[] {
        "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
    });
  }

  protected BoolParser(BoolParser parser) {
    super(parser);
  }

  @Override
  public Boolean parse(int offset, String... args) {
    return Boolean.parseBoolean(args[offset]);
  }

  @Override
  public ParameterParser<Boolean> copyParser() {
    return new BoolParser(this);
  }

}
