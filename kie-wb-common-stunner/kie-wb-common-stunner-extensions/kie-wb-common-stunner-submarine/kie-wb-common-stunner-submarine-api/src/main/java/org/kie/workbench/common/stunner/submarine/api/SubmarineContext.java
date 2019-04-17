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

package org.kie.workbench.common.stunner.submarine.api;

/**
 * TODO: Refactor as just SOURCE scoped annotation
 * <p>
 * Using this interface just to easily identify all contexts resulting from the functional decomposition being performed
 * during the submarine initiative refactoring.
 * Any component, service or whatever managed bean that requires access to any remote context should, at least, provide
 * two implementations for its declared SubmarineContext subtype:
 * <ul>
 * <li>The "Standalone" implementation - This implementation targets submarine based editors - it should depend as less as possible on backend</li>
 * <li>The "Project" implementation - This implementation targets KIE Workbench based editors - it's able to depend on all KIE related services</li>
 * </ul>
 */
public interface SubmarineContext {

}
