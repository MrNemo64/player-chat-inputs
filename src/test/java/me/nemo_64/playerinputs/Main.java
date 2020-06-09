package me.nemo_64.playerinputs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.nemo_64.playerinputs.chatinput.PlayerChatInput;
import me.nemo_64.playerinputs.chatinput.PlayerChatInput.EndReason;
import me.nemo_64.playerinputs.chatinput.PlayerChatInput.PlayerChatInputBuilder;

public class Main extends JavaPlugin implements CommandExecutor {

	@Override
	public void onEnable() {
		getCommand("test").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String[] valid = new String[] { "si", "sí", "no", "yes", "true", "false", "1", "0" };
		String[] yes = new String[] { "si", "sí", "yes", "true", "1" };
		PlayerChatInput<Integer> chain = new PlayerChatInputBuilder<Integer>(this, (Player) sender)
				.isValidInput((p, str) -> {
					try {
						int val = Integer.valueOf(str);
						return val > 0;
					} catch (Exception e) {
						return false;
					}
				}).setValue((p, str) -> {
					return Integer.valueOf(str);
				}).onInvalidInput((p, str) -> {
					p.sendMessage("That is not a number");
					return true;
				}).onFinish((p, value) -> {
					p.sendMessage(value + "! is " + factorialOf(value));
				}).onCancel((p) -> {
					p.sendMessage("Canceled the factorial-calculation");
				}).invalidInputMessage(null).sendValueMessage("Send a number to calculate").build();

		PlayerChatInput<Boolean> in = new PlayerChatInputBuilder<Boolean>(this, (Player) sender)
				.isValidInput((player, str) -> {
					for (String s : valid)
						if (str.equalsIgnoreCase(s))
							return true;
					return false;
				}).setValue((player, str) -> {
					for (String s : yes)
						if (s.equalsIgnoreCase(str))
							return true;
					return false;
				}).onFinish((player, response) -> {
					player.sendMessage("→" + (response ? "you accepted" : "you denied"));
				}).onCancel((player) -> {
					player.sendMessage("→Cancelled :(");
				}).onExpire((player) -> {
					player.sendMessage("→You ran out of time :(");
				}).expiresAfter(100).onExpireMessage("→You ran out of time")
				.chainAfter(chain, EndReason.RUN_OUT_OF_TIME).build();
		in.start();
		return false;
	}

	private long factorialOf(int num) {
		if (num <= 1)
			return 1;
		return factorialOf(num - 1) * num;
	}
}
