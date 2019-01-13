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

import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.entity.BaseEntityFactory;
import org.rookit.auto.entity.EntityFactory;
import org.rookit.auto.entity.nowrite.NoWriteEntityFactory;
import org.rookit.auto.entity.nowrite.NoWritePartialEntityFactory;
import org.rookit.auto.entity.PartialEntityFactory;
import org.rookit.auto.identifier.BaseEntityIdentifierFactory;
import org.rookit.auto.identifier.EntityIdentifierFactory;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.naming.LeafSingleParameterResolver;
import org.rookit.auto.javapoet.naming.SelfSinglePartialParameterResolver;
import org.rookit.auto.javapoet.type.EmptyLeafTypeSourceFactory;
import org.rookit.auto.javapoet.type.TypeSourceAdapter;
import org.rookit.auto.naming.AbstractNamingModule;
import org.rookit.auto.naming.BaseJavaPoetNamingFactory;
import org.rookit.auto.naming.NamingFactory;
import org.rookit.auto.naming.PackageReference;
import org.rookit.auto.source.SingleTypeSourceFactory;
import org.rookit.storage.update.filter.source.config.ConfigurationModule;
import org.rookit.storage.utils.PartialUpdateFilter;
import org.rookit.storage.utils.UpdateFilter;
import org.rookit.storage.utils.config.UpdateFilterConfig;
import org.rookit.storage.utils.filter.Filter;
import org.rookit.storage.utils.filter.FilterBase;
import org.rookit.storage.utils.filter.PartialFilter;
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
        bindNaming(UpdateFilter.class);
        bindNaming(PartialUpdateFilter.class);
        bind(SingleTypeSourceFactory.class).annotatedWith(PartialUpdateFilter.class)
                .to(UpdateFilterPartialTypeSourceFactory.class).in(Singleton.class);

        bind(PartialEntityFactory.class)
                .to(UpdateFilterPartialEntityFactory.class).in(Singleton.class);
    }

    @Singleton
    @Provides
    @PartialUpdateFilter
    JavaPoetNamingFactory partialUpdateFilterNamingFactory(@UpdateFilter final PackageReference packageReference,
                                                           final UpdateFilterConfig config) {
        return BaseJavaPoetNamingFactory.create(packageReference, config.entitySuffix(), config.partialEntityPrefix(),
                EMPTY);
    }

    @Singleton
    @Provides
    @UpdateFilter
    JavaPoetNamingFactory updateFilterNamingFactory(@UpdateFilter final PackageReference packageReference,
                                                    final UpdateFilterConfig config) {
        return BaseJavaPoetNamingFactory.create(packageReference, config.entitySuffix(), EMPTY, EMPTY);
    }

    @Singleton
    @Provides
    @UpdateFilter
    PackageReference updateFilterPackage(final UpdateFilterConfig config) {
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
    EntityFactory updateFilterEntityFactory(final PartialEntityFactory partialEntityFactory,
                                            @UpdateFilter final EntityIdentifierFactory identifierFactory,
                                            @UpdateFilter final SingleTypeSourceFactory typeSpecFactory) {
        return BaseEntityFactory.create(partialEntityFactory, identifierFactory, typeSpecFactory);
    }

    @Singleton
    @Provides
    @UpdateFilter
    EntityIdentifierFactory updateFilterIdentifierFactory(@UpdateFilter final NamingFactory namingFactory) {
        return BaseEntityIdentifierFactory.create(namingFactory);
    }

    @Singleton
    @Provides
    @PartialUpdateFilter
    EntityIdentifierFactory partialUpdateFilterIdentifierFactory(
            @PartialUpdateFilter final NamingFactory namingFactory) {
        return BaseEntityIdentifierFactory.create(namingFactory);
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
    SingleTypeSourceFactory updateFilterTypeSourceFactory(final TypeSourceAdapter adapter,
                                                          @UpdateFilter final JavaPoetParameterResolver resolver) {
        return EmptyLeafTypeSourceFactory.create(adapter, resolver);
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
}
