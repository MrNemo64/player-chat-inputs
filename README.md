# player-inputs
Utilities to get a player input

##Usage
In this example I'll be using a command

```
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.nemo_64.chatinput.PlayerChatInput;
import me.nemo_64.chatinput.PlayerChatInput.PlayerChatInputBuilder;

public class TestCommand implements CommandExecutor {

	private Plugin plugin;

	public TestCommand(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) { // PlayerChatInput only works with players
     	       sender.sendMessage("Only for players");
     	       return false;
		}

		Player player = (Player) sender;

		// This comand will ask for a number n and will send to the player n! so we will
		// work with integers
		PlayerChatInputBuilder<Integer> builder = new PlayerChatInputBuilder<Integer>(plugin, player);

		builder.isValidInput((p, str) -> { // Set the validation
			try {
				int val = Integer.valueOf(str);
				return val > 0;// We only accept numbers greater than 0
			} catch (Exception e) {
				return false;// The input was not an integer
			}
		});

		builder.setValue((p, str) -> {
			// We convert the input string to a number
			return Integer.valueOf(str);
		});

  
		builder.onFinish((p, value) -> {
		    // when the player inputs a string that is a number greater that 0 we send a message
		    p.sendMessage(value + "! is " + factorialOf(value));
		});

		builder.onCancel((p) -> {
			// if the player cancels, we send a message
			p.sendMessage("Canceled the factorial-calculation");
		});

		builder.invalidInputMessage("That is not a number/Can calculate the factorial of it");// Message if the input is // inalid
		builder.sendValueMessage("Send a number to calculate"); // Asking for the number
		builder.toCancel("cancel"); // Message that the player must send to cancel

		PlayerChatInput<Integer> in = builder.build(); // Build the PlayerChatInput
	
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
