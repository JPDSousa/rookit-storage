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
package org.rookit.storage.filter.source.method.type.collection;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import one.util.streamex.StreamEx;
import org.rookit.auto.javapoet.method.MethodSpecFactory;
import org.rookit.convention.auto.javapoet.method.ConventionTypeElementMethodSpecVisitors;
import org.rookit.convention.auto.javax.ConventionTypeElement;
import org.rookit.convention.auto.javax.visitor.ConventionTypeElementVisitor;
import org.rookit.storage.filter.source.guice.No;
import org.rookit.storage.guice.filter.PartialFilter;
import org.rookit.utils.adapt.Adapter;
import org.rookit.utils.guice.Collection;
import org.rookit.utils.guice.Self;
import org.rookit.utils.string.template.Template1;

import javax.lang.model.type.TypeMirror;

final class CollectionMethodVisitor implements Provider<ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void>> {

    private final ConventionTypeElementMethodSpecVisitors visitors;
    private final Adapter<ConventionTypeElement> collectionAdapter;
    private final Adapter<ConventionTypeElement> collectionUnwrapper;
    private final MethodSpecFactory methodSpecFactory;
    private final Template1 notContainsTemplate;
    private final Template1 containsTemplate;

    @Inject
    private CollectionMethodVisitor(final ConventionTypeElementMethodSpecVisitors visitors,
                                    @Collection final Adapter<ConventionTypeElement> collectionAdapter,
                                    @Collection(unwrap = true) final Adapter<ConventionTypeElement> collectionUnwrapper,
                                    @PartialFilter final MethodSpecFactory methodSpecFactory,
                                    @No final Template1 notContainsTemplate,
                                    @Self final Template1 containsTemplate) {
        this.visitors = visitors;
        this.collectionAdapter = collectionAdapter;
        this.collectionUnwrapper = collectionUnwrapper;
        this.methodSpecFactory = methodSpecFactory;
        this.notContainsTemplate = notContainsTemplate;
        this.containsTemplate = containsTemplate;
    }

    @Override
    public ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> get() {
        // unwrap collections
        final ConventionTypeElementVisitor<StreamEx<MethodSpec>, Void> baseVisitor = createBaseVisitor();
        return this.visitors.streamExConventionBuilder(baseVisitor)
                .withConventionTypeAdapter(this.collectionAdapter)
                .add(this.visitors.streamExConventionBuilder(baseVisitor)
                        .withConventionTypeAdapter(this.collectionUnwrapper)
                        .build())
                .build();
    }

    private <P> ConventionTypeElementVisitor<StreamEx<MethodSpec>, P> createBaseVisitor() {
        return this.visitors.<P>templateMethodSpecVisitorBuilder(
                this.methodSpecFactory,
                this.containsTemplate,
                property -> containsParam(property.type())
        ).add(
                this.visitors
                        .<P>templateMethodSpecVisitorBuilder(
                                this.methodSpecFactory,
                                this.notContainsTemplate,
                                property -> notContainsParam(property.type())
                        ).build()
        ).build();
    }

    private java.util.Collection<ParameterSpec> containsParam(final TypeMirror typeMirror) {
        // TODO this name should be configurable
        return createParam(typeMirror, "present");
    }

    private java.util.Collection<ParameterSpec> notContainsParam(final TypeMirror typeMirror) {
        // TODO this name should be configurable
        return createParam(typeMirror, "absent");
    }

    private java.util.Collection<ParameterSpec> createParam(final TypeMirror typeMirror, final String paramName) {
        return ImmutableList.of(ParameterSpec.builder(TypeName.get(typeMirror), paramName).build());
    }

    @Override
    public String toString() {
        return "CollectionMethodVisitor{" +
                "visitors=" + this.visitors +
                ", collectionAdapter=" + this.collectionAdapter +
                ", collectionUnwrapper=" + this.collectionUnwrapper +
                ", methodSpecFactory=" + this.methodSpecFactory +
                ", notContainsTemplate=" + this.notContainsTemplate +
                ", containsTemplate=" + this.containsTemplate +
                "}";
    }
}
