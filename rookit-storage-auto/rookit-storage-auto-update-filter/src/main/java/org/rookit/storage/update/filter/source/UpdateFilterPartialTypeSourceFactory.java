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
package org.rookit.storage.update.filter.source;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.type.AbstractInterfaceTypeSourceFactory;
import org.rookit.auto.javapoet.type.TypeSourceAdapter;
import org.rookit.auto.javax.element.ExtendedTypeElement;
import org.rookit.storage.update.filter.UpdateFilterQuery;
import org.rookit.storage.utils.PartialUpdateFilter;
import org.rookit.storage.utils.filter.PartialFilter;

import java.util.Collection;

//TODO this class is too similar to QueryPartialTypeSourceFactory
final class UpdateFilterPartialTypeSourceFactory extends AbstractInterfaceTypeSourceFactory {

    private final JavaPoetParameterResolver resolver;
    private final TypeVariableName updateFilter;
    private final TypeName updateFilterTypeName;

    @Inject
    private UpdateFilterPartialTypeSourceFactory(@PartialUpdateFilter final JavaPoetParameterResolver parameterResolver,
                                                 final TypeSourceAdapter adapter,
                                                 @PartialFilter final JavaPoetParameterResolver resolver,
                                                 @PartialUpdateFilter final TypeVariableName updateFilter) {
        super(parameterResolver, adapter);
        this.resolver = resolver;
        this.updateFilter = updateFilter;
        this.updateFilterTypeName = ClassName.get(UpdateFilterQuery.class);
    }

    @Override
    protected Collection<MethodSpec> methodsFor(final ExtendedTypeElement element) {
        return ImmutableSet.of();
    }

    @Override
    public Collection<TypeName> parentNamesOf(final ExtendedTypeElement baseElement) {
        final ImmutableSet.Builder<TypeName> builder = ImmutableSet.<TypeName>builder()
                .addAll(super.parentNamesOf(baseElement))
                .add(this.resolver.resolveParameters(baseElement, this.updateFilter));
        if (baseElement.isTopLevel()) {
            builder.add(this.updateFilterTypeName);
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return "UpdateFilterPartialTypeSourceFactory{" +
                "resolver=" + this.resolver +
                ", updateFilter=" + this.updateFilter +
                ", updateFilterTypeName=" + this.updateFilterTypeName +
                "} " + super.toString();
    }
}