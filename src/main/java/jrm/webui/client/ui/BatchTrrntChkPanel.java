package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.TreeGrid;

import jrm.webui.client.Client;
import jrm.webui.client.datasources.DSBatchTrntChkReportTree;
import jrm.webui.client.datasources.DSBatchTrntChkSDR;
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.protocol.Q_TrntChk;
import jrm.webui.client.ui.RemoteFileChooser.CallBack;
import jrm.webui.client.ui.RemoteFileChooser.PathInfo;

public class BatchTrrntChkPanel extends VLayout //NOSONAR
{

	private static final String TRNTCHK_MODE = "trntchk.mode";
	private static final String FILENAME = "FILENAME";
	private static final String SRC = "src";
	private static final String DST = "dst";
	private static final String RESULT = "result";
	private static final String SELECTED = "selected";
	private static final String STATUS = "status";
	private static final String TITLE = "title";
	private static final String LENGTH = "length";
	private static final String SHOW_OK = "showOK";
	private static final String STATUS_OK = "OK";
	private static final String STATUS_SIZE = "SIZE";
	private static final String STATUS_SHA1 = "SHA1";
	private static final String STATUS_MISSING = "MISSING";
	private static final String STATUS_SKIPPED = "SKIPPED";
	private static final String STATUS_UNKNOWN = "UNKNWON";
	private static final String ICON_BULLET_GO = "icons/bullet_go.png";

	ListGrid sdr;

	public BatchTrrntChkPanel() {
		setHeight100();
		sdr = buildSdrGrid();
		addMember(sdr);
		addMember(buildBottomForm());
	}

	private ListGrid buildSdrGrid() {
		ListGrid grid = new ListGrid() //NOSONAR
        {
			@Override
			protected Canvas getExpansionComponent(ListGridRecord rcrd) {
				return buildExpansionGrid(rcrd);
			}
		};
		grid.setHeight100();
		grid.setCanEdit(true);
		grid.setCanHover(true);
		grid.setHoverAutoFitWidth(true);
		grid.setHoverAutoFitMaxWidth("50%");
		grid.setSelectionType(SelectionStyle.MULTIPLE);
		grid.setCanSort(false);
		grid.setAutoFitExpandField(RESULT);
		grid.setAutoFitFieldsFillViewport(true);
		grid.setAutoFetchData(true);
		grid.setCanExpandRecords(true);
		grid.setContextMenu(buildSdrContextMenu(grid));
		grid.setDataSource(DSBatchTrntChkSDR.getInstance());
		grid.setFields(
				buildSrcField(),
				buildDstField(),
				buildResultField(),
				buildSelectedField());
		return grid;
	}

	private ListGridField buildSrcField() {
		ListGridField field = new ListGridField(SRC, Client.getSession().getMsg("MainFrame.TorrentFiles"));
		field.setWidth("35%");
		field.setCanEdit(false);
		return field;
	}

	private ListGridField buildDstField() {
		ListGridField field = new ListGridField(DST, Client.getSession().getMsg("MainFrame.DstDirs"));
		field.setCanEdit(false);
		return field;
	}

	private ListGridField buildResultField() {
		ListGridField field = new ListGridField(RESULT, Client.getSession().getMsg("MainFrame.Result"));
		field.setWidth("35%");
		field.setCanEdit(false);
		return field;
	}

	private ListGridField buildSelectedField() {
		ListGridField field = new ListGridField(SELECTED);
		field.setWidth(20);
		field.setAlign(Alignment.CENTER);
		return field;
	}

	private Menu buildSdrContextMenu(ListGrid grid) {
		Menu contextMenu = new Menu();
		contextMenu.addItem(buildUpdateOrAddMenuItem(grid));
		contextMenu.addItem(buildSetDestMenuItem(grid));
		contextMenu.addItem(buildDeleteMenuItem(grid));
		return contextMenu;
	}

	private MenuItem buildUpdateOrAddMenuItem(ListGrid grid) {
		MenuItem item = new MenuItem();
		item.setDynamicTitleFunction((target, menu, menuItem) -> Client.getSession().getMsg(
				grid.getSelectedRecords().length == 1
						? "BatchToolsTrrntChkPanel.mntmUpdTorrent.text"
						: "BatchToolsTrrntChkPanel.mntmAddTorrent.text"));
		item.addClickHandler(e -> new RemoteFileChooser("addTrnt",
				Client.getSession().getSetting("dir.addTrnt", null), updateOrAddCB(grid)));
		return item;
	}

	private MenuItem buildSetDestMenuItem(ListGrid grid) {
		MenuItem item = new MenuItem();
		item.setTitle("Set Destination");
		item.setEnableIfCondition((target, menu, menuItem) -> grid.getSelectedRecords().length == 1);
		item.addClickHandler(e -> new RemoteFileChooser("updTrnt",
				Client.getSession().getSetting("dir.updTrnt", null), setDestCB(grid)));
		return item;
	}

	private MenuItem buildDeleteMenuItem(ListGrid grid) {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.mntmDelTorrent.text"));
		item.setEnableIfCondition((target, menu, menuItem) -> grid.getSelectedRecords().length > 0);
		item.addClickHandler(e -> grid.removeSelectedData());
		return item;
	}

	private CallBack setDestCB(ListGrid grid) {
		return pi -> updDataRecursive(grid, pi, grid.getRecordIndex(grid.getSelectedRecord()), 0, DST);
	}

	private CallBack updateOrAddCB(ListGrid grid) {
		return pi -> {
			Record rec = grid.getSelectedRecord();
			if (rec != null) {
				updDataRecursive(grid, pi, grid.getRecordIndex(rec), 0, SRC);
			} else {
				addDataRecursive(grid, pi, 0, SRC);
			}
		};
	}

	private void addDataRecursive(ListGrid grid, PathInfo[] pi, int i, String attr) {
		if (i < pi.length) {
			Record rec = new Record(Collections.singletonMap(attr, pi[i].path));
			grid.addData(rec, (dsResponse, data, dsRequest) -> addDataRecursive(grid, pi, i + 1, attr));
		}
	}

	private void updDataRecursive(ListGrid grid, PathInfo[] pi, int start, int i, String attr) {
		if (i < pi.length) {
			if (start + i < grid.getTotalRows()) {
				Record rec = grid.getRecord(start + i);
				rec.setAttribute(attr, pi[i].path);
				grid.updateData(rec, (dsResponse, data, dsRequest) -> updDataRecursive(grid, pi, start, i + 1, attr));
			} else {
				addDataRecursive(grid, pi, i, attr);
			}
		}
	}

	private TreeGrid buildExpansionGrid(ListGridRecord rcrd) {
		TreeGrid grid = new TreeGrid();
		Boolean[] showok = {null};

		grid.setHeight(200);
		grid.setCanEdit(false);
		grid.setCanHover(true);
		grid.setHoverAutoFitWidth(true);
		grid.setHoverAutoFitMaxWidth("50%");
		grid.setSelectionType(SelectionStyle.NONE);
		grid.setCanSort(false);
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);
		grid.setAutoFitExpandField(TITLE);
		grid.setAutoFitFieldsFillViewport(true);
		grid.setAutoFetchData(true);
		grid.setShowConnectors(true);
		grid.setShowOpener(true);
		grid.setShowOpenIcons(true);
		grid.setShowCustomIconOpen(true);
		grid.setDataFetchMode(FetchMode.PAGED);
		grid.setContextMenu(buildExpansionContextMenu(grid, rcrd, showok));
		grid.setDataSource(buildExpansionDataSource(rcrd, showok));
		grid.setFields(
				buildTitleField(rcrd),
				buildLengthField(),
				buildStatusField());
		return grid;
	}

	private Menu buildExpansionContextMenu(TreeGrid grid, ListGridRecord rcrd, Boolean[] showok) {
		Menu contextMenu = new Menu();
		MenuItem showokItem = new MenuItem();
		showokItem.setTitle(Client.getSession().getMsg("ReportFrame.chckbxmntmShowOkEntries.text"));
		showokItem.addClickHandler(e -> {
			final var map = new HashMap<String, String>();
			map.put(SRC, rcrd.getAttribute(SRC));
			map.put(SHOW_OK, Boolean.toString(!(showok[0] == null || showok[0])));
			grid.getDataSource().getRequestProperties().setData(map);
			grid.invalidateCache();
		});
		showokItem.setCheckIfCondition((target, menu, item) -> showok[0] == null || showok[0]);
		contextMenu.setItems(showokItem);
		return contextMenu;
	}

	private com.smartgwt.client.data.DataSource buildExpansionDataSource(ListGridRecord rcrd, Boolean[] showok) {
		final var ds = DSBatchTrntChkReportTree.getInstance();
		final var extradata = new HashMap<String, String>();
		extradata.put(SRC, rcrd.getAttribute(SRC));
		extradata.put(SHOW_OK, Boolean.toString(showok[0] == null || showok[0]));
		ds.setCB(data -> showok[0] = Optional.ofNullable(XMLTools.selectString(data, "/response/showOK"))
				.map(Boolean::valueOf).orElse(true)).setExtraData(extradata);
		return ds;
	}

	private ListGridField buildStatusField() {
		ListGridField field = new ListGridField(STATUS);
		field.setWidth(100);
		field.setCellFormatter((value, rec, rowNum, colNum) -> statusFormatter(value));
		return field;
	}

	private ListGridField buildTitleField(ListGridRecord rcrd) {
		ListGridField field = new ListGridField(TITLE);
		field.setHoverCustomizer(new HoverCustomizer() {
			@Override
			public String hoverHTML(Object value, ListGridRecord rec, int rowNum, int colNum) {
				return rcrd.getAttribute(TITLE);
			}
		});
		return field;
	}

	private ListGridField buildLengthField() {
		ListGridField field = new ListGridField(LENGTH);
		field.setWidth(100);
		field.setHoverCustomizer(new HoverCustomizer() {
			@Override
			public String hoverHTML(Object value, ListGridRecord rec, int rowNum, int colNum) {
				return Optional.ofNullable(rec.getAttributeAsLong(LENGTH)).map(l -> readableFileSize(l)).orElse(null);
			}
		});
		return field;
	}

	private static String statusFormatter(Object value) {
		if (value == null) {
			return null;
		}
        return switch (value.toString()) {
            case STATUS_OK -> "<b style='color:green'>" + value + "</b>";
            case STATUS_SIZE -> "<b style='color:red'>" + value + "</b>";
            case STATUS_SHA1 -> "<b style='color:red'>" + value + "</b>";
            case STATUS_MISSING -> "<span style='color:red'>" + value + "</span>";
            case STATUS_SKIPPED -> "<span style='color:orange'>" + value + "</span>";
            case STATUS_UNKNOWN -> "<i style='color:gray'>" + value + "</i>";
            default -> value.toString();
        };
	}

	private static String readableFileSize(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return NumberFormat.getFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	private DynamicForm buildBottomForm() {
		DynamicForm form = new DynamicForm();
		form.setWidth100();
		form.setHeight(20);
		form.setNumCols(9);
		form.setColWidths("*", 90, 10, "*", 10, "*", 10, "*", 100);

		SelectItem checkModeItem = buildCheckModeItem();
		CheckboxItem detectArchivedFolderItem = buildDetectArchivedFolderItem();
		CheckboxItem removeUnknownFilesItem = buildRemoveUnknownFilesItem();
		CheckboxItem removeWrongSizedItem = buildRemoveWrongSizedItem();
		ButtonItem startItem = buildStartItem();

		form.setItems(checkModeItem, detectArchivedFolderItem, removeUnknownFilesItem, removeWrongSizedItem, startItem);
		return form;
	}

	private SelectItem buildCheckModeItem() {
		SelectItem item = new SelectItem();
		item.setValueMap(FILENAME, "FILESIZE", "SHA1");
		item.setWidth(100);
		item.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.lblCheckMode.text"));
		item.setDefaultValue(Client.getSession().getSetting(TRNTCHK_MODE, FILENAME));
		item.addChangedHandler(e -> {
			Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty(TRNTCHK_MODE, (String) e.getValue())));
			e.getForm().getItem("remove_wrong_sized_files").setDisabled(FILENAME.equals(e.getValue()));
		});
		return item;
	}

	private CheckboxItem buildDetectArchivedFolderItem() {
		CheckboxItem item = new CheckboxItem();
		item.setTitle(Client.getSession().getMsg("BatchTrrntChkPanel.chckbxDetectArchivedFolder.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.detect_archived_folders", true));
		item.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("trntchk.detect_archived_folders", (Boolean) e.getValue()))));
		return item;
	}

	private CheckboxItem buildRemoveUnknownFilesItem() {
		CheckboxItem item = new CheckboxItem();
		item.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.chckbxRemoveUnknownFiles.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.remove_unknown_files", false));
		item.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_unknown_files", (Boolean) e.getValue()))));
		return item;
	}

	private CheckboxItem buildRemoveWrongSizedItem() {
		CheckboxItem item = new CheckboxItem("remove_wrong_sized_files");
		item.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.chckbxRemoveWrongSized.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.remove_wrong_sized_files", false));
		item.setDisabled(FILENAME.equals(Client.getSession().getSetting(TRNTCHK_MODE, FILENAME)));
		item.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_wrong_sized_files", (Boolean) e.getValue()))));
		return item;
	}

	private ButtonItem buildStartItem() {
		ButtonItem item = new ButtonItem();
		item.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.TrntCheckStart.text"));
		item.setIcon(ICON_BULLET_GO);
		item.setAlign(Alignment.RIGHT);
		item.setStartRow(false);
		item.setWidth("*");
		item.addClickHandler(e -> Client.sendMsg(JsonUtils.stringify(Q_TrntChk.Start.instantiate())));
		return item;
	}
}
