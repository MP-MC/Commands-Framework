# CommandManager
The commandManager framework helps you create commands. 
This framework is annotation based, and it is designed
to be simplest as possible.

## Features:
 - [x] Annotation based
 - [x] Commands auto-completion (Brigadier)
 - [x] Automatic argument parsing
 - [x] Automatic command registration
 - [x] Optional parameters
 - [x] Greedy parameters

## TODOs
- [ ] Automatic help generation

## How to use it
This is a simple implementation of the command manager.

    public class MyCommand extends Command {

        @CommandRoot( aliases = {"test2"} )
        @CommandNode( label = "test", permission = "test.test" )
        public void myRootNode(CommandContext context, @StringParam String arg1, @PlayerParam Player arg2) {
            Player sender = context.getSource(Player.class);
            sender.sendMessage("Hello World!");
            arg2.sendMessage("Hello " + arg1);
        }

    }

    public class MyPlugin extends JavaPlugin {

        @Override
        public void onEnable() {
            CommandManager commandManager = new CommandManager(this);
            commandManager.register(new MyCommand());
        }

    }
