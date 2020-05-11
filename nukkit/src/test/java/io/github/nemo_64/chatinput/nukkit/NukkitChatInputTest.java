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

package io.github.nemo_64.chatinput.nukkit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.TaskHandler;
import io.github.nemo_64.chatinput.Task;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

final class NukkitChatInputTest {

    public static final UUID PLAYER_UUID = UUID.randomUUID();

    private static final Plugin PLUGIN = Mockito.mock(Plugin.class);

    private static final Player PLAYER = Mockito.mock(Player.class);

    private static final NukkitChatInput<Integer> CHAT_INPUT = NukkitChatInputBuilder.<Integer>builder(NukkitChatInputTest.PLUGIN, NukkitChatInputTest.PLAYER)
        .build();

    private final TaskHandler taskHandler = Mockito.mock(TaskHandler.class);

    @BeforeAll
    static void prepare() {
        new HandlerList();
        Mockito.when(NukkitChatInputTest.PLAYER.getUniqueId())
            .thenReturn(NukkitChatInputTest.PLAYER_UUID);
    }

    @Test
    void createTask() {
        final Task<TaskHandler> task = NukkitChatInputTest.CHAT_INPUT.createTask(this.taskHandler);
    }

    @Test
    void whenQuit() {
        final PlayerQuitEvent event = new PlayerQuitEvent(NukkitChatInputTest.PLAYER, "Quit Message");
        NukkitChatInputTest.CHAT_INPUT.whenQuit(event);
    }

    @Test
    void whenChat() {
        final PlayerChatEvent event = new PlayerChatEvent(NukkitChatInputTest.PLAYER, "Test message");
        NukkitChatInputTest.CHAT_INPUT.whenChat(event);
    }

    @Test
    void get() {
        Assertions.assertEquals(NukkitChatInputTest.CHAT_INPUT, NukkitChatInputTest.CHAT_INPUT.get(), "The get method in ChatInput not giving the correct object!");
    }

    @Test
    void unregisterListeners() {
        NukkitChatInputTest.CHAT_INPUT.unregisterListeners();
    }

}