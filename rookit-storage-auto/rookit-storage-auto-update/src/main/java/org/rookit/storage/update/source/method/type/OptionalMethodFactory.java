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
package org.rookit.storage.update.source.method.type;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javax.ExtendedTypeMirror;
import org.rookit.auto.javax.ExtendedTypeMirrorFactory;
import org.rookit.auto.javax.property.ExtendedProperty;
import org.rookit.auto.javax.property.PropertyAdapter;
import org.rookit.storage.utils.PartialUpdate;
import org.rookit.storage.utils.TopUpdate;
import org.rookit.utils.repetition.Repetition;

import java.util.Optional;

final class OptionalMethodFactory extends AbstractUpdateOptionalMethodFactory {

    @Inject
    private OptionalMethodFactory(final ExtendedTypeMirrorFactory factory,
                                  final PropertyAdapter adapter,
                                  @TopUpdate final Provider<MethodFactory> topMethodFactory,
                                  @PartialUpdate final MethodSpecFactory methodSpecFactory) {
        super(methodSpecFactory, topMethodFactory, adapter, factory.createWithErasure(Optional.class));
    }

    @Override
    public boolean isCompatible(final ExtendedProperty property) {
        final Repetition repetition = property.repetition();
        return repetition.isOptional() && !repetition.isMulti();
    }

    @Override
    protected ExtendedTypeMirror unwrapOptional(final ExtendedProperty property) {
        return Iterables.getOnlyElement(property.type().typeParameters());
    }

}
