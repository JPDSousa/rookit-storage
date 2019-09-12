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
package org.rookit.storage.filter.source.method;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeVariableName;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodSpecFactories;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javax.naming.NamingFactory;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.type.filter.TypeFilter;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitors;
import org.rookit.convention.auto.javax.visitor.TypeBasedMethodVisitor;
import org.rookit.convention.auto.property.Property;
import org.rookit.storage.filter.source.guice.Type;
import org.rookit.storage.filter.source.method.annotation.MethodAnnotationModule;
import org.rookit.storage.filter.source.method.type.TypeMethodFactoryModule;
import org.rookit.storage.guice.FallbackFilter;
import org.rookit.storage.guice.TopFilter;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.utils.guice.Self;
import org.rookit.utils.string.template.Template1;

import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("MethodMayBeStatic")
public final class FilterMethodModule extends AbstractModule {

    private static final Module MODULE = Modules.combine(new FilterMethodModule(),
            MethodAnnotationModule.getModule(),
            TypeMethodFactoryModule.getModule()
    );

    public static Module getModule() {
        return MODULE;
    }

    private FilterMethodModule() {}

    @Override
    protected void configure() {

    }

    @Singleton
    @Provides
    @PartialFilter
    MethodSpecFactory genericMethodSpecFactory(final MethodSpecFactories factories,
                                               @PartialFilter final TypeVariableName typeVariableName,
                                               @PartialFilter final NamingFactory namingFactory,
                                               @Self final Template1 noopTemplate) {
        return factories.createInterfaceMethodSpecFactory(typeVariableName,
                namingFactory, noopTemplate);
    }

    @Singleton
    @Provides
    @TopFilter
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> topFilterFactory(
            final ConventionTypeElementVisitors visitors,
            @Filter final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> annotationFactory,
            @Type final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> typeVisitor,
            @Filter final Predicate<Property> annotationPredicate) {
        return visitors.streamExConventionBuilder(annotationFactory)
                .routeThroughFilter(typeVisitor, annotationPredicate)
                .build();
    }

    @Provides
    @Singleton
    @Type
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> typeFactory(
            final ConventionTypeElementVisitors visitors,
            @Filter final Set<TypeBasedMethodVisitor<Void>> typeVisitors,
            @FallbackFilter final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> genericMethodVisitor,
            @Type final Predicate<Property> typeFilter) {
        return visitors.streamExConventionBuilder(typeVisitors)
                .routeThroughFilter(genericMethodVisitor, typeFilter)
                .build();
    }

    @Provides
    @Singleton
    @Type
    Predicate<Property> typeFilter(
            @Filter final Set<TypeBasedMethodVisitor<Void>> typeFactories) {
        return TypeFilter.create(typeFactories);
    }

    @Singleton
    @Provides
    @FallbackFilter
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> fallbackFilter(
            final ConventionTypeElementMethodSpecVisitors visitors,
            @PartialFilter final MethodSpecFactory methodSpecFactory,
            @Self final Template1 noopTemplate) {
        return visitors.<Void>templateMethodSpecVisitorBuilder(methodSpecFactory, noopTemplate)
                .build();
    }
}
