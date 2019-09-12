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

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.squareup.javapoet.MethodSpec;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.update.source.guice.Optional;
import org.rookit.storage.update.source.guice.Remove;
import org.rookit.utils.string.template.Template1;

@SuppressWarnings("MethodMayBeStatic")
public final class OptionalMethodModule extends AbstractModule {

    private static final Module MODULE = new OptionalMethodModule();

    public static Module getModule() {
        return MODULE;
    }

    private OptionalMethodModule() {}

    @SuppressWarnings({"AnonymousInnerClassMayBeStatic", "AnonymousInnerClass", "EmptyClass"})
    @Override
    protected void configure() {
        bind(new TypeLiteral<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>>() {})
                .annotatedWith(Optional.class)
                .toProvider(OptionalMethodFactory.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @Remove
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> removeMethodFactory(
            final ConventionTypeElementMethodSpecVisitors visitors,
            @PartialUpdate final MethodSpecFactory methodSpecFactory,
            @Remove final Template1 template) {
        return visitors.<Void>templateMethodSpecVisitorBuilder(methodSpecFactory, template)
                .build();
    }
}
