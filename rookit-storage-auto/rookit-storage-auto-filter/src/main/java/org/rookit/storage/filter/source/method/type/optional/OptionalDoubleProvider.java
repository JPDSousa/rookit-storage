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
package org.rookit.storage.filter.source.method.type.optional;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.rookit.auto.javapoet.method.TypeBasedMethodFactory;
import org.rookit.auto.javax.ExtendedTypeMirror;
import org.rookit.auto.javax.ExtendedTypeMirrorFactory;

import javax.lang.model.type.TypeKind;
import java.util.OptionalDouble;

final class OptionalDoubleProvider implements Provider<TypeBasedMethodFactory> {

    private final PrimitiveOptionalFactory factory;
    private final ExtendedTypeMirror type;
    private final TypeKind kind;

    @Inject
    private OptionalDoubleProvider(final ExtendedTypeMirrorFactory typeMirrorFactory,
                                   final PrimitiveOptionalFactory factory) {
        this.factory = factory;
        this.type = typeMirrorFactory.createWithErasure(OptionalDouble.class);
        this.kind = TypeKind.DOUBLE;
    }

    @Override
    public TypeBasedMethodFactory get() {
        return this.factory.create(this.type, this.kind);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("factory", this.factory)
                .add("type", this.type)
                .add("kind", this.kind)
                .toString();
    }
}
