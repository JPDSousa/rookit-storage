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
package org.rookit.storage.update.source.method;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.javapoet.method.AnnotationBasedMethodFactory;
import org.rookit.auto.javapoet.method.GenericMethodFactory;
import org.rookit.auto.javapoet.method.GenericMethodSpecFactory;
import org.rookit.auto.javapoet.method.MethodFactory;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javapoet.method.TopMethodFactory;
import org.rookit.auto.javapoet.method.TypeBasedMethodFactory;
import org.rookit.auto.javapoet.method.annotation.BaseAnnotationBasedMethodFactory;
import org.rookit.auto.javapoet.method.annotation.EntityMethodFactory;
import org.rookit.auto.javapoet.method.annotation.PropertyMethodFactory;
import org.rookit.auto.javax.property.PropertyAdapter;
import org.rookit.auto.javax.property.PropertyExtractor;
import org.rookit.auto.naming.NamingFactory;
import org.rookit.storage.update.source.method.type.TypeMethodFactoryModule;
import org.rookit.storage.utils.FallbackUpdate;
import org.rookit.storage.utils.PartialUpdate;
import org.rookit.storage.utils.TopUpdate;
import org.rookit.storage.utils.Update;
import org.rookit.storage.utils.UpdateEntity;
import org.rookit.storage.utils.UpdateProperty;
import org.rookit.storage.utils.config.UpdateConfig;

import javax.annotation.processing.Messager;
import javax.lang.model.util.Types;
import java.util.Set;

@SuppressWarnings("MethodMayBeStatic")
public final class MethodModule extends AbstractModule {

    private static final Module MODULE = Modules.combine(new MethodModule(),
            TypeMethodFactoryModule.getModule());

    public static Module getModule() {
        return MODULE;
    }

    private MethodModule() {}

    @Override
    protected void configure() {

    }

    @Singleton
    @Provides
    @PartialUpdate
    MethodSpecFactory genericMethodFactory(@PartialUpdate final TypeVariableName typeVariableName,
                                           @PartialUpdate final NamingFactory namingFactory) {
        return GenericMethodSpecFactory.create(typeVariableName, namingFactory);
    }

    @Singleton
    @Provides
    @FallbackUpdate
    MethodFactory fallbackUpdate(@PartialUpdate final MethodSpecFactory methodSpecFactory,
                                 final UpdateConfig config) {
        return GenericMethodFactory.createWithoutFinals(methodSpecFactory, config.methodPrefix());
    }

    @SuppressWarnings("TypeMayBeWeakened")
    @Singleton
    @Provides
    @TopUpdate
    MethodFactory topUpdateMethodFactory(@FallbackUpdate final MethodFactory genericMethodFactory,
                                         @Update final AnnotationBasedMethodFactory annotationFactory,
                                         @Update final Set<TypeBasedMethodFactory> typeBasedFactories,
                                         final Messager messager) {
        return TopMethodFactory.create(genericMethodFactory,
                annotationFactory,
                typeBasedFactories,
                messager);
    }

    @Singleton
    @Provides
    @Update
    AnnotationBasedMethodFactory genericAnnotationMethodFactory(
            @UpdateEntity final AnnotationBasedMethodFactory entityFactory,
            @UpdateProperty final AnnotationBasedMethodFactory propertyFactory,
            final Messager messager) {
        return BaseAnnotationBasedMethodFactory.create(entityFactory, propertyFactory, messager);
    }

    @Singleton
    @Provides
    @UpdateEntity
    AnnotationBasedMethodFactory entityMethodFactory(final Types types,
                                                     @PartialUpdate final MethodSpecFactory methodSpecFactory) {
        return EntityMethodFactory.create(types, methodSpecFactory);
    }

    @Singleton
    @Provides
    @UpdateProperty
    AnnotationBasedMethodFactory propertyMethodFactory(final PropertyExtractor propertyExtractor,
                                                       @TopUpdate final Provider<MethodFactory> methodFactory,
                                                       final PropertyAdapter adapter) {
        return PropertyMethodFactory.create(propertyExtractor, methodFactory, adapter);
    }

}
