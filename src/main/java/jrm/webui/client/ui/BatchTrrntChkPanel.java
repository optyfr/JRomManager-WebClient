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

/**
 * SmartGWT panel for the batch torrent checker UI.
 * <p>
 * Shows an SDR (source torrent / destination dir / result) grid with expandable
 * per-row detail tree grids, and a bottom form offering the check mode
 * (filename / filesize / SHA1), archived-folder detection, removal of unknown
 * files and wrong-sized files, and a start button.
 *
 * @since 2.5
 */
public class BatchTrrntChkPanel extends VLayout //NOSONAR
{

	/** Settings key for the torrent check mode. */
	private static final String TRNTCHK_MODE = "trntchk.mode";
	/** Value for the filename-only check mode. */
	private static final String FILENAME = "FILENAME";
	/** Name of the column holding the source torrent path. */
	private static final String SRC = "src";
	/** Name of the column holding the destination directory path. */
	private static final String DST = "dst";
	/** Name of the column holding the operation result. */
	private static final String RESULT = "result";
	/** Name of the column holding the selection flag. */
	private static final String SELECTED = "selected";
	/** Name of the detail tree column holding the entry status. */
	private static final String STATUS = "status";
	/** Name of the detail tree column holding the entry title. */
	private static final String TITLE = "title";
	/** Name of the detail tree column holding the entry length. */
	private static final String LENGTH = "length";
	/** Extra-data key used to toggle display of OK entries. */
	private static final String SHOW_OK = "showOK";
	/** Status value for an OK entry. */
	private static final String STATUS_OK = "OK";
	/** Status value for a size-mismatch entry. */
	private static final String STATUS_SIZE = "SIZE";
	/** Status value for a SHA1-mismatch entry. */
	private static final String STATUS_SHA1 = "SHA1";
	/** Status value for a missing entry. */
	private static final String STATUS_MISSING = "MISSING";
	/** Status value for a skipped entry. */
	private static final String STATUS_SKIPPED = "SKIPPED";
	/** Status value for an unknown entry. */
	private static final String STATUS_UNKNOWN = "UNKNWON";
	/** Icon path for the start action. */
	private static final String ICON_BULLET_GO = "icons/bullet_go.png";

	/** The SDR (source torrent / destination dir / result) grid. */
	ListGrid sdr;

	/**
	 * Constructs the panel, building the SDR grid and the bottom control form.
	 */
	public BatchTrrntChkPanel() {
		setHeight100();
		sdr = buildSdrGrid();
		addMember(sdr);
		addMember(buildBottomForm());
	}

	/**
	 * Builds the SDR grid with its fields, context menu, and expandable rows.
	 *
	 * @return the configured list grid
	 */
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

	/** Builds the SDR "src" column field. */
	private ListGridField buildSrcField() {
		ListGridField field = new ListGridField(SRC, Client.getSession().getMsg("MainFrame.TorrentFiles"));
		field.setWidth("35%");
		field.setCanEdit(false);
		return field;
	}

	/** Builds the SDR "dst" column field. */
	private ListGridField buildDstField() {
		ListGridField field = new ListGridField(DST, Client.getSession().getMsg("MainFrame.DstDirs"));
		field.setCanEdit(false);
		return field;
	}

	/** Builds the SDR "result" column field. */
	private ListGridField buildResultField() {
		ListGridField field = new ListGridField(RESULT, Client.getSession().getMsg("MainFrame.Result"));
		field.setWidth("35%");
		field.setCanEdit(false);
		return field;
	}

	/** Builds the SDR "selected" column field. */
	private ListGridField buildSelectedField() {
		ListGridField field = new ListGridField(SELECTED);
		field.setWidth(20);
		field.setAlign(Alignment.CENTER);
		return field;
	}

	/**
	 * Builds the SDR grid context menu with update/add, set destination, and
	 * delete actions.
	 *
	 * @param grid
	 *            the grid the menu is attached to
	 * @return the configured menu
	 */
	private Menu buildSdrContextMenu(ListGrid grid) {
		Menu contextMenu = new Menu();
		contextMenu.addItem(buildUpdateOrAddMenuItem(grid));
		contextMenu.addItem(buildSetDestMenuItem(grid));
		contextMenu.addItem(buildDeleteMenuItem(grid));
		return contextMenu;
	}

	/**
	 * Builds the "update or add torrent" menu item which opens a remote file chooser.
	 *
	 * @param grid
	 *            the grid the item is attached to
	 * @return the configured menu item
	 */
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

	/**
	 * Builds the "set destination" menu item, enabled only on single selection.
	 *
	 * @param grid
	 *            the grid the item is attached to
	 * @return the configured menu item
	 */
	private MenuItem buildSetDestMenuItem(ListGrid grid) {
		MenuItem item = new MenuItem();
		item.setTitle("Set Destination");
		item.setEnableIfCondition((target, menu, menuItem) -> grid.getSelectedRecords().length == 1);
		item.addClickHandler(e -> new RemoteFileChooser("updTrnt",
				Client.getSession().getSetting("dir.updTrnt", null), setDestCB(grid)));
		return item;
	}

	/**
	 * Builds the "delete torrent" menu item, enabled only on selection.
	 *
	 * @param grid
	 *            the grid the item is attached to
	 * @return the configured menu item
	 */
	private MenuItem buildDeleteMenuItem(ListGrid grid) {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.mntmDelTorrent.text"));
		item.setEnableIfCondition((target, menu, menuItem) -> grid.getSelectedRecords().length > 0);
		item.addClickHandler(e -> grid.removeSelectedData());
		return item;
	}

	/**
	 * Builds the callback that updates the destination column of the selected row.
	 *
	 * @param grid
	 *            the grid being updated
	 * @return the remote file chooser callback
	 */
	private CallBack setDestCB(ListGrid grid) {
		return pi -> updDataRecursive(grid, pi, grid.getRecordIndex(grid.getSelectedRecord()), 0, DST);
	}

	/**
	 * Builds the callback that updates the source column of the selected row, or
	 * adds new rows if no row is selected.
	 *
	 * @param grid
	 *            the grid being updated
	 * @return the remote file chooser callback
	 */
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

	/**
	 * Recursively adds records to the given grid, one per selected path.
	 *
	 * @param grid
	 *            the grid to add records to
	 * @param pi
	 *            the selected path infos
	 * @param i
	 *            the current path index
	 * @param attr
	 *            the attribute name to set with the path
	 */
	private void addDataRecursive(ListGrid grid, PathInfo[] pi, int i, String attr) {
		if (i < pi.length) {
			Record rec = new Record(Collections.singletonMap(attr, pi[i].path));
			grid.addData(rec, (dsResponse, data, dsRequest) -> addDataRecursive(grid, pi, i + 1, attr));
		}
	}

	/**
	 * Recursively updates records in the given grid with the selected paths,
	 * falling back to adding new records when the grid runs out of rows.
	 *
	 * @param grid
	 *            the grid to update records in
	 * @param pi
	 *            the selected path infos
	 * @param start
	 *            the grid row index at which to start updating
	 * @param i
	 *            the current path index
	 * @param attr
	 *            the attribute name to set with the path
	 */
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

	/**
	 * Builds the expandable detail tree grid for a given SDR row, showing the
	 * per-entry status (OK / SIZE / SHA1 / MISSING / SKIPPED / UNKNOWN) of the
	 * checked torrent.
	 *
	 * @param rcrd
	 *            the parent SDR record to display details for
	 * @return the configured tree grid
	 */
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

	/**
	 * Builds the context menu of the expansion tree grid, offering a toggle to
	 * show or hide OK entries.
	 *
	 * @param grid
	 *            the tree grid the menu is attached to
	 * @param rcrd
	 *            the parent SDR record
	 * @param showok
	 *            mutable holder for the current "show OK" state
	 * @return the configured menu
	 */
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

	/**
	 * Builds the data source for the expansion tree grid, injecting the parent
	 * SDR source path and the "show OK" flag as extra data, and refreshing the
	 * show-OK state from the server response.
	 *
	 * @param rcrd
	 *            the parent SDR record
	 * @param showok
	 *            mutable holder for the current "show OK" state
	 * @return the configured data source
	 */
	private com.smartgwt.client.data.DataSource buildExpansionDataSource(ListGridRecord rcrd, Boolean[] showok) {
		final var ds = DSBatchTrntChkReportTree.getInstance();
		final var extradata = new HashMap<String, String>();
		extradata.put(SRC, rcrd.getAttribute(SRC));
		extradata.put(SHOW_OK, Boolean.toString(showok[0] == null || showok[0]));
		ds.setCB(data -> showok[0] = Optional.ofNullable(XMLTools.selectString(data, "/response/showOK"))
				.map(Boolean::valueOf).orElse(true)).setExtraData(extradata);
		return ds;
	}

	/**
	 * Builds the "status" column field of the expansion tree grid, with a cell
	 * formatter that color-codes the status value.
	 *
	 * @return the configured list grid field
	 */
	private ListGridField buildStatusField() {
		ListGridField field = new ListGridField(STATUS);
		field.setWidth(100);
		field.setCellFormatter((value, rec, rowNum, colNum) -> statusFormatter(value));
		return field;
	}

	/**
	 * Builds the "title" column field of the expansion tree grid, whose hover
	 * tooltip shows the parent SDR row title.
	 *
	 * @param rcrd
	 *            the parent SDR record
	 * @return the configured list grid field
	 */
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

	/**
	 * Builds the "length" column field of the expansion tree grid, whose hover
	 * tooltip shows a human-readable file size.
	 *
	 * @return the configured list grid field
	 */
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

	/**
	 * Formats a torrent-check status value as color-coded HTML.
	 *
	 * @param value
	 *            the raw status value
	 * @return the formatted HTML string, or {@code null} if the value is {@code null}
	 */
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

	/**
	 * Converts a byte count into a human-readable file size string (e.g. "1.5 KB").
	 *
	 * @param size
	 *            the size in bytes
	 * @return the formatted file size string
	 */
	private static String readableFileSize(long size) {
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return NumberFormat.getFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * Builds the bottom control form hosting the check mode selector, the
	 * archived-folder detection, remove-unknown-files and remove-wrong-sized
	 * checkboxes, and the start button.
	 *
	 * @return the configured dynamic form
	 */
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

	/**
	 * Builds the check mode selector (FILENAME / FILESIZE / SHA1), persisted as a
	 * global property. Changing the mode also disables the remove-wrong-sized
	 * checkbox when the FILENAME mode is selected.
	 *
	 * @return the configured select item
	 */
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

	/**
	 * Builds the "detect archived folders" checkbox, persisted as a global property.
	 *
	 * @return the configured checkbox item
	 */
	private CheckboxItem buildDetectArchivedFolderItem() {
		CheckboxItem item = new CheckboxItem();
		item.setTitle(Client.getSession().getMsg("BatchTrrntChkPanel.chckbxDetectArchivedFolder.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.detect_archived_folders", true));
		item.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("trntchk.detect_archived_folders", (Boolean) e.getValue()))));
		return item;
	}

	/**
	 * Builds the "remove unknown files" checkbox, persisted as a global property.
	 *
	 * @return the configured checkbox item
	 */
	private CheckboxItem buildRemoveUnknownFilesItem() {
		CheckboxItem item = new CheckboxItem();
		item.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.chckbxRemoveUnknownFiles.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.remove_unknown_files", false));
		item.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_unknown_files", (Boolean) e.getValue()))));
		return item;
	}

	/**
	 * Builds the "remove wrong sized files" checkbox, persisted as a global
	 * property and disabled when the check mode is FILENAME.
	 *
	 * @return the configured checkbox item
	 */
	private CheckboxItem buildRemoveWrongSizedItem() {
		CheckboxItem item = new CheckboxItem("remove_wrong_sized_files");
		item.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.chckbxRemoveWrongSized.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.remove_wrong_sized_files", false));
		item.setDisabled(FILENAME.equals(Client.getSession().getSetting(TRNTCHK_MODE, FILENAME)));
		item.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_wrong_sized_files", (Boolean) e.getValue()))));
		return item;
	}

	/**
	 * Builds the "start" button which triggers the batch torrent check on the server.
	 *
	 * @return the configured button item
	 */
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
