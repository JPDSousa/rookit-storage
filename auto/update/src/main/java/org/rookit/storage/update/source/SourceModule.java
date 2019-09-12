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

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.naming.LeafSingleSingleParameterResolver;
import org.rookit.auto.javapoet.type.EmptyLeafTypeSourceFactory;
import org.rookit.auto.javapoet.type.JavaPoetTypeSourceFactory;
import org.rookit.auto.javax.naming.IdentifierFactory;
import org.rookit.auto.javax.pack.ExtendedPackageElement;
import org.rookit.auto.source.CodeSourceContainerFactory;
import org.rookit.auto.source.CodeSourceFactory;
import org.rookit.auto.source.type.SingleTypeSourceFactory;
import org.rookit.convention.auto.entity.BaseEntityFactory;
import org.rookit.convention.auto.entity.BasePartialEntityFactory;
import org.rookit.convention.auto.entity.parent.ParentExtractor;
import org.rookit.convention.auto.javax.ConventionTypeElementFactory;
import org.rookit.storage.api.config.UpdateConfig;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.guice.Update;
import org.rookit.storage.guice.UpdateFilter;
import org.rookit.storage.update.source.config.ConfigurationModule;
import org.rookit.storage.update.source.identifier.IdentifierModule;
import org.rookit.storage.update.source.method.MethodModule;
import org.rookit.storage.update.source.naming.NamingModule;
import org.rookit.utils.optional.OptionalFactory;

@SuppressWarnings("MethodMayBeStatic")
public final class SourceModule extends AbstractModule {

    private static final Module MODULE = Modules.override(
            org.rookit.storage.update.filter.source.SourceModule.getModule()
    ).with(
            new SourceModule(),
            ConfigurationModule.getModule(),
            MethodModule.getModule(),
            NamingModule.getModule(),
            IdentifierModule.getModule()
    );

    public static Module getModule() {
        return MODULE;
    }

    private SourceModule() {}

    @Override
    protected void configure() {
        bind(JavaPoetParameterResolver.class).annotatedWith(PartialUpdate.class)
                .to(PartialUpdateParameterResolver.class).in(Singleton.class);
        bind(SingleTypeSourceFactory.class).annotatedWith(PartialUpdate.class)
                .to(PartialUpdateTypeSourceFactory.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    CodeSourceFactory updatePartialEntityFactory(@PartialUpdate final IdentifierFactory identifierFactory,
                                                 @PartialUpdate final SingleTypeSourceFactory typeSourceFactory,
                                                 final OptionalFactory optionalFactory,
                                                 final ParentExtractor extractor,
                                                 final CodeSourceContainerFactory containerFactory,
                                                 final ConventionTypeElementFactory elementFactory) {
        return BasePartialEntityFactory.create(identifierFactory, typeSourceFactory, optionalFactory,
                extractor, containerFactory, elementFactory);
    }

    @Provides
    @Singleton
    CodeSourceFactory updateEntityFactory(final CodeSourceFactory codeSourceFactory,
                                          @Update final IdentifierFactory identifierFactory,
                                          @Update final SingleTypeSourceFactory typeSpecFactory) {
        return BaseEntityFactory.create(codeSourceFactory, identifierFactory, typeSpecFactory);
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
    SingleTypeSourceFactory update(final JavaPoetTypeSourceFactory adapter,
                                   @Update final JavaPoetParameterResolver resolver) {
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
    ExtendedPackageElement updatePackage(final UpdateConfig config) {
        return config.basePackage();
    }
}
