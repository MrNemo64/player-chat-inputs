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
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.plugin.PluginManager;
import java.lang.reflect.Field;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public final class NkktChatEventTest {

    private static final Player player = Mockito.mock(Player.class);

    private static final PluginManager pluginManager = Mockito.mock(PluginManager.class);

    private static final Server server = Mockito.mock(Server.class);

    private static PlayerChatEvent event;

    private final NkktChatEvent bkktChatEvent = new NkktChatEvent(NkktChatEventTest.event);

    @BeforeAll
    static void prepare() {
        try {
            final Field instance = Server.class.getDeclaredField("instance");
            final boolean acc = instance.isAccessible();
            instance.setAccessible(true);
            instance.set(null, NkktChatEventTest.server);
            instance.setAccessible(acc);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        Mockito.when(NkktChatEventTest.server.getPluginManager())
            .thenReturn(NkktChatEventTest.pluginManager);
        Mockito.doAnswer(invocationOnMock -> new HashSet<>())
            .when(NkktChatEventTest.pluginManager)
            .getPermissionSubscriptions(ArgumentMatchers.anyString());
        NkktChatEventTest.event =
            new PlayerChatEvent(NkktChatEventTest.player, "Test message");
    }

    @Test
    void testCancel() {
        this.bkktChatEvent.cancel();
        Assertions.assertTrue(NkktChatEventTest.event.isCancelled(), "The chat event couldn't be cancelled!");
    }

    @Test
    void testMessage() {
        Assertions.assertEquals("Test message", this.bkktChatEvent.message(), "The chat event's message is not the `Test message`!");
    }

    @Test
    void testSender() {
        Assertions.assertEquals(NkktChatEventTest.player, this.bkktChatEvent.sender().get(), "The chat event's sender is not the #player!");
    }

}