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

import com.jcabi.aspects.Cacheable;
import com.jcabi.dynamo.Credentials;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.retry.ReRegion;
import com.jcabi.log.Logger;
import com.jcabi.manifests.Manifests;
import java.io.IOException;
import org.takes.http.Exit;
import org.takes.http.FtCLI;

/**
 * Command line entry.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class Entry {

    /**
     * Main entry point.
     * @param args Arguments
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        final Questions questions = new DyQuestions(Entry.dynamo());
        new FtCLI(
            new TkApp(questions),
            args
        ).start(Exit.NEVER);
    }

    /**
     * Dynamo DB region.
     * @return Region
     */
    @Cacheable(forever = true)
    private static Region dynamo() {
        final String key = Manifests.read("Mody-DynamoKey");
        Credentials creds = new Credentials.Simple(
            key,
            Manifests.read("Mody-DynamoSecret")
        );
        if (key.startsWith("AAAAA")) {
            final int port = Integer.parseInt(
                System.getProperty("dynamo.port")
            );
            creds = new Credentials.Direct(creds, port);
            Logger.warn(Entry.class, "test DynamoDB at port #%d", port);
        }
        Logger.info(Entry.class, "DynamoDB connected as %s", key);
        return new Region.Prefixed(
            new ReRegion(new Region.Simple(creds)), "rt-"
        );
    }

}
