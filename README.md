# player-inputs
Utilities to get a player input for those who don't want to use the Conversation API

## Usage
Explanation: https://www.spigotmc.org/threads/player-chat-inputs.437565/
In this example I'll be using a command

```java
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.nemo_64.chatinput.PlayerChatInput;
import me.nemo_64.chatinput.PlayerChatInput.PlayerChatInputBuilder;

public final class TestCommand implements CommandExecutor {

	private final Plugin plugin;

	public TestCommand(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) { // PlayerChatInput only works with players
     	       sender.sendMessage("Only for players");
     	       return false;
		}
        final Player player = (Player) sender;

		// This comand will ask for a number n and will send to the player n! so we will
		// work with integers
		PlayerChatInput<Integer> chatInput = new PlayerChatInputBuilder<Integer>(plugin, player)
                    .isValidInput((p, str) -> { // Set the validation
			try {
				int val = Integer.valueOf(str);
				return val > 0;// We only accept numbers greater than 0
			} catch (Exception e) {
				return false;// The input was not an integer
			}})
                    .setValue((p, str) -> {
			// We convert the input string to a number
			return Integer.valueOf(str);
		    })
                    .onInvalidInput((p, str) -> {
			p.sendMessage("That is not a number"); // Send a message if the input is invalid
			return true; // Send the messages stablished with invalidInputMessage(String) and sendValueMessage(String) 
		    })
                    .onFinish((p, value) -> {
		        // when the player inputs a string that is a number greater that 0 we send a message
		        p.sendMessage(value + "! is " + factorialOf(value));
		    })
                    .onCancel((p) -> {
			// if the player cancels, we send a message
			p.sendMessage("Canceled the factorial-calculation");
		    })
                    .invalidInputMessage("That is not a number/Can calculate the factorial of it");// Message if the input is invalid
                    .sendValueMessage("Send a number to calculate"); // Asking for the number
		    .toCancel("cancel"); // Message that the player must send to cancel
                    .build(); // Build the PlayerChatInput
	
		in.start(); // Ask for the number

		return false;
	}

	private long factorialOf(int num) {
		if (num <= 1)
			return 1;
		return factorialOf(num - 1) * num;
	}

}
```
