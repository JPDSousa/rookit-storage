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

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;
import org.rookit.convention.auto.javax.visitor.TypeBasedMethodVisitor;
import org.rookit.storage.filter.source.guice.After;
import org.rookit.storage.filter.source.guice.Before;
import org.rookit.storage.filter.source.guice.Between;
import org.rookit.storage.filter.source.guice.Time;
import org.rookit.storage.filter.source.method.type.collection.CollectionModule;
import org.rookit.storage.filter.source.method.type.optional.OptionalModule;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.utils.guice.Self;
import org.rookit.utils.string.template.Template1;
import org.rookit.utils.string.template.TemplateFactory;

@SuppressWarnings("MethodMayBeStatic")
public final class TypeMethodFactoryModule extends AbstractModule {

    private static final Module MODULE = Modules.combine(new TypeMethodFactoryModule(),
            OptionalModule.getModule(),
            CollectionModule.getModule());

    public static Module getModule() {
        return MODULE;
    }

    private TypeMethodFactoryModule() {}

    @SuppressWarnings({"AnonymousInnerClassMayBeStatic", "AnonymousInnerClass", "EmptyClass"})
    @Override
    protected void configure() {
        final Multibinder<TypeBasedMethodVisitor> multibinder = Multibinder.newSetBinder(binder(),
                TypeBasedMethodVisitor.class, Filter.class);
        multibinder.addBinding().toProvider(LocalDateMethodFactory.class).in(Singleton.class);
        multibinder.addBinding().toProvider(DurationMethodFactoryProvider.class).in(Singleton.class);

        final Multibinder<Template1> opMultibinder = Multibinder
                .newSetBinder(binder(), Template1.class, Time.class);
        opMultibinder.addBinding().to(Key.get(Template1.class, Self.class));
        opMultibinder.addBinding().to(Key.get(Template1.class, Before.class));
        opMultibinder.addBinding().to(Key.get(Template1.class, After.class));

        // TODO addAll binding to the between factory
    }

    @Provides
    @Singleton
    @Before
    Template1 beforeTemplate(final TemplateFactory templateFactory) {
        return templateFactory.template1("{}Before");
    }

    @Provides
    @Singleton
    @After
    Template1 afterTemplate(final TemplateFactory templateFactory) {
        return templateFactory.template1("{}After");
    }

    @Provides
    @Singleton
    @Between
    Template1 betweenTemplate(final TemplateFactory templateFactory) {
        return templateFactory.template1("{}Between");
    }

}
