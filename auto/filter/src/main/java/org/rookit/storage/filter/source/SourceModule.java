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
package org.rookit.storage.filter.source;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.identifier.JavaPoetIdentifierFactories;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.naming.LeafSingleParameterResolver;
import org.rookit.auto.javapoet.naming.SelfSinglePartialParameterResolver;
import org.rookit.auto.javapoet.type.EmptyInterfaceTypeSourceFactory;
import org.rookit.auto.javapoet.type.JavaPoetTypeSourceFactory;
import org.rookit.auto.javax.naming.IdentifierFactory;
import org.rookit.auto.javax.naming.NamingFactory;
import org.rookit.auto.source.CodeSourceContainerFactory;
import org.rookit.auto.source.CodeSourceFactory;
import org.rookit.auto.source.spec.SpecFactory;
import org.rookit.auto.source.type.SingleTypeSourceFactory;
import org.rookit.convention.auto.entity.BaseEntityFactory;
import org.rookit.convention.auto.entity.BasePartialEntityFactory;
import org.rookit.convention.auto.entity.parent.ParentExtractor;
import org.rookit.convention.auto.javapoet.type.ConventionSingleTypeSourceFactories;
import org.rookit.convention.auto.javax.ConventionTypeElementFactory;
import org.rookit.storage.api.config.FilterConfig;
import org.rookit.storage.filter.source.config.ConfigurationModule;
import org.rookit.storage.filter.source.method.FilterMethodModule;
import org.rookit.storage.filter.source.naming.NamingModule;
import org.rookit.storage.guice.TopFilter;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.storage.guice.filter.FilterBase;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.utils.optional.OptionalFactory;

import java.util.concurrent.Executor;

@SuppressWarnings("MethodMayBeStatic")
public final class SourceModule extends AbstractModule {

    private static final Module MODULE = Modules.combine(
            new SourceModule(),
            ConfigurationModule.getModule(),
            FilterMethodModule.getModule(),
            NamingModule.getModule()
    );

    public static Module getModule() {
        return MODULE;
    }

    private SourceModule() {}

    @Override
    protected void configure() {
        bind(CodeSourceFactory.class).to(Key.get(CodeSourceFactory.class, FilterBase.class)).in(Singleton.class);
        bind(CodeSourceFactory.class).to(Key.get(CodeSourceFactory.class, PartialFilter.class))
                .in(Singleton.class);
        bind(CodeSourceFactory.class).annotatedWith(PartialFilter.class)
                .to(Key.get(CodeSourceFactory.class, FilterBase.class)).in(Singleton.class);
    }

    @Provides
    @Singleton
    @PartialFilter
    TypeVariableName typeVariableName(final FilterConfig config) {
        return config.parameterName();
    }

    @Provides
    @Singleton
    @FilterBase
    CodeSourceFactory filterEntityFactory(@PartialFilter final CodeSourceFactory codeSourceFactory,
                                          @Filter final IdentifierFactory identifierFactory,
                                          @Filter final SingleTypeSourceFactory typeSpecFactory) {
        return BaseEntityFactory.create(codeSourceFactory, identifierFactory, typeSpecFactory);
    }

    @Provides
    @Singleton
    @FilterBase
    CodeSourceFactory filterPartialEntityFactory(@PartialFilter final IdentifierFactory identifierFactory,
                                                 @PartialFilter final SingleTypeSourceFactory typeSpecFactory,
                                                 final OptionalFactory optionalFactory,
                                                 final ParentExtractor extractor,
                                                 final CodeSourceContainerFactory containerFactory,
                                                 final ConventionTypeElementFactory elementFactory) {
        return BasePartialEntityFactory.create(identifierFactory, typeSpecFactory, optionalFactory,
                extractor, containerFactory, elementFactory);
    }

    @Provides
    @PartialFilter
    @Singleton
    IdentifierFactory filterIdentifier(final JavaPoetIdentifierFactories factories,
                                       @PartialFilter final NamingFactory namingFactory) {
        return factories.create(namingFactory);
    }

    @Provides
    @Filter
    @Singleton
    IdentifierFactory filterEntityIdentifier(final JavaPoetIdentifierFactories factories,
                                             @Filter final NamingFactory namingFactory) {
        return factories.create(namingFactory);
    }

    @Singleton
    @Provides
    @PartialFilter
    JavaPoetParameterResolver partialFilter(@PartialFilter final JavaPoetNamingFactory namingFactory,
                                            @PartialFilter final TypeVariableName filterType) {
        return SelfSinglePartialParameterResolver.create(namingFactory, filterType);
    }

    @Singleton
    @Provides
    @Filter
    JavaPoetParameterResolver filter(@Filter final JavaPoetNamingFactory namingFactory,
                                     @PartialFilter final JavaPoetNamingFactory genericNamingFactory) {
        return LeafSingleParameterResolver.create(namingFactory, genericNamingFactory);
    }

    @Singleton
    @Provides
    @PartialFilter
    SingleTypeSourceFactory filterTypeSourceFactory(final ConventionSingleTypeSourceFactories factories,
                                                    @PartialFilter final JavaPoetParameterResolver parameterResolver,
                                                    @TopFilter final SpecFactory<MethodSpec> specFactory,
                                                    final JavaPoetTypeSourceFactory adapter,
                                                    final ConventionTypeElementFactory elementFactory) {
        return factories.propertyBasedTypeSourceFactory(parameterResolver, adapter, specFactory, elementFactory);
    }

    @Provides
    @Singleton
    @Filter
    SingleTypeSourceFactory filterTypeSourceFactory(@Filter final JavaPoetParameterResolver parameterResolver,
                                                    @Filter final JavaPoetNamingFactory namingFactory,
                                                    final Executor executor) {
        return EmptyInterfaceTypeSourceFactory.create(parameterResolver, namingFactory, executor);
    }

}
