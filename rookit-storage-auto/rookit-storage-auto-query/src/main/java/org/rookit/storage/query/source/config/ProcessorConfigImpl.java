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
package org.rookit.storage.query.source.config;

import com.google.inject.Inject;
import org.rookit.auto.config.ProcessorConfig;
import org.rookit.storage.api.config.FilterConfig;
import org.rookit.storage.api.config.QueryConfig;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

final class ProcessorConfigImpl implements ProcessorConfig {

    private final FilterConfig filterConfig;
    private final QueryConfig queryConfig;
    private final Messager messager;

    @Inject
    private ProcessorConfigImpl(final FilterConfig filterConfig,
                                final QueryConfig queryConfig,
                                final Messager messager) {
        this.filterConfig = filterConfig;
        this.queryConfig = queryConfig;
        this.messager = messager;
    }

    @Override
    public boolean isEnabled() {
        final boolean filterEnabled = this.filterConfig.isEnabled();
        final boolean queryEnabled = this.queryConfig.isEnabled();
        if (queryEnabled && !filterEnabled) {
            this.messager.printMessage(Diagnostic.Kind.WARNING, "Considering query processor disabled, as " +
                    "filter processor is not enabled.");
        }
        return filterEnabled && queryEnabled;
    }

    @Override
    public String toString() {
        return "ProcessorConfigImpl{" +
                "filterConfig=" + this.filterConfig +
                ", queryConfig=" + this.queryConfig +
                ", messager=" + this.messager +
                "}";
    }
}
