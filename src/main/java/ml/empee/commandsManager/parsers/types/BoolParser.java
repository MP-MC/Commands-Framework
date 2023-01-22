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
public class BoolParser extends ParameterParser<Boolean> {

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder(
            "bool", "This parameter can only contain a true or false value",
            Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().toString()))
    );
  }

  @Override
  public Boolean parse(int offset, String... args) {
    return Boolean.parseBoolean(args[offset]);
  }

  @Override
  public ParameterParser<Boolean> copyParser() {
    return copyParser(new BoolParser());
  }

}
