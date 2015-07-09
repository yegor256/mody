/**
 * Copyright (c) 2015, Yegor Bugayenko
 * All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.yegor256.mody;

import java.io.IOException;

/**
 * Safe Questions.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 */
final class SafeQuestions implements Questions {

    /**
     * Origin.
     */
    private final transient Questions origin;

    /**
     * Ctor.
     * @param qtn Origin
     */
    SafeQuestions(final Questions qtn) {
        this.origin = qtn;
    }

    @Override
    public Iterable<String> pending() throws IOException {
        return this.origin.pending();
    }

    @Override
    public String put(final String coords, final String text)
        throws IOException {
        if (coords.isEmpty()) {
            throw new IllegalArgumentException("coords can't be empty text");
        }
        if (!SafeQuestions.isValid(coords)) {
            throw new IllegalArgumentException(
                String.format("invalid coord '%s'", coords)
            );
        }
        if (text.isEmpty()) {
            throw new IllegalArgumentException("question text can't be empty");
        }
        return this.origin.put(coords, text);
    }

    @Override
    public void answer(final String coords, final String text)
        throws IOException {
        if (coords.isEmpty()) {
            throw new IllegalArgumentException("coords can't be empty");
        }
        if (!SafeQuestions.isValid(coords)) {
            throw new IllegalArgumentException(
                String.format("invalid coords '%s'", coords)
            );
        }
        if (text.isEmpty()) {
            throw new IllegalArgumentException("answer text can't be empty");
        }
        this.origin.answer(coords, text);
    }

    /**
     * Validate coords and throw if failed.
     * @param coords The coords
     * @return TRUE if valid
     */
    private static boolean isValid(final String coords) {
        return coords.matches("[0-9a-zA-Z\\-/]+");
    }

}
