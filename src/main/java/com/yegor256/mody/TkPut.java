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
import java.net.HttpURLConnection;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqForm;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Put new question.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 */
final class TkPut implements Take {

    /**
     * Questions.
     */
    private final transient Questions questions;

    /**
     * Ctor.
     * @param qtns Questions
     */
    TkPut(final Questions qtns) {
        this.questions = qtns;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final RqForm.Smart form = new RqForm.Smart(new RqForm.Base(req));
        final String answer = this.questions.put(
            String.format(
                "%s-%s-%s",
                form.single("repo"),
                form.single("issue"),
                form.single("comment")
            ),
            form.single("text")
        );
        final Response rsp;
        if (answer.isEmpty()) {
            rsp = new RsWithStatus(HttpURLConnection.HTTP_NO_CONTENT);
        } else {
            rsp = new RsText(answer);
        }
        return rsp;
    }

}
