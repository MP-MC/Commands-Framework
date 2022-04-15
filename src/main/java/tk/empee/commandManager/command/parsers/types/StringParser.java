package tk.empee.commandManager.command.parsers.types;

import tk.empee.commandManager.command.parsers.types.annotations.StringParam;

public class StringParser extends ParameterParser<String> {

    public StringParser(String label, String defaultValue) {
        super(StringParam.class, label, defaultValue);
    }

    @Override
    public String parse(int offset, String... args) {
        return args[offset];
    }

}
