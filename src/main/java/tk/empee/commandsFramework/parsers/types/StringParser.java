package tk.empee.commandsFramework.parsers.types;

import tk.empee.commandsFramework.parsers.ParameterParser;
import tk.empee.commandsFramework.parsers.ParserDescription;

public class StringParser extends ParameterParser<String> {

    public static final StringParser DEFAULT = new StringParser("", "");

    public StringParser(String label, String defaultValue) {
        super(label, defaultValue);

        descriptor = new ParserDescription("string", "This parameter can only contain string value", new String[]{
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
    }

    @Override
    public String parse(int offset, String... args) {
        return args[offset];
    }

}
