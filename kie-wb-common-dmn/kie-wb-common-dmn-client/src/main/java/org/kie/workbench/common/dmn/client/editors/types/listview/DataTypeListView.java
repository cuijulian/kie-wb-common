/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListComponent;
import org.uberfire.client.views.pfly.selectpicker.ElementHelper;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.isHidden;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.ARROW_BUTTON_SELECTOR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.PARENT_UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.isRightArrow;
import static org.uberfire.client.views.pfly.selectpicker.ElementHelper.remove;

@Templated
@ApplicationScoped
public class DataTypeListView implements DataTypeList.View {

    @DataField("list-items")
    private final HTMLDivElement listItems;

    @DataField("placeholder")
    private final HTMLDivElement placeholder;

    @DataField("collapsed-description")
    private final HTMLDivElement collapsedDescription;

    @DataField("expanded-description")
    private final HTMLDivElement expandedDescription;

    @DataField("view-more")
    private final HTMLAnchorElement viewMore;

    @DataField("view-less")
    private final HTMLAnchorElement viewLess;

    @DataField("add-button")
    private final HTMLButtonElement addButton;

    @DataField("search-bar-container")
    private final HTMLDivElement searchBarContainer;

    @DataField("expand-all")
    private final HTMLAnchorElement expandAll;

    @DataField("collapse-all")
    private final HTMLAnchorElement collapseAll;

    @DataField("no-data-types-found")
    private final HTMLDivElement noDataTypesFound;

    @DataField("read-only-message")
    private final HTMLDivElement readOnlyMessage;

    @DataField("read-only-message-close-button")
    private final HTMLButtonElement readOnlyMessageCloseButton;

    @DataField("import-data-object-button")
    private final HTMLButtonElement importDataObjectButton;

    private final ScrollHelper scrollHelper;

    private final ImportDataObjectModal importDataObjectModal;

    private DataTypeList presenter;

    @Inject
    public DataTypeListView(final HTMLDivElement listItems,
                            final HTMLDivElement collapsedDescription,
                            final HTMLDivElement expandedDescription,
                            final HTMLAnchorElement viewMore,
                            final HTMLAnchorElement viewLess,
                            final HTMLButtonElement addButton,
                            final HTMLDivElement placeholder,
                            final HTMLDivElement searchBarContainer,
                            final HTMLAnchorElement expandAll,
                            final HTMLAnchorElement collapseAll,
                            final HTMLDivElement noDataTypesFound,
                            final HTMLDivElement readOnlyMessage,
                            final HTMLButtonElement readOnlyMessageCloseButton,
                            final ScrollHelper scrollHelper,
                            final HTMLButtonElement importDataObjectButton,
                            final ImportDataObjectModal importDataObjectModal) {
        this.listItems = listItems;
        this.collapsedDescription = collapsedDescription;
        this.expandedDescription = expandedDescription;
        this.viewMore = viewMore;
        this.viewLess = viewLess;
        this.addButton = addButton;
        this.placeholder = placeholder;
        this.searchBarContainer = searchBarContainer;
        this.expandAll = expandAll;
        this.collapseAll = collapseAll;
        this.noDataTypesFound = noDataTypesFound;
        this.readOnlyMessage = readOnlyMessage;
        this.readOnlyMessageCloseButton = readOnlyMessageCloseButton;
        this.scrollHelper = scrollHelper;
        this.importDataObjectButton = importDataObjectButton;
        this.importDataObjectModal = importDataObjectModal;
    }

    @Override
    public void init(final DataTypeList presenter) {
        this.presenter = presenter;

        setupSearchBar();
        importDataObjectModal.setup(this::importDataObjects);
        setupListElement();
    }

    void importDataObjects(final List<DataObject> imported) {
        presenter.importDataObjects(imported);
    }

    private void setupSearchBar() {
        searchBarContainer.appendChild(presenter.getSearchBar().getElement());
    }

    @PostConstruct
    public void setup() {
        collapseDescription();
    }

    private void setupListElement() {
        listItems.appendChild(getDndListComponent().getElement());
    }

    private DNDListComponent getDndListComponent() {
        return presenter.getDNDListComponent();
    }

    @Override
    public void showOrHideNoCustomItemsMessage() {
        if (!hasCustomDataType()) {
            showPlaceHolder();
        } else {
            showListItems();
        }
    }

    boolean hasCustomDataType() {
        final NodeList<Element> childNodes = listItems.querySelectorAll("[" + UUID_ATTR + "]");
        return !Objects.isNull(childNodes) && childNodes.length > 0;
    }

    @Override
    public void addSubItems(final DataType dataType,
                            final List<DataTypeListItem> listItems) {

        Element parent = getDataTypeRow(dataType);

        for (final DataTypeListItem item : listItems) {

            final HTMLElement itemElement = item.getDragAndDropElement();

            hideItemElementIfParentIsCollapsed(itemElement, parent);

            ElementHelper.insertAfter(itemElement, parent);
            parent = itemElement;
        }

        showArrowIconIfDataTypeHasChildren(dataType);
        showOrHideNoCustomItemsMessage();
    }

    @EventHandler("expand-all")
    public void expandAll(final ClickEvent e) {
        presenter.expandAll();
    }

    @EventHandler("collapse-all")
    public void collapseAll(final ClickEvent e) {
        presenter.collapseAll();
    }

    @EventHandler("add-button")
    public void onAddClick(final ClickEvent e) {
        scrollHelper.animatedScrollToBottom(listItems);
        presenter.addDataType();
    }

    @EventHandler("import-data-object-button")
    public void onImportDataObjectClick(final ClickEvent e) {
        importDataObjectModal.show();
    }

    @EventHandler("read-only-message-close-button")
    public void onReadOnlyMessageCloseButtonClick(final ClickEvent e) {
        hide(readOnlyMessage);
    }

    void hideItemElementIfParentIsCollapsed(final HTMLElement itemElement,
                                            final Element parent) {

        final boolean isCollapsedParent = isCollapsed(parent.querySelector(ARROW_BUTTON_SELECTOR));
        final boolean isHiddenParent = isHidden(parent);

        if (isCollapsedParent || isHiddenParent) {
            hide(itemElement);
        } else {
            show(itemElement);
        }
    }

    void showArrowIconIfDataTypeHasChildren(final DataType dataType) {
        if (hasChildren(dataType)) {
            show(getDataTypeRow(dataType).querySelector(ARROW_BUTTON_SELECTOR));
        } else {
            hide(getDataTypeRow(dataType).querySelector(ARROW_BUTTON_SELECTOR));
        }
    }

    private boolean hasChildren(final DataType dataType) {
        return listItems.querySelectorAll("[" + PARENT_UUID_ATTR + "=\"" + dataType.getUUID() + "\"]").length > 0;
    }

    @Override
    public void removeItem(final DataType dataType) {

        cleanSubTypes(dataType.getUUID());

        final Optional<Element> dataTypeRow = Optional.ofNullable(getDataTypeRow(dataType));

        dataTypeRow.ifPresent(this::removeDataTypeRow);

        showOrHideNoCustomItemsMessage();
    }

    @Override
    public void cleanSubTypes(final DataType dataType) {
        cleanSubTypes(dataType.getUUID());
    }

    void cleanSubTypes(final String uuid) {

        final String selector = "[" + PARENT_UUID_ATTR + "=\"" + uuid + "\"]";
        final NodeList<Element> subDataTypeRows = listItems.querySelectorAll(selector);

        for (int i = 0; i < subDataTypeRows.length; i++) {
            final Element item = subDataTypeRows.getAt(i);
            if (item != null && item.parentNode != null) {
                cleanSubTypes(item.getAttribute(UUID_ATTR));
                removeDataTypeRow(item);
            }
        }
    }

    private void removeDataTypeRow(final Element item) {
        presenter.removeItem(item.getAttribute(UUID_ATTR));
        remove(item);
    }

    @Override
    public void insertBelow(final DataTypeListItem listItem,
                            final DataType reference) {

        final Element elementReference = getLastSubDataTypeElement(reference);
        ElementHelper.insertAfter(listItem.getDragAndDropElement(), elementReference);
    }

    @Override
    public void insertAbove(final DataTypeListItem listItem,
                            final DataType reference) {

        final Element elementReference = getDataTypeRow(reference);
        ElementHelper.insertBefore(listItem.getDragAndDropElement(), elementReference);
    }

    private boolean isCollapsed(final Element arrow) {
        return isRightArrow(arrow);
    }

    @EventHandler("view-more")
    public void onClickViewMore(final ClickEvent event) {
        expandDescription();
    }

    @EventHandler("view-less")
    public void onClickViewLess(final ClickEvent event) {
        collapseDescription();
    }

    void expandDescription() {
        collapsedDescription.hidden = true;
        expandedDescription.hidden = false;
        viewLess.hidden = false;
        viewMore.hidden = true;
    }

    void collapseDescription() {
        collapsedDescription.hidden = false;
        expandedDescription.hidden = true;
        viewLess.hidden = true;
        viewMore.hidden = false;
    }

    Element getDataTypeRow(final DataType dataType) {
        return listItems.querySelector("[" + UUID_ATTR + "=\"" + dataType.getUUID() + "\"]");
    }

    Element getLastSubDataTypeElement(final DataType reference) {
        return getLastSubDataTypeElement(getDataTypeRow(reference));
    }

    Element getLastSubDataTypeElement(final Element element) {

        final String parentUUID = element.getAttribute(UUID_ATTR);
        final List<Element> nestedElements = getNestedElements(parentUUID);

        if (nestedElements.isEmpty()) {
            return element;
        } else {
            return getLastSubDataTypeElement(nestedElements.get(nestedElements.size() - 1));
        }
    }

    private List<Element> getNestedElements(final String parentUUID) {

        final String selector = "[" + PARENT_UUID_ATTR + "=\"" + parentUUID + "\"]";
        final NodeList<Element> nestedElements = listItems.querySelectorAll(selector);
        final List<Element> list = new ArrayList<>();

        for (int i = 0; i < nestedElements.length; i++) {
            final Element element = nestedElements.getAt(i);
            final boolean isVisible = getDndListComponent().getPositionY(element) > -1;
            if (isVisible) {
                list.add(element);
            }
        }

        return list;
    }

    @Override
    public void showNoDataTypesFound() {
        show(noDataTypesFound);
        hide(placeholder);
        hide(listItems);
    }

    void showListItems() {
        hide(noDataTypesFound);
        hide(placeholder);
        show(listItems);
    }

    void showPlaceHolder() {
        hide(noDataTypesFound);
        show(placeholder);
        hide(listItems);
    }

    @Override
    public void showReadOnlyMessage(final boolean show) {
        if (show) {
            show(readOnlyMessage);
        } else {
            hide(readOnlyMessage);
        }
    }

    @Override
    public HTMLDivElement getListItems() {
        return listItems;
    }
}
