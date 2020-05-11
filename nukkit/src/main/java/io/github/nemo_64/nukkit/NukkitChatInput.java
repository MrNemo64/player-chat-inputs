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
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.TaskHandler;
import io.github.nemo_64.chatinput.CiPlugin;
import io.github.nemo_64.chatinput.PlayerChatInput;
import io.github.nemo_64.chatinput.Task;
import io.github.nemo_64.nukkit.impl.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NukkitChatInput<T> extends PlayerChatInput<T, Player, NkktSender, TaskHandler, NkktChatEvent,
    NkktQuitEvent, Listener> implements Listener {

    public NukkitChatInput(@NotNull final CiPlugin<TaskHandler, Listener> ciPlugin,
                           @NotNull final NkktSender sender, @Nullable final T startOn,
                           @Nullable final String invalidInputMessage, @Nullable final String sendValueMessage,
                           @NotNull final BiFunction<NkktSender, String, Boolean> isValidInput,
                           @NotNull final BiFunction<NkktSender, String, T> setValue,
                           @NotNull final BiConsumer<NkktSender, T> onFinish,
                           @NotNull final Consumer<NkktSender> onCancel, @NotNull final String cancel,
                           @NotNull final BiFunction<NkktSender, String, Boolean> onInvalidInput,
                           final boolean repeat, @NotNull final Consumer<NkktSender> onExpire, final long expire) {
        super(ciPlugin, sender, startOn, invalidInputMessage, sendValueMessage,
            isValidInput, setValue, onFinish, onCancel, cancel, onInvalidInput, repeat, onExpire, expire);
    }

    public NukkitChatInput(@NotNull final Plugin plugin, @NotNull final Player sender, @Nullable final T startOn,
                           @Nullable final String invalidInputMessage, @Nullable final String sendValueMessage,
                           @NotNull final BiFunction<Player, String, Boolean> isValidInput,
                           @NotNull final BiFunction<Player, String, T> setValue,
                           @NotNull final BiConsumer<Player, T> onFinish, @NotNull final Consumer<Player> onCancel,
                           @NotNull final String cancel,
                           @NotNull final BiFunction<Player, String, Boolean> onInvalidInput, final boolean repeat,
                           @NotNull final Consumer<Player> onExpire, final long expire) {
        this(new NkktPlugin(plugin), new NkktSender(sender), startOn, invalidInputMessage, sendValueMessage,
            (bukkitSender, s) -> isValidInput.apply(bukkitSender.get(), s),
            (bukkitSender, s) -> setValue.apply(bukkitSender.get(), s),
            (bukkitSender, t) -> onFinish.accept(bukkitSender.get(), t),
            bukkitSender -> onCancel.accept(bukkitSender.get()), cancel,
            (bukkitSender, s) -> onInvalidInput.apply(bukkitSender.get(), s), repeat,
            bukkitSender -> onExpire.accept(bukkitSender.get()), expire);
    }

    @NotNull
    @Override
    public Task<TaskHandler> createTask(@NotNull final TaskHandler task) {
        return new NkktTask(task);
    }

    @EventHandler
    public void whenQuit(final PlayerQuitEvent event) {
        this.onQuit(new NkktQuitEvent(event));
    }

    @EventHandler
    public void whenChat(final PlayerChatEvent event) {
        this.onChat(new NkktChatEvent(event));
    }

    @NotNull
    @Override
    public NukkitChatInput<T> get() {
        return this;
    }

    @Override
    public void unregisterListeners() {
        HandlerList.unregisterAll(this);
    }

}
