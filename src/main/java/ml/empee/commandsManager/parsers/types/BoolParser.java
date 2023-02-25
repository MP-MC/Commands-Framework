package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.helpers.Tuple;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BoolParser extends ParameterParser<Boolean> {

  private static final List<String> SUGGESTIONS = Arrays.asList("true", "false");

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
  public List<String> buildSuggestions(CommandSender source, String arg) {
    return SUGGESTIONS;
  }

  @Override
  public ParameterParser<Boolean> copyParser() {
    return copyParser(new BoolParser());
  }

}
