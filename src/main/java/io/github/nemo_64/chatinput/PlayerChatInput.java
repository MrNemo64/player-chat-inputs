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

package io.github.nemo_64.chatinput;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.bukkit.conversations.Conversation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class created to get inputs from players without using the
 * {@link Conversation Conversation} api
 *
 * @param <T> The input type. Ex: String, Integer, Boolean
 * @author Nemo_64
 * @version 1.1
 */
public class PlayerChatInput<T> implements Listener {

    @NotNull
    private final BiFunction<Player, String, Boolean> onInvalidInput;

    @NotNull
    private final BiFunction<Player, String, Boolean> isValidInput;

    @NotNull
    private final BiFunction<Player, String, T> setValue;

    @NotNull
    private final BiConsumer<Player, T> onFinish;

    @NotNull
    private final Consumer<Player> onCancel;

    @NotNull
    private final Player player;

    @Nullable
    private final String invalidInputMessage;

    @Nullable
    private final String sendValueMessage;

    @NotNull
    private final String cancel;

    @NotNull
    private final Plugin plugin;

    private final boolean repeat;

    @Nullable
    private T value;

    /**
     * @param plugin The main class of the plugin
     * @param player The player that is going to input the value
     * @param startOn The start value
     * @param invalidInputMessage Message that will be sent to the player if the input is invalid
     * @param sendValueMessage Message that will be sent to the player to ask for the input
     * @param isValidInput Checks if the player input is valid
     * @param setValue Used to set the value.<br>
     * Since we can't know to what transform the string that the player
     * sends, it must be converted to the value latter
     * @param onFinish Called when the player inputs a valid string
     * @param onCancel Called when the player cancels
     * @param cancel The string that the player has to send to cancel the process
     * @param onInvalidInput Called when the input is invalid
     */
    public PlayerChatInput(@NotNull Plugin plugin, @NotNull Player player, @Nullable T startOn,
                           @Nullable String invalidInputMessage, @Nullable String sendValueMessage,
                           @NotNull BiFunction<Player, String, Boolean> isValidInput,
                           @NotNull BiFunction<Player, String, T> setValue, @NotNull BiConsumer<Player, T> onFinish,
                           @NotNull Consumer<Player> onCancel, @NotNull String cancel,
                           @NotNull BiFunction<Player, String, Boolean> onInvalidInput, boolean repeat) {
        Objects.requireNonNull(plugin, "plugin can't be null");
        Objects.requireNonNull(player, "player can't be null");
        Objects.requireNonNull(isValidInput, "isValidInput can't be null");
        Objects.requireNonNull(setValue, "setValue can't be null");
        Objects.requireNonNull(onFinish, "onFinish can't be null");
        Objects.requireNonNull(onFinish, "onCancel can't be null");
        Objects.requireNonNull(onInvalidInput, "onInvalidInput can't be null");
        Objects.requireNonNull(cancel, "cancel can't be null");
        this.plugin = plugin;
        this.player = player;
        this.invalidInputMessage = invalidInputMessage;
        this.sendValueMessage = sendValueMessage;
        this.isValidInput = isValidInput;
        this.setValue = setValue;
        this.onFinish = onFinish;
        this.onCancel = onCancel;
        this.cancel = cancel.trim().isEmpty() ? "cancel" : cancel;
        this.onInvalidInput = onInvalidInput;
        this.value = startOn;
        this.repeat = repeat;
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (!player.getUniqueId().equals(e.getPlayer().getUniqueId())) {
            return;
        }
        e.setCancelled(true);
        if (e.getMessage().equalsIgnoreCase(cancel)) {
            onCancel.accept(player);
            unregister();
            return;
        }
        if (isValidInput.apply(player, e.getMessage())) {
            value = setValue.apply(player, e.getMessage());
            onFinish.accept(player, value);
            unregister();
        } else {
            if (onInvalidInput.apply(player, e.getMessage())) {
                if (invalidInputMessage != null) {
                    player.sendMessage(invalidInputMessage);
                }
                if (sendValueMessage != null && repeat) {
                    player.sendMessage(sendValueMessage);
                }
            }
            if (!repeat) {
                unregister();
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        if (e.getPlayer().getUniqueId().equals(player.getUniqueId())) {
            onCancel.accept(player);
            unregister();
        }
    }

    /**
     * Gets the value that the player has inputted or the default value
     *
     * @return The value
     */
    @Nullable
    @SuppressWarnings("unused")
    public T getValue() {
        return value;
    }

    /**
     * When this method is called the input will be asked to the player
     */
    @SuppressWarnings("unused")
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        if (sendValueMessage != null) {
            player.sendMessage(sendValueMessage);
        }
    }

    /**
     * When this method is called all the events in this input handler are
     * unregistered<br>
     * Only use if necessary. The class unregisters itself when it has finished/the
     * player leaves
     */
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Builder for the {@link PlayerChatInput} class
     *
     * @param <U> The {@link PlayerChatInput} type
     * @author Nemo_64
     */
    public static final class PlayerChatInputBuilder<U> {

        @NotNull
        private final Player player;

        @NotNull
        private final Plugin main;

        @NotNull
        private BiFunction<Player, String, Boolean> onInvalidInput;

        @NotNull
        private BiFunction<Player, String, Boolean> isValidInput;

        @NotNull
        private BiFunction<Player, String, U> setValue;

        @NotNull
        private BiConsumer<Player, U> onFinish;

        @NotNull
        private Consumer<Player> onCancel;

        @Nullable
        private String invalidInputMessage;

        @Nullable
        private String sendValueMessage;

        @NotNull
        private String cancel;

        @Nullable
        private U value;

        private boolean repeat;

        /**
         * @param main The main class of the plugin
         * @param player The player that will send the input
         */
        public PlayerChatInputBuilder(@NotNull Plugin main, @NotNull Player player) {
            this.main = main;
            this.player = player;
            invalidInputMessage = "That is not a valid input";
            sendValueMessage = "Send in the chat the value";
            cancel = "cancel";
            onInvalidInput = (p, mes) -> true;
            isValidInput = (p, mes) -> true;
            setValue = (p, mes) -> value;
            onFinish = (p, val) -> {
            };
            onCancel = (p) -> {
            };
            repeat = true;
        }

        /**
         * Sets the code that will be ejecuted if the player send an invalid input
         *
         * @param onInvalidInput A {@link BiFunction} with the code
         * to be ejected <br>
         * If this returns true, the message set with the
         * {@link #invalidInputMessage(String)} will be sent to the player
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> onInvalidInput(@NotNull BiFunction<Player, String, Boolean> onInvalidInput) {
            this.onInvalidInput = onInvalidInput;
            return this;
        }

        /**
         * Checks if the given input is valid
         *
         * @param isValidInput A {@link java.util.function.BiFunction BiFunction} with the code
         * to be ejected <br>
         * This code must check if the value that the player has inputted is
         * valid. For example<br>
         * <pre>
         *      try {
         *          Integer.valueOf(str); return true;
         *      } catch (final Exception ignored) {
         *          return false;
         *      }
         *     </pre><br>
         * Will check if the input is an integer
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> isValidInput(@NotNull BiFunction<Player, String, Boolean> isValidInput) {
            this.isValidInput = isValidInput;
            return this;
        }

        /**
         * Sets the value. Since the {@link PlayerChatInput} is a generic class, it
         * doesn't know how to convert the<br>
         * string input to the correct variable type. Because of this, we must provide
         * the code to do the cast
         *
         * @param setValue A {@link java.util.function.BiFunction BiFunction} with the code
         * to be ejected to cast the string input to the correct type
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> setValue(@NotNull BiFunction<Player, String, U> setValue) {
            this.setValue = setValue;
            return this;
        }

        /**
         * Code to be ejecuted when the player inputs a valid string and the casting is
         * successful
         *
         * @param onFinish A {@link java.util.function.BiFunction BiFunction} with the code
         * to be ejected when the player inputs a valid string
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> onFinish(@NotNull BiConsumer<Player, U> onFinish) {
            this.onFinish = onFinish;
            return this;
        }

        /**
         * Code to be ejecuted when the player sends as input what has been previously
         * set<br>
         * with the {{@link #toCancel(String)}} method
         *
         * @param onCancel A {@link java.util.function.BiFunction BiFunction} with the code
         * to be ejecuted when the player cancells the input operation
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> onCancel(@NotNull Consumer<Player> onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        /**
         * Message to be sent to the player when the input is invalid
         *
         * @param invalidInputMessage The message
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> invalidInputMessage(@Nullable String invalidInputMessage) {
            this.invalidInputMessage = invalidInputMessage;
            return this;
        }

        /**
         * Message to be sent to the player when asking for the input
         *
         * @param sendValueMessage The message
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> sendValueMessage(@Nullable String sendValueMessage) {
            this.sendValueMessage = sendValueMessage;
            return this;
        }

        /**
         * Message that the player must sent to cancel<br>
         * By default is "cancel"
         *
         * @param cancel The message
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> toCancel(@NotNull String cancel) {
            this.cancel = cancel;
            return this;
        }

        /**
         * Sets the default value
         *
         * @param def The default value
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
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
         * @param repeat the boolean
         * @return the builder.
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInputBuilder<U> repeat(boolean repeat) {
            this.repeat = repeat;
            return this;
        }

        /**
         * Creates the {@link PlayerChatInput}
         *
         * @return A new {@link PlayerChatInput}
         */
        @NotNull
        @SuppressWarnings("unused")
        public PlayerChatInput<U> build() {
            return new PlayerChatInput<>(main, player, value, invalidInputMessage, sendValueMessage, isValidInput,
                setValue, onFinish, onCancel, cancel, onInvalidInput, repeat);
        }

    }

}
