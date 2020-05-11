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

package io.github.nemo_64.chatinput.nukkit.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.ServerScheduler;
import io.github.nemo_64.chatinput.nukkit.NukkitChatInput;
import io.github.nemo_64.chatinput.nukkit.NukkitChatInputBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public final class NkktPluginTest {

    private static final Plugin plugin = Mockito.mock(Plugin.class);

    private static final Server server = Mockito.mock(Server.class);

    private static final PluginManager pluginManager = Mockito.mock(PluginManager.class);

    private static final ServerScheduler scheduler = Mockito.mock(ServerScheduler.class);

    private final Player player = Mockito.mock(Player.class);

    private final NkktPlugin bkktPlugin = new NkktPlugin(NkktPluginTest.plugin);

    private final NukkitChatInput<Integer> chatInput = NukkitChatInputBuilder.integer(NkktPluginTest.plugin, this.player)
        .build();

    private String isWorking = "not-working";

    @BeforeAll
    static void prepare() {
        Mockito.when(NkktPluginTest.plugin.getServer())
            .thenReturn(NkktPluginTest.server);
        Mockito.when(NkktPluginTest.server.getPluginManager())
            .thenReturn(NkktPluginTest.pluginManager);
        Mockito.when(NkktPluginTest.server.getScheduler())
            .thenReturn(NkktPluginTest.scheduler);
        Mockito.doAnswer(invocation -> {
            final Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(NkktPluginTest.scheduler)
            .scheduleDelayedTask(ArgumentMatchers.any(Plugin.class), ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean());
    }

    @Test
    void registerEvent() {
        this.bkktPlugin.registerEvent(this.chatInput);
    }

    @Test
    void createRunTaskLater() {
        this.bkktPlugin.createRunTaskLater(() -> this.isWorking = "working", 20L);
        Assertions.assertEquals("working", this.isWorking, "Task couldn't run!");
    }

}