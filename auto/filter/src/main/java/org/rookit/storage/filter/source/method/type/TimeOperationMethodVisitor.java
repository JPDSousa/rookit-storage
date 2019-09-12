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

import com.google.inject.Inject;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javax.visitor.StreamExtendedElementVisitor;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.convention.auto.property.Property;
import org.rookit.storage.filter.source.guice.Time;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.utils.string.template.Template1;

import java.util.Collection;
import java.util.Set;

final class TimeOperationMethodVisitor<P> implements ConventionTypeElementVisitor<StreamEx<MethodSpec>, P>,
        StreamExtendedElementVisitor<MethodSpec, P> {

    private final MethodSpecFactory methodSpecFactory;
    private final Collection<Template1> templates;

    @Inject
    private TimeOperationMethodVisitor(@PartialFilter final MethodSpecFactory methodSpecFactory,
                                       @Time final Set<Template1> templates) {
        this.methodSpecFactory = methodSpecFactory;
        this.templates = templates;
    }

    private StreamEx<MethodSpec> create(final Property property) {
        final String propertyName = property.name();
        final TypeName typeName = TypeName.get(property.type());
        final ParameterSpec param = ParameterSpec.builder(typeName, propertyName).build();

        return StreamEx.of(this.templates)
                .map(template -> this.methodSpecFactory.create(propertyName,template, param));
    }

    @Override
    public StreamEx<MethodSpec> visitConventionType(final ConventionTypeElement element, final P parameter) {
        return StreamEx.of(element.properties())
                .flatMap(this::create);
    }

    @Override
    public String toString() {
        return "TimeOperationMethodVisitor{" +
                "methodSpecFactory=" + this.methodSpecFactory +
                ", templates=" + this.templates +
                "}";
    }
}
