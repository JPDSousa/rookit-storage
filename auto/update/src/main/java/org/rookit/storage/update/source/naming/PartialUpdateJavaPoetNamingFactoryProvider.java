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
package org.rookit.storage.update.source.naming;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactories;
import org.rookit.auto.javapoet.naming.JavaPoetNamingFactory;
import org.rookit.auto.javax.pack.ExtendedPackageElement;
import org.rookit.storage.api.config.UpdateConfig;
import org.rookit.storage.guice.Update;

final class PartialUpdateJavaPoetNamingFactoryProvider implements Provider<JavaPoetNamingFactory> {

    private final JavaPoetNamingFactories factories;
    private final ExtendedPackageElement extendedPackageElement;
    private final UpdateConfig config;

    @Inject
    private PartialUpdateJavaPoetNamingFactoryProvider(final JavaPoetNamingFactories factories,
                                                       @Update final ExtendedPackageElement packageElement,
                                                       final UpdateConfig config) {
        this.factories = factories;
        this.extendedPackageElement = packageElement;
        this.config = config;
    }

    @Override
    public JavaPoetNamingFactory get() {
        return this.factories.create(this.extendedPackageElement,
                this.config.entityTemplate(),
                this.config.methodTemplate());
    }

    @Override
    public String toString() {
        return "PartialUpdateJavaPoetNamingFactoryProvider{" +
                "factories=" + this.factories +
                ", extendedPackageElement=" + this.extendedPackageElement +
                ", config=" + this.config +
                "}";
    }
}
