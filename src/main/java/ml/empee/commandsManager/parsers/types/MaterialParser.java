package ml.empee.commandsManager.parsers.types;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.Tuple;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class MaterialParser extends ParameterParser<Material> {

  public static final MaterialParser DEFAULT = new MaterialParser("material", "");

  private static final List<String> MATERIALS;

  static {
    MATERIALS = Arrays.stream(Material.values()).map(
        Material::name
    ).collect(Collectors.toList());
  }

  protected MaterialParser(String label, String defaultValue) {
    super(label, defaultValue);
  }

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("material", "This parameter can only contain a material name",
        Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().name()))
    );
  }

  protected MaterialParser(MaterialParser parser) {
    super(parser);
  }

  @Override
  public Material parse(int offset, String... args) {
    Material material = Material.getMaterial(args[offset].toUpperCase(Locale.ROOT));
    if (material == null) {
      throw new CommandException("The value &e" + args[offset] + "&c must be a material");
    }

    return material;
  }

  @Override
  public List<String> buildSuggestions(CommandSender source, String arg) {
    return MATERIALS;
  }

  @Override
  public MaterialParser copyParser() {
    return new MaterialParser(this);
  }
}
