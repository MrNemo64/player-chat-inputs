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

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ChatInputBuilder<T, P, S extends Sender<P>, X, L> {

    @NotNull
    protected final ChatInputPlugin<X, L> plugin;

    @NotNull
    protected final S sender;

    @NotNull
    protected BiFunction<S, String, Boolean> onInvalidInput = (p, mes) -> true;

    @NotNull
    protected BiFunction<S, String, Boolean> isValidInput = (p, mes) -> true;

    @NotNull
    protected BiFunction<S, String, T> setValue = (p, mes) -> this.value;

    @NotNull
    protected BiConsumer<S, T> onFinish = (p, val) -> {
    };

    @NotNull
    protected Consumer<S> onCancel = p -> {
    };

    @NotNull
    protected Consumer<S> onExpire = p -> {
    };

    @Nullable
    protected String invalidInputMessage = "That is not a valid input";

    @Nullable
    protected String sendValueMessage = "Send in the chat the value";

    protected long expire = -1L;

    protected boolean repeat = true;

    @NotNull
    protected String cancel = "cancel";

    @Nullable
    protected T value;

    public ChatInputBuilder(@NotNull final ChatInputPlugin<X, L> plugin, @NotNull final S sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    @NotNull
    public ChatInputBuilder<T, P, S, X, L> expire(final long expire) {
        this.expire = expire;
        return this;
    }

    /**
     * Message to be sent to the player when the input is invalid
     *
     * @param message The message
     * @return the builder.
     */
    @NotNull
    public ChatInputBuilder<T, P, S, X, L> invalidInputMessage(@Nullable final String message) {
        this.invalidInputMessage = message;
        return this;
    }

    /**
     * Message to be sent to the player when asking for the input
     *
     * @param message The message
     * @return the builder.
     */
    @NotNull
    public ChatInputBuilder<T, P, S, X, L> sendValueMessage(@Nullable final String message) {
        this.sendValueMessage = message;
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
    public ChatInputBuilder<T, P, S, X, L> toCancel(@NotNull final String cancel) {
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
    public ChatInputBuilder<T, P, S, X, L> defaultValue(@Nullable final T def) {
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
    public ChatInputBuilder<T, P, S, X, L> repeat(final boolean repeat) {
        this.repeat = repeat;
        return this;
    }

    @NotNull
    public ChatInputBuilder<T, P, S, X, L> onExpire(@NotNull final Consumer<S> onExpire) {
        this.onExpire = onExpire;
        return this;
    }

    /**
     * Sets the code that will be executed if the player send an invalid input
     *
     * @param onInvalidInput A {@link BiFunction} with the code
     * to be executed <br>
     * If this returns true, the message set with the
     * {@link #invalidInputMessage(String)} will be sent to the player
     * @return the builder.
     */
    @NotNull
    public ChatInputBuilder<T, P, S, X, L> onInvalidInput(@NotNull final BiFunction<S, String, Boolean> onInvalidInput) {
        this.onInvalidInput = onInvalidInput;
        return this;
    }

    /**
     * Checks if the given input is valid
     *
     * @param isValidInput A {@link BiFunction BiFunction} with the code
     * to be executed <br>
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
    public ChatInputBuilder<T, P, S, X, L> isValidInput(@NotNull final BiFunction<S, String, Boolean> isValidInput) {
        this.isValidInput = isValidInput;
        return this;
    }

    /**
     * Code to be executed when the player inputs a valid string and the casting is
     * successful
     *
     * @param onFinish A {@link BiFunction BiFunction} with the code
     * to be executed when the player inputs a valid string
     * @return the builder.
     */
    @NotNull
    public ChatInputBuilder<T, P, S, X, L> onFinish(@NotNull final BiConsumer<S, T> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    /**
     * Code to be executed when the player sends as input what has been previously
     * set<br>
     * with the {{@link #toCancel(String)}} method
     *
     * @param onCancel A {@link BiFunction BiFunction} with the code
     * to be executed when the player cancels the input operation
     * @return the builder.
     */
    @NotNull
    public ChatInputBuilder<T, P, S, X, L> onCancel(@NotNull final Consumer<S> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    /**
     * Sets the value. Since the {@link ChatInput} is a generic class, it
     * doesn't know how to convert the<br>
     * string input to the correct variable type. Because of this, we must provide
     * the code to do the cast
     *
     * @param setValue A {@link BiFunction BiFunction} with the code
     * to be executed to cast the string input to the correct type
     * @return the builder.
     */
    @NotNull
    public ChatInputBuilder<T, P, S, X, L> setValue(@NotNull final BiFunction<S, String, T> setValue) {
        this.setValue = setValue;
        return this;
    }

    @NotNull
    public abstract ChatInput<T, X, L> build();

}
