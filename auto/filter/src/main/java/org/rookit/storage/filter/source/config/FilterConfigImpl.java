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
package org.rookit.storage.filter.source.config;

import com.squareup.javapoet.TypeVariableName;
import org.rookit.auto.naming.PackageReference;
import org.rookit.config.Configuration;
import org.rookit.storage.api.config.FilterConfig;

final class FilterConfigImpl implements FilterConfig {

    private final Configuration configuration;
    private final PackageReference basePackage;
    private final String partialEntityPrefix;
    private final String name;

    FilterConfigImpl(final Configuration configuration,
                     final PackageReference basePackage,
                     final String partialEntityPrefix,
                     final String name) {
        this.configuration = configuration;
        this.basePackage = basePackage;
        this.partialEntityPrefix = partialEntityPrefix;
        this.name = name;
    }

    @Override
    public TypeVariableName parameterName() {
        return TypeVariableName.get(this.configuration.getString("parameterName"));
    }

    @Override
    public PackageReference basePackage() {
        return this.basePackage.resolve(this.configuration.getString("basePackage"));
    }

    @Override
    public String entitySuffix() {
        return this.configuration.getString("entitySuffix");
    }

    @Override
    public String partialEntityPrefix() {
        return this.partialEntityPrefix;
    }

    @Override
    public String methodPrefix() {
        return this.configuration.getString("methodPrefix");
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return this.configuration.getBoolean("enabled");
    }

    @Override
    public String toString() {
        return "FilterConfigImpl{" +
                "configuration=" + this.configuration +
                ", basePackage=" + this.basePackage +
                ", partialEntityPrefix='" + this.partialEntityPrefix + '\'' +
                "}";
    }
}
