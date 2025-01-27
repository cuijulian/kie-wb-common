/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.uberfire.backend.vfs.Path;

@Dependent
public class DMNClientServicesProxyImpl implements DMNClientServicesProxy {

    @Override
    public void loadModels(final Path path,
                           final ServiceCallback<List<IncludedModel>> callback) {
        callback.onSuccess(Collections.emptyList());
    }

    @Override
    public void loadNodesFromImports(final List<DMNIncludedModel> includedModels,
                                     final ServiceCallback<List<DMNIncludedNode>> callback) {
        callback.onSuccess(Collections.emptyList());
    }

    @Override
    public void loadPMMLDocumentsFromImports(final Path path,
                                             final List<PMMLIncludedModel> includedModels,
                                             final ServiceCallback<List<PMMLDocumentMetadata>> callback) {
        callback.onSuccess(Collections.emptyList());
    }

    @Override
    public void loadItemDefinitionsByNamespace(final String modelName, String namespace,
                                               final ServiceCallback<List<ItemDefinition>> callback) {
        callback.onSuccess(Collections.emptyList());
    }

    @Override
    public void parseFEELList(final String source,
                              final ServiceCallback<List<String>> callback) {
        callback.onSuccess(FEELListParser.parse(source));
    }

    @Override
    public void parseRangeValue(final String source,
                                final ServiceCallback<RangeValue> callback) {
        callback.onSuccess(FEELRangeParser.parse(source));
    }

    @Override
    public void isValidVariableName(final String source,
                                    final ServiceCallback<Boolean> callback) {
        callback.onSuccess(true);
    }

    @Override
    public void getTimeZones(final ServiceCallback<List<DMNSimpleTimeZone>> callback) {
        callback.onSuccess(Collections.emptyList());
    }

    @Override
    public void loadDataObjects(final ServiceCallback<List<DataObject>> callback) {
        callback.onSuccess(Collections.emptyList());
    }
}
