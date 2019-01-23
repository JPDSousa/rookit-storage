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
package org.rookit.storage.update.source.config;

import com.google.inject.Inject;
import org.rookit.auto.config.ProcessorConfig;
import org.rookit.storage.api.config.FilterConfig;
import org.rookit.storage.api.config.UpdateConfig;
import org.rookit.storage.api.config.UpdateFilterConfig;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

final class ProcessorConfigImpl implements ProcessorConfig {

    private final FilterConfig filterConfig;
    private final UpdateFilterConfig updateFilterConfig;
    private final UpdateConfig updateConfig;
    private final Messager messager;

    @Inject
    private ProcessorConfigImpl(final FilterConfig filterConfig,
                                final UpdateFilterConfig updateFilterConfig,
                                final UpdateConfig updateConfig,
                                final Messager messager) {
        this.filterConfig = filterConfig;
        this.updateFilterConfig = updateFilterConfig;
        this.updateConfig = updateConfig;
        this.messager = messager;
    }

    @Override
    public boolean isEnabled() {
        final boolean filterEnabled = this.filterConfig.isEnabled();
        final boolean updateFilterEnabled = this.updateFilterConfig.isEnabled();
        final boolean updateEnabled = this.updateConfig.isEnabled();

        if (updateEnabled && !filterEnabled) {
            this.messager.printMessage(Diagnostic.Kind.WARNING, "Considering update processor disabled, as " +
                    "filter processor is disabled.");
        } else if (updateEnabled && !updateFilterEnabled) {
            this.messager.printMessage(Diagnostic.Kind.WARNING, "Considering update processor disabled, as " +
                    "update filter processor is disabled.");
        }

        return filterEnabled && updateFilterEnabled && updateEnabled;
    }

    @Override
    public String toString() {
        return "ProcessorConfigImpl{" +
                "filterConfig=" + this.filterConfig +
                ", updateFilterConfig=" + this.updateFilterConfig +
                ", updateConfig=" + this.updateConfig +
                ", messager=" + this.messager +
                "}";
    }
}
