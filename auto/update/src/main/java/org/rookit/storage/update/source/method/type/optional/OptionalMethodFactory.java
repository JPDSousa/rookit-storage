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
package org.rookit.storage.update.source.method.type.optional;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.squareup.javapoet.MethodSpec;
import one.util.streamex.StreamEx;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.update.source.guice.Remove;
import org.rookit.utils.adapt.Adapter;
import org.rookit.utils.guice.Optional;

final class OptionalMethodFactory implements Provider<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> {

    private final Adapter<ConventionTypeElement> optionalUnwrapper;
    private final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> removeMethodFactory;
    private final Provider<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> topMethodFactory;
    private final ConventionTypeElementMethodSpecVisitors visitors;

    @Inject
    private OptionalMethodFactory(
            @Optional(unwrap = true) final Adapter<ConventionTypeElement> optionalUnwrapper,
            @Remove final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> removeMethodFactory,
            @PartialUpdate final Provider<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> topMethodFactory,
            final ConventionTypeElementMethodSpecVisitors visitors) {
        this.optionalUnwrapper = optionalUnwrapper;
        this.removeMethodFactory = removeMethodFactory;
        this.topMethodFactory = topMethodFactory;
        this.visitors = visitors;
    }

    @Override
    public ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> get() {
        return this.visitors.streamExConventionBuilder(this.removeMethodFactory)
                .add(this.topMethodFactory)
                .withConventionTypeAdapter(this.optionalUnwrapper)
                .build();
    }

    @Override
    public String toString() {
        return "OptionalMethodFactory{" +
                "optionalUnwrapper=" + this.optionalUnwrapper +
                ", removeMethodFactory=" + this.removeMethodFactory +
                ", topMethodFactory=" + this.topMethodFactory +
                ", visitors=" + this.visitors +
                "}";
    }
}
