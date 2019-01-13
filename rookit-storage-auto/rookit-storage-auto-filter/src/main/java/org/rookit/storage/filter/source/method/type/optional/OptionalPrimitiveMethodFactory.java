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

import com.google.inject.Provider;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javax.ExtendedTypeMirror;
import org.rookit.auto.javax.ExtendedTypeMirrorFactory;
import org.rookit.auto.javax.property.PropertyAdapter;

import javax.lang.model.type.TypeKind;

final class OptionalPrimitiveMethodFactory extends AbstractFilterOptionalMethodFactory {

    private final ExtendedTypeMirror type;
    private final ExtendedTypeMirror typeKind;

    OptionalPrimitiveMethodFactory(final ExtendedTypeMirror type,
                                   final TypeKind primitiveType,
                                   final MethodSpecFactory methodSpecFactory,
                                   final Provider<MethodFactory> methodFactory,
                                   final PropertyAdapter propertyAdapter,
                                   final ExtendedTypeMirrorFactory typeMirrorFactory) {
        super(methodSpecFactory, methodFactory, propertyAdapter);
        this.type = type;
        this.typeKind = typeMirrorFactory.create(primitiveType);
    }

    @Override
    public ExtendedTypeMirror type() {
        return this.type;
    }

    @Override
    ExtendedTypeMirror unwrapOptional(final ExtendedTypeMirror optional) {
        return this.typeKind;
    }
}
