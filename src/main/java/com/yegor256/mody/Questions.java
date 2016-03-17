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
 * Questions.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.50
 */
public interface Questions {

    /**
     * All pending now.
     * @return All un-answered questions
     * @throws IOException If fails
     */
    Iterable<String> pending() throws IOException;

    /**
     * Put new.
     * @param coords Coordinates
     * @param text Text of it
     * @return Answer or empty if not yet answered
     * @throws IOException If fails
     */
    String put(String coords, String text) throws IOException;

    /**
     * Complain about already made answer.
     * @param coords Coordinates
     * @param text Text of complaint
     * @throws IOException If fails
     * @since 0.4
     */
    void complain(String coords, String text) throws IOException;

    /**
     * Guess an answer.
     * @param coords Coordinates
     * @return Most possible answer
     * @throws IOException If fails
     */
    String guess(String coords) throws IOException;

    /**
     * Answer it.
     * @param coords Coordinates
     * @param text Text to use
     * @throws IOException If fails
     */
    void answer(String coords, String text) throws IOException;

}
