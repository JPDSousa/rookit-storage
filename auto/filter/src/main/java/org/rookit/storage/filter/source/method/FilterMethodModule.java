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
import com.squareup.javapoet.TypeVariableName;
import org.apache.commons.lang3.StringUtils;
import org.rookit.auto.javapoet.method.AnnotationBasedMethodFactory;
import org.rookit.auto.javapoet.method.GenericMethodFactory;
import org.rookit.auto.javapoet.method.GenericMethodSpecFactory;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javapoet.method.TopMethodFactory;
import org.rookit.auto.javapoet.method.TypeBasedMethodFactory;
import org.rookit.auto.naming.NamingFactory;
import org.rookit.storage.filter.source.method.annotation.MethodAnnotationModule;
import org.rookit.storage.filter.source.method.type.TypeMethodFactoryModule;
import org.rookit.storage.guice.FallbackFilter;
import org.rookit.storage.guice.TopFilter;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.storage.guice.filter.PartialFilter;

import javax.annotation.processing.Messager;
import java.util.Set;

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
    MethodSpecFactory genericMethodSpecFactory(@PartialFilter final TypeVariableName typeVariableName,
                                               @PartialFilter final NamingFactory namingFactory) {
        return GenericMethodSpecFactory.create(typeVariableName, namingFactory);
    }

    @SuppressWarnings("TypeMayBeWeakened")
    @Singleton
    @Provides
    @TopFilter
    MethodFactory topFilterFactory(@FallbackFilter final MethodFactory genericMethodFactory,
                                   @Filter final AnnotationBasedMethodFactory annotationFactory,
                                   @Filter final Set<TypeBasedMethodFactory> typeFactories,
                                   final Messager messager) {
        return TopMethodFactory.create(genericMethodFactory, annotationFactory, typeFactories, messager);
    }

    @Singleton
    @Provides
    @FallbackFilter
    MethodFactory fallbackFilter(@PartialFilter final MethodSpecFactory methodSpecFactory) {
        return GenericMethodFactory.create(methodSpecFactory, StringUtils.EMPTY);
    }
}
