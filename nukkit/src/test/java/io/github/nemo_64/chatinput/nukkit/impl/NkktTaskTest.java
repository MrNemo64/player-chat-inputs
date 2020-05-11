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

import cn.nukkit.scheduler.TaskHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

final class NkktTaskTest {

    private static final TaskHandler task = Mockito.mock(TaskHandler.class);

    private static boolean cancelled = false;

    private final NkktTask bkktTask = new NkktTask(NkktTaskTest.task);

    @BeforeAll
    static void prepare() {
        Mockito.when(NkktTaskTest.task.isCancelled())
            .thenReturn(true);
        Mockito.doAnswer(invocationOnMock -> {
            NkktTaskTest.cancelled = true;
            return null;
        }).when(NkktTaskTest.task)
            .cancel();
    }

    @Test
    void isCancelled() {
        Assertions.assertTrue(this.bkktTask.isCancelled(), "The task not cancelled!");
    }

    @Test
    void cancel() {
        this.bkktTask.cancel();
        Assertions.assertTrue(NkktTaskTest.cancelled, "The task couldn't be cancelled!");
    }

}