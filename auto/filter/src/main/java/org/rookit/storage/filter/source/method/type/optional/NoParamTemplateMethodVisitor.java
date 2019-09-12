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

import com.squareup.javapoet.MethodSpec;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javax.visitor.StreamExtendedElementVisitor;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.convention.auto.property.Property;
import org.rookit.utils.adapt.Adapter;
import org.rookit.utils.string.template.Template1;

final class NoParamTemplateMethodVisitor<P> implements ConventionTypeElementVisitor<StreamEx<MethodSpec>, P>,
        StreamExtendedElementVisitor<MethodSpec, P> {

    private final MethodSpecFactory methodSpecFactory;
    private final Template1 template;
    private final Adapter<ConventionTypeElement> adapter;

    NoParamTemplateMethodVisitor(final MethodSpecFactory methodSpecFactory,
                                 final Template1 template,
                                 final Adapter<ConventionTypeElement> adapter) {
        this.methodSpecFactory = methodSpecFactory;
        this.template = template;
        this.adapter = adapter;
    }

    @Override
    public StreamEx<MethodSpec> visitConventionType(final ConventionTypeElement element, final P parameter) {
        return StreamEx.of(this.adapter.adapt(element).properties())
                .map(Property::name)
                .map(name -> this.methodSpecFactory.create(name, this.template));
    }

    @Override
    public String toString() {
        return "NoParamTemplateMethodVisitor{" +
                "methodSpecFactory=" + this.methodSpecFactory +
                ", template=" + this.template +
                ", adapter=" + this.adapter +
                "}";
    }
}
