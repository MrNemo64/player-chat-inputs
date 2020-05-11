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
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

final class NkktSenderTest {

    private static final UUID uuid = UUID.randomUUID();

    private static final Player player = Mockito.mock(Player.class);

    private static String isSent = "not-sent";

    private final NkktSender sender = new NkktSender(NkktSenderTest.player);

    @BeforeAll
    static void prepare() {
        Mockito.when(NkktSenderTest.player.getUniqueId())
            .thenReturn(NkktSenderTest.uuid);
        Mockito.doAnswer(invocation -> {
            NkktSenderTest.isSent = invocation.getArgument(0);
            return null;
        }).when(NkktSenderTest.player)
            .sendMessage(ArgumentMatchers.anyString());
    }

    @Test
    void getUniqueId() {
        Assertions.assertTrue(this.sender.getUniqueId().equals(NkktSenderTest.player.getUniqueId()), "The uuid is not equal with the same player!");
    }

    @Test
    void sendMessage() {
        this.sender.sendMessage("test");
        Assertions.assertEquals("test", NkktSenderTest.isSent, "The message couldn't send to the sender!");
    }

    @Test
    void get() {
        Assertions.assertEquals(NkktSenderTest.player, this.sender.get(), "The sender's get method is not same with #player!");
    }

}