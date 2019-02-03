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
package org.rookit.storage.update.filter.source.config;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.rookit.auto.config.AutoConfig;
import org.rookit.auto.config.DependencyAwareProcessorConfig;
import org.rookit.auto.config.ProcessorConfig;
import org.rookit.auto.naming.PackageReferenceFactory;
import org.rookit.storage.api.config.FilterConfig;
import org.rookit.storage.api.config.UpdateFilterConfig;

import javax.annotation.processing.Messager;

@SuppressWarnings("MethodMayBeStatic")
public final class ConfigurationModule extends AbstractModule {

    private static final Module MODULE = new ConfigurationModule();

    public static Module getModule() {
        return MODULE;
    }

    private ConfigurationModule() {}

    @Override
    protected void configure() {

    }

    @SuppressWarnings("TypeMayBeWeakened") // due to guice
    @Provides
    @Singleton
    ProcessorConfig processorConfig(final UpdateFilterConfig delegate,
                                    final FilterConfig dependency,
                                    final Messager messager) {
        return DependencyAwareProcessorConfig.create(
                delegate,
                ImmutableSet.of(dependency),
                messager
        );
    }

    @Provides
    @Singleton
    UpdateFilterConfig updateFilterConfig(final AutoConfig config,
                                          final PackageReferenceFactory packageFactory) {
        final String name = "updateFilter";
        return new UpdateFilterConfigImpl(config.getProcessorConfig(name), packageFactory, name);
    }

}
