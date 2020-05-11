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

import io.github.nemo_64.chatinput.Task;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public final class BukkitChatInputTest {

    public static final UUID PLAYER_UUID = UUID.randomUUID();

    private static final Plugin PLUGIN = Mockito.mock(Plugin.class);

    private static final Player PLAYER = Mockito.mock(Player.class);

    private static final BukkitChatInput<Integer> CHAT_INPUT = BukkitChatInputBuilder.integer(BukkitChatInputTest.PLUGIN, BukkitChatInputTest.PLAYER)
        .build();

    private final BukkitTask bukkitTask = Mockito.mock(BukkitTask.class);

    @BeforeAll
    static void prepare() {
        new HandlerList();
        Mockito.when(BukkitChatInputTest.PLAYER.getUniqueId())
            .thenReturn(BukkitChatInputTest.PLAYER_UUID);
    }

    @Test
    void createTask() {
        final Task<BukkitTask> task = BukkitChatInputTest.CHAT_INPUT.createTask(this.bukkitTask);
    }

    @Test
    void whenQuit() {
        final PlayerQuitEvent event = new PlayerQuitEvent(BukkitChatInputTest.PLAYER, "Quit Message");
        BukkitChatInputTest.CHAT_INPUT.whenQuit(event);
    }

    @Test
    void whenChat() {
        final AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, BukkitChatInputTest.PLAYER,
            "Test message", new HashSet<>());
        BukkitChatInputTest.CHAT_INPUT.whenChat(event);
    }

    @Test
    void get() {
        Assertions.assertEquals(BukkitChatInputTest.CHAT_INPUT, BukkitChatInputTest.CHAT_INPUT.get(), "The get method in ChatInput not giving the correct object!");
    }

    @Test
    void unregisterListeners() {
        BukkitChatInputTest.CHAT_INPUT.unregisterListeners();
    }

}