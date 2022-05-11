# Commands Framework
The commands framework helps you create commands with advanced features. 
This framework is annotation based, and it is designed
to be simplest as possible.

## Features:
 - [x] Annotation based
 - [x] Commands auto-completion (Brigadier)
 - [x] Automatic argument parsing
 - [x] Automatic command registration
 - [x] Optional parameters
 - [x] Greedy parameters
 - [x] Automatic interactive help generation

## TODOs
 - [ ] Add support for sub-command aliases

## How to use it
This is a simple implementation of the command manager.

```java
public class MyCommand extends Command {

    @CommandRoot( aliases = {"test2"} )
    @CommandNode( label = "test", permission = "test.test" )
    public void myRootNode(CommandContext context, String arg1, Player arg2) {
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
```
[You can find a more advanced example by clicking here](https://github.com/MP-MC/Commands-Framework/tree/master/example-plugin/src/main/java/tk/empee/demo)