package tk.empee.commandManager.command.parameters.parsers.greedy;

import tk.empee.commandManager.command.parameters.parsers.ParameterParser;
import tk.empee.commandManager.command.parameters.parsers.annotations.StringParam;

public class GreedyStringParser extends ParameterParser<String> implements ParameterParser.Greedy {

    public GreedyStringParser(String label, String defaultValue) {
        super(StringParam.class, label, defaultValue);
    }

    @Override
    public String parse(int offset, String... args) {
        StringBuilder string = new StringBuilder(args[offset]);
        for(int i=offset+1; i<args.length; i++) {
            string.append(' ').append(args[i]);
        }

        return string.toString();
    }

}
