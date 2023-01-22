package ml.empee.commandsManager.parsers.types;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ml.empee.commandsManager.parsers.DescriptionBuilder;
import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.utils.helpers.Tuple;
import org.bukkit.Material;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MaterialParser extends ParameterParser<Material> {

  private boolean onlyBlocks;
  private List<String> materials;

  @Override
  public DescriptionBuilder getDescriptionBuilder() {
    return new DescriptionBuilder("material", "This parameter can only contain a material name",
            Tuple.of("Default value: ", (getDefaultValue() == null ? "none" : getDefaultValue().name()))
    );
  }

  public void setOnlyBlocks(boolean onlyBlocks) {
    this.onlyBlocks = onlyBlocks;
  }

  @Override
  public Material parse(int offset, String... args) {
    Material material = Material.getMaterial(args[offset].toUpperCase(Locale.ROOT));
    if(material == null) {
      throw new CommandException("The value &e" + args[offset] + "&r must be a material");
    } else if(onlyBlocks && !material.isBlock()) {
      throw new CommandException("The value &e" + args[offset] + "&r must be a block");
    }

    return material;
  }

  @Override
  public List<String> buildSuggestions(CommandSender source, String arg) {
    if(materials == null) {
      materials = Arrays.stream(Material.values())
              .filter(m -> !onlyBlocks || m.isBlock())
              .map(Material::name)
              .collect(Collectors.toList());
    }

    return materials;
  }

  @Override
  public ParameterParser<Material> copyParser() {
    MaterialParser parser = new MaterialParser();
    parser.onlyBlocks = this.onlyBlocks;
    return copyParser(parser);
  }
}
