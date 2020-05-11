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
import io.github.nemo_64.chatinput.Sender;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class NkktSender implements Sender<Player> {

    @NotNull
    private final Player player;

    public NkktSender(@NotNull final Player player) {
        this.player = player;
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public void sendMessage(@NotNull final String message) {
        this.player.sendMessage(message);
    }

    @NotNull
    @Override
    public Player get() {
        return this.player;
    }

}
