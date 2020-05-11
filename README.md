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
import org.bukkit.ciPlugin.Plugin;
import io.github.nemo_64.chatinput.bukkit.BukkitChatInput;
import io.github.nemo_64.chatinput.bukkit.BukkitChatInputBuilder;

public final class TestCommand implements CommandExecutor {

    private final Plugin plugin;
    
    public TestCommand(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { // PlayerChatInput only works with players
               sender.sendMessage("Only for players");
               return false;
        }
        // This comand will ask for a number n and will send to the player n! so we will
        // work with integers
        final BukkitChatInput<Integer> chatInput = BukkitChatInputBuilder.builder(plugin, (Player) sender)
            .isValidInput((player, input) -> { // Set the validation
                try {
                    int val = Integer.valueOf(input);
                    return val > 0;// We only accept numbers greater than 0
                } catch (Exception e) {
                    return false;// The input was not an integer
                }
            })
            .setValue((player, input) -> {
                // We convert the input string to a number
                return Integer.valueOf(input);
            })
            .onInvalidInput((player, input) -> {
                // Send a message if the input is invalid
                player.sendMessage("That is not a number");
                // Send the messages stablished with invalidInputMessage(String) and sendValueMessage(String)
                return true;
            })
            .onFinish((player, value) -> {
                // when the player inputs a string that is a number greater that 0 we send a message
                player.sendMessage(value + "! is " + this.factorialOf(value));
            })
            .onCancel(player -> {
                // if the player cancels, we send a message
                player.sendMessage("Canceled the factorial-calculation");
            })
            .onExpire(player - {
                // if the input time expires.
                player.sendMessage("Input expired!");
            })
            .expire(20L * 30L)
            .repeat(true)
            .invalidInputMessage("That is not a number/Can calculate the factorial of it");// Message if the input is invalid
            .sendValueMessage("Send a number to calculate"); // Asking for the number
            .toCancel("cancel"); // Message that the player must send to cancel
            .build(); // Build the PlayerChatInput
        chatInput.start(); // Ask for the number
        return false;
    }

    private long factorialOf(final int num) {
        if (num <= 1)
            return 1;
        return this.factorialOf(num - 1) * num;
    }

}
```
