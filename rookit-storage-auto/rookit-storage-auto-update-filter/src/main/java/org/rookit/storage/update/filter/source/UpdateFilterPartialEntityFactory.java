/*******************************************************************************
 * Copyright (C) 2018 Joao Sousa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.rookit.storage.update.filter.source;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import one.util.streamex.StreamEx;
import org.rookit.auto.entity.AbstractPartialEntityFactory;
import org.rookit.auto.entity.PartialEntity;
import org.rookit.auto.entity.PartialEntityFactory;
import org.rookit.auto.identifier.EntityIdentifierFactory;
import org.rookit.auto.javax.element.ExtendedTypeElement;
import org.rookit.auto.source.SingleTypeSourceFactory;
import org.rookit.storage.utils.PartialUpdateFilter;
import org.rookit.storage.utils.filter.PartialFilter;
import org.rookit.utils.optional.OptionalFactory;

//TODO this class is really similar to QueryPartialEntityFactory
final class UpdateFilterPartialEntityFactory extends AbstractPartialEntityFactory {

    private final PartialEntityFactory filterFactory;

    @Inject
    private UpdateFilterPartialEntityFactory(@PartialUpdateFilter final EntityIdentifierFactory identifierFactory,
                                             @PartialUpdateFilter final SingleTypeSourceFactory typeSourceFactory,
                                             @PartialFilter final PartialEntityFactory filterFactory,
                                             final OptionalFactory optionalFactory) {
        super(identifierFactory, typeSourceFactory, optionalFactory);
        this.filterFactory = filterFactory;
    }

    @Override
    protected StreamEx<PartialEntity> entitiesFor(final ExtendedTypeElement parent) {
        return parent.child()
                .map(child -> StreamEx.of(this.filterFactory.create(child), create(parent)))
                .orElseGet(() -> StreamEx.of(create(parent)));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("filterFactory", this.filterFactory)
                .toString();
    }
}
