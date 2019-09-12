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
package org.rookit.storage.update.source.method;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeVariableName;
import one.util.streamex.StreamEx;
import org.rookit.auto.guice.Flat;
import org.rookit.auto.javapoet.method.MethodSpecFactories;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javax.naming.NamingFactory;
import org.rookit.auto.javax.visitor.ExtendedElementVisitor;
import org.rookit.auto.javax.visitor.ExtendedElementVisitors;
import org.rookit.auto.source.spec.SpecFactories;
import org.rookit.auto.source.spec.SpecFactory;
import org.rookit.convention.auto.javapoet.ConventionAutoFactories;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.type.filter.TypeFilter;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitors;
import org.rookit.convention.auto.javax.visitor.TypeBasedMethodVisitor;
import org.rookit.convention.auto.property.Property;
import org.rookit.storage.api.config.UpdateConfig;
import org.rookit.storage.guice.FallbackUpdate;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.guice.TopUpdate;
import org.rookit.storage.guice.Update;
import org.rookit.storage.guice.UpdateEntity;
import org.rookit.storage.guice.UpdateProperty;
import org.rookit.storage.update.source.guice.Type;
import org.rookit.storage.update.source.method.template.TemplateModule;
import org.rookit.storage.update.source.method.type.TypeMethodFactoryModule;
import org.rookit.utils.adapt.Adapter;
import org.rookit.utils.guice.Mutable;
import org.rookit.utils.guice.Self;
import org.rookit.utils.string.template.Template1;

import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("MethodMayBeStatic")
public final class MethodModule extends AbstractModule {

    private static final Module MODULE = Modules.combine(
            new MethodModule(),
            TypeMethodFactoryModule.getModule(),
            TemplateModule.getModule()
    );

    public static Module getModule() {
        return MODULE;
    }

    private MethodModule() {}

    @Singleton
    @Provides
    @PartialUpdate
    MethodSpecFactory genericMethodFactory(final MethodSpecFactories factories,
                                           @PartialUpdate final TypeVariableName typeVariableName,
                                           @PartialUpdate final NamingFactory namingFactory,
                                           @Self final Template1 noopTemplate) {
        return factories.createInterfaceMethodSpecFactory(typeVariableName,
                namingFactory, noopTemplate);
    }

    @Singleton
    @Provides
    @FallbackUpdate
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> fallbackUpdate(
            final ConventionTypeElementMethodSpecVisitors visitors,
            @PartialUpdate final MethodSpecFactory methodSpecFactory,
            final UpdateConfig config,
            @Mutable final Adapter<ConventionTypeElement> adapter) {
        // TODO inject me
        return visitors.<Void>templateMethodSpecVisitorBuilder(methodSpecFactory, config.methodTemplate())
                .withConventionTypeAdapter(adapter)
                .build();
    }

    @Singleton
    @Provides
    @TopUpdate
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> topFilterFactory(
            final ConventionTypeElementVisitors visitors,
            @Update final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> annotationVisitor,
            @Type final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> typeVisitor,
            @Update final Predicate<Property> annotationPredicate) {
        return visitors.streamExConventionBuilder(annotationVisitor)
                .routeThroughFilter(typeVisitor, annotationPredicate)
                .build();
    }

    @Provides
    @Singleton
    @Type
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> typeFactory(
            final ConventionTypeElementVisitors visitors,
            @Update final Set<TypeBasedMethodVisitor<Void>> typeVisitors,
            @FallbackUpdate final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> genericMethodVisitor,
            @Type final Predicate<Property> typeFilter) {
        return visitors.streamExConventionBuilder(typeVisitors)
                .routeThroughFilter(genericMethodVisitor, typeFilter)
                .build();
    }

    @Provides
    @Singleton
    @Type
    Predicate<Property> typeFilter(@Update final Set<TypeBasedMethodVisitor<Void>> typeFactories) {
        return TypeFilter.create(typeFactories);
    }

    @Singleton
    @Provides
    @Update
    SpecFactory<MethodSpec> genericAnnotationMethodFactory(
            final ExtendedElementVisitors visitors,
            final SpecFactories specs,
            @UpdateEntity final ExtendedElementVisitor<StreamEx<MethodSpec>, Void> entityFactory,
            @UpdateProperty final ExtendedElementVisitor<StreamEx<MethodSpec>, Void> propertyFactory) {
        return specs.fromVisitor(
                visitors.streamExBuilder(entityFactory)
                        .withDirtyFallback(propertyFactory)
                        .build()
        );
    }

    @Singleton
    @Provides
    @UpdateEntity
    ExtendedElementVisitor<StreamEx<MethodSpec>, Void> entityMethodFactory(
            final ConventionTypeElementVisitors visitors,
            final ConventionAutoFactories factories,
            @PartialUpdate final MethodSpecFactory methodSpecFactory) {
        return visitors.<MethodSpec, Void>createPropertyLevelVisitor(
                factories.createTypeTransformation(methodSpecFactory))
                .build();
    }

    @Singleton
    @Provides
    @UpdateProperty
    ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> propertyMethodFactory(
            final ConventionTypeElementMethodSpecVisitors visitors,
            @Flat final Adapter<ConventionTypeElement> elementAdapter,
            @TopUpdate final Provider<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> methodVisitor) {
        return visitors.streamExConventionBuilder(methodVisitor)
                .withConventionTypeAdapter(elementAdapter)
                .build();
    }

}
