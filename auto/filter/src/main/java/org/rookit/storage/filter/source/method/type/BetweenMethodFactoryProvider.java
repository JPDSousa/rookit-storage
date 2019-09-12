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

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.convention.auto.property.Property;
import org.rookit.storage.filter.source.guice.Between;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.utils.string.template.Template1;

import java.util.Collection;

final class BetweenMethodFactoryProvider implements Provider<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> {

    private static Collection<ParameterSpec> createParameters(final Property property) {
        final TypeName typeName = TypeName.get(property.type());

        // TODO inject me
        final ParameterSpec before = ParameterSpec.builder(typeName, "before").build();
        final ParameterSpec after = ParameterSpec.builder(typeName, "after").build();

        return ImmutableList.of(before, after);
    }
    
    private final MethodSpecFactory methodSpecFactory;
    private final Template1 template;
    private final ConventionTypeElementMethodSpecVisitors visitors;

    @Inject
    private BetweenMethodFactoryProvider(@PartialFilter final MethodSpecFactory methodSpecFactory,
                                         @Between final Template1 template,
                                         final ConventionTypeElementMethodSpecVisitors visitors) {
        this.methodSpecFactory = methodSpecFactory;
        this.template = template;
        this.visitors = visitors;
    }
    
    @Override
    public ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> get() {
        return this.visitors.<Void>
                templateMethodSpecVisitorBuilder(
                this.methodSpecFactory,
                this.template,
                BetweenMethodFactoryProvider::createParameters
        ).build();
    }

    @Override
    public String toString() {
        return "BetweenMethodFactoryProvider{" +
                "methodSpecFactory=" + this.methodSpecFactory +
                ", template=" + this.template +
                ", visitors=" + this.visitors +
                "}";
    }
}
