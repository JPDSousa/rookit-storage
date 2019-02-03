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
package org.rookit.storage.filter.source.method.annotation;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.rookit.auto.javapoet.method.AnnotationBasedMethodFactory;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javapoet.method.annotation.BaseAnnotationBasedMethodFactory;
import org.rookit.auto.javapoet.method.annotation.EntityMethodFactory;
import org.rookit.auto.javapoet.method.annotation.PropertyMethodFactory;
import org.rookit.auto.javax.property.PropertyAdapter;
import org.rookit.auto.javax.property.PropertyExtractor;
import org.rookit.storage.guice.FilterEntity;
import org.rookit.storage.guice.FilterProperty;
import org.rookit.storage.guice.TopFilter;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.storage.guice.filter.PartialFilter;

import javax.annotation.processing.Messager;
import javax.lang.model.util.Types;

@SuppressWarnings("MethodMayBeStatic")
public final class MethodAnnotationModule extends AbstractModule {

    private static final Module MODULE = new MethodAnnotationModule();

    public static Module getModule() {
        return MODULE;
    }

    private MethodAnnotationModule() {}

    @Override
    protected void configure() {

    }

    @Singleton
    @Provides
    @FilterEntity
    AnnotationBasedMethodFactory entityMethodFactory(final Types types,
                                                     @PartialFilter final MethodSpecFactory methodSpecFactory) {
        return EntityMethodFactory.create(types, methodSpecFactory);
    }

    @Singleton
    @Provides
    @FilterProperty
    AnnotationBasedMethodFactory propertyMethodFactory(final PropertyExtractor propertyExtractor,
                                                       @TopFilter final Provider<MethodFactory> methodFactory,
                                                       final PropertyAdapter adapter) {
        return PropertyMethodFactory.create(propertyExtractor, methodFactory, adapter);
    }

    @Singleton
    @Provides
    @Filter
    AnnotationBasedMethodFactory genericAnnotationMethodFactory(
            @FilterEntity final AnnotationBasedMethodFactory entityFactory,
            @FilterProperty final AnnotationBasedMethodFactory propertyFactory,
            final Messager messager) {
        return BaseAnnotationBasedMethodFactory.create(entityFactory, propertyFactory, messager);
    }
}
