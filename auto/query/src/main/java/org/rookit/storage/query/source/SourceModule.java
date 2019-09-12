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
package org.rookit.storage.query.source;

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
import org.rookit.auto.javapoet.naming.LeafSingleSingleParameterResolver;
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
import org.rookit.storage.api.config.QueryConfig;
import org.rookit.storage.guice.ElementQuery;
import org.rookit.storage.guice.PartialQuery;
import org.rookit.storage.guice.Query;
import org.rookit.storage.guice.QueryFilter;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.storage.guice.filter.FilterBase;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.storage.query.source.config.ConfigurationModule;
import org.rookit.utils.guice.Self;
import org.rookit.utils.optional.OptionalFactory;
import org.rookit.utils.primitive.VoidUtils;

import static org.rookit.auto.guice.RookitAutoModuleTools.bindNaming;

@SuppressWarnings("MethodMayBeStatic")
public final class SourceModule extends AbstractModule {

    private static final Module MODULE = Modules.override(
            org.rookit.storage.filter.source.SourceModule.getModule()
    ).with(
            new SourceModule(),
            ConfigurationModule.getModule()
    );

    public static Module getModule() {
        return MODULE;
    }

    private SourceModule() {}

    @Override
    protected void configure() {
        bindNaming(binder(), PartialQuery.class);
        bindNaming(binder(), Query.class);

        bind(JavaPoetParameterResolver.class).annotatedWith(PartialQuery.class)
                .to(QueryJavaPoetPartialParameterResolver.class).in(Singleton.class);
        bind(JavaPoetParameterResolver.class).annotatedWith(QueryFilter.class)
                .to(QueryFilterJavaPoetPartialParameterResolver.class).in(Singleton.class);

        bind(SingleTypeSourceFactory.class).annotatedWith(PartialQuery.class).to(QueryPartialTypeSourceFactory.class)
                .in(Singleton.class);
    }

    @SuppressWarnings("TypeMayBeWeakened") // intentional due to guice
    @Provides
    @Singleton
    @Filter
    ParentExtractor extractor(final Provider<CodeSourceFactory> thisFactory,
                              @Filter final CodeSourceFactory filterCodeSourceFactory,
                              final PropertyFactory propertyFactory) {
        return MultiFactoryParentExtractor.create(LazyPartialEntityFactory.create(thisFactory), filterCodeSourceFactory,
                propertyFactory);
    }

    @Provides
    @Singleton
    CodeSourceFactory create(@PartialQuery final IdentifierFactory identifierFactory,
                             @PartialQuery final SingleTypeSourceFactory typeSpecFactory,
                             @Filter final ParentExtractor extractor,
                             final OptionalFactory optionalFactory,
                             final CodeSourceContainerFactory containerFactory,
                             final ConventionTypeElementFactory elementFactory) {
        return BasePartialEntityFactory.create(identifierFactory, typeSpecFactory, optionalFactory,
                extractor, containerFactory, elementFactory);
    }

    @Singleton
    @Provides
    @PartialQuery
    ExtendedPackageElement queryPackage(final QueryConfig config) {
        return config.basePackage();
    }

    @Provides
    @Singleton
    CodeSourceFactory queryEntityFactory(final CodeSourceFactory codeSourceFactory,
                                         @Query final IdentifierFactory identifierFactory,
                                         @Query final SingleTypeSourceFactory typeSpecFactory) {
        return BaseEntityFactory.create(codeSourceFactory, identifierFactory, typeSpecFactory);
    }

    @Provides
    @Singleton
    @PartialQuery
    TypeVariableName typeVariableName(final QueryConfig config) {
        return config.parameterName();
    }

    @Provides
    @Singleton
    @ElementQuery
    TypeVariableName elementTypeVariableName(final QueryConfig config) {
        return config.elementParameterName();
    }

    @Provides
    @PartialQuery
    @Singleton
    IdentifierFactory queryIdentifier(final JavaPoetIdentifierFactories factories,
                                      @PartialQuery final NamingFactory namingFactory) {
        return factories.create(namingFactory);
    }

    @Provides
    @Query
    @Singleton
    IdentifierFactory queryEntityIdentifier(final JavaPoetIdentifierFactories factories,
                                            @Query final NamingFactory namingFactory) {
        return factories.create(namingFactory);
    }

    @Singleton
    @Provides
    @Query
    JavaPoetParameterResolver query(@PartialQuery final JavaPoetNamingFactory generic,
                                    @Self final JavaPoetNamingFactory self,
                                    @Query final JavaPoetNamingFactory query) {
        return LeafSingleSingleParameterResolver.create(generic, self, query);
    }

    @Singleton
    @Provides
    @Query
    SingleTypeSourceFactory queryTypeSourceFactory(final JavaPoetTypeSourceFactory adapter,
                                                   @Query final JavaPoetParameterResolver parameterResolver) {
        return EmptyLeafTypeSourceFactory.create(adapter, parameterResolver);
    }

    @Singleton
    @Provides
    @PartialQuery
    JavaPoetNamingFactory queryNamingFactory(final JavaPoetNamingFactories factories,
                                             @PartialQuery final ExtendedPackageElement packageElement,
                                             final QueryConfig config) {
        return factories.create(packageElement, config.entityTemplate(), config.methodTemplate());
    }

    @Singleton
    @Provides
    @Query
    JavaPoetNamingFactory queryEntityNamingFactory(final JavaPoetNamingFactories factories,
                                                   @PartialQuery final ExtendedPackageElement packageElement,
                                                   final QueryConfig config) {
        // TODO should this have the exact same binding as the one above??????
        return factories.create(packageElement, config.entityTemplate(), config.methodTemplate());
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

    @Provides
    @Singleton
    @PartialFilter
    TypeVariableName filterTypeVariableName(final QueryConfig config) {
        return config.parameterName();
    }

}
