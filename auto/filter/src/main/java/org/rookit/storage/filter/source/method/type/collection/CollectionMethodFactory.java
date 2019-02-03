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
package org.rookit.storage.filter.source.method.type.collection;

import com.google.common.base.MoreObjects;
import com.google.inject.Inject;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javapoet.method.TypeBasedMethodFactory;
import org.rookit.auto.javax.ExtendedTypeMirror;
import org.rookit.auto.javax.ExtendedTypeMirrorFactory;
import org.rookit.auto.javax.element.ExtendedTypeElement;
import org.rookit.auto.javax.property.ExtendedProperty;
import org.rookit.storage.guice.filter.PartialFilter;

import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.extractSingleton;

final class CollectionMethodFactory implements TypeBasedMethodFactory {

    private final ExtendedTypeMirror type;
    private final MethodSpecFactory methodSpecFactory;

    @Inject
    private CollectionMethodFactory(final ExtendedTypeMirrorFactory factory,
                                    @PartialFilter final MethodSpecFactory methodSpecFactory) {
        this.methodSpecFactory = methodSpecFactory;
        this.type = factory.createWithErasure(Collection.class);
    }

    @Override
    public ExtendedTypeMirror type() {
        return this.type;
    }

    @Override
    public boolean isCompatible(final ExtendedProperty property) {
        return type().isSameTypeErasure(property.type());
    }

    @Override
    public Stream<MethodSpec> create(final ExtendedTypeElement owner, final ExtendedProperty property) {
        final ExtendedTypeMirror type = property.type();
        final TypeMirror unwrapped = extractSingleton(type.typeParameters());

        final String propertyName = property.name();
        return Stream.of(
                createContains(unwrapped, propertyName),
                createContains(type, propertyName),
                createNotContains(unwrapped, propertyName),
                createNotContains(type, propertyName)
        );
    }

    private MethodSpec createNotContains(final TypeMirror returnType, final String propertyName) {
        final ParameterSpec param = ParameterSpec.builder(TypeName.get(returnType), "absent").build();
        return this.methodSpecFactory.create(propertyName, "no", param);
    }

    private MethodSpec createContains(final TypeMirror returnType, final String propertyName) {
        final ParameterSpec param = ParameterSpec.builder(TypeName.get(returnType), "present").build();
        return this.methodSpecFactory.create(propertyName, param);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", this.type)
                .add("methodSpecFactory", this.methodSpecFactory)
                .toString();
    }
}
