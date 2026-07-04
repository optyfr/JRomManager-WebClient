package jrm.webui.client.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.DataArrivedEvent;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_CatVer;
import jrm.webui.client.protocol.Q_NPlayers;
import jrm.webui.client.protocol.Q_Profile;

/**
 * Advanced filters panel with category/version and player count filters.
 *
 * @since 2.5
 */
public final class ScannerAdvFiltersPanel extends HLayout /* NOSONAR */ {
    /** Record attribute indicating whether a node is selected. */
    private static final String IS_SELECTED = "isSelected";
    /** Record attribute indicating whether a node is a folder. */
    private static final String IS_FOLDER = "isFolder";
    /** Text item holding the path to the CatVer file. */
    TextItem catverPath;
    /** Tree grid displaying the category/version hierarchy. */
    CatVerTree catverTree;
    /** Text item holding the path to the NPlayers file. */
    TextItem nplayersPath;
    /** List grid displaying the player-count entries. */
    NPlayersList nplayersList;

    /**
     * List grid displaying the player-count entries, with selection synchronized to the profile properties.
     *
     * @since 2.5
     */
    class NPlayersList extends ListGrid /* NOSONAR */ {
        /** When {@code false} selection changes are not propagated to the server (used while loading data). */
        boolean enableEvents = false;

        /**
         * REST datasource for the NPlayers list grid.
         *
         * @since 2.5
         */
        class DataSource extends RestDataSource {
            /**
             * Constructs the datasource, configuring its ID, URL, format, operation bindings, and fields.
             */
            DataSource() {
                setID("NPlayers");
                setDataURL("/datasources/" + getID());
                setDataFormat(DSDataFormat.XML);
                OperationBinding ob = new OperationBinding();
                ob.setOperationType(DSOperationType.FETCH);
                ob.setDataProtocol(DSProtocol.POSTXML);
                setOperationBindings(ob);
                addField(new DataSourceTextField("Name", Client.getSession().getMsg("MainFrame.NPlayers")));
                DataSourceField dsf = new DataSourceTextField("ID");
                dsf.setPrimaryKey(true);
                dsf.setHidden(true);
                addField(dsf);
                dsf = new DataSourceBooleanField(IS_SELECTED);
                dsf.setHidden(true);
                addField(dsf);
                dsf = new DataSourceIntegerField("Cnt");
                dsf.setHidden(true);
                addField(dsf);
            }
        }

        /**
         * Constructs the NPlayers list grid, configuring selection, handlers, datasource, and context menu.
         */
        NPlayersList() {
            setSelectionAppearance(SelectionAppearance.CHECKBOX);
            setShowAllRecords(true);
            setShowSelectedStyle(false);
            setAutoFetchData(true);
            setSelectionProperty(IS_SELECTED);
            setAlternateRecordStyles(false);
            setCanEdit(false);
            setCanRemoveRecords(false);
            addSelectionChangedHandler(event -> {
                if (enableEvents) {
                    Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("ID"), event.getState())));
                    if (ProfileViewer.canResetPV)
                        ProfileViewer.reset();
                }
            });
            addDataArrivedHandler(event -> enableEvents = true);
            setDataSource(new DataSource());
            ListGridField field = new ListGridField("Name");
            field.setCellFormatter((value, recrd, rowNum, colNum) -> value + " (" + recrd.getAttribute("Cnt") + ")");
            setFields(field);
            Menu menu = new Menu();
            MenuItem item = new MenuItem(Client.getSession().getMsg("MainFrame.SelectAll"));
            item.addClickHandler(event -> nplayersList.selectAllRecords());
            menu.addItem(item);
            item = new MenuItem(Client.getSession().getMsg("MainFrame.SelectNone"));
            item.addClickHandler(event -> nplayersList.deselectAllRecords());
            menu.addItem(item);
            item = new MenuItem(Client.getSession().getMsg("MainFrame.InvertSelection"));
            item.addClickHandler(event -> {
                ListGridRecord[] toUnselect = nplayersList.getSelectedRecords();
                List<ListGridRecord> toUnselectList = Arrays.asList(toUnselect);
                ListGridRecord[] toSelect = Stream.of(nplayersList.getRecords()).filter(r -> !toUnselectList.contains(r)).toList()
                        .toArray(new ListGridRecord[0]);
                nplayersList.deselectRecords(toUnselect);
                nplayersList.selectRecords(toSelect);
            });
            menu.addItem(item);
            item = new MenuItem();
            item.setIsSeparator(true);
            menu.addItem(item);
            item = new MenuItem(Client.getSession().getMsg("ScannerAdvFilterPanel.mntmClear_1.text"));
            item.addClickHandler(event -> Client.sendMsg(JsonUtils.stringify(Q_NPlayers.Load.instantiate().setPath(null))));
            menu.addItem(item);
            setContextMenu(menu);
        }
    }

    /**
     * Tree grid displaying the category/version hierarchy, with cascade selection synchronized to the profile properties.
     *
     * @since 2.5
     */
    class CatVerTree extends TreeGrid /* NOSONAR */ {
        /** When {@code false} selection changes are not propagated to the server (used while loading data). */
        boolean enableEvents = false;

        /**
         * REST datasource for the CatVer tree grid.
         *
         * @since 2.5
         */
        class DataSource extends RestDataSource {
            /**
             * Constructs the datasource, configuring its ID, URL, format, operation bindings, and fields.
             */
            DataSource() {
                setID("CatVer");
                setDataURL("/datasources/" + getID());
                setDataFormat(DSDataFormat.XML);
                OperationBinding ob = new OperationBinding();
                ob.setOperationType(DSOperationType.FETCH);
                ob.setDataProtocol(DSProtocol.POSTXML);
                setOperationBindings(ob);
                DataSourceField dsf = new DataSourceTextField("Name", Client.getSession().getMsg("MainFrame.Categories"));
                addField(dsf);
                dsf = new DataSourceTextField("ID");
                dsf.setPrimaryKey(true);
                addField(dsf);
                dsf = new DataSourceTextField("ParentID");
                dsf.setForeignKey("ID");
                dsf.setRootValue(1);
                addField(dsf);
                dsf = new DataSourceBooleanField("isOpen");
                dsf.setHidden(true);
                addField(dsf);
                dsf = new DataSourceBooleanField(IS_SELECTED);
                dsf.setHidden(true);
                addField(dsf);
                dsf = new DataSourceBooleanField(IS_FOLDER);
                dsf.setHidden(true);
                addField(dsf);
                dsf = new DataSourceIntegerField("Cnt");
                dsf.setHidden(true);
                addField(dsf);
            }
        }

        /**
         * Formats a category/version cell, appending the count of selected descendant leaves for folder nodes.
         *
         * @param value the cell value
         * @param recrd the list grid record
         * @return the formatted cell text
         */
        private String formatCatVerCell(Object value, ListGridRecord recrd) {
            TreeNode node = Tree.nodeForRecord(recrd);
            if (Boolean.TRUE.equals(node.getAttributeAsBoolean(IS_FOLDER))) {
                TreeNode[] children = getData().getDescendantLeaves(node);
                if (children != null) {
                    int count = 0;
                    for (TreeNode child : children) {
                        if (Boolean.TRUE.equals(isSelected(child)))
                            count += Integer.parseInt(child.getAttribute("Cnt"));
                    }
                    return value + " (" + count + ")";
                }
            }
            return value + " (" + recrd.getAttribute("Cnt") + ")";
        }

        /**
         * Constructs the CatVer tree grid, configuring selection, handlers, datasource, and context menu.
         */
        CatVerTree() {
            setShowAllRecords(true);
            setSelectionAppearance(SelectionAppearance.CHECKBOX);
            setShowSelectedStyle(false);
            setShowPartialSelection(true);
            setShowConnectors(true);
            setCascadeSelection(true);
            setCellPadding(0);
            setCanEdit(false);
            setCanRemoveRecords(false);
            setAutoFetchData(true);
            Tree tree = new Tree();
            tree.setModelType(TreeModelType.PARENT);
            tree.setRootValue(1);
            tree.setNameProperty("Name");
            tree.setIdField("ID");
            tree.setParentIdField("ParentID");
            tree.setOpenProperty("isOpen");
            tree.setIsFolderProperty(IS_FOLDER);
            setDataProperties(tree);
            setSelectionProperty(IS_SELECTED);
            setTreeFieldTitle(Client.getSession().getMsg("MainFrame.Categories"));
            setNodeIcon(null);
            setFolderIcon(null);
            addSelectionChangedHandler(event -> {
                if (enableEvents) {
                    Client.sendMsg(JsonUtils.stringify(
                            Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("ID"), !isPartiallySelected(event.getRecord()) && event.getState())));
                    if (ProfileViewer.canResetPV)
                        ProfileViewer.reset();
                }
            });
            addDataArrivedHandler((DataArrivedEvent event) -> {
                enableEvents = true;
                markForRedraw();
            });
            setDataSource(new DataSource());
            TreeGridField field = new TreeGridField("Name");
            field.setCellFormatter((value, recrd, rowNum, colNum) -> formatCatVerCell(value, recrd));
            setFields(field);
            Menu menu = new Menu();
            MenuItem mnitem = new MenuItem();
            mnitem.setTitle(Client.getSession().getMsg("MainFrame.Select"));
            Menu smenu = new Menu();
            MenuItem smnitem = new MenuItem();
            smnitem.setTitle(Client.getSession().getMsg("MainFrame.All"));
            smnitem.addClickHandler(event -> catverTree.selectAllRecords());
            smenu.addItem(smnitem);
            smnitem = new MenuItem();
            smnitem.setTitle(Client.getSession().getMsg("MainFrame.Mature"));
            smnitem.addClickHandler(event -> catverTree.selectRecords(catverTree.getData().findAll(new AdvancedCriteria("Name", OperatorId.ENDS_WITH, "* Mature *"))));
            smenu.addItem(smnitem);
            mnitem.setSubmenu(smenu);
            menu.addItem(mnitem);
            mnitem = new MenuItem();
            mnitem.setTitle(Client.getSession().getMsg("MainFrame.Unselect"));
            smenu = new Menu();
            smnitem = new MenuItem();
            smnitem.setTitle(Client.getSession().getMsg("MainFrame.All"));
            smnitem.addClickHandler(event -> catverTree.deselectAllRecords());
            smenu.addItem(smnitem);
            smnitem = new MenuItem();
            smnitem.setTitle(Client.getSession().getMsg("MainFrame.Mature"));
            smnitem.addClickHandler(event -> catverTree.deselectRecords(catverTree.getData().findAll(new AdvancedCriteria("Name", OperatorId.ENDS_WITH, "* Mature *"))));
            smenu.addItem(smnitem);
            mnitem.setSubmenu(smenu);
            menu.addItem(mnitem);
            mnitem = new MenuItem();
            mnitem.setIsSeparator(true);
            menu.addItem(mnitem);
            mnitem = new MenuItem(Client.getSession().getMsg("ScannerAdvFilterPanel.mntmClear.text"));
            mnitem.addClickHandler(event -> Client.sendMsg(JsonUtils.stringify(Q_CatVer.Load.instantiate().setPath(null))));
            menu.addItem(mnitem);
            setContextMenu(menu);
        }
    }

    /**
     * Constructs the advanced filters panel, building the NPlayers and CatVer layouts side by side.
     */
    public ScannerAdvFiltersPanel() {
        super();
        VLayout nplayersLayout = new VLayout();
        nplayersLayout.setShowResizeBar(true);
        DynamicForm nplayersForm = new DynamicForm();
        nplayersForm.setCellPadding(0);
        nplayersForm.setNumCols(2);
        nplayersForm.setColWidths("*", 26);
        nplayersPath = new TextItem();
        nplayersPath.setShowTitle(false);
        nplayersPath.setWidth("*");
        nplayersPath.setCanEdit(false);
        ButtonItem nplayersFrbt = new ButtonItem();
        nplayersFrbt.setStartRow(false);
        nplayersFrbt.setIcon("icons/disk.png");
        nplayersFrbt.setTitle(null);
        nplayersFrbt.setValueIconRightPadding(0);
        nplayersFrbt.setEndRow(false);
        nplayersFrbt.addClickHandler(
                event -> new RemoteFileChooser("NPlayers", null, path -> Client.sendMsg(JsonUtils.stringify(Q_NPlayers.Load.instantiate().setPath(path[0].path)))));
        nplayersForm.setItems(nplayersPath, nplayersFrbt);
        nplayersList = new NPlayersList();
        nplayersLayout.setMembers(nplayersForm, nplayersList);
        VLayout catverLayout = new VLayout();
        DynamicForm catverForm = new DynamicForm();
        catverForm.setWidth100();
        catverForm.setCellPadding(0);
        catverForm.setNumCols(2);
        catverForm.setColWidths("*", 26);
        catverPath = new TextItem();
        catverPath.setShowTitle(false);
        catverPath.setWidth("*");
        catverPath.setCanEdit(false);
        ButtonItem catverFrbt = new ButtonItem();
        catverFrbt.setStartRow(false);
        catverFrbt.setIcon("icons/disk.png");
        catverFrbt.setTitle(null);
        catverFrbt.setValueIconRightPadding(0);
        catverFrbt.setEndRow(false);
        catverFrbt.addClickHandler(event -> new RemoteFileChooser("CatVer", null, path -> Client.sendMsg(JsonUtils.stringify(Q_CatVer.Load.instantiate().setPath(path[0].path)))));
        catverForm.setItems(catverPath, catverFrbt);
        catverTree = new CatVerTree();
        catverLayout.setMembers(catverForm, catverTree);
        setMembers(nplayersLayout, catverLayout);
    }

}
