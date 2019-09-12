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
package org.rookit.storage.update.source.method.type.collection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.convention.auto.property.Property;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.update.source.guice.Remove;
import org.rookit.utils.string.template.Template1;

import java.util.Collection;
import java.util.function.Function;

final class RemoveMethodFactoryProvider implements Provider<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> {

    private final Template1 removeTemplate;
    private final MethodSpecFactory methodSpecFactory;
    private final Function<Property, Collection<ParameterSpec>> parameterResolver;
    private final ConventionTypeElementMethodSpecVisitors visitors;

    @Inject
    private RemoveMethodFactoryProvider(
            @Remove final Template1 removeTemplate,
            @PartialUpdate final MethodSpecFactory methodSpecFactory,
            @org.rookit.storage.update.source.guice
                    .Collection final Function<Property, Collection<ParameterSpec>> parameterResolver,
            final ConventionTypeElementMethodSpecVisitors visitors) {
        this.removeTemplate = removeTemplate;
        this.methodSpecFactory = methodSpecFactory;
        this.parameterResolver = parameterResolver;
        this.visitors = visitors;
    }

    @Override
    public ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> get() {
        return this.visitors.<Void>
                templateMethodSpecVisitorBuilder(
                this.methodSpecFactory,
                this.removeTemplate,
                this.parameterResolver
        ).build();
    }

    @Override
    public String toString() {
        return "RemoveMethodFactoryProvider{" +
                "removeTemplate=" + this.removeTemplate +
                ", methodSpecFactory=" + this.methodSpecFactory +
                ", parameterResolver=" + this.parameterResolver +
                ", visitors=" + this.visitors +
                "}";
    }
}
