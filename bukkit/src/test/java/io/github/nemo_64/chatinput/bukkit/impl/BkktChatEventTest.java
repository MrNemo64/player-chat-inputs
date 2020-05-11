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

import java.util.HashSet;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

final class BkktChatEventTest {

    private final Player player = Mockito.mock(Player.class);

    private final AsyncPlayerChatEvent event =
        new AsyncPlayerChatEvent(true, this.player, "Test message", new HashSet<>());

    private final BkktChatEvent bkktChatEvent = new BkktChatEvent(this.event);

    @Test
    void testCancel() {
        this.bkktChatEvent.cancel();
        Assertions.assertTrue(this.event.isCancelled(), "The chat event couldn't be cancelled!");
    }

    @Test
    void testMessage() {
        Assertions.assertEquals("Test message", this.bkktChatEvent.message(), "The chat event's message is not the `Test message`!");
    }

    @Test
    void testSender() {
        Assertions.assertEquals(this.player, this.bkktChatEvent.sender().get(), "The chat event's sender is not the #player!");
    }

}