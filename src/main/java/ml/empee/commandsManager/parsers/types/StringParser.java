package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.helpers.Tuple;

@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StringParser extends ParameterParser<String> {

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("string", "This parameter can only contain string value",
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue()))
    );
  }

  @Override
  public String parse(int offset, String... args) {
    return args[offset];
  }

  @Override
  public ParameterParser<String> copyParser() {
    StringParser parser = new StringParser();
    parser.label = label;
    parser.defaultValue = defaultValue;
    return parser;
  }
}
