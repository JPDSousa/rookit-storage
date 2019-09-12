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
package org.rookit.storage.filter.source.method.type.optional;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.squareup.javapoet.MethodSpec;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.storage.filter.source.guice.Any;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.utils.adapt.Adapter;
import org.rookit.utils.guice.Optional;
import org.rookit.utils.string.template.Template1;

final class SomeMethodFactoryProvider implements Provider<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> {

    private final MethodSpecFactory methodSpecFactory;
    private final Template1 someTemplate;
    private final Adapter<ConventionTypeElement> adapter;

    @Inject
    private SomeMethodFactoryProvider(@PartialFilter final MethodSpecFactory methodSpecFactory,
                                      @Any final Template1 someTemplate,
                                      @Optional final Adapter<ConventionTypeElement> adapter) {
        this.methodSpecFactory = methodSpecFactory;
        this.someTemplate = someTemplate;
        this.adapter = adapter;
    }

    @Override
    public ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> get() {
        return new NoParamTemplateMethodVisitor<>(this.methodSpecFactory, this.someTemplate, this.adapter);
    }

    @Override
    public String toString() {
        return "SomeMethodFactoryProvider{" +
                "methodSpecFactory=" + this.methodSpecFactory +
                ", someTemplate=" + this.someTemplate +
                ", adapter=" + this.adapter +
                "}";
    }
}
