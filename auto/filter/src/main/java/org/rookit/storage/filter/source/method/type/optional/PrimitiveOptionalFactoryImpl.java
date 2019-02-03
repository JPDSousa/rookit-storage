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

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javapoet.method.TypeBasedMethodFactory;
import org.rookit.auto.javax.ExtendedTypeMirror;
import org.rookit.auto.javax.ExtendedTypeMirrorFactory;
import org.rookit.auto.javax.property.PropertyAdapter;
import org.rookit.storage.guice.TopFilter;
import org.rookit.storage.guice.filter.PartialFilter;

import javax.lang.model.type.TypeKind;

final class PrimitiveOptionalFactoryImpl implements PrimitiveOptionalFactory {

    private final MethodSpecFactory methodSpecFactory;
    private final Provider<MethodFactory> methodFactory;
    private final PropertyAdapter propertyAdapter;
    private final ExtendedTypeMirrorFactory typeMirrorFactory;

    @Inject
    private PrimitiveOptionalFactoryImpl(@PartialFilter final MethodSpecFactory methodSpecFactory,
                                         @TopFilter final Provider<MethodFactory> methodFactory,
                                         final PropertyAdapter propertyAdapter,
                                         final ExtendedTypeMirrorFactory typeMirrorFactory) {
        this.methodSpecFactory = methodSpecFactory;
        this.methodFactory = methodFactory;
        this.propertyAdapter = propertyAdapter;
        this.typeMirrorFactory = typeMirrorFactory;
    }

    @Override
    public TypeBasedMethodFactory create(final ExtendedTypeMirror optionalType, final TypeKind primitiveType) {
        return new OptionalPrimitiveMethodFactory(
                optionalType,
                primitiveType,
                this.methodSpecFactory,
                this.methodFactory,
                this.propertyAdapter,
                this.typeMirrorFactory);
    }

    @Override
    public String toString() {
        return "PrimitiveOptionalFactoryImpl{" +
                "methodSpecFactory=" + this.methodSpecFactory +
                ", methodFactory=" + this.methodFactory +
                ", propertyAdapter=" + this.propertyAdapter +
                ", typeMirrorFactory=" + this.typeMirrorFactory +
                "}";
    }
}
