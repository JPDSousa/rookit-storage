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
package org.rookit.storage.filter.source.method.type;

import com.google.common.base.MoreObjects;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javapoet.method.TypeBasedMethodFactory;
import org.rookit.auto.javax.element.ExtendedTypeElement;
import org.rookit.auto.javax.property.ExtendedProperty;

import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.EMPTY;

abstract class AbstractTimeMethodFactory implements TypeBasedMethodFactory {

    private final MethodSpecFactory methodSpecFactory;

    AbstractTimeMethodFactory(final MethodSpecFactory methodSpecFactory) {
        this.methodSpecFactory = methodSpecFactory;
    }

    private TypeName typeName() {
        return TypeName.get(type());
    }

    @Override
    public Stream<MethodSpec> create(final ExtendedTypeElement owner, final ExtendedProperty property) {
        return Stream.of(getOn(property), getInterval(property, "before"),
                getInterval(property, "after"), getBetween(property));
    }

    private MethodSpec getInterval(final ExtendedProperty executableElement, final String suffix) {
        final String propertyName = executableElement.name();

        return this.methodSpecFactory.create(propertyName, EMPTY, suffix,
                ParameterSpec.builder(typeName(), propertyName).build());
    }

    private MethodSpec getOn(final ExtendedProperty executable) {
        final String propertyName = executable.name();

        return this.methodSpecFactory.create(propertyName, ParameterSpec.builder(typeName(), propertyName).build());
    }

    private MethodSpec getBetween(final ExtendedProperty executableElement) {
        final String propertyName = executableElement.name();
        return this.methodSpecFactory.create(propertyName, EMPTY, "between",
                ParameterSpec.builder(typeName(), "before").build(),
                ParameterSpec.builder(typeName(), "after").build());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("methodSpecFactory", this.methodSpecFactory)
                .toString();
    }
}
