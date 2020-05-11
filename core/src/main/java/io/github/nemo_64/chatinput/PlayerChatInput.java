/*
 * MIT License
 *
 * Copyright (c) 2020 MrNemo64
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.nemo_64.chatinput;

import io.github.nemo_64.chatinput.event.ChatEvent;
import io.github.nemo_64.chatinput.event.QuitEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class created to get inputs from players.
 *
 * @param <T> The input type. Ex: String, Integer, Boolean
 * @author Nemo_64
 * @version 1.1
 */
public abstract class PlayerChatInput<T, P, S extends Sender<P>, X, A extends ChatEvent<P>, B extends QuitEvent<P>, L>
    implements ChatInput<T, X, L> {

    @NotNull
    protected final BiFunction<S, String, Boolean> onInvalidInput;

    @NotNull
    protected final BiFunction<S, String, Boolean> isValidInput;

    @NotNull
    protected final BiFunction<S, String, T> setValue;

    @NotNull
    protected final BiConsumer<S, T> onFinish;

    @NotNull
    protected final Consumer<S> onCancel;

    @NotNull
    protected final Consumer<S> onExpire;

    @NotNull
    protected final S sender;

    @Nullable
    protected final String invalidInputMessage;

    @Nullable
    protected final String sendValueMessage;

    @NotNull
    protected final String cancel;

    @NotNull
    protected final ChatInputPlugin<X, L> ciPlugin;

    protected final boolean repeat;

    protected final long expire;

    @Nullable
    protected Task<X> expireTask;

    @Nullable
    protected T value;

    /**
     * @param ciPlugin The main class of the plugin
     * @param sender The sender that is going to input the value
     * @param startOn The start value
     * @param invalidInputMessage Message that will be sent to the sender if the input is invalid
     * @param sendValueMessage Message that will be sent to the sender to ask for the input
     * @param isValidInput Checks if the sender input is valid
     * @param setValue Used to set the value.<br>
     * Since we can't know to what transform the string that the sender
     * sends, it must be converted to the value latter
     * @param onFinish Called when the sender inputs a valid string
     * @param onCancel Called when the sender cancels
     * @param onExpire Called when the sender didn't complete the situation
     * @param cancel The string that the sender has to send to cancel the process
     * @param onInvalidInput Called when the input is invalid
     */
    protected PlayerChatInput(@NotNull final ChatInputPlugin<X, L> ciPlugin, @NotNull final S sender, @Nullable final T startOn,
                              @Nullable final String invalidInputMessage, @Nullable final String sendValueMessage,
                              @NotNull final BiFunction<S, String, Boolean> isValidInput,
                              @NotNull final BiFunction<S, String, T> setValue,
                              @NotNull final BiConsumer<S, T> onFinish, @NotNull final Consumer<S> onCancel,
                              @NotNull final String cancel,
                              @NotNull final BiFunction<S, String, Boolean> onInvalidInput, final boolean repeat,
                              @NotNull final Consumer<S> onExpire, final long expire) {
        Objects.requireNonNull(ciPlugin, "plugin can't be null");
        Objects.requireNonNull(sender, "sender can't be null");
        Objects.requireNonNull(isValidInput, "isValidInput can't be null");
        Objects.requireNonNull(setValue, "setValue can't be null");
        Objects.requireNonNull(onFinish, "onFinish can't be null");
        Objects.requireNonNull(onCancel, "onCancel can't be null");
        Objects.requireNonNull(onExpire, "onExpire can't be null");
        Objects.requireNonNull(onInvalidInput, "onInvalidInput can't be null");
        Objects.requireNonNull(cancel, "cancel can't be null");
        this.ciPlugin = ciPlugin;
        this.sender = sender;
        this.invalidInputMessage = invalidInputMessage;
        this.sendValueMessage = sendValueMessage;
        this.isValidInput = isValidInput;
        this.setValue = setValue;
        this.onFinish = onFinish;
        this.onCancel = onCancel;
        this.onExpire = onExpire;
        if (cancel.trim().isEmpty()) {
            this.cancel = "cancel";
        } else {
            this.cancel = cancel;
        }
        this.onInvalidInput = onInvalidInput;
        this.value = startOn;
        this.repeat = repeat;
        this.expire = expire;
    }

    /**
     * Gets the value that the player has inputted or the default value
     *
     * @return The value
     */
    @NotNull
    @Override
    public final Optional<T> getValue() {
        return Optional.ofNullable(this.value);
    }

    /**
     * When this method is called the input will be asked to the player
     */
    @Override
    public final void start() {
        this.ciPlugin.registerEvent(this.get());
        if (this.expire != -1L) {
            this.expireTask = this.createTask(
                this.ciPlugin.createRunTaskLater(() ->
                    this.getExpireTask()
                        .filter(task -> !task.isCancelled())
                        .ifPresent(task -> {
                            this.onExpire.accept(this.sender);
                            this.unregister();
                        }), this.expire));
        }
        this.getSendValueMessage()
            .ifPresent(this.sender::sendMessage);
    }

    /**
     * When this method is called all the events in this input handler are
     * unregistered<br>
     * Only use if necessary. The class unregisters itself when it has finished/the
     * player leaves
     */
    @Override
    public final void unregister() {
        this.unregisterListeners();
        this.getExpireTask().ifPresent(Task::cancel);
    }

    @NotNull
    @Override
    public final Optional<Task<X>> getExpireTask() {
        return Optional.ofNullable(this.expireTask);
    }

    @NotNull
    @Override
    public final Optional<String> getInvalidInputMessage() {
        return Optional.ofNullable(this.invalidInputMessage);
    }

    @NotNull
    @Override
    public final Optional<String> getSendValueMessage() {
        return Optional.ofNullable(this.sendValueMessage);
    }

    @NotNull
    public abstract Task<X> createTask(@NotNull X task);

    public final void onChat(@NotNull final A event) {
        if (!this.sender.getUniqueId().equals(event.sender().getUniqueId())) {
            return;
        }
        event.cancel();
        final String message = event.message();
        if (message.equalsIgnoreCase(this.cancel)) {
            this.onCancel.accept(this.sender);
            this.unregister();
            return;
        }
        if (this.isValidInput.apply(this.sender, message)) {
            this.value = this.setValue.apply(this.sender, message);
            this.onFinish.accept(this.sender, this.value);
            this.unregister();
        } else {
            if (this.onInvalidInput.apply(this.sender, message)) {
                this.getInvalidInputMessage()
                    .ifPresent(this.sender::sendMessage);
                this.getSendValueMessage()
                    .filter(s -> this.repeat)
                    .ifPresent(this.sender::sendMessage);
            }
            if (!this.repeat) {
                this.unregister();
            }
        }
    }

    public final void onQuit(@NotNull final B event) {
        if (event.sender().getUniqueId().equals(this.sender.getUniqueId())) {
            this.onCancel.accept(this.sender);
            this.unregister();
        }
    }

}
