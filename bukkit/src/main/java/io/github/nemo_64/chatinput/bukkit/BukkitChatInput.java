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

package io.github.nemo_64.chatinput.bukkit;

import io.github.nemo_64.chatinput.CiPlugin;
import io.github.nemo_64.chatinput.PlayerChatInput;
import io.github.nemo_64.chatinput.Task;
import io.github.nemo_64.chatinput.bukkit.impl.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BukkitChatInput<T> extends PlayerChatInput<T, BkktSender, BukkitTask, BkktChatEvent,
    BkktQuitEvent, Listener> implements Listener {

    public BukkitChatInput(@NotNull final CiPlugin<BukkitTask, Listener> ciPlugin,
                           @NotNull final BkktSender sender, @Nullable final T startOn,
                           @Nullable final String invalidInputMessage, @Nullable final String sendValueMessage,
                           @NotNull final BiFunction<BkktSender, String, Boolean> isValidInput,
                           @NotNull final BiFunction<BkktSender, String, T> setValue,
                           @NotNull final BiConsumer<BkktSender, T> onFinish,
                           @NotNull final Consumer<BkktSender> onCancel, @NotNull final String cancel,
                           @NotNull final BiFunction<BkktSender, String, Boolean> onInvalidInput,
                           final boolean repeat, @NotNull final Consumer<BkktSender> onExpire, final long expire) {
        super(ciPlugin, sender, startOn, invalidInputMessage, sendValueMessage,
            isValidInput, setValue, onFinish, onCancel, cancel, onInvalidInput, repeat, onExpire, expire);
    }

    public BukkitChatInput(@NotNull final Plugin plugin, @NotNull final Player sender, @Nullable final T startOn,
                           @Nullable final String invalidInputMessage, @Nullable final String sendValueMessage,
                           @NotNull final BiFunction<Player, String, Boolean> isValidInput,
                           @NotNull final BiFunction<Player, String, T> setValue,
                           @NotNull final BiConsumer<Player, T> onFinish, @NotNull final Consumer<Player> onCancel,
                           @NotNull final String cancel,
                           @NotNull final BiFunction<Player, String, Boolean> onInvalidInput, final boolean repeat,
                           @NotNull final Consumer<Player> onExpire, final long expire) {
        this(new BkktPlugin(plugin), new BkktSender(sender), startOn, invalidInputMessage, sendValueMessage,
            (bukkitSender, s) -> isValidInput.apply(bukkitSender.get(), s),
            (bukkitSender, s) -> setValue.apply(bukkitSender.get(), s),
            (bukkitSender, t) -> onFinish.accept(bukkitSender.get(), t),
            bukkitSender -> onCancel.accept(bukkitSender.get()), cancel,
            (bukkitSender, s) -> onInvalidInput.apply(bukkitSender.get(), s), repeat,
            bukkitSender -> onExpire.accept(bukkitSender.get()), expire);
    }

    @NotNull
    @Override
    public Task<BukkitTask> createTask(@NotNull final BukkitTask task) {
        return new BkktTask(task);
    }

    @EventHandler
    public void whenQuit(final PlayerQuitEvent event) {
        this.onQuit(new BkktQuitEvent(event));
    }

    @EventHandler
    public void whenChat(final AsyncPlayerChatEvent event) {
        this.onChat(new BkktChatEvent(event));
    }

    @NotNull
    @Override
    public BukkitChatInput<T> getListener() {
        return this;
    }

    @Override
    public void unregisterListeners() {
        HandlerList.unregisterAll(this);
    }

}
