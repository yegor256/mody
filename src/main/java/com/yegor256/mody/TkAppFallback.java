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

import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fallback.Fallback;
import org.takes.facets.fallback.FbChain;
import org.takes.facets.fallback.FbStatus;
import org.takes.facets.fallback.RqFallback;
import org.takes.facets.fallback.TkFallback;
import org.takes.misc.Opt;
import org.takes.rs.RsText;
import org.takes.rs.RsVelocity;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;
import org.takes.tk.TkWrap;

/**
 * App with fallback.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class TkAppFallback extends TkWrap {

    /**
     * Revision of yegor256.
     */
    private static final String REV = Manifests.read("Mody-Revision");

    /**
     * Ctor.
     * @param take Take
     */
    TkAppFallback(final Take take) {
        super(TkAppFallback.make(take));
    }

    /**
     * Authenticated.
     * @param take Takes
     * @return Authenticated takes
     */
    private static Take make(final Take take) {
        return new TkFallback(
            take,
            new FbChain(
                new FbStatus(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    new RsWithStatus(
                        new RsText("page not found"),
                        HttpURLConnection.HTTP_NOT_FOUND
                    )
                ),
                new FbStatus(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    new RsWithStatus(
                        new RsText("invalid request"),
                        HttpURLConnection.HTTP_BAD_REQUEST
                    )
                ),
                new Fallback() {
                    @Override
                    public Opt<Response> route(final RqFallback req)
                        throws IOException {
                        return new Opt.Single<>(TkAppFallback.fatal(req));
                    }
                }
            )
        );
    }

    /**
     * Make fatal error page.
     * @param req Request
     * @return Response
     * @throws IOException If fails
     */
    private static Response fatal(final RqFallback req) throws IOException {
        return new RsWithStatus(
            new RsWithType(
                new RsVelocity(
                    TkAppFallback.class.getResource("error.html.vm"),
                    new RsVelocity.Pair(
                        "err",
                        ExceptionUtils.getStackTrace(req.throwable())
                    ),
                    new RsVelocity.Pair("rev", TkAppFallback.REV)
                ),
                "text/html"
            ),
            HttpURLConnection.HTTP_INTERNAL_ERROR
        );
    }

}
