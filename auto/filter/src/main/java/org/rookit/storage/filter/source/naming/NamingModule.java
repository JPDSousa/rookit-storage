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
package org.rookit.storage.filter.source.naming;

import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.naming.AbstractNamingModule;
import org.rookit.auto.naming.BaseJavaPoetNamingFactory;
import org.rookit.auto.naming.PackageReference;
import org.rookit.storage.api.config.FilterConfig;
import org.rookit.storage.guice.filter.Filter;
import org.rookit.storage.guice.filter.PartialFilter;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@SuppressWarnings("MethodMayBeStatic")
public final class NamingModule extends AbstractNamingModule {

    private static final Module MODULE = new NamingModule();

    public static Module getModule() {
        return MODULE;
    }

    private NamingModule() {}

    @Override
    protected void configure() {
        bindNaming(PartialFilter.class);
        bindNaming(Filter.class);
    }

    @Singleton
    @Provides
    PackageReference basePackage(final FilterConfig filterConfig) {
        return filterConfig.basePackage();
    }

    @Singleton
    @Provides
    @Filter
    static JavaPoetNamingFactory filterEntityNamingFactory(final PackageReference packageReference,
                                                           final FilterConfig config) {
        // TODO in the future this mapping logic between FilterConfig and BaseJavaPoetNamingFactory might be moved into
        // TODO a dedicated implementation of JavaPoetNamingFactory
        return BaseJavaPoetNamingFactory.create(packageReference, config.entitySuffix(), EMPTY, config.methodPrefix());
    }

    @Singleton
    @Provides
    @PartialFilter
    JavaPoetNamingFactory filterNamingFactory(final PackageReference packageReference,
                                              final FilterConfig config) {
        // TODO in the future this mapping logic between FilterConfig and BaseJavaPoetNamingFactory might be moved into
        // TODO a dedicated implementation of JavaPoetNamingFactory
        return BaseJavaPoetNamingFactory.create(packageReference, config.entitySuffix(),
                config.partialEntityPrefix(),
                config.methodPrefix());
    }
}
