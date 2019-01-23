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

import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.entity.BaseEntityFactory;
import org.rookit.auto.entity.BasePartialEntityFactory;
import org.rookit.auto.entity.EntityFactory;
import org.rookit.auto.entity.PartialEntityFactory;
import org.rookit.auto.entity.lazy.LazyPartialEntityFactory;
import org.rookit.auto.entity.nowrite.NoWriteEntityFactory;
import org.rookit.auto.entity.nowrite.NoWritePartialEntityFactory;
import org.rookit.auto.entity.parent.MultiFactoryParentExtractor;
import org.rookit.auto.entity.parent.ParentExtractor;
import org.rookit.auto.guice.Self;
import org.rookit.auto.identifier.BaseEntityIdentifierFactory;
import org.rookit.auto.identifier.EntityIdentifierFactory;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.naming.LeafSingleSingleParameterResolver;
import org.rookit.auto.javapoet.type.EmptyLeafTypeSourceFactory;
import org.rookit.auto.javapoet.type.TypeSourceAdapter;
import org.rookit.auto.naming.AbstractNamingModule;
import org.rookit.auto.naming.BaseJavaPoetNamingFactory;
import org.rookit.auto.naming.NamingFactory;
import org.rookit.auto.naming.PackageReference;
import org.rookit.auto.source.SingleTypeSourceFactory;
import org.rookit.storage.query.source.config.ConfigurationModule;
import org.rookit.storage.utils.ElementQuery;
import org.rookit.storage.utils.PartialQuery;
import org.rookit.storage.utils.Query;
import org.rookit.storage.utils.QueryFilter;
import org.rookit.storage.api.config.QueryConfig;
import org.rookit.storage.utils.filter.Filter;
import org.rookit.storage.utils.filter.FilterBase;
import org.rookit.storage.utils.filter.PartialFilter;
import org.rookit.utils.optional.OptionalFactory;
import org.rookit.utils.primitive.VoidUtils;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@SuppressWarnings("MethodMayBeStatic")
public final class SourceModule extends AbstractNamingModule {

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
        bindNaming(PartialQuery.class);
        bindNaming(Query.class);

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
    ParentExtractor extractor(final Provider<PartialEntityFactory> thisFactory,
                              @Filter final EntityFactory filterEntityFactory) {
        return MultiFactoryParentExtractor.create(LazyPartialEntityFactory.create(thisFactory), filterEntityFactory);
    }

    @Provides
    @Singleton
    PartialEntityFactory create(@PartialQuery final EntityIdentifierFactory identifierFactory,
                                @PartialQuery final SingleTypeSourceFactory typeSpecFactory,
                                @Filter final ParentExtractor extractor,
                                final OptionalFactory optionalFactory) {
        return BasePartialEntityFactory.create(identifierFactory, typeSpecFactory, optionalFactory, extractor);
    }

    @Singleton
    @Provides
    @PartialQuery
    PackageReference queryPackage(final QueryConfig config) {
        return config.basePackage();
    }

    @Provides
    @Singleton
    EntityFactory queryEntityFactory(final PartialEntityFactory partialEntityFactory,
                                     @Query final EntityIdentifierFactory identifierFactory,
                                     @Query final SingleTypeSourceFactory typeSpecFactory) {
        return BaseEntityFactory.create(partialEntityFactory, identifierFactory, typeSpecFactory);
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
    EntityIdentifierFactory queryIdentifier(@PartialQuery final NamingFactory namingFactory) {
        return BaseEntityIdentifierFactory.create(namingFactory);
    }

    @Provides
    @Query
    @Singleton
    EntityIdentifierFactory queryEntityIdentifier(@Query final NamingFactory namingFactory) {
        return BaseEntityIdentifierFactory.create(namingFactory);
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
    SingleTypeSourceFactory queryTypeSourceFactory(final TypeSourceAdapter adapter,
                                                   @Query final JavaPoetParameterResolver parameterResolver) {
        return EmptyLeafTypeSourceFactory.create(adapter, parameterResolver);
    }

    @Singleton
    @Provides
    @PartialQuery
    JavaPoetNamingFactory queryNamingFactory(@PartialQuery final PackageReference packageReference,
                                             final QueryConfig config) {
        return BaseJavaPoetNamingFactory.create(packageReference, config.entitySuffix(), config.partialEntityPrefix(),
                config.methodPrefix());
    }

    @Singleton
    @Provides
    @Query
    JavaPoetNamingFactory queryEntityNamingFactory(@PartialQuery final PackageReference packageReference,
                                                   final QueryConfig config) {
        return BaseJavaPoetNamingFactory.create(packageReference, config.entitySuffix(), EMPTY, config.methodPrefix());
    }

    @Provides
    @Singleton
    @Filter
    EntityFactory filterEntityFactory(@FilterBase final EntityFactory entityFactory,
                                      final VoidUtils voidUtils) {
        return NoWriteEntityFactory.create(entityFactory, voidUtils);
    }

    @Provides
    @Singleton
    @PartialFilter
    PartialEntityFactory filterPartialEntityFactory(@FilterBase final PartialEntityFactory entityFactory,
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
