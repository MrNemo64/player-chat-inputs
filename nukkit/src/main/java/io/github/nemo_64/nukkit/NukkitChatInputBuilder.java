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

package io.github.nemo_64.nukkit;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.TaskHandler;
import io.github.nemo_64.chatinput.ChatInputBuilder;
import io.github.nemo_64.chatinput.PlayerChatInput;
import io.github.nemo_64.nukkit.impl.NkktPlugin;
import io.github.nemo_64.nukkit.impl.NkktSender;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for the {@link PlayerChatInput} class
 *
 * @param <T> The {@link PlayerChatInput} type
 * @author Nemo_64
 */
public final class NukkitChatInputBuilder<T> extends ChatInputBuilder<T, Player, NkktSender, TaskHandler, Listener> {

    /**
     * @param plugin The main class of the plugin
     * @param player The player that will send the input
     */
    public NukkitChatInputBuilder(@NotNull final Plugin plugin, @NotNull final Player player) {
        super(new NkktPlugin(plugin), new NkktSender(player));
    }

    @NotNull
    public static <T> NukkitChatInputBuilder<T> builder(@NotNull final Plugin plugin, @NotNull final Player player) {
        return new NukkitChatInputBuilder<>(plugin, player);
    }

    /**
     * Creates the {@link PlayerChatInput}
     *
     * @return A new {@link PlayerChatInput}
     */
    @NotNull
    @Override
    public NukkitChatInput<T> build() {
        return new NukkitChatInput<>(this.ciPlugin, this.sender, this.value, this.invalidInputMessage,
            this.sendValueMessage, this.isValidInput, this.setValue, this.onFinish, this.onCancel, this.cancel,
            this.onInvalidInput, this.repeat, this.onExpire, this.expire);
    }

}