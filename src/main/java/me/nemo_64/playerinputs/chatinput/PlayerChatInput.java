/*
 * MIT License
 * 
 * Copyright (c) 2020 Nemo_64
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.nemo_64.playerinputs.chatinput;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Class created to get inputs from players without using the
 * {@link org.bukkit.conversations.Conversation Conversation} api
 * 
 * @author Nemo_64
 * @version 1.2
 * @param <T>
 *            The input type. Ex: String, Integer, Boolean
 */
public class PlayerChatInput<T> implements Listener {

	private EnumMap<EndReason, PlayerChatInput<?>> chainAfter;
	private BiFunction<Player, String, Boolean> onInvalidInput;
	private BiFunction<Player, String, Boolean> isValidInput;
	private BiFunction<Player, String, T> setValue;
	private BiConsumer<Player, T> onFinish;
	private Consumer<Player> onCancel;
	private Consumer<Player> onExpire;
	private Runnable onDisconnect;
	private Player player;

	private String invalidInputMessgae;
	private String sendValueMessage;
	private String onExpireMessage;
	private String cancel;

	private Plugin main;

	private int expiresAfter;
	private boolean started;
	private boolean repeat;

	private T value;

	private BukkitTask task;

	private EndReason end;

	/**
	 * @param plugin
	 *            The main class of the plugin
	 * @param player
	 *            The player that is going to input the value
	 * @param startOn
	 *            The start value
	 * @param invalidInputMessgae
	 *            Message that will be sent to the player if the input is invalid
	 * @param sendValueMessage
	 *            Message that will be sent to the player to ask for the input
	 * @param isValidInput
	 *            Checks if the player input is valid
	 * @param setValue
	 *            Used to set the value.<br>
	 *            Since we can't know to what transform the string that the player
	 *            sends, it must be converted to the value latter
	 * @param onFinish
	 *            Called when the player inputs a valid string
	 * @param onCancel
	 *            Called when the player cancells
	 * @param cancel
	 *            The string that the player has to send to cancel the process
	 * @param onInvalidInput
	 *            Called when the input is invalid
	 * @param repeat
	 *            If true and the input is invalid, another input will be expected
	 * @param chainAfter
	 *            When the live cicle ends depending on the end another
	 *            PlayerChatInput will be ejecuted
	 * @param expiresAfter
	 *            Ticks that the player has to answer. -1 for not having a limited
	 *            time. (20ticks = 1sec)
	 * @param onExpire
	 *            Code to run if the player runs out of time
	 * @param whenExpireMessage
	 *            Message to send to the player if he runs out of time. If null, the
	 *            message won't be sent
	 * @param onDisconnect
	 *            Code to be runned if the player disconnects
	 */
	public PlayerChatInput(@Nonnull Plugin plugin, @Nonnull Player player, @Nullable T startOn,
			@Nullable String invalidInputMessgae, @Nullable String sendValueMessage,
			@Nonnull BiFunction<Player, String, Boolean> isValidInput, @Nonnull BiFunction<Player, String, T> setValue,
			@Nonnull BiConsumer<Player, T> onFinish, @Nonnull Consumer<Player> onCancel, @Nonnull String cancel,
			@Nonnull BiFunction<Player, String, Boolean> onInvalidInput, boolean repeat,
			@Nullable EnumMap<EndReason, PlayerChatInput<?>> chainAfter, int expiresAfter,
			@Nonnull Consumer<Player> onExpire, @Nullable String whenExpireMessage, @Nonnull Runnable onDisconnect) {
		Objects.requireNonNull(plugin, "main can't be null");
		Objects.requireNonNull(player, "player can't be null");
		Objects.requireNonNull(invalidInputMessgae, "isValidInput can't be null");
		Objects.requireNonNull(sendValueMessage, "isValidInput can't be null");
		Objects.requireNonNull(isValidInput, "isValidInput can't be null");
		Objects.requireNonNull(setValue, "setValue can't be null");
		Objects.requireNonNull(onFinish, "onFinish can't be null");
		Objects.requireNonNull(onFinish, "onCancel can't be null");
		Objects.requireNonNull(onInvalidInput, "onInvalidInput can't be null");
		Objects.requireNonNull(cancel, "cancel can't be null");
		Objects.requireNonNull(onExpire, "onExpire can't be null");
		Objects.requireNonNull(onDisconnect, "onDisconnect can't be null");
		this.main = plugin;
		this.player = player;
		this.invalidInputMessgae = invalidInputMessgae;
		this.sendValueMessage = sendValueMessage;
		this.isValidInput = isValidInput;
		this.setValue = setValue;
		this.onFinish = onFinish;
		this.onCancel = onCancel;
		this.cancel = cancel == null ? "cancel" : cancel;
		this.onInvalidInput = onInvalidInput;
		this.value = startOn;
		this.repeat = repeat;
		this.chainAfter = chainAfter;
		this.expiresAfter = expiresAfter;
		this.onExpire = onExpire;
		this.onExpireMessage = whenExpireMessage;
		this.onDisconnect = onDisconnect;
	}

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
		if (!player.getUniqueId().equals(e.getPlayer().getUniqueId()))
			return;
		if (!isStarted()) // We have already ended
			return;
		e.setCancelled(true);
		Bukkit.getScheduler().runTask(main, () -> runEventOnMainThread(e.getMessage())); // Jump to main thread
	}

	private void runEventOnMainThread(String message) {
		if (message.equalsIgnoreCase(cancel)) { // Player cancells input
			onCancel.accept(player);
			end(EndReason.PLAYER_CANCELLS);
			return;
		}
		if (isValidInput.apply(player, message)) { // Is a valid input?
			value = setValue.apply(player, message); // Transform the value
			onFinish.accept(player, value); // Ron onFinish
			end(EndReason.FINISH);
		} else {
			if (onInvalidInput.apply(player, message)) {
				if (invalidInputMessgae != null)
					player.sendMessage(invalidInputMessgae);
				if (sendValueMessage != null && repeat)
					player.sendMessage(sendValueMessage);
			}
			if (!repeat) { // We only acepted anwers 1
				onExpire.accept(player);
				end(EndReason.INVALID_INPUT);
			}
		}
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		if (e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
			if (!isStarted())// We have already ended
				return;
			onDisconnect.run();
			end(EndReason.PLAYER_DISCONECTS);
		}
	}

	@Nullable
	/**
	 * Gets the value that the player has inputed or the default value
	 * 
	 * @return The value
	 */
	public T getValue() {
		return value;
	}

	@Nullable
	/**
	 * Gets the reason why this playerchatinput has finished
	 * 
	 * @return Null if it hasn't finish yet
	 */
	public EndReason getEndReason() {
		return end;
	}

	/**
	 * When this method is called the input will be asked to the player
	 */
	public void start() {
		// The player can only be in one active PlayerChatInput at a time
		if (isInputing(player.getUniqueId()))
			throw new IllegalAccessError("Can't ask for input to a player that is already inputing");
		addPlayer(player.getUniqueId());

		// Start the listener
		main.getServer().getPluginManager().registerEvents(this, this.main);

		// There is a limit of time
		if (expiresAfter > 0)
			task = Bukkit.getScheduler().runTaskLater(main, () -> {
				if (!isStarted()) // We have ended somewhere else
					return;
				onExpire.accept(player);
				if (onExpireMessage != null)
					player.sendMessage(onExpireMessage);
				end(EndReason.RUN_OUT_OF_TIME);
			}, expiresAfter);
		if (sendValueMessage != null)
			player.sendMessage(sendValueMessage);
		started = true;
		end = null;
	}

	/**
	 * When this method is called all the events in this input handler are
	 * unregistered<br>
	 * Only use if necesary. The class unregisters itself when it has finished/the
	 * player leaves
	 */
	public void unregister() {
		// Maybe the timer is still running
		if (task != null)
			task.cancel();
		// The player can be asked for an input again
		removePlayer(player.getUniqueId());
		// Unregister events
		HandlerList.unregisterAll(this);
	}

	/**
	 * Unregisters events and starts the chain if there is a chain
	 * 
	 * @param reason
	 *            The reason why the input-porces has endedu
	 */
	public void end(EndReason reason) {
		started = false;
		end = reason;
		unregister();
		// There is something to chain
		if (chainAfter != null)
			// There is something to chain with out end
			if (chainAfter.get(end) != null)
				// Start the new input
				chainAfter.get(end).start();
	}

	/**
	 * Checks if waiting for an input
	 * 
	 * @return True if this input process is started
	 */
	public boolean isStarted() {
		return started;
	}

	/*
	 * STATIC START
	 */
	private static List<UUID> players = new ArrayList<UUID>();

	private static void addPlayer(UUID player) {
		players.add(player);
	}

	private static void removePlayer(UUID player) {
		players.remove(player);
	}

	/**
	 * Checks if a player is in an input-proces
	 * 
	 * @param player
	 *            The UUID of the player to check if it is in an inpit-process
	 * 
	 * @return True if the player is in an input process
	 */
	public static boolean isInputing(UUID player) {
		return players.contains(player);
	}

	/*
	 * STATIC END
	 */

	/**
	 * Builder for the {@link PlayerChatInput} class
	 * 
	 * @author Nemo_64
	 * @version 1.2
	 * @param <U>
	 *            The {@link PlayerChatInput} type
	 */
	public static class PlayerChatInputBuilder<U> {

		private EnumMap<EndReason, PlayerChatInput<?>> chainAfter;
		private BiFunction<Player, String, Boolean> onInvalidInput;
		private BiFunction<Player, String, Boolean> isValidInput;
		private BiFunction<Player, String, U> setValue;
		private BiConsumer<Player, U> onFinish;
		private Consumer<Player> onCancel;
		private Consumer<Player> onExpire;
		private Runnable onDisconnect;
		private Player player;

		private String invalidInputMessage;
		private String sendValueMessage;
		private String whenExpire;
		private String cancel;

		private U value;

		private int expiresAfter;
		private boolean repeat;

		private Plugin main;

		/**
		 * @param main
		 *            The main class of the plugin
		 * @param player
		 *            The player that will send the input
		 */
		public PlayerChatInputBuilder(@Nonnull Plugin main, @Nonnull Player player) {
			this.main = main;
			this.player = player;

			invalidInputMessage = "That is not a valid input";
			sendValueMessage = "Send in the chat the value";
			whenExpire = "You ran out of time to answer";
			cancel = "cancel";

			onInvalidInput = (p, mes) -> {
				return true;
			};
			isValidInput = (p, mes) -> {
				return true;
			};
			setValue = (p, mes) -> {
				return value;
			};
			onFinish = (p, val) -> {};
			onCancel = (p) -> {};
			onExpire = (p) -> {};
			onDisconnect = () -> {};

			expiresAfter = -1;

			repeat = true;
		}

		/**
		 * Sets the code that will be ejecuted if the player send an invalid input
		 * 
		 * @param onInvalidInput
		 *            A {@link java.util.function.BiFunction BiFunction} with the code
		 *            to be ejecuted <br>
		 *            If this returns true, the message setted with the
		 *            {@link #invalidInputMessage(String)} will be sent to the player
		 * 
		 * @return This builder
		 * 
		 */
		public PlayerChatInputBuilder<U> onInvalidInput(@Nonnull BiFunction<Player, String, Boolean> onInvalidInput) {
			this.onInvalidInput = onInvalidInput;
			return this;
		}

		/**
		 * Checks if the given input is valid
		 * 
		 * @param isValidInput
		 *            A {@link java.util.function.BiFunction BiFunction} with the code
		 *            to be ejecuted <br>
		 *            This code must check if the value that the player has inputted is
		 *            vaid.
		 * 
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> isValidInput(@Nonnull BiFunction<Player, String, Boolean> isValidInput) {
			this.isValidInput = isValidInput;
			return this;
		}

		/**
		 * Sets the value. Since the {@link PlayerChatInput} is a generic class, it
		 * doesn't know how to convert the<br>
		 * string input to the correct variable type. Because of this, we must provide
		 * the code to do the cast
		 * 
		 * @param setValue
		 *            A {@link java.util.function.BiFunction BiFunction} with the code
		 *            to be ejecuted to cast the string input to the correct type
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> setValue(@Nonnull BiFunction<Player, String, U> setValue) {
			this.setValue = setValue;
			return this;
		}

		/**
		 * Code to be ejecuted when the player inputs a valid string and the casting is
		 * succesfull
		 * 
		 * @param onFinish
		 *            A {@link java.util.function.BiFunction BiFunction} with the code
		 *            to be ejecuted when the player inputs a valid string
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> onFinish(@Nonnull BiConsumer<Player, U> onFinish) {
			this.onFinish = onFinish;
			return this;
		}

		/**
		 * Code to be ejecuted when the player sends as input what has been previously
		 * set<br>
		 * with the {@link #toCancel(String)} method
		 * 
		 * @param onCancel
		 *            A {@link java.util.function.BiFunction BiFunction} with the code
		 *            to be ejecuted when the player cancells the input operation
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> onCancel(@Nonnull Consumer<Player> onCancel) {
			this.onCancel = onCancel;
			return this;
		}

		/**
		 * Message to be sent to the player when the input is invalid
		 * 
		 * @param invalidInputMessage
		 *            The message
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> invalidInputMessage(@Nullable String invalidInputMessage) {
			this.invalidInputMessage = invalidInputMessage;
			return this;
		}

		/**
		 * Message to be sent to the player when asking for the input
		 * 
		 * @param sendValueMessage
		 *            The message
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> sendValueMessage(@Nullable String sendValueMessage) {
			this.sendValueMessage = sendValueMessage;
			return this;
		}

		/**
		 * Message that the player must sent to cancel<br>
		 * By default is "cancel"
		 * 
		 * @param cancel
		 *            The message
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> toCancel(@Nonnull String cancel) {
			this.cancel = cancel;
			return this;
		}

		/**
		 * Sets the default value
		 * 
		 * @param def
		 *            The default value
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> defaultValue(@Nullable U def) {
			this.value = def;
			return this;
		}

		/**
		 * If true and the player sends an invalid input,
		 * {@link #onInvalidInput(BiFunction)} will run and another inputs will be
		 * asked.<br>
		 * If false and the player sends an invalid input,
		 * {@link #onInvalidInput(BiFunction)} will run and no more inputs will be
		 * expected.
		 * 
		 * @param repeat
		 *            If true and the player sends an invalid input, another one will be
		 *            asked
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> repeat(boolean repeat) {
			this.repeat = repeat;
			return this;
		}

		/**
		 * When this PlayerChatInput ends, depending on the end the specified <br>
		 * new event will be runed.<br>
		 * 
		 * @param after
		 *            What PlayerChatInput to run
		 * @param toChain
		 *            When to run it. {@link EndReason#PLAYER_DISCONECTS
		 *            PLAYER_DISCONECTS} will be ignored
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> chainAfter(@Nonnull PlayerChatInput<?> toChain, @Nonnull EndReason... after) {
			if (this.chainAfter == null)
				chainAfter = new EnumMap<>(EndReason.class);
			for (EndReason cm : after) {
				if (cm == EndReason.PLAYER_DISCONECTS)
					continue;
				this.chainAfter.put(cm, toChain);
			}
			return this;
		}

		/**
		 * Sets the code to be ejecuted when the time expires
		 * 
		 * @param onExpire
		 *            Code to be runned
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> onExpire(@Nonnull Consumer<Player> onExpire) {
			this.onExpire = onExpire;
			return this;
		}

		/**
		 * Message sent when the time expires
		 * 
		 * @param message
		 *            The message to be sent
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> onExpireMessage(@Nullable String message) {
			this.whenExpire = message;
			return this;
		}

		/**
		 * Ticks that the player has to answer
		 * 
		 * @param ticks
		 *            The amount of ticks (20 ticks = 1 second)
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> expiresAfter(@Nonnegative int ticks) {
			if (ticks > 0)
				this.expiresAfter = ticks;
			return this;
		}

		/**
		 * Code to be runned if the player disconnects
		 * 
		 * @param onDisconnect
		 *            Code to be runned
		 * @return This builder
		 */
		public PlayerChatInputBuilder<U> onPlayerDiconnect(@Nonnull Runnable onDisconnect) {
			this.onDisconnect = onDisconnect;
			return this;
		}

		/**
		 * Creates the {@link PlayerChatInput}
		 * 
		 * @return A new {@link PlayerChatInput}
		 */
		public PlayerChatInput<U> build() {
			return new PlayerChatInput<U>(main, player, value, invalidInputMessage, sendValueMessage, isValidInput,
					setValue, onFinish, onCancel, cancel, onInvalidInput, repeat, chainAfter, expiresAfter, onExpire,
					whenExpire, onDisconnect);
		}
	}

	/**
	 * An enum with all the posible ends to a input-process
	 * 
	 * @since 1.2
	 * @author Nemo_64
	 */
	public static enum EndReason {

		/**
		 * Used when the player sends as input the cancellling string
		 */
		PLAYER_CANCELLS,
		/**
		 * The input-process ended succesfuly
		 */
		FINISH,
		/**
		 * The player ran out of time to answer
		 */
		RUN_OUT_OF_TIME,
		/**
		 * The player disconected
		 */
		PLAYER_DISCONECTS,
		/**
		 * The player sent an invalid input and the repeating mode is off
		 */
		INVALID_INPUT,
		/**
		 * A plugin ended the input process
		 */
		CUSTOM;

	}

}