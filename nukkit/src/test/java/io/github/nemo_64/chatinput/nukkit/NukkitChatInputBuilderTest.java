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
import cn.nukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

final class NukkitChatInputBuilderTest {

    private final Plugin plugin = Mockito.mock(Plugin.class);

    private final Player player = Mockito.mock(Player.class);

    @Test
    void builder() {
        final NukkitChatInputBuilder<Integer> builder = NukkitChatInputBuilder.builder(this.plugin, this.player);
    }

    @Test
    void integer() {
        final NukkitChatInputBuilder<Integer> builder = NukkitChatInputBuilder.integer(this.plugin, this.player);
    }

    @Test
    void build() {
        final NukkitChatInputBuilder<Integer> builder = new NukkitChatInputBuilder<>(this.plugin, this.player);
        final NukkitChatInput<Integer> build = builder.build();
    }

}