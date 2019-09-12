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
package org.rookit.storage.filter.source.method.annotation;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.squareup.javapoet.MethodSpec;
import one.util.streamex.StreamEx;
import org.rookit.auto.guice.Flat;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javax.type.ExtendedTypeElement;
import org.rookit.auto.javax.visitor.ExtendedElementVisitor;
import org.rookit.auto.javax.visitor.ExtendedElementVisitors;
import org.rookit.auto.source.spec.SpecFactories;
import org.rookit.auto.source.spec.SpecFactory;
import org.rookit.auto.spec.AutoSpecFactories;
import org.rookit.convention.auto.javapoet.ConventionAutoFactories;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.ConventionTypeElementFactory;
import org.rookit.convention.auto.javax.type.adapter.ConventionTypeAdapters;
import org.rookit.convention.auto.property.Property;
import org.rookit.convention.auto.property.PropertyFactory;
import org.rookit.convention.auto.property.filter.ConventionPropertyFilters;
import org.rookit.storage.guice.FilterEntity;
import org.rookit.storage.guice.FilterProperty;
import org.rookit.storage.guice.TopFilter;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.utils.adapt.Adapter;

import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("MethodMayBeStatic")
public final class MethodAnnotationModule extends AbstractModule {

    private static final Module MODULE = new MethodAnnotationModule();

    public static Module getModule() {
        return MODULE;
    }

    private MethodAnnotationModule() {}

    @Override
    protected void configure() {

    }

    @Singleton
    @Provides
    @FilterEntity
    // TODO so much stuff in here. Please break it down
    ExtendedElementVisitor<StreamEx<MethodSpec>, Void> entityMethodFactory(
            final ConventionTypeElementMethodSpecVisitors visitors,
            final ConventionAutoFactories javaPoetFactories,
            final ConventionTypeAdapters conventionAdapters,
            @PartialFilter final MethodSpecFactory methodSpecFactory,
            final ConventionTypeElementFactory elementFactory,
            final PropertyFactory propertyFactory) {
        final Predicate<Property> propertyFilter = ConventionPropertyFilters.createEntityFilter(propertyFactory);
        final BiFunction<ConventionTypeElement, Property, StreamEx<MethodSpec>> typeTransformation =
                javaPoetFactories.createTypeTransformation(methodSpecFactory);

        return visitors.<MethodSpec, Void>createPropertyLevelVisitor(typeTransformation)
                .withConventionTypeAdapter(conventionAdapters
                                                   .createPropertyFilterAdapter(propertyFilter, elementFactory))
                .build();
    }

    @Singleton
    @Provides
    @FilterProperty
    SpecFactory<MethodSpec> propertyMethodFactory(
            final AutoSpecFactories autoFactories,
            @Flat final Adapter<ExtendedTypeElement> elementAdapter,
            @TopFilter final Provider<SpecFactory<MethodSpec>> methodFactory) {
        final SpecFactory<MethodSpec> lazyFactory = autoFactories.createLazyFactory(methodFactory);
        return autoFactories.createAdapterFactory(lazyFactory, elementAdapter);
    }

    @Singleton
    @Provides
    @Filter
    SpecFactory<MethodSpec> genericAnnotationMethodFactory(
            final SpecFactories specs,
            final ExtendedElementVisitors visitors,
            @FilterEntity final ExtendedElementVisitor<StreamEx<MethodSpec>, Void> entityFactory,
            @FilterProperty final ExtendedElementVisitor<StreamEx<MethodSpec>, Void> propertyFactory) {
        return specs.fromVisitor(
                visitors.streamExBuilder(entityFactory)
                        .withDirtyFallback(propertyFactory)
                        .build()
        );
    }
}
