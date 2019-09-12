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
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.type.JavaPoetTypeSourceFactory;
import org.rookit.auto.javax.naming.Identifier;
import org.rookit.auto.javax.type.ExtendedTypeElement;
import org.rookit.auto.source.spec.SpecFactory;
import org.rookit.auto.source.type.SingleTypeSourceFactory;
import org.rookit.auto.source.type.TypeSource;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.ConventionTypeElementFactory;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.guice.PartialUpdateFilter;
import org.rookit.storage.guice.TopUpdate;
import org.rookit.storage.update.UpdateQuery;

import java.util.Collection;
import java.util.stream.Collectors;

// TODO semi copy-pasted from PropertyBasedTypeSourceFactory
final class PartialUpdateTypeSourceFactory implements SingleTypeSourceFactory {

    private final SpecFactory<MethodSpec> specFactory;
    private final TypeName updateQueryTypeName;
    private final JavaPoetParameterResolver parameterResolver;
    private final JavaPoetTypeSourceFactory adapter;
    private final ConventionTypeElementFactory elementFactory;

    @Inject
    private PartialUpdateTypeSourceFactory(@PartialUpdate final JavaPoetParameterResolver parameterResolver,
                                           final JavaPoetTypeSourceFactory adapter,
                                           @TopUpdate final SpecFactory<MethodSpec> specFactory,
                                           @PartialUpdateFilter final TypeVariableName updateFilter,
                                           final ConventionTypeElementFactory elementFactory) {
        this.parameterResolver = parameterResolver;
        this.adapter = adapter;
        this.specFactory = specFactory;
        this.updateQueryTypeName = ParameterizedTypeName.get(
                ClassName.get(UpdateQuery.class),
                updateFilter
        );
        this.elementFactory = elementFactory;
    }

    @Override
    public TypeSource create(final Identifier identifier, final ExtendedTypeElement element) {
        final ConventionTypeElement conventionElement = this.elementFactory.extendType(element);
        final TypeSpec spec = TypeSpec.interfaceBuilder(identifier.name())
                .addSuperinterfaces(parentNamesOf(conventionElement))
                .addMethods(methodsFor(conventionElement))
                .build();

        return this.adapter.fromTypeSpec(identifier, spec);
    }

    private Collection<MethodSpec> methodsFor(final ExtendedTypeElement element) {
        return this.specFactory.create(element)
                .collect(Collectors.toSet());
    }

    private Collection<TypeName> parentNamesOf(final ConventionTypeElement baseElement) {
        final ImmutableSet.Builder<TypeName> builder = ImmutableSet.builder();

        if (baseElement.isTopLevel()) {
            builder.add(this.updateQueryTypeName);
        }

        return builder.build();
    }

    @Override
    public String toString() {
        return "PartialUpdateTypeSourceFactory{" +
                ", methodFactory=" + this.specFactory +
                ", updateQueryTypeName=" + this.updateQueryTypeName +
                "} " + super.toString();
    }
}
