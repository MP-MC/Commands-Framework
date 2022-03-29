package tk.empee.commandManager.command.parameters.parsers;

import tk.empee.commandManager.command.parameters.parsers.annotations.StringParam;

public class StringParser extends ParameterParser<String> {

    public StringParser(String label, String defaultValue) {
        super(StringParam.class, label, defaultValue);
    }

    @Override
    public String parse(int offset, String... args) {
        return args[offset];
    }

}
