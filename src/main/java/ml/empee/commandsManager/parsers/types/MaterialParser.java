package ml.empee.commandsManager.parsers.types;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.Tuple;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

@SuperBuilder
@NoArgsConstructor
public class MaterialParser extends ParameterParser<Material> {

  private static final List<String> MATERIALS;

  static {
    MATERIALS = Arrays.stream(Material.values()).map(
        Material::name
    ).collect(Collectors.toList());
  }

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("material", "This parameter can only contain a material name",
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().name()))
    );
  }
  @Override
  public Material parse(int offset, String... args) {
    Material material = Material.getMaterial(args[offset].toUpperCase(Locale.ROOT));
    if (material == null) {
      throw new CommandException("The value &e" + args[offset] + "&r must be a material");
    }

    return material;
  }

  @Override
  public List<String> buildSuggestions(CommandSender source, String arg) {
    return MATERIALS;
  }

  @Override
  public ParameterParser<Material> copyParser() {
    MaterialParser parser = new MaterialParser();
    parser.label = label;
    parser.defaultValue = defaultValue;
    return parser;
  }
}
