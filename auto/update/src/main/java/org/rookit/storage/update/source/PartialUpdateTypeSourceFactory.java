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
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.type.AbstractInterfaceTypeSourceFactory;
import org.rookit.auto.javapoet.type.TypeSourceAdapter;
import org.rookit.auto.javax.element.ExtendedTypeElement;
import org.rookit.auto.javax.property.PropertyExtractor;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.guice.PartialUpdateFilter;
import org.rookit.storage.guice.TopUpdate;
import org.rookit.storage.update.UpdateQuery;

import java.util.Collection;
import java.util.stream.Collectors;

// TODO semi copy-pasted from PropertyBasedTypeSourceFactory
final class PartialUpdateTypeSourceFactory extends AbstractInterfaceTypeSourceFactory {

    private final PropertyExtractor extractor;
    private final MethodFactory methodFactory;
    private final TypeName updateQueryTypeName;

    @Inject
    private PartialUpdateTypeSourceFactory(@PartialUpdate final JavaPoetParameterResolver parameterResolver,
                                           final TypeSourceAdapter adapter,
                                           final PropertyExtractor extractor,
                                           @TopUpdate final MethodFactory methodFactory,
                                           @PartialUpdateFilter final TypeVariableName updateFilter) {
        super(parameterResolver, adapter);
        this.extractor = extractor;
        this.methodFactory = methodFactory;
        this.updateQueryTypeName = ParameterizedTypeName.get(
                ClassName.get(UpdateQuery.class),
                updateFilter
        );
    }

    @Override
    protected Collection<MethodSpec> methodsFor(final ExtendedTypeElement element) {
        return this.extractor.fromType(element)
                .filter(this.methodFactory::isCompatible)
                .flatMap(property -> this.methodFactory.create(element, property))
                .collect(Collectors.toSet());
    }

    @Override
    protected Collection<TypeName> parentNamesOf(final ExtendedTypeElement baseElement) {
        final ImmutableSet.Builder<TypeName> builder = ImmutableSet.<TypeName>builder()
                .addAll(super.parentNamesOf(baseElement));

        if (baseElement.isTopLevel()) {
            builder.add(this.updateQueryTypeName);
        }

        return builder.build();
    }

    @Override
    public String toString() {
        return "PartialUpdateTypeSourceFactory{" +
                "extractor=" + this.extractor +
                ", methodFactory=" + this.methodFactory +
                ", updateQueryTypeName=" + this.updateQueryTypeName +
                "} " + super.toString();
    }
}
