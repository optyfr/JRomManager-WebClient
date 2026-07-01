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

public class ProfilePanel extends VLayout //NOSONAR
{
	private static final String PARENT = "Parent";
	private static final String PARENT_ID = "ParentID";
	private static final String TITLE = "title";
	private static final String PATH = "Path";
	private static final String ID = "ID";
	private static final String POST_XML_SUFFIX = "/datasources/";
	private static final String ICON_FOLDER_ADD = "icons/folder_add.png";
	private static final String ICON_FOLDER_DELETE = "icons/folder_delete.png";
	private static final String ICON_PAGE_ADD = "icons/page_add.png";
	private static final String ICON_SCRIPT_GO = "icons/script_go.png";
	private static final String ICON_SCRIPT_DELETE = "icons/script_delete.png";
	private static final String ICON_SCRIPT_EDIT = "icons/script_edit.png";
	private static final String ICON_BIN = "icons/bin.png";
	private static final String ICON_APP_GO = "icons/application_go.png";

	ListGrid listgrid;
	TreeGrid treegrid;
	String parentPath;

	public ProfilePanel() {
		super();
		addMembers(buildSplitPane());
	}

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

	private void loadProfile(RecordDoubleClickEvent event) {
		Record rec = event.getRecord();
		Q_Profile.Load.instantiate()
				.setPath(rec.getAttribute(PARENT), rec.getAttribute("File"))
				.send();
	}

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

	private void onTreeRecordClick(com.smartgwt.client.widgets.grid.events.RecordClickEvent event) {
		listgrid.setCriteria(createPathCriteria(event.getRecord().getAttribute(PATH)));
		listgrid.invalidateCache();
	}

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

	private static Criteria createPathCriteria(String path) {
		if (path == null) {
			return null;
		}
		Criteria criteria = new Criteria();
		criteria.addCriteria(PARENT, path);
		return criteria;
	}

	private static Record createSrcParentFileRecord(String src, String parentValue, String file) {
		Record rec = new Record();
		rec.setAttribute("Src", src);
		rec.setAttribute(PARENT, parentValue);
		rec.setAttribute("File", file);
		return rec;
	}

	private static OperationBinding createOperationBinding(DSOperationType type) {
		OperationBinding binding = new OperationBinding();
		binding.setOperationType(type);
		binding.setDataProtocol(DSProtocol.POSTXML);
		return binding;
	}

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

	private MenuItem buildSeparatorMenuItem() {
		MenuItem item = new MenuItem();
		item.setIsSeparator(true);
		return item;
	}

	private MenuItem buildDeleteProfileMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.mntmDeleteProfile.text"));
		item.setIcon(ICON_SCRIPT_DELETE);
		item.setEnableIfCondition((Canvas target, Menu menu, MenuItem menuItem) -> listgrid.anySelected());
		item.addClickHandler(e -> listgrid.removeSelectedData());
		return item;
	}

	private MenuItem buildRenameProfileMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.mntmRenameProfile.text"));
		item.setIcon(ICON_SCRIPT_EDIT);
		item.setEnableIfCondition((Canvas target, Menu menu, MenuItem menuItem) -> listgrid.getSelectedRecords().length == 1);
		item.addClickHandler(e -> listgrid.startEditing(listgrid.getRecordIndex(listgrid.getSelectedRecord())));
		return item;
	}

	private MenuItem buildDropCacheMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.mntmDropCache.text"));
		item.setIcon(ICON_BIN);
		item.setEnableIfCondition((Canvas target, Menu menu, MenuItem menuItem) -> listgrid.getSelectedRecords().length == 1);
		item.addClickHandler(e -> listgrid.getDataSource().performCustomOperation("DropCache", listgrid.getSelectedRecord()));
		return item;
	}

	private Menu buildTreeContextMenu() {
		Menu menu = new Menu();
		menu.setItems(buildCreateFolderMenuItem(), buildDeleteFolderMenuItem());
		return menu;
	}

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

	private IButton buildManageUploadsButton() {
		IButton button = new IButton("Manage files uploads");
		button.setAutoFit(true);
		button.setIcon(ICON_PAGE_ADD);
		button.addClickHandler(e -> new RemoteFileChooser("manageUploads", null, null));
		return button;
	}

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

	void refreshListGrid() {
		String selection = listgrid.getSelectedState();
		listgrid.refreshData((dsResponse, data, dsRequest) -> listgrid.setSelectedState(selection));
	}
}
