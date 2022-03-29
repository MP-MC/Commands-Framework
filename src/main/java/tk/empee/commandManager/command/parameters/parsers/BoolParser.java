package tk.empee.commandManager.command.parameters.parsers;

import tk.empee.commandManager.command.parameters.parsers.annotations.BoolParam;

public class BoolParser extends ParameterParser<Boolean> {
    public BoolParser(String label, String defaultValue) {
        super(BoolParam.class, label, defaultValue);
    }

    @Override
    public Boolean parse(int offset, String... args) {
        return Boolean.parseBoolean(args[offset]);
    }
}
