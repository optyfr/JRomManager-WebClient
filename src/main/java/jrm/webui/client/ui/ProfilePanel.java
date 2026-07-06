package jrm.webui.client.ui;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.PreserveOpenState;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.ui.RemoteFileChooser.PathInfo;

/**
 * Profile management panel showing a tree of profile folders on the left and the
 * list of profiles contained in the selected folder on the right.
 * <p>
 * Provides context menus and toolbar buttons to import DAT files, import from
 * MAME, create/delete/rename profiles, drop caches, and create/delete profile
 * folders.
 *
 * @since 2.5
 */
public class ProfilePanel extends VLayout //NOSONAR
{
	/** Record attribute holding the parent folder path of a profile. */
	private static final String PARENT = "Parent";
	/** Record attribute holding the parent identifier of a tree node. */
	private static final String PARENT_ID = "ParentID";
	/** Record attribute/field holding the display title of a tree node. */
	private static final String TITLE = "title";
	/** Record attribute holding the filesystem path of a tree node. */
	private static final String PATH = "Path";
	/** Record attribute holding the unique identifier of a tree node. */
	private static final String ID = "ID";
	/** URL prefix for datasource REST endpoints. */
	private static final String POST_XML_SUFFIX = "/datasources/";
	/** Icon for the "create folder" menu item. */
	private static final String ICON_FOLDER_ADD = "icons/folder_add.png";
	/** Icon for the "delete folder" menu item. */
	private static final String ICON_FOLDER_DELETE = "icons/folder_delete.png";
	/** Icon for the "manage uploads" button. */
	private static final String ICON_PAGE_ADD = "icons/page_add.png";
	/** Icon for the "import DAT" action. */
	private static final String ICON_SCRIPT_GO = "icons/script_go.png";
	/** Icon for the "delete profile" menu item. */
	private static final String ICON_SCRIPT_DELETE = "icons/script_delete.png";
	/** Icon for the "rename profile" menu item. */
	private static final String ICON_SCRIPT_EDIT = "icons/script_edit.png";
	/** Icon for the "drop cache" menu item. */
	private static final String ICON_BIN = "icons/bin.png";
	/** Icon for the "import from MAME" button. */
	private static final String ICON_APP_GO = "icons/application_go.png";

	/** The list grid displaying profiles contained in the selected tree folder. */
	ListGrid listgrid;
	/** The tree grid displaying the profile folder hierarchy. */
	TreeGrid treegrid;
	/** The currently selected parent path, updated from the list datasource response. */
	String parentPath;

	/**
	 * Constructs the profile panel and adds the split pane containing the tree and list grids.
	 */
	public ProfilePanel() {
		super();
		addMembers(buildSplitPane());
	}

	/**
	 * Builds the split pane with the profile folder tree as navigation pane and the profile list as detail pane.
	 *
	 * @return the configured split pane
	 */
	private SplitPane buildSplitPane() {
		SplitPane splitPane = new SplitPane();
		listgrid = buildListGrid();
		treegrid = buildTreeGrid();
		splitPane.setNavigationPane(treegrid);
		splitPane.setNavigationPaneWidth(200);
		splitPane.setShowNavigationBar(true);
		splitPane.setShowDetailToolStrip(true);
		splitPane.setDetailToolButtons(
				buildManageUploadsButton(),
				buildImportDatButton(),
				buildImportMameButton());
		splitPane.setDetailPane(listgrid);
		return splitPane;
	}

	/**
	 * Builds the profile list grid with its hover behavior, double-click handler, context menu, and datasource.
	 *
	 * @return the configured list grid
	 */
	private ListGrid buildListGrid() {
		ListGrid grid = new ListGrid();
		grid.setShowFilterEditor(false);
		grid.setShowHover(true);
		grid.setCanHover(true);
		grid.setHoverWidth(200);
		grid.setEditEvent(ListGridEditEvent.NONE);
		grid.addRecordDoubleClickHandler(this::loadProfile);
		grid.setContextMenu(buildListContextMenu());
		grid.setDataSource(buildListDataSource());
		return grid;
	}

	/**
	 * Loads the profile corresponding to the double-clicked list record.
	 *
	 * @param event the record double-click event
	 */
	private void loadProfile(RecordDoubleClickEvent event) {
		Record rec = event.getRecord();
		Q_Profile.Load.instantiate()
				.setPath(rec.getAttribute(PARENT), rec.getAttribute("File"))
				.send();
	}

	/**
	 * Builds the REST datasource backing the profile list grid, defining its fields and operation bindings.
	 * <p>
	 * The datasource extracts the parent path from each fetch response and stores it in {@link #parentPath}.
	 *
	 * @return the configured REST datasource
	 */
	private RestDataSource buildListDataSource() {
		RestDataSource ds = new RestDataSource() {
			@Override
			protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data) {
				if (dsResponse.getStatus() == 0) {
					parentPath = XMLTools.selectString(data, "/response/parent");
				}
				super.transformResponse(dsResponse, dsRequest, data);
			}
		};
		ds.setID("profilesList");
		ds.setDataFormat(DSDataFormat.XML);
		ds.setDataURL(POST_XML_SUFFIX + ds.getID());
		ds.setOperationBindings(
				createOperationBinding(DSOperationType.FETCH),
				createOperationBinding(DSOperationType.ADD),
				createOperationBinding(DSOperationType.UPDATE),
				createOperationBinding(DSOperationType.REMOVE),
				createOperationBinding(DSOperationType.CUSTOM));
		DataSourceTextField nameField = new DataSourceTextField("Name", Client.getSession().getMsg("FileTableModel.Profile"));
		nameField.setCanEdit(true);
		DataSourceTextField parentField = new DataSourceTextField(PARENT);
		parentField.setHidden(true);
		parentField.setPrimaryKey(true);
		DataSourceTextField fileField = new DataSourceTextField("File");
		fileField.setHidden(true);
		fileField.setPrimaryKey(true);
		DataSourceTextField verField = new DataSourceTextField("version", Client.getSession().getMsg("FileTableModel.Version"));
		DataSourceTextField haveSetsField = new DataSourceTextField("haveSets", Client.getSession().getMsg("FileTableModel.HaveSets"));
		DataSourceTextField haveRomsField = new DataSourceTextField("haveRoms", Client.getSession().getMsg("FileTableModel.HaveRoms"));
		DataSourceTextField haveDisksField = new DataSourceTextField("haveDisks", Client.getSession().getMsg("FileTableModel.HaveDisks"));
		DataSourceTextField createdField = new DataSourceTextField("created", Client.getSession().getMsg("FileTableModel.Created"));
		DataSourceTextField scannedField = new DataSourceTextField("scanned", Client.getSession().getMsg("FileTableModel.Scanned"));
		DataSourceTextField fixedField = new DataSourceTextField("fixed", Client.getSession().getMsg("FileTableModel.Fixed"));
		ds.setFields(nameField, parentField, fileField, verField, haveSetsField, haveRomsField, haveDisksField, createdField, scannedField, fixedField);
		return ds;
	}

	/**
	 * Builds the profile folder tree grid with its hover behavior, context menu, data-arrival and record-click handlers, and datasource.
	 *
	 * @return the configured tree grid
	 */
	private TreeGrid buildTreeGrid() {
		TreeGrid tree = new TreeGrid();
		tree.setShowRoot(true);
		tree.setAutoFetchData(true);
		tree.setLoadDataOnDemand(false);
		tree.setAutoFitFieldWidths(true);
		tree.setAutoPreserveOpenState(PreserveOpenState.ALWAYS);
		tree.setIndentSize(10);
		tree.setExtraIconGap(0);
		tree.setShowHover(true);
		tree.setHoverWidth(200);
		tree.setCanHover(true);
		tree.setContextMenu(buildTreeContextMenu());
		tree.addDataArrivedHandler(this::onTreeDataArrived);
		tree.addRecordClickHandler(this::onTreeRecordClick);
		tree.setDataSource(buildTreeDataSource());
		tree.setFields(buildTitleField());
		return tree;
	}

	/**
	 * Selects the first tree node when data arrives and refreshes the list grid accordingly.
	 *
	 * @param event the data-arrived event (unused)
	 */
	private void onTreeDataArrived(@SuppressWarnings("unused") DataArrivedEvent event) //NOSONAR
	{
		treegrid.selectSingleRecord(0);
		listgrid.setCriteria(null);
		if (Boolean.TRUE.equals(listgrid.willFetchData(listgrid.getCriteria()))) {
			listgrid.fetchData();
		} else {
			listgrid.invalidateCache();
		}
	}

	/**
	 * Updates the list grid criteria to show profiles contained in the clicked tree node's path.
	 *
	 * @param event the record-click event
	 */
	private void onTreeRecordClick(com.smartgwt.client.widgets.grid.events.RecordClickEvent event) {
		listgrid.setCriteria(createPathCriteria(event.getRecord().getAttribute(PATH)));
		listgrid.invalidateCache();
	}

	/**
	 * Builds the REST datasource backing the profile folder tree grid, defining its fields and operation bindings.
	 *
	 * @return the configured REST datasource
	 */
	private RestDataSource buildTreeDataSource() {
		RestDataSource ds = new RestDataSource();
		ds.setID("profilesTree");
		ds.setOperationBindings(
				createOperationBinding(DSOperationType.FETCH),
				createOperationBinding(DSOperationType.ADD),
				createOperationBinding(DSOperationType.UPDATE),
				createOperationBinding(DSOperationType.REMOVE));
		ds.setDataFormat(DSDataFormat.XML);
		ds.setDataURL(POST_XML_SUFFIX + ds.getID());
		DataSourceTextField nameField = new DataSourceTextField(TITLE);
		DataSourceTextField pathField = new DataSourceTextField(PATH);
		pathField.setHidden(true);
		DataSourceIntegerField idField = new DataSourceIntegerField(ID);
		idField.setPrimaryKey(true);
		idField.setRequired(true);
		DataSourceIntegerField parentIdField = new DataSourceIntegerField(PARENT_ID);
		parentIdField.setRequired(true);
		parentIdField.setForeignKey(ds.getID() + ".ID");
		parentIdField.setRootValue("0");
		ds.setFields(nameField, pathField, idField, parentIdField);
		return ds;
	}

	/**
	 * Builds the title field for the tree grid, with a hover customizer displaying the node title.
	 *
	 * @return the configured list grid field
	 */
	private ListGridField buildTitleField() {
		ListGridField field = new ListGridField(TITLE);
		field.setHoverCustomizer(new HoverCustomizer() {
			@Override
			public String hoverHTML(Object value, ListGridRecord recrd, int rowNum, int colNum) {
				return recrd.getAttribute(TITLE);
			}
		});
		return field;
	}

	/**
	 * Creates a {@link Criteria} filtering the list grid by the given parent path.
	 *
	 * @param path the parent folder path, or {@code null} for no criteria
	 * @return the criteria, or {@code null} if {@code path} is {@code null}
	 */
	private static Criteria createPathCriteria(String path) {
		if (path == null) {
			return null;
		}
		Criteria criteria = new Criteria();
		criteria.addCriteria(PARENT, path);
		return criteria;
	}

	/**
	 * Creates a record describing a source file to import into a parent profile folder.
	 *
	 * @param src the source identifier (e.g. {@code "importDat"})
	 * @param parentValue the parent folder path
	 * @param file the file name
	 * @return the configured record
	 */
	private static Record createSrcParentFileRecord(String src, String parentValue, String file) {
		Record rec = new Record();
		rec.setAttribute("Src", src);
		rec.setAttribute(PARENT, parentValue);
		rec.setAttribute("File", file);
		return rec;
	}

	/**
	 * Creates an operation binding for a REST datasource using the POSTXML protocol.
	 *
	 * @param type the datasource operation type
	 * @return the configured operation binding
	 */
	private static OperationBinding createOperationBinding(DSOperationType type) {
		OperationBinding binding = new OperationBinding();
		binding.setOperationType(type);
		binding.setDataProtocol(DSProtocol.POSTXML);
		return binding;
	}

	/**
	 * Builds the context menu for the profile list grid.
	 *
	 * @return the configured menu
	 */
	private Menu buildListContextMenu() {
		Menu menu = new Menu();
		menu.setItems(
				buildImportDatMenuItem(),
				buildSeparatorMenuItem(),
				buildDeleteProfileMenuItem(),
				buildRenameProfileMenuItem(),
				buildDropCacheMenuItem());
		return menu;
	}

	/**
	 * Builds the "Import DAT" menu item, opening a remote file chooser to select DAT files to import.
	 *
	 * @return the configured menu item
	 */
	private MenuItem buildImportDatMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.btnImportDat.text"));
		item.setIcon(ICON_SCRIPT_GO);
		item.addClickHandler(e -> new RemoteFileChooser("importDat", null, paths -> {
			for (PathInfo p : paths) {
				listgrid.addData(createSrcParentFileRecord(p.path, parentPath, p.name));
			}
		}));
		return item;
	}

	/**
	 * Builds a separator menu item.
	 *
	 * @return the configured separator menu item
	 */
	private MenuItem buildSeparatorMenuItem() {
		MenuItem item = new MenuItem();
		item.setIsSeparator(true);
		return item;
	}

	/**
	 * Builds the "Delete profile" menu item, removing the selected profiles from the list grid.
	 *
	 * @return the configured menu item
	 */
	private MenuItem buildDeleteProfileMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.mntmDeleteProfile.text"));
		item.setIcon(ICON_SCRIPT_DELETE);
		item.setEnableIfCondition((Canvas target, Menu menu, MenuItem menuItem) -> listgrid.anySelected());
		item.addClickHandler(e -> listgrid.removeSelectedData());
		return item;
	}

	/**
	 * Builds the "Rename profile" menu item, starting inline editing of the selected profile.
	 *
	 * @return the configured menu item
	 */
	private MenuItem buildRenameProfileMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.mntmRenameProfile.text"));
		item.setIcon(ICON_SCRIPT_EDIT);
		item.setEnableIfCondition((Canvas target, Menu menu, MenuItem menuItem) -> listgrid.getSelectedRecords().length == 1);
		item.addClickHandler(e -> listgrid.startEditing(listgrid.getRecordIndex(listgrid.getSelectedRecord())));
		return item;
	}

	/**
	 * Builds the "Drop cache" menu item, triggering a custom datasource operation to drop the cache of the selected profile.
	 *
	 * @return the configured menu item
	 */
	private MenuItem buildDropCacheMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.mntmDropCache.text"));
		item.setIcon(ICON_BIN);
		item.setEnableIfCondition((Canvas target, Menu menu, MenuItem menuItem) -> listgrid.getSelectedRecords().length == 1);
		item.addClickHandler(e -> listgrid.getDataSource().performCustomOperation("DropCache", listgrid.getSelectedRecord()));
		return item;
	}

	/**
	 * Builds the context menu for the profile folder tree grid.
	 *
	 * @return the configured menu
	 */
	private Menu buildTreeContextMenu() {
		Menu menu = new Menu();
		menu.setItems(buildCreateFolderMenuItem(), buildDeleteFolderMenuItem());
		return menu;
	}

	/**
	 * Builds the "Create folder" menu item, adding a new folder node under the selected tree node.
	 *
	 * @return the configured menu item
	 */
	private MenuItem buildCreateFolderMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.mntmCreateFolder.text"));
		item.setIcon(ICON_FOLDER_ADD);
		item.addClickHandler(e -> {
			TreeNode node = treegrid.getSelectedRecord();
			Record rec = new Record();
			rec.setAttribute(PARENT_ID, node.getAttribute(ID));
			rec.setAttribute(TITLE, Client.getSession().getMsg("MainFrame.NewFolder"));
			rec.setAttribute(PATH, node.getAttribute(PATH));
			treegrid.getDataSource().addData(rec,
					(dsResponse, data, dsRequest) -> treegrid.startEditing(treegrid.getRecordIndex(dsResponse.getData()[0])));
		});
		item.setEnableIfCondition((Canvas target, Menu menu, MenuItem menuItem) -> treegrid.anySelected());
		return item;
	}

	/**
	 * Builds the "Delete folder" menu item, removing the selected tree node and refreshing the list grid.
	 *
	 * @return the configured menu item
	 */
	private MenuItem buildDeleteFolderMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.mntmDeleteFolder.text"));
		item.setIcon(ICON_FOLDER_DELETE);
		item.addClickHandler(e -> {
			TreeNode node = treegrid.getSelectedRecord();
			String parentID = node.getAttribute(PARENT_ID);
			treegrid.removeSelectedData((DSResponse dsResponse, Object data, DSRequest dsRequest) -> {
				Record rec = new Record();
				rec.setAttribute(ID, parentID);
				treegrid.selectSingleRecord(rec);
				Record selected = treegrid.getSelectedRecord();
				listgrid.setCriteria(createPathCriteria(selected.getAttribute(PATH)));
				listgrid.invalidateCache();
			});
		});
		item.setEnableIfCondition((Canvas target, Menu menu, MenuItem menuItem) -> treegrid.anySelected());
		return item;
	}

	/**
	 * Builds the "Manage files uploads" button, opening a remote file chooser for upload management.
	 *
	 * @return the configured button
	 */
	private IButton buildManageUploadsButton() {
		IButton button = new IButton("Manage files uploads");
		button.setAutoFit(true);
		button.setIcon(ICON_PAGE_ADD);
		button.addClickHandler(e -> new RemoteFileChooser("manageUploads", null, null));
		return button;
	}

	/**
	 * Builds the "Import DAT" toolbar button, opening a remote file chooser and selecting each imported record.
	 *
	 * @return the configured button
	 */
	private IButton buildImportDatButton() {
		IButton button = new IButton(Client.getSession().getMsg("MainFrame.btnImportDat.text"));
		button.setAutoFit(true);
		button.setIcon(ICON_SCRIPT_GO);
		button.addClickHandler(e -> new RemoteFileChooser("importDat", null, paths -> {
			for (PathInfo p : paths) {
				Record rec = createSrcParentFileRecord(p.path, parentPath, p.name);
				listgrid.addData(rec, (dsResponse, data, dsRequest) -> listgrid.selectRecord(rec));
			}
		}));
		return button;
	}

	/**
	 * Builds the "Import from MAME" menu button, offering import with or without software lists.
	 *
	 * @return the configured menu button
	 */
	private IMenuButton buildImportMameButton() {
		MenuItem withoutSL = new MenuItem();
		withoutSL.setTitle("without Software list");
		withoutSL.addClickHandler(e -> Q_Profile.Import.instantiate().setParent(parentPath).setSL(false).send());

		MenuItem withSL = new MenuItem();
		withSL.setTitle("with Software list");
		withSL.addClickHandler(e -> Q_Profile.Import.instantiate().setParent(parentPath).setSL(true).send());

		Menu importMenu = new Menu();
		importMenu.addItem(withoutSL);
		importMenu.addItem(withSL);

		IMenuButton button = new IMenuButton();
		button.setTitle(Canvas.imgHTML(ICON_APP_GO) + " Import from Mame");
		button.setAutoFit(true);
		button.setMenu(importMenu);
		return button;
	}

	/**
	 * Refreshes the profile list grid while preserving the current selection state.
	 */
	void refreshListGrid() {
		String selection = listgrid.getSelectedState();
		listgrid.refreshData((dsResponse, data, dsRequest) -> listgrid.setSelectedState(selection));
	}
}
