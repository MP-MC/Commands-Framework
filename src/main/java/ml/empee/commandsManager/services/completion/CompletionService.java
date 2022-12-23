package ml.empee.commandsManager.services.completion;

import ml.empee.commandsManager.command.CommandExecutor;

public interface CompletionService {

  void registerCompletions(CommandExecutor command);

}
