package jrm.webui.client.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.utils.EnhJSO;

/**
 * Panel containing systems, sources, and filter form controls.
 *
 * @since 2.5
 */
public final class ScannerFiltersPanel extends HLayout // NOSONAR
{
    private static final String PROPERTY = "property";
    private static final String SELECTED = "selected";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String BIOS = "BIOS";
    private static final String SOFTWARELIST = "SOFTWARELIST";
    private static final String MN_SELECT = "MainFrame.mnSelect.text";
    private static final String MN_UNSELECT = "MainFrame.mnUnselect.text";
    private static final String MNTM_SELECT_ALL = "MainFrame.mntmSelectAll.text";
    private static final String MNTM_SELECT_NONE = "MainFrame.mntmSelectNone.text";
    private static final String MNTM_ALL_BIOS = "MainFrame.mntmAllBios.text";
    private static final String MNTM_ALL_SOFTWARES = "MainFrame.mntmAllSoftwares.text";
    private static final String MNTM_INVERT_SELECTION = "MainFrame.mntmInvertSelection.text";

    Systems systems;
    Sources sources;
    FilterForm filterForm;

    /**
     * Common base for the {@link Systems} and {@link Sources} filter grids, holding the shared
     * configuration, selection synchronization and context-menu builders so the subclasses only
     * declare their grid-specific items.
     */
    abstract class FilterListGrid extends ListGrid // NOSONAR
    {
        /** When {@code false} selection changes are not propagated to the server (used while loading data). */
        protected boolean sendPropertyUpdates = true;

        protected FilterListGrid(String titleMsg) {
            setShowAllRecords(true);
            setAlternateRecordStyles(false);
            setSelectionProperty(SELECTED);
            setFields(new ListGridField(NAME, titleMsg));
            setSelectionAppearance(SelectionAppearance.CHECKBOX);
            setShowSelectedStyle(false);
            setCanEdit(false);
            setCanRemoveRecords(false);
            addSelectionChangedHandler(event -> {
                if (sendPropertyUpdates) {
                    Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute(PROPERTY), event.getState())));
                    if (ProfileViewer.canResetPV)
                        ProfileViewer.reset();
                }
            });
        }

        /** Builds the "Select all" menu item. */
        protected final MenuItem buildSelectAllMenuItem() {
            MenuItem item = new MenuItem(Client.getSession().getMsg(MNTM_SELECT_ALL));
            item.addClickHandler(event -> ProfileViewer.reset(this::selectAllRecords));
            return item;
        }

        /** Builds the "Select none" menu item. */
        protected final MenuItem buildSelectNoneMenuItem() {
            MenuItem item = new MenuItem(Client.getSession().getMsg(MNTM_SELECT_NONE));
            item.addClickHandler(event -> ProfileViewer.reset(this::deselectAllRecords));
            return item;
        }

        /** Builds the "Invert selection" menu item. */
        protected final MenuItem buildInvertSelectionMenuItem() {
            MenuItem item = new MenuItem(Client.getSession().getMsg(MNTM_INVERT_SELECTION));
            item.addClickHandler(event -> ProfileViewer.reset(this::invertSelection));
            return item;
        }

        /** Inverts the current selection. */
        private void invertSelection() {
            ListGridRecord[] toUnselect = getSelectedRecords();
            List<ListGridRecord> toUnselectList = Arrays.asList(toUnselect);
            ListGridRecord[] toSelect = Stream.of(getRecords()).filter(r -> !toUnselectList.contains(r)).toList().toArray(new ListGridRecord[0]);
            deselectRecords(toUnselect);
            selectRecords(toSelect);
        }

        @Override
        public ListGrid setData(Record[] data) {
            ProfileViewer.reset(() -> {
                sendPropertyUpdates = false;
                super.setData(data);
                sendPropertyUpdates = true;
            });
            return this;
        }
    }

    /**
     * The systems filter grid, with additional "select/deselect by type" menu entries.
     */
    class Systems extends FilterListGrid // NOSONAR
    {
        public Systems() {
            super(Client.getSession().getMsg("MainFrame.systemsFilter.viewportBorderTitle"));
            setContextMenu(buildSystemsMenu());
        }

        private Menu buildSystemsMenu() {
            Menu menu = new Menu();
            menu.setItems(buildSelectMenuItem(), buildUnselectMenuItem(), buildInvertSelectionMenuItem());
            return menu;
        }

        private MenuItem buildSelectMenuItem() {
            MenuItem item = new MenuItem();
            item.setTitle(Client.getSession().getMsg(MN_SELECT));
            Menu submenu = new Menu();
            submenu.setItems(
                    buildSelectAllMenuItem(),
                    buildSelectByTypeMenuItem(MNTM_ALL_BIOS, BIOS, true),
                    buildSelectByTypeMenuItem(MNTM_ALL_SOFTWARES, SOFTWARELIST, true));
            item.setSubmenu(submenu);
            return item;
        }

        private MenuItem buildUnselectMenuItem() {
            MenuItem item = new MenuItem();
            item.setTitle(Client.getSession().getMsg(MN_UNSELECT));
            Menu submenu = new Menu();
            submenu.setItems(
                    buildSelectNoneMenuItem(),
                    buildSelectByTypeMenuItem(MNTM_ALL_BIOS, BIOS, false),
                    buildSelectByTypeMenuItem(MNTM_ALL_SOFTWARES, SOFTWARELIST, false));
            item.setSubmenu(submenu);
            return item;
        }

        /**
         * Builds a menu item that selects or deselects all records matching the given type.
         *
         * @param titleMsg the i18n message key used as title
         * @param type     the record {@code type} attribute value to match
         * @param select   {@code true} to select the matching records, {@code false} to deselect them
         * @return the configured menu item
         */
        private MenuItem buildSelectByTypeMenuItem(String titleMsg, String type, boolean select) {
            MenuItem item = new MenuItem(Client.getSession().getMsg(titleMsg));
            item.addClickHandler(event -> ProfileViewer.reset(() -> {
                ListGridRecord[] records = Stream.of(getRecords()).filter(r -> type.equals(r.getAttribute(TYPE))).toList().toArray(new ListGridRecord[0]);
                if (select)
                    selectRecords(records);
                else
                    deselectRecords(records);
            }));
            return item;
        }
    }

    /**
     * The sources filter grid, reusing the common select/none/invert menu items.
     */
    class Sources extends FilterListGrid // NOSONAR
    {
        public Sources() {
            super(Client.getSession().getMsg("MainFrame.sourcesFilter.viewportBorderTitle"));
            setContextMenu(buildSourcesMenu());
        }

        private Menu buildSourcesMenu() {
            Menu menu = new Menu();
            menu.setItems(buildSelectAllMenuItem(), buildSelectNoneMenuItem(), buildInvertSelectionMenuItem());
            return menu;
        }
    }

    /**
     * The filter form holding the include/orientation/status/year filter controls.
     */
    class FilterForm extends SettingsForm // NOSONAR
    {
        public FilterForm() {
            this(null);
        }

        public FilterForm(EnhJSO settings) {
            setWidth("80%");
            setLayoutAlign(Alignment.CENTER);
            setNumCols(3);
            setColWidths("*", 80, "*");
            setItems(
                    buildBooleanCheckbox("chckbxIncludeClones", "MainFrame.chckbxIncludeClones.text"),
                    buildBooleanCheckbox("chckbxIncludeDisks", "MainFrame.chckbxIncludeDisks.text"),
                    buildBooleanCheckbox("chckbxIncludeSamples", "MainFrame.chckbxIncludeSamples.text"),
                    buildSelectItem("cbMachineType", "MainFrame.lblMachineType.text", new String[] {"any", "upright", "cocktail"}, "any"),
                    buildSelectItem("cbOrientation", "MainFrame.lblOrientation.text", new String[] {"any", "horizontal", "vertical"}, "any"),
                    buildSelectItem("cbDriverStatus", "MainFrame.lblDriverStatus.text", new String[] {"good", "imperfect", "preliminary"}, "preliminary"),
                    buildSelectItem("cbSwMinSupport", "MainFrame.lblSwMinSupport.text", new String[] {"no", "partial", "yes"}, "no"),
                    buildYearMinSelect(),
                    buildYearLabel(),
                    buildYearMaxSelect());
            if (hasSettings)
                initPropertyItemValues(settings);
        }

        /**
         * Builds a boolean checkbox bound to its profile property.
         *
         * @param name     the form item name (mapped through {@code fname2name})
         * @param titleMsg the i18n message key used as title
         * @return the configured checkbox
         */
        private CheckboxItem buildBooleanCheckbox(String name, String titleMsg) {
            CheckboxItem item = new CheckboxItem(name, Client.getSession().getMsg(titleMsg));
            item.setTitleColSpan(2);
            item.setLabelAsTitle(true);
            item.addChangedHandler(event -> ProfileViewer.reset(() -> setPropertyItemValue(item.getName(), fname2name.get(item.getName()), (boolean) item.getValue())));
            item.setDefaultValue(true);
            return item;
        }

        /**
         * Builds a select item bound to its profile property.
         *
         * @param name         the form item name (mapped through {@code fname2name})
         * @param titleMsg     the i18n message key used as title
         * @param valueMap     the selectable values
         * @param defaultValue the default selected value
         * @return the configured select item
         */
        private SelectItem buildSelectItem(String name, String titleMsg, String[] valueMap, String defaultValue) {
            SelectItem item = new SelectItem(name, Client.getSession().getMsg(titleMsg));
            item.setTitleColSpan(2);
            item.setWidth("*");
            item.setValueMap(valueMap);
            item.setDefaultValue(defaultValue);
            item.addChangedHandler(event -> ProfileViewer.reset(() -> setPropertyItemValue(item.getName(), fname2name.get(item.getName()), item.getValue().toString())));
            return item;
        }

        /** Builds the minimum-year select item (no title, ends the row). */
        private SelectItem buildYearMinSelect() {
            SelectItem item = new SelectItem("cbYearMin");
            configureYearSelect(item);
            item.setEndRow(false);
            return item;
        }

        /** Builds the maximum-year select item (no title, starts the row). */
        private SelectItem buildYearMaxSelect() {
            SelectItem item = new SelectItem("cbYearMax");
            configureYearSelect(item);
            item.setStartRow(false);
            return item;
        }

        /** Applies the shared configuration to a year select item. */
        private void configureYearSelect(SelectItem item) {
            item.setShowTitle(false);
            item.setWidth("*");
            item.addChangedHandler(event -> ProfileViewer.reset(() -> setPropertyItemValue(item.getName(), fname2name.get(item.getName()), item.getValue().toString())));
        }

        /** Builds the centered "Year" label displayed between the two year selects. */
        private StaticTextItem buildYearLabel() {
            StaticTextItem item = new StaticTextItem();
            item.setShowTitle(false);
            item.setDefaultValue(Client.getSession().getMsg("MainFrame.lblYear.text"));
            item.setTextAlign(Alignment.CENTER);
            item.setWidth("*");
            return item;
        }
    }

    public ScannerFiltersPanel() {
        super();
        setWidth100();
        setHeight100();
        final var left = new VLayout();
        left.setShowResizeBar(true);
        left.addMember(new LayoutSpacer("*", "*"));
        filterForm = new FilterForm();
        left.addMember(filterForm);
        left.addMember(new LayoutSpacer("*", "*"));
        final var right = new VLayout();
        systems = new Systems();
        systems.setShowResizeBar(true);
        right.addMember(systems);
        sources = new Sources();
        right.addMember(sources);
        setMembers(left, right);
    }

}
