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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.type.AbstractInterfaceTypeSourceFactory;
import org.rookit.auto.javapoet.type.TypeSourceAdapter;
import org.rookit.auto.javax.element.ExtendedTypeElement;
import org.rookit.storage.utils.ElementQuery;
import org.rookit.storage.utils.PartialQuery;
import org.rookit.storage.utils.Query;
import org.rookit.storage.utils.filter.PartialFilter;

import javax.lang.model.element.Modifier;
import java.util.Collection;

//TODO this class is too similar to UpdateFilterPartialTypeSourceFactory
final class QueryPartialTypeSourceFactory extends AbstractInterfaceTypeSourceFactory {

    private final JavaPoetParameterResolver filterParameterResolver;
    private final JavaPoetNamingFactory namingFactory;
    private final TypeName queryTypeName;

    @Inject
    private QueryPartialTypeSourceFactory(@PartialQuery final JavaPoetParameterResolver parameterResolver,
                                          final TypeSourceAdapter adapter,
                                          @PartialFilter final JavaPoetParameterResolver fParameterResolver,
                                          @Query final JavaPoetNamingFactory namingFactory,
                                          @ElementQuery final TypeVariableName typeVariableName) {
        super(parameterResolver, adapter);
        this.filterParameterResolver = fParameterResolver;
        this.namingFactory = namingFactory;
        this.queryTypeName = ParameterizedTypeName.get(
                ClassName.get(org.rookit.storage.query.Query.class),
                typeVariableName
        );
    }

    @Override
    public Collection<MethodSpec> methodsFor(final ExtendedTypeElement element) {
        return element.upstreamEntity()
                .map(this::adapterMethod)
                .map(ImmutableSet::of)
                .orElse(ImmutableSet.of());
    }

    private MethodSpec adapterMethod(final ExtendedTypeElement element) {
        final ClassName className = this.namingFactory.classNameFor(element);
        return MethodSpec.methodBuilder("to" + className.simpleName())
                .returns(TypeVariableName.get(className.toString()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .build();
    }

    @Override
    public Collection<TypeName> parentNamesOf(final ExtendedTypeElement baseElement) {
        final ImmutableSet.Builder<TypeName> builder = ImmutableSet.<TypeName>builder()
                .addAll(super.parentNamesOf(baseElement))
                .add(this.filterParameterResolver.resolveParameters(baseElement));

        if (baseElement.isTopLevel()) {
            builder.add(this.queryTypeName);
        }

        return builder.build();
    }

    @Override
    public String toString() {
        return "QueryPartialTypeSourceFactory{" +
                "filterParameterResolver=" + this.filterParameterResolver +
                ", namingFactory=" + this.namingFactory +
                ", queryTypeName=" + this.queryTypeName +
                "} " + super.toString();
    }
}
