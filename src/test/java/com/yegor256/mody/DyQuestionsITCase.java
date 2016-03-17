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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration case for {@link DyQuestions}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class DyQuestionsITCase {

    /**
     * DyQuestions can manage a question.
     * @throws Exception If some problem inside
     */
    @Test
    public void managesQuestion() throws Exception {
        final Questions questions = new DyQuestions(new Dynamo());
        final String coords = "test/test-89-32";
        questions.put(coords, "How are you?");
        questions.put(coords, "");
        MatcherAssert.assertThat(
            questions.pending(),
            Matchers.hasItem("test/test-89-32 2 How are you?")
        );
        questions.answer(coords, "I'm good, thanks!");
        MatcherAssert.assertThat(
            questions.put(coords, ""),
            Matchers.containsString("thanks!")
        );
        MatcherAssert.assertThat(
            questions.pending(),
            Matchers.not(
                Matchers.hasItem(
                    Matchers.startsWith(coords)
                )
            )
        );
    }

    /**
     * DyQuestions can manage complaints.
     * @throws Exception If some problem inside
     */
    @Test
    public void managesComplaints() throws Exception {
        final Questions questions = new DyQuestions(new Dynamo());
        final String coords = "test/test-55-32";
        questions.put(coords, "ok, let's try");
        questions.answer(coords, "let's close it");
        questions.complain(coords, "no, let's open it again");
        MatcherAssert.assertThat(
            questions.pending(),
            Matchers.hasItem(
                Matchers.startsWith(coords)
            )
        );
    }

    /**
     * DyQuestions can guess right.
     * @throws Exception If some problem inside
     */
    @Test
    @Ignore
    public void guessesRight() throws Exception {
        final Questions questions = new DyQuestions(new Dynamo());
        final String[][] base = {
            {"what is the weather today?", "it's rainy"},
            {"how is the weather?", "it's rain"},
            {"how good is the weather", "bad, it's rainy"},
            {"what is your name?", "Jeffrey"},
            {"how old are you?", "I'm 21"},
        };
        for (int idx = 0; idx < base.length; ++idx) {
            final String coords = String.format("test/t-%d", idx);
            questions.put(coords, base[idx][0]);
            questions.answer(coords, base[idx][1]);
        }
        MatcherAssert.assertThat(
            questions.guess("the weather is good?"),
            Matchers.equalTo("rain")
        );
    }

}
