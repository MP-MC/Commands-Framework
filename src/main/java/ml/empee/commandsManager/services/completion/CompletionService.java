package ml.empee.commandsManager.services.completion;

import ml.empee.commandsManager.command.Command;

public interface CompletionService {

  void registerCompletions(Command command);

}
