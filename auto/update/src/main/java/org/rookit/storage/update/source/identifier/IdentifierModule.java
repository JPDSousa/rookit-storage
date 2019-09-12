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
package org.rookit.storage.update.source.identifier;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import org.rookit.storage.guice.PartialUpdate;
import org.rookit.storage.guice.Update;

import static org.rookit.auto.guice.RookitAutoModuleTools.bindIdentifierFactory;

public final class IdentifierModule extends AbstractModule {

    private static final Module MODULE = new IdentifierModule();

    public static Module getModule() {
        return MODULE;
    }

    private IdentifierModule() {}

    @Override
    protected void configure() {
        bindIdentifierFactory(binder(), Update.class).toProvider(UpdateJavaPoetIdentifierFactoryProvider.class)
                .in(Singleton.class);
        bindIdentifierFactory(binder(), PartialUpdate.class)
                .toProvider(PartialUpdateJavaPoetIdentifierFactoryProvider.class).in(Singleton.class);
    }

}
