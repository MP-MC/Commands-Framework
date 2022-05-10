package tk.empee.commandManager.parsers.types;

import tk.empee.commandManager.parsers.ParameterParser;
import tk.empee.commandManager.parsers.ParserDescription;
import tk.empee.commandManager.parsers.types.annotations.BoolParam;

public class BoolParser extends ParameterParser<Boolean> {
    public BoolParser(String label, String defaultValue) {
        super(BoolParam.class, label, defaultValue);

        descriptor = new ParserDescription("bool", "This parameter can only contain a true or false value", new String[]{
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
    }

    @Override
    public Boolean parse(int offset, String... args) {
        return Boolean.parseBoolean(args[offset]);
    }
}
