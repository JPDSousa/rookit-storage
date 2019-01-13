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

import com.google.inject.Provider;
import com.squareup.javapoet.MethodSpec;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javapoet.method.TypeBasedMethodFactory;
import org.rookit.auto.javax.ExtendedTypeMirror;
import org.rookit.auto.javax.property.ExtendedProperty;
import org.rookit.auto.javax.property.PropertyAdapter;
import org.rookit.auto.javax.element.ExtendedTypeElement;

import java.util.stream.Stream;

abstract class AbstractUpdateOptionalMethodFactory implements TypeBasedMethodFactory {

    private final ExtendedTypeMirror type;
    private final PropertyAdapter adapter;
    private final Provider<MethodFactory> topMethodFactory;
    private final MethodSpecFactory methodSpecFactory;

    AbstractUpdateOptionalMethodFactory(final MethodSpecFactory methodSpecFactory,
                                        final Provider<MethodFactory> topMethodFactory,
                                        final PropertyAdapter adapter,
                                        final ExtendedTypeMirror type) {
        this.methodSpecFactory = methodSpecFactory;
        this.topMethodFactory = topMethodFactory;
        this.type = type;
        this.adapter = adapter;
    }

    @Override
    public ExtendedTypeMirror type() {
        return this.type;
    }

    @Override
    public Stream<MethodSpec> create(final ExtendedTypeElement owner, final ExtendedProperty property) {
        final String propertyName = property.name();

        return StreamEx.of(createUnset(propertyName))
                .append(createGeneric(owner, property));
    }

    private Stream<MethodSpec> createGeneric(final ExtendedTypeElement owner, final ExtendedProperty property) {
        final ExtendedTypeMirror type = unwrapOptional(property);

        final MethodFactory methodFactory = this.topMethodFactory.get();
        final ExtendedProperty unwrappedProperty = this.adapter.changeReturnType(property, type);
        if (methodFactory.isCompatible(unwrappedProperty)) {
            return methodFactory.create(owner, unwrappedProperty);
        }
        return Stream.empty();
    }

    protected abstract ExtendedTypeMirror unwrapOptional(ExtendedProperty property);

    private MethodSpec createUnset(final String propertyName) {
        return this.methodSpecFactory.create(propertyName, "remove");
    }

    @Override
    public String toString() {
        return "OptionalMethodFactory{" +
                "type=" + this.type +
                ", adapter=" + this.adapter +
                ", topMethodFactory=" + this.topMethodFactory +
                ", methodSpecFactory=" + this.methodSpecFactory +
                "} " + super.toString();
    }
}
