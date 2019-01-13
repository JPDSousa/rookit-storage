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
package org.rookit.storage.filter.source.method.type;

import com.google.inject.Inject;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.auto.javax.ExtendedTypeMirror;
import org.rookit.auto.javax.ExtendedTypeMirrorFactory;
import org.rookit.storage.utils.filter.PartialFilter;

import java.time.LocalDate;

final class LocalDateMethodFactory extends AbstractTimeMethodFactory {

    private final ExtendedTypeMirror type;

    @Inject
    private LocalDateMethodFactory(final ExtendedTypeMirrorFactory factory,
                                   @PartialFilter final MethodSpecFactory methodSpecFactory) {
        super(methodSpecFactory);
        this.type = factory.createWithErasure(LocalDate.class);
    }

    @Override
    public ExtendedTypeMirror type() {
        return this.type;
    }

    @Override
    public String toString() {
        return "LocalDateMethodFactory{" +
                "type=" + this.type +
                "} " + super.toString();
    }
}
