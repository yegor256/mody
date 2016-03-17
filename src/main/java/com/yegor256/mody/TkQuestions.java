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
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Href;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeTransform;
import org.xembly.Directives;

/**
 * Index resource, front page of the website.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.50
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class TkQuestions implements Take {

    /**
     * Questions.
     */
    private final transient Questions questions;

    /**
     * Ctor.
     * @param qtns Questions
     */
    TkQuestions(final Questions qtns) {
        this.questions = qtns;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final Href home = new RqHref.Base(req).href().path("answer");
        return new RsPage(
            "/xsl/questions.xsl",
            req,
            new XeAppend(
                "questions",
                new XeTransform<>(
                    this.questions.pending(),
                    txt -> this.source(txt, home)
                )
            ),
            new XeLink("answer", "/answer")
        );
    }

    /**
     * Convert question to source.
     * @param txt Text
     * @param home Home HREF
     * @return Source
     * @throws IOException If fails
     */
    private XeSource source(final String txt, final Href home)
        throws IOException {
        final String[] parts = txt.split("\\s", 3);
        final String coords = parts[0];
        return new XeAppend(
            "question",
            new XeDirectives(
                new Directives()
                    .add("coords").set(coords).up()
                    .add("count").set(parts[1]).up()
                    .add("text").set(parts[2]).up()
                    .add("guess")
                    .set(this.questions.guess(coords))
                    .up()
            ),
            new XeLink("answer", home)
        );
    }

}
