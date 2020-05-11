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

import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BkktSenderTest {

    private static final UUID uuid = UUID.randomUUID();

    private static final Player player = Mockito.mock(Player.class);

    private final BkktSender sender = new BkktSender(BkktSenderTest.player);

    private final String isSent = "not-sent";

    @BeforeAll
    static void prepare() {
        Mockito.when(BkktSenderTest.player.getUniqueId())
            .thenReturn(BkktSenderTest.uuid);
    }

    @Test
    void getUniqueId() {
        Assertions.assertTrue(this.sender.getUniqueId().equals(BkktSenderTest.player.getUniqueId()), "The uuid is not equal with the same player!");
    }

    @Test
    void sendMessage() {
        this.sender.
    }

    @Test
    void get() {
    }

}