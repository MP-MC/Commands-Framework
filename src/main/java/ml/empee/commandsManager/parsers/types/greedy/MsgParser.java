package ml.empee.commandsManager.parsers.types.greedy;

import ml.empee.commandsManager.parsers.ParameterParser;
import ml.empee.commandsManager.parsers.ParserDescription;

public class MsgParser extends ParameterParser<String> implements GreedyParser {

    public MsgParser(String label, String defaultValue) {
        super(label, defaultValue);

        descriptor = new ParserDescription("message", "This parameter can only contain a string value with spaces", new String[]{
                "Default value: ", (defaultValue.isEmpty() ? "none" : defaultValue)
        });
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
