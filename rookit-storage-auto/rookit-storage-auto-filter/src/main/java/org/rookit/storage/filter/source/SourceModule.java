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
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.entity.BaseEntityFactory;
import org.rookit.auto.entity.BasePartialEntityFactory;
import org.rookit.auto.entity.EntityFactory;
import org.rookit.auto.entity.PartialEntityFactory;
import org.rookit.auto.entity.parent.ParentExtractor;
import org.rookit.auto.identifier.BaseEntityIdentifierFactory;
import org.rookit.auto.identifier.EntityIdentifierFactory;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javapoet.naming.JavaPoetParameterResolver;
import org.rookit.auto.javapoet.naming.LeafSingleParameterResolver;
import org.rookit.auto.javapoet.naming.SelfSinglePartialParameterResolver;
import org.rookit.auto.javapoet.type.EmptyInterfaceTypeSourceFactory;
import org.rookit.auto.javapoet.type.PropertyBasedTypeSourceFactory;
import org.rookit.auto.javapoet.type.TypeSourceAdapter;
import org.rookit.auto.javax.property.PropertyExtractor;
import org.rookit.auto.naming.NamingFactory;
import org.rookit.auto.source.SingleTypeSourceFactory;
import org.rookit.storage.api.config.FilterConfig;
import org.rookit.storage.filter.source.config.ConfigurationModule;
import org.rookit.storage.filter.source.method.FilterMethodModule;
import org.rookit.storage.filter.source.naming.NamingModule;
import org.rookit.storage.utils.TopFilter;
import org.rookit.storage.utils.filter.Filter;
import org.rookit.storage.utils.filter.FilterBase;
import org.rookit.storage.utils.filter.PartialFilter;
import org.rookit.utils.optional.OptionalFactory;
import org.rookit.utils.primitive.VoidUtils;

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
        bind(EntityFactory.class).to(Key.get(EntityFactory.class, FilterBase.class)).in(Singleton.class);
        bind(PartialEntityFactory.class).to(Key.get(PartialEntityFactory.class, PartialFilter.class))
                .in(Singleton.class);
        bind(PartialEntityFactory.class).annotatedWith(PartialFilter.class)
                .to(Key.get(PartialEntityFactory.class, FilterBase.class)).in(Singleton.class);
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
    EntityFactory filterEntityFactory(@PartialFilter final PartialEntityFactory partialEntityFactory,
                                      @Filter final EntityIdentifierFactory identifierFactory,
                                      @Filter final SingleTypeSourceFactory typeSpecFactory) {
        return BaseEntityFactory.create(partialEntityFactory, identifierFactory, typeSpecFactory);
    }

    @Provides
    @Singleton
    @FilterBase
    PartialEntityFactory filterPartialEntityFactory(@PartialFilter final EntityIdentifierFactory identifierFactory,
                                                    @PartialFilter final SingleTypeSourceFactory typeSpecFactory,
                                                    final OptionalFactory optionalFactory,
                                                    final ParentExtractor extractor) {
        return BasePartialEntityFactory.create(identifierFactory, typeSpecFactory, optionalFactory, extractor);
    }

    @Provides
    @PartialFilter
    @Singleton
    EntityIdentifierFactory filterIdentifier(@PartialFilter final NamingFactory namingFactory) {
        return BaseEntityIdentifierFactory.create(namingFactory);
    }

    @Provides
    @Filter
    @Singleton
    EntityIdentifierFactory filterEntityIdentifier(@Filter final NamingFactory namingFactory) {
        return BaseEntityIdentifierFactory.create(namingFactory);
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
    SingleTypeSourceFactory filterTypeSourceFactory(@PartialFilter final JavaPoetParameterResolver parameterResolver,
                                                    final PropertyExtractor extractor,
                                                    @TopFilter final MethodFactory methodFactory,
                                                    final TypeSourceAdapter adapter) {
        return PropertyBasedTypeSourceFactory.create(parameterResolver, methodFactory, adapter, extractor);
    }

    @Provides
    @Singleton
    @Filter
    SingleTypeSourceFactory filterTypeSourceFactory(@Filter final JavaPoetParameterResolver parameterResolver,
                                                    @Filter final JavaPoetNamingFactory namingFactory,
                                                    final VoidUtils voidUtils) {
        return EmptyInterfaceTypeSourceFactory.create(parameterResolver, namingFactory, voidUtils);
    }

}
