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

import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.jcabi.dynamo.AttributeUpdates;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import java.io.IOException;
import java.util.Iterator;

/**
 * Questions in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 1.0
 */
final class DyQuestions implements Questions {

    /**
     * Table name.
     */
    private static final String TBL = "questions";

    /**
     * Index name.
     */
    private static final String IDX = "pending";

    /**
     * Hash.
     */
    private static final String HASH = "coords";

    /**
     * Question text.
     */
    private static final String ATTR_QUESTION = "text";

    /**
     * Answer text.
     */
    private static final String ATTR_ANSWER = "answer";

    /**
     * Counter.
     */
    private static final String ATTR_COUNT = "count";

    /**
     * Empty answer.
     */
    private static final String EMPTY = "-";

    /**
     * Region.
     */
    private final transient Region region;

    /**
     * Ctor.
     * @param reg Region
     */
    DyQuestions(final Region reg) {
        this.region = reg;
    }

    @Override
    public Iterable<String> pending() {
        return Iterables.transform(
            this.region.table(DyQuestions.TBL)
                .frame()
                .through(
                    new QueryValve()
                        .withIndexName(DyQuestions.IDX)
                        .withConsistentRead(false)
                        .withSelect(Select.ALL_ATTRIBUTES)
                )
                .where(DyQuestions.ATTR_ANSWER, DyQuestions.EMPTY),
            new Function<Item, String>() {
                @Override
                public String apply(final Item input) {
                    try {
                        return String.format(
                            "%s %s %s",
                            input.get(DyQuestions.HASH).getS(),
                            input.get(DyQuestions.ATTR_COUNT).getN(),
                            input.get(DyQuestions.ATTR_QUESTION).getS()
                        );
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
    }

    @Override
    public String put(final String coords, final String text)
        throws IOException {
        final Iterator<Item> items = this.region.table(DyQuestions.TBL)
            .frame()
            .through(new QueryValve())
            .where(DyQuestions.HASH, coords)
            .iterator();
        final Item item;
        if (items.hasNext()) {
            item = items.next();
        } else {
            item = this.region.table(DyQuestions.TBL).put(
                new Attributes()
                    .with(DyQuestions.HASH, coords)
                    .with(DyQuestions.ATTR_QUESTION, text)
                    .with(
                        DyQuestions.ATTR_COUNT,
                        new AttributeValue().withN("0")
                    )
            );
        }
        item.put(
            new AttributeUpdates().with(
                DyQuestions.ATTR_COUNT,
                new AttributeValueUpdate()
                    .withValue(new AttributeValue().withN("1"))
                    .withAction(AttributeAction.ADD)
            )
        );
        if (!item.has(DyQuestions.ATTR_ANSWER)) {
            item.put(
                DyQuestions.ATTR_ANSWER,
                new AttributeValueUpdate()
                    .withValue(new AttributeValue(DyQuestions.EMPTY))
                    .withAction(AttributeAction.PUT)
            );
        }
        String answer = item.get(DyQuestions.ATTR_ANSWER).getS();
        if (DyQuestions.EMPTY.equals(answer)) {
            answer = "";
        }
        return answer;
    }

    @Override
    public void complain(final String coords, final String text)
        throws IOException {
        final Item item = this.region.table(DyQuestions.TBL)
            .frame()
            .through(new QueryValve())
            .where(DyQuestions.HASH, coords)
            .iterator().next();
        item.put(
            DyQuestions.ATTR_ANSWER,
            new AttributeValueUpdate()
                .withValue(new AttributeValue(DyQuestions.EMPTY))
                .withAction(AttributeAction.PUT)
        );
        item.put(
            DyQuestions.ATTR_QUESTION,
            new AttributeValueUpdate().withValue(
                new AttributeValue(
                    String.format(
                        "%s<br/><br/>%s",
                        item.get(DyQuestions.ATTR_QUESTION).getS(),
                        text
                    )
                )
            ).withAction(AttributeAction.PUT)
        );
    }

    @Override
    public void answer(final String coords, final String text)
        throws IOException {
        final Item item = this.region.table(DyQuestions.TBL)
            .frame()
            .through(new QueryValve())
            .where(DyQuestions.HASH, coords)
            .iterator()
            .next();
        item.put(
            new AttributeUpdates().with(
                DyQuestions.ATTR_ANSWER,
                new AttributeValueUpdate()
                    .withValue(new AttributeValue(text))
                    .withAction(AttributeAction.PUT)
            )
        );
    }

}
