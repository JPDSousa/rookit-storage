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
package org.rookit.storage.update.source.method.template;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.rookit.storage.update.source.guice.Add;
import org.rookit.storage.update.source.guice.AddAll;
import org.rookit.storage.update.source.guice.Remove;
import org.rookit.storage.update.source.guice.RemoveAll;
import org.rookit.utils.string.template.Template1;
import org.rookit.utils.string.template.TemplateFactory;

@SuppressWarnings("MethodMayBeStatic")
public final class TemplateModule extends AbstractModule {

    private static final Module MODULE = new TemplateModule();

    public static Module getModule() {
        return MODULE;
    }

    private TemplateModule() {}

    @Provides
    @Singleton
    @Add
    Template1 addTemplate(final TemplateFactory templateFactory) {
        return templateFactory.template1("addAll{}");
    }

    @Provides
    @Singleton
    @AddAll
    Template1 addAllTemplate(final TemplateFactory templateFactory) {
        return templateFactory.template1("addAll{}");
    }

    @Provides
    @Singleton
    @Remove
    Template1 removeTemplate(final TemplateFactory templateFactory) {
        return templateFactory.template1("remove{}");
    }

    @Provides
    @Singleton
    @RemoveAll
    Template1 removeAllTemplate(final TemplateFactory templateFactory) {
        return templateFactory.template1("removeAll{}");
    }
}
