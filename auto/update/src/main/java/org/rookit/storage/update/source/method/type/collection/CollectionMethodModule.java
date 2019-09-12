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

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import one.util.streamex.StreamEx;
import org.rookit.auto.source.spec.SpecFactory;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.convention.auto.property.Property;
import org.rookit.storage.guice.Update;
import org.rookit.storage.update.source.guice.Collection;
import org.rookit.utils.adapt.Adapter;

import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("MethodMayBeStatic")
public final class CollectionMethodModule extends AbstractModule {

    private static final Module MODULE = new CollectionMethodModule();

    public static Module getModule() {
        return MODULE;
    }

    private CollectionMethodModule() {}

    @SuppressWarnings({"AnonymousInnerClassMayBeStatic", "AnonymousInnerClass", "EmptyClass"})
    @Override
    protected void configure() {
        final Multibinder<SpecFactory<MethodSpec>> multibinder = Multibinder.newSetBinder(binder(),
                new TypeLiteral<SpecFactory<MethodSpec>>() {}, Update.class);
        multibinder.addBinding().to(Key.get(new TypeLiteral<SpecFactory<MethodSpec>>() {}, Collection.class))
                .in(Singleton.class);

        final Multibinder<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> opMultiBinder = Multibinder
                .newSetBinder(binder(),
                        new TypeLiteral<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>>() {},
                        Collection.class);
        opMultiBinder.addBinding().toProvider(AddMethodFactoryProvider.class).in(Singleton.class);
        opMultiBinder.addBinding().toProvider(RemoveMethodFactoryProvider.class).in(Singleton.class);
        opMultiBinder.addBinding().toProvider(AddAllMethodFactoryProvider.class).in(Singleton.class);
        opMultiBinder.addBinding().toProvider(RemoveAllMethodFactoryProvider.class).in(Singleton.class);

        bind(new TypeLiteral<Function<Property, java.util.Collection<ParameterSpec>>>() {})
                .annotatedWith(Collection.class)
                .toInstance(this::createCollectionParam);
    }

    private java.util.Collection<ParameterSpec> createCollectionParam(final Property property) {
        return ImmutableList.of(ParameterSpec.builder(TypeName.get(property.type()), property.name()).build());
    }
    
    @Provides
    @Singleton
    @Collection
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> collection(
            @Collection final Set<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> opFactories,
            @org.rookit.utils.guice.Collection(unwrap = true) final Adapter<ConventionTypeElement> collectionUnwrapper,
            final ConventionTypeElementMethodSpecVisitors visitors) {
        return visitors.streamExConventionBuilder(opFactories)
                .withConventionTypeAdapter(collectionUnwrapper)
                .build();
    }
}
