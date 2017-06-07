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

import com.google.common.collect.Iterables;
import com.jcabi.aspects.Tv;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithType;

/**
 * Favicon.ico.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.5
 */
final class TkFavicon implements Take {

    /**
     * Questions.
     */
    private final transient Questions questions;

    /**
     * Ctor.
     * @param qtns Questions
     */
    TkFavicon(final Questions qtns) {
        this.questions = qtns;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final int pending = Iterables.size(this.questions.pending());
        final int width = 64;
        final int height = 64;
        final BufferedImage image = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_RGB
        );
        final Graphics graph = image.getGraphics();
        graph.setColor(Color.WHITE);
        graph.fillRect(0, 0, width, height);
        if (pending > 0) {
            final String text;
            if (pending >= Tv.HUNDRED) {
                text = "99";
            } else {
                text = Integer.toString(pending);
            }
            graph.setColor(Color.BLACK);
            graph.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height / 2));
            graph.drawString(
                text,
                width - width / Tv.TEN
                    - graph.getFontMetrics().stringWidth(text),
                height - height / Tv.TEN
            );
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", baos);
        return new RsWithType(
            new RsWithBody(baos.toByteArray()),
            "image/gif"
        );
    }

}
