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
import com.google.inject.Provider;
import com.squareup.javapoet.MethodSpec;
import one.util.streamex.StreamEx;
import org.rookit.auto.javax.type.ExtendedTypeMirror;
import org.rookit.auto.javax.type.ExtendedTypeMirrorFactory;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.convention.auto.javax.visitor.TypeBasedMethodVisitor;
import org.rookit.storage.filter.source.guice.Time;

import java.time.LocalDate;

final class LocalDateMethodFactory implements Provider<TypeBasedMethodVisitor<Void>> {

    private final ExtendedTypeMirror type;
    private final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> delegate;
    private final ConventionTypeElementMethodSpecVisitors specs;

    @Inject
    private LocalDateMethodFactory(final ExtendedTypeMirrorFactory factory,
                                   @Time final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> delegate,
                                   final ConventionTypeElementMethodSpecVisitors specs) {
        this.type = factory.createWithErasure(LocalDate.class);
        this.delegate = delegate;
        this.specs = specs;
    }

    @Override
    public TypeBasedMethodVisitor<Void> get() {
        return this.specs.streamExMethodBuilder(this.delegate)
                .withType(this.type)
                .build();
    }

    @Override
    public String toString() {
        return "LocalDateMethodFactory{" +
                "type=" + this.type +
                ", delegate=" + this.delegate +
                ", specs=" + this.specs +
                "}";
    }
}
