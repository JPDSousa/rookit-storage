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
package org.rookit.storage.query.source;

import com.google.inject.Inject;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javax.type.ExtendedTypeElement;
import org.rookit.storage.guice.filter.PartialFilter;

final class QueryFilterJavaPoetPartialParameterResolver implements JavaPoetParameterResolver {

    private final JavaPoetParameterResolver fallbackFactory;
    private final JavaPoetNamingFactory filterNamingFactory;
    private final TypeVariableName filterType;

    @Inject
    private QueryFilterJavaPoetPartialParameterResolver(@PartialFilter final JavaPoetParameterResolver fallbackFactory,
                                                        @PartialFilter final JavaPoetNamingFactory filterNamingFactory,
                                                        @PartialFilter final TypeVariableName filterType) {
        this.fallbackFactory = fallbackFactory;
        this.filterNamingFactory = filterNamingFactory;
        this.filterType = filterType;
    }

    @Override
    public TypeName resolveParameters(final ExtendedTypeElement element, final TypeVariableName... typeVariables) {
        final TypeVariableName typeVariable = (typeVariables.length > 0) ? typeVariables[0] : this.filterType;
        return ParameterizedTypeName.get(this.filterNamingFactory.classNameFor(element), typeVariable);
    }

    @Override
    public Iterable<TypeVariableName> createParameters(final ExtendedTypeElement element) {
        return this.fallbackFactory.createParameters(element);
    }

    @Override
    public String toString() {
        return "QueryFilterJavaPoetPartialParameterResolver{" +
                "fallbackFactory=" + this.fallbackFactory +
                ", filterNamingFactory=" + this.filterNamingFactory +
                ", filterType=" + this.filterType +
                "}";
    }
}
