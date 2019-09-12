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
package org.rookit.storage.update.filter.source;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.identifier.JavaPoetIdentifierFactories;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactories;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.naming.LeafSingleParameterResolver;
import org.rookit.auto.javapoet.naming.SelfSinglePartialParameterResolver;
import org.rookit.auto.javapoet.type.EmptyLeafTypeSourceFactory;
import org.rookit.auto.javapoet.type.JavaPoetTypeSourceFactory;
import org.rookit.auto.javax.naming.IdentifierFactory;
import org.rookit.auto.javax.naming.NamingFactory;
import org.rookit.auto.javax.pack.ExtendedPackageElement;
import org.rookit.auto.source.CodeSourceContainerFactory;
import org.rookit.auto.source.CodeSourceFactory;
import org.rookit.auto.source.type.SingleTypeSourceFactory;
import org.rookit.convention.auto.entity.BaseEntityFactory;
import org.rookit.convention.auto.entity.BasePartialEntityFactory;
import org.rookit.convention.auto.entity.lazy.LazyPartialEntityFactory;
import org.rookit.convention.auto.entity.nowrite.NoWriteEntityFactory;
import org.rookit.convention.auto.entity.nowrite.NoWritePartialEntityFactory;
import org.rookit.convention.auto.entity.parent.MultiFactoryParentExtractor;
import org.rookit.convention.auto.entity.parent.ParentExtractor;
import org.rookit.convention.auto.javax.ConventionTypeElementFactory;
import org.rookit.convention.auto.property.PropertyFactory;
import org.rookit.storage.api.config.UpdateFilterConfig;
import org.rookit.storage.guice.PartialUpdateFilter;
import org.rookit.storage.guice.UpdateFilter;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.storage.guice.filter.FilterBase;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.storage.update.filter.source.config.ConfigurationModule;
import org.rookit.utils.guice.Self;
import org.rookit.utils.optional.OptionalFactory;
import org.rookit.utils.primitive.VoidUtils;
import org.rookit.utils.string.template.Template1;

import static org.rookit.auto.guice.RookitAutoModuleTools.bindNaming;

@SuppressWarnings("MethodMayBeStatic")
public final class SourceModule extends AbstractModule {

    private static final Module MODULE = Modules.combine(
            new SourceModule(),
            ConfigurationModule.getModule()
    );

    public static Module getModule() {
        return MODULE;
    }

    private SourceModule() {}

    @Override
    protected void configure() {
        bindNaming(binder(), UpdateFilter.class);
        bindNaming(binder(), PartialUpdateFilter.class);
        bind(SingleTypeSourceFactory.class).annotatedWith(PartialUpdateFilter.class)
                .to(UpdateFilterPartialTypeSourceFactory.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    @PartialFilter
    ParentExtractor parentExtractor(@PartialFilter final CodeSourceFactory filterFactory,
                                    final Provider<CodeSourceFactory> partialEntityFactory,
                                    final PropertyFactory propertyFactory) {
        return MultiFactoryParentExtractor.create(LazyPartialEntityFactory.create(partialEntityFactory),
                filterFactory, propertyFactory);
    }

    @Provides
    @Singleton
    CodeSourceFactory partialEntityFactory(@PartialUpdateFilter final IdentifierFactory identifierFactory,
                                           @PartialUpdateFilter final SingleTypeSourceFactory typeSourceFactory,
                                           @PartialFilter final ParentExtractor extractor,
                                           final OptionalFactory optionalFactory,
                                           final CodeSourceContainerFactory containerFactory,
                                           final ConventionTypeElementFactory elementFactory) {
        return BasePartialEntityFactory.create(identifierFactory, typeSourceFactory, optionalFactory,
                extractor, containerFactory, elementFactory);
    }

    @Singleton
    @Provides
    @PartialUpdateFilter
    JavaPoetNamingFactory partialUpdateFilterNamingFactory(final JavaPoetNamingFactories factories,
                                                           @UpdateFilter final ExtendedPackageElement packageElement,
                                                           final UpdateFilterConfig config,
                                                           @Self final Template1 noopTemplate) {
        return factories.create(packageElement, config.entityTemplate(), noopTemplate);
    }

    @Singleton
    @Provides
    @UpdateFilter
    JavaPoetNamingFactory updateFilterNamingFactory(final JavaPoetNamingFactories factories,
                                                    @UpdateFilter final ExtendedPackageElement packageElement,
                                                    final UpdateFilterConfig config,
                                                    @Self final Template1 noopTemplate) {
        return factories.create(packageElement, config.entityTemplate(), noopTemplate);
    }

    @Singleton
    @Provides
    @UpdateFilter
    ExtendedPackageElement updateFilterPackage(final UpdateFilterConfig config) {
        return config.basePackage();
    }

    @Provides
    @Singleton
    @PartialUpdateFilter
    TypeVariableName parameterName(final UpdateFilterConfig config) {
        return config.parameterName();
    }

    @Provides
    @Singleton
    CodeSourceFactory updateFilterEntityFactory(final CodeSourceFactory codeSourceFactory,
                                                @UpdateFilter final IdentifierFactory identifierFactory,
                                                @UpdateFilter final SingleTypeSourceFactory typeSpecFactory) {
        return BaseEntityFactory.create(codeSourceFactory, identifierFactory, typeSpecFactory);
    }

    @Singleton
    @Provides
    @UpdateFilter
    IdentifierFactory updateFilterIdentifierFactory(final JavaPoetIdentifierFactories factories,
                                                    @UpdateFilter final NamingFactory namingFactory) {
        return factories.create(namingFactory);
    }

    @Singleton
    @Provides
    @PartialUpdateFilter
    IdentifierFactory partialUpdateFilterIdentifierFactory(
            final JavaPoetIdentifierFactories factories,
            @PartialUpdateFilter final NamingFactory namingFactory) {
        return factories.create(namingFactory);
    }

    @Singleton
    @Provides
    @PartialUpdateFilter
    JavaPoetParameterResolver partialUpdateFilter(
            @PartialUpdateFilter final JavaPoetNamingFactory partialUpdateFilter,
            @PartialUpdateFilter final TypeVariableName type) {
        return SelfSinglePartialParameterResolver.create(partialUpdateFilter, type);
    }

    @Singleton
    @Provides
    @UpdateFilter
    JavaPoetParameterResolver updateFilter(@UpdateFilter final JavaPoetNamingFactory updateFilter,
                                           @PartialUpdateFilter final JavaPoetNamingFactory partialUpdateFilter) {
        return LeafSingleParameterResolver.create(updateFilter, partialUpdateFilter);
    }

    @Singleton
    @Provides
    @UpdateFilter
    SingleTypeSourceFactory updateFilterTypeSourceFactory(final JavaPoetTypeSourceFactory adapter,
                                                          @UpdateFilter final JavaPoetParameterResolver resolver) {
        return EmptyLeafTypeSourceFactory.create(adapter, resolver);
    }

    @Provides
    @Singleton
    @Filter
    CodeSourceFactory filterEntityFactory(@FilterBase final CodeSourceFactory codeSourceFactory,
                                          final VoidUtils voidUtils) {
        return NoWriteEntityFactory.create(codeSourceFactory, voidUtils);
    }

    @Provides
    @Singleton
    @PartialFilter
    CodeSourceFactory filterPartialEntityFactory(@FilterBase final CodeSourceFactory entityFactory,
                                                 final VoidUtils voidUtils) {
        return NoWritePartialEntityFactory.create(entityFactory, voidUtils);
    }
}
