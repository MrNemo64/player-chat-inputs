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

package io.github.nemo_64.chatinput.bukkit.impl;

import io.github.nemo_64.chatinput.bukkit.BukkitChatInput;
import io.github.nemo_64.chatinput.bukkit.BukkitChatInputBuilder;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public final class BkktPluginTest {

    private static final Plugin plugin = Mockito.mock(Plugin.class);

    private static final Server server = Mockito.mock(Server.class);

    private static final PluginManager pluginManager = Mockito.mock(PluginManager.class);

    private static final BukkitScheduler scheduler = Mockito.mock(BukkitScheduler.class);

    private final Player player = Mockito.mock(Player.class);

    private final BkktPlugin bkktPlugin = new BkktPlugin(BkktPluginTest.plugin);

    private final BukkitChatInput<Integer> chatInput = BukkitChatInputBuilder.integer(BkktPluginTest.plugin, this.player)
        .build();

    private String isWorking = "not-working";

    @BeforeAll
    static void prepare() {
        Mockito.when(BkktPluginTest.plugin.getServer())
            .thenReturn(BkktPluginTest.server);
        Mockito.when(BkktPluginTest.server.getPluginManager())
            .thenReturn(BkktPluginTest.pluginManager);
        Mockito.when(BkktPluginTest.server.getScheduler())
            .thenReturn(BkktPluginTest.scheduler);
        Mockito.doAnswer(invocation -> {
            final Runnable runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(BkktPluginTest.scheduler)
            .runTaskLater(ArgumentMatchers.any(Plugin.class), ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(long.class));
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