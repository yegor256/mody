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

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.takes.Take;
import org.takes.facets.hamcrest.HmRsStatus;
import org.takes.rq.RqFake;

/**
 * Test case to ping {@link TkApp}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.6
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@RunWith(Parameterized.class)
public final class PingingTest {

    /**
     * The URL to ping.
     */
    private final transient String url;

    /**
     * Ctor.
     * @param addr The URL to test
     */
    public PingingTest(final String addr) {
        this.url = addr;
    }

    /**
     * Params for JUnit.
     * @return Parameters
     */
    @Parameterized.Parameters
    public static Collection<Object[]> params() {
        return Arrays.asList(
            new Object[][] {
                {"/?x=y"},
                {"/robots.txt"},
                {"/put"},
                {"/complain"},
                {"/answer"},
                {"/xsl/layout.xsl"},
            }
        );
    }

    /**
     * App can render the URL.
     * @throws Exception If some problem inside
     */
    @Test
    public void rendersAllPossibleUrls() throws Exception {
        final Take take = new TkApp(new FkQuestions());
        MatcherAssert.assertThat(
            this.url,
            take.act(new RqFake("INFO", this.url)),
            Matchers.not(
                new HmRsStatus(
                    HttpURLConnection.HTTP_NOT_FOUND
                )
            )
        );
    }

}
