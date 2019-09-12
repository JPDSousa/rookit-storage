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
package org.rookit.storage.update.source;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javax.type.ExtendedTypeElement;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.guice.PartialUpdateFilter;

final class PartialUpdateParameterResolver implements JavaPoetParameterResolver {

    private final TypeVariableName updateType;
    private final JavaPoetNamingFactory updateFactory;

    private final TypeVariableName updateFilterType;
    private final JavaPoetNamingFactory updateFilterFactory;

    @Inject
    private PartialUpdateParameterResolver(@PartialUpdate final TypeVariableName updateType,
                                           @PartialUpdate final JavaPoetNamingFactory updateFactory,
                                           @PartialUpdateFilter final TypeVariableName updateFilterType,
                                           @PartialUpdateFilter final JavaPoetNamingFactory updateFilterFactory) {
        this.updateType = updateType;
        this.updateFactory = updateFactory;
        this.updateFilterType = updateFilterType;
        this.updateFilterFactory = updateFilterFactory;
    }

    @Override
    public TypeName resolveParameters(final ExtendedTypeElement element, final TypeVariableName... typeVariables) {
        final ClassName className = this.updateFactory.classNameFor(element);
        final TypeVariableName firstType = (typeVariables.length > 0) ? typeVariables[0] : this.updateType;
        final TypeVariableName secondType = (typeVariables.length > 1) ? typeVariables[1] : this.updateFilterType;

        return ParameterizedTypeName.get(className, firstType, secondType);
    }

    @Override
    public Iterable<TypeVariableName> createParameters(final ExtendedTypeElement element) {
        return ImmutableSet.of(updateType(element), updateFilterType(element));
    }

    private TypeVariableName updateType(final ExtendedTypeElement element) {
        final ClassName className = this.updateFactory.classNameFor(element);
        final ParameterizedTypeName parameterized = ParameterizedTypeName.get(className,
                this.updateType,
                this.updateFilterType);
        return TypeVariableName.get(this.updateType.name, parameterized);
    }

    private TypeVariableName updateFilterType(final ExtendedTypeElement element) {
        final ClassName className = this.updateFilterFactory.classNameFor(element);
        final ParameterizedTypeName parameterized = ParameterizedTypeName.get(className, this.updateFilterType);

        return TypeVariableName.get(this.updateFilterType.name, parameterized);
    }

    @Override
    public String toString() {
        return "PartialUpdateParameterResolver{" +
                "updateType=" + this.updateType +
                ", updateFactory=" + this.updateFactory +
                ", updateFilterType=" + this.updateFilterType +
                ", updateFilterFactory=" + this.updateFilterFactory +
                "}";
    }
}
