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
package org.rookit.storage.update.source;

import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.entity.BaseEntityFactory;
import org.rookit.auto.entity.BasePartialEntityFactory;
import org.rookit.auto.entity.EntityFactory;
import org.rookit.auto.entity.PartialEntityFactory;
import org.rookit.auto.entity.parent.ParentExtractor;
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
import org.rookit.storage.update.source.config.ConfigurationModule;
import org.rookit.storage.update.source.method.MethodModule;
import org.rookit.storage.utils.PartialUpdate;
import org.rookit.storage.utils.Update;
import org.rookit.storage.utils.UpdateFilter;
import org.rookit.storage.api.config.UpdateConfig;
import org.rookit.utils.optional.OptionalFactory;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@SuppressWarnings("MethodMayBeStatic")
public final class SourceModule extends AbstractNamingModule {

    private static final Module MODULE = Modules.override(
            org.rookit.storage.update.filter.source.SourceModule.getModule()
    ).with(
            new SourceModule(),
            ConfigurationModule.getModule(),
            MethodModule.getModule()
    );

    public static Module getModule() {
        return MODULE;
    }

    private SourceModule() {}

    @Override
    protected void configure() {
        bindNaming(Update.class);
        bindNaming(PartialUpdate.class);
        bind(JavaPoetParameterResolver.class).annotatedWith(PartialUpdate.class)
                .to(PartialUpdateParameterResolver.class).in(Singleton.class);
        bind(SingleTypeSourceFactory.class).annotatedWith(PartialUpdate.class)
                .to(PartialUpdateTypeSourceFactory.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    PartialEntityFactory updatePartialEntityFactory(@PartialUpdate final EntityIdentifierFactory identifierFactory,
                                                    @PartialUpdate final SingleTypeSourceFactory typeSourceFactory,
                                                    final OptionalFactory optionalFactory,
                                                    final ParentExtractor extractor) {
        return BasePartialEntityFactory.create(identifierFactory, typeSourceFactory, optionalFactory, extractor);
    }

    @Provides
    @Singleton
    EntityFactory updateEntityFactory(final PartialEntityFactory partialEntityFactory,
                                      @Update final EntityIdentifierFactory identifierFactory,
                                      @Update final SingleTypeSourceFactory typeSpecFactory) {
        return BaseEntityFactory.create(partialEntityFactory, identifierFactory, typeSpecFactory);
    }

    @Provides
    @Singleton
    @PartialUpdate
    TypeVariableName typeVariableName(final UpdateConfig config) {
        return config.parameterName();
    }

    @Provides
    @Singleton
    @Update
    SingleTypeSourceFactory update(final TypeSourceAdapter adapter, @Update final JavaPoetParameterResolver resolver) {
        return EmptyLeafTypeSourceFactory.create(adapter, resolver);
    }

    @Singleton
    @Provides
    @Update
    JavaPoetParameterResolver update(@Update final JavaPoetNamingFactory update,
                                     @UpdateFilter final JavaPoetNamingFactory updateFilter,
                                     @PartialUpdate final JavaPoetNamingFactory genericNamingFactory) {
        return LeafSingleSingleParameterResolver.create(genericNamingFactory, update, updateFilter);
    }

    @Singleton
    @Provides
    @Update
    EntityIdentifierFactory updateIdentifierFactory(@Update final NamingFactory namingFactory) {
        return BaseEntityIdentifierFactory.create(namingFactory);
    }

    @Singleton
    @Provides
    @PartialUpdate
    EntityIdentifierFactory partialUpdateIdentifierFactory(@PartialUpdate final NamingFactory namingFactory) {
        return BaseEntityIdentifierFactory.create(namingFactory);
    }

    @Singleton
    @Provides
    @Update
    PackageReference updatePackage(final UpdateConfig config) {
        return config.basePackage();
    }

    @Singleton
    @Provides
    @PartialUpdate
    JavaPoetNamingFactory partialUpdateNamingFactory(@Update final PackageReference packageReference,
                                                     final UpdateConfig config) {
        return BaseJavaPoetNamingFactory.create(packageReference, config.entitySuffix(), config.partialEntityPrefix(),
                EMPTY);
    }


    @Singleton
    @Provides
    @Update
    JavaPoetNamingFactory updateNamingFactory(@Update final PackageReference packageReference,
                                              final UpdateConfig config) {
        return BaseJavaPoetNamingFactory.create(packageReference, config.entitySuffix(), EMPTY, EMPTY);
    }
}
