package ml.empee.commandsManager.parsers.types;

import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserDescription;

public class BoolParser extends ParameterParser<Boolean> {

    public static final BoolParser DEFAULT = new BoolParser("", "");

    public BoolParser(String label, String defaultValue) {
        super(label, defaultValue);

        descriptor = new ParserDescription("bool", "This parameter can only contain a true or false value", new String[]{
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
    }

    @Override
    public Boolean parse(int offset, String... args) {
        return Boolean.parseBoolean(args[offset]);
    }
}
