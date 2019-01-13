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
import com.squareup.javapoet.MethodSpec;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javapoet.method.TypeBasedMethodFactory;
import org.rookit.auto.javax.ExtendedTypeMirror;
import org.rookit.auto.javax.element.ExtendedTypeElement;
import org.rookit.auto.javax.property.ExtendedProperty;
import org.rookit.auto.javax.property.PropertyAdapter;

import java.util.stream.Stream;

abstract class AbstractFilterOptionalMethodFactory implements TypeBasedMethodFactory {

    private final MethodSpecFactory methodSpecFactory;
    private final Provider<MethodFactory> methodFactory;
    private final PropertyAdapter propertyAdapter;

    AbstractFilterOptionalMethodFactory(final MethodSpecFactory methodSpecFactory,
                                        final Provider<MethodFactory> methodFactory,
                                        final PropertyAdapter propertyAdapter) {
        this.methodSpecFactory = methodSpecFactory;
        this.methodFactory = methodFactory;
        this.propertyAdapter = propertyAdapter;
    }

    @Override
    public Stream<MethodSpec> create(final ExtendedTypeElement owner, final ExtendedProperty property) {
        final String propertyName = property.name();
        final ExtendedTypeMirror returnType = unwrapOptional(property.type());

        return Stream.concat(Stream.of(getNoneMethodSpec(propertyName), getSomeMethodSpec(propertyName)),
                getGenericMethodSpec(owner, property, returnType));
    }

    abstract ExtendedTypeMirror unwrapOptional(ExtendedTypeMirror optional);

    private MethodSpec getNoneMethodSpec(final String propertyName) {
        return this.methodSpecFactory.create(propertyName, "no");
    }

    private MethodSpec getSomeMethodSpec(final String propertyName) {
        return this.methodSpecFactory.create(propertyName, "any");
    }

    private Stream<MethodSpec> getGenericMethodSpec(final ExtendedTypeElement owner,
                                                    final ExtendedProperty property,
                                                    final ExtendedTypeMirror unwrappedType) {
        final ExtendedProperty unwrappedProperty = this.propertyAdapter.changeReturnType(property, unwrappedType);
        final MethodFactory methodFactory = this.methodFactory.get();

        return methodFactory.isCompatible(unwrappedProperty) ? methodFactory.create(owner, unwrappedProperty)
                : Stream.empty();
    }

    @Override
    public String toString() {
        return "AbstractFilterOptionalMethodFactory{" +
                "methodSpecFactory=" + this.methodSpecFactory +
                ", methodFactory=" + this.methodFactory +
                ", propertyAdapter=" + this.propertyAdapter +
                "} " + super.toString();
    }
}
