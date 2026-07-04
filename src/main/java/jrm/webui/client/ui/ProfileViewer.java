package jrm.webui.client.ui;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import jrm.webui.client.Client;
import jrm.webui.client.datasources.DSAnyware;
import jrm.webui.client.datasources.DSAnywareList;
import jrm.webui.client.datasources.DSAnywareListList;

/**
 * Window displaying the contents of a profile as three synchronized list grids:
 * the list of ware lists (e.g. machine lists), the wares within the selected
 * list (e.g. machines), and the entries (ROMs/disks/samples) within the
 * selected ware.
 * <p>
 * Each grid is paired with a toolbar of status filter toggle buttons. The
 * viewer registers itself with the {@link Client} child-window list and can be
 * reset (refreshed) programmatically through the static {@link #reset()} API.
 *
 * @since 2.5
 */
public class ProfileViewer extends Window //NOSONAR
{
	/** Status value indicating a fully complete set. */
	private static final String STATUS_COMPLETE = "COMPLETE";
	/** Status value indicating a fully missing set. */
	private static final String STATUS_MISSING = "MISSING";
	/** Status value indicating a partially complete set. */
	private static final String STATUS_PARTIAL = "PARTIAL";
	/** Status value indicating an unknown set state. */
	private static final String STATUS_UNKNOWN = "UNKNOWN";
	/** Record attribute holding the status of a ware or entry. */
	private static final String STATUS = "status";
	/** Extra-data key used to signal a datasource reset. */
	private static final String RESET = "reset";

	/** The grid displaying the list of ware lists. */
	AnywareListList anywareListList;
	/** The grid displaying the wares within the selected ware list. */
	AnywareList anywareList;
	/** The grid displaying the entries within the selected ware. */
	Anyware anyware;

	/**
	 * Builds a server-side result set that disables client-side filtering and sorting.
	 *
	 * @return the configured result set
	 */
	private static ResultSet buildServerResultSet() {
		var rs = new ResultSet();
		rs.setUseClientFiltering(false);
		rs.setUseClientSorting(false);
		return rs;
	}

	/**
	 * List grid displaying the available ware lists (e.g. machine lists) of the current profile.
	 * <p>
	 * Selecting a list resets the {@link AnywareList} grid to display its contents.
	 *
	 * @since 2.5
	 */
	class AnywareListList extends ListGrid //NOSONAR
    {
		/** Status-to-icon mapping for ware list rows. */
		private static final Map<String, String> STATUS_ICONS = Map.of(
				STATUS_COMPLETE, "/images/disk_multiple_green.png",
				STATUS_MISSING, "/images/disk_multiple_red.png",
				STATUS_PARTIAL, "/images/disk_multiple_orange.png",
				STATUS_UNKNOWN, "/images/disk_multiple_gray.png");
		/** The datasource backing this grid. */
		private final DSAnywareListList ds;

		/**
		 * Constructs the ware-list grid, binding its datasource and selection/data handlers.
		 */
		public AnywareListList() {
			super();
			ds = DSAnywareListList.getInstance();
			setCanEdit(false);
			setCanRemoveRecords(false);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.SINGLE);
			setDataProperties(buildServerResultSet());
			setCanSort(false);
			addSelectionChangedHandler(event -> {
				if (event.getState())
					anywareList.reset(event.getRecord(), getDataSource());
			});
			addDataArrivedHandler(event -> {
				ds.setExtraData(Collections.emptyMap());
				if (getTotalRows() > 0 && !Boolean.TRUE.equals(anySelected()))
					selectSingleRecord(0);
			});
			setDataSource(ds,
					buildField("name", 80, "25%", null),
					buildField("description", 20, null, null),
					buildField("have", 80, "25%", Alignment.CENTER));
		}

		/**
		 * Returns the status icon for the name field, falling back to the default behavior otherwise.
		 */
		@Override
		public String getValueIcon(ListGridField field, Object value, ListGridRecord recrd) {
			if ("name".equals(field.getName()))
				return STATUS_ICONS.get(recrd.getAttribute(STATUS));
			return super.getValueIcon(field, value, recrd);
		}

		/**
		 * Resets the grid by signaling a reset to the datasource and refreshing or fetching its data.
		 */
		public void reset() {
			ds.setExtraData(Collections.singletonMap(RESET, "true"));
			if (Boolean.TRUE.equals(willFetchData(null)))
				fetchData();
			else
				refreshData();
		}
	}

	/**
	 * Builds a list grid field with the given minimum width, width, and alignment.
	 *
	 * @param name the field name
	 * @param minWidth the minimum width in pixels
	 * @param width the width, or {@code null} to leave it unset
	 * @param align the alignment, or {@code null} to leave it unset
	 * @return the configured list grid field
	 */
	private static ListGridField buildField(String name, int minWidth, String width, Alignment align) {
		var field = new ListGridField(name);
		field.setMinWidth(minWidth);
		if (width != null)
			field.setWidth(width);
		if (align != null)
			field.setAlign(align);
		return field;
	}

	/**
	 * Builds a titled list grid field with the given minimum width, width, alignment, edit, and filter capabilities.
	 *
	 * @param name the field name
	 * @param title the field title
	 * @param minWidth the minimum width in pixels
	 * @param width the width, or {@code null} to leave it unset
	 * @param align the alignment, or {@code null} to leave it unset
	 * @param canEdit whether the field is editable
	 * @param canFilter whether the field can be filtered
	 * @return the configured list grid field
	 */
	private static ListGridField buildField(String name, String title, int minWidth, String width,
			Alignment align, boolean canEdit, boolean canFilter) {
		var field = new ListGridField(name, title);
		field.setMinWidth(minWidth);
		if (width != null)
			field.setWidth(width);
		if (align != null)
			field.setAlign(align);
		field.setCanEdit(canEdit);
		field.setCanFilter(canFilter);
		return field;
	}

	/**
	 * List grid displaying the wares (machines or software) within the selected ware list.
	 * <p>
	 * Selecting a ware resets the {@link Anyware} grid to display its entries.
	 * Double-clicking a {@code cloneof} or {@code romof} cell navigates to the
	 * referenced ware.
	 *
	 * @since 2.5
	 */
	class AnywareList extends ListGrid //NOSONAR
    {
		/** Record attribute holding the name of a ware. */
		private static final String NAME = "name";
		/** Status-to-icon mapping for ware rows. */
		private static final Map<String, String> STATUS_ICONS = Map.of(
				STATUS_COMPLETE, "/images/folder_closed_green.png",
				STATUS_MISSING, "/images/folder_closed_red.png",
				STATUS_PARTIAL, "/images/folder_closed_orange.png",
				STATUS_UNKNOWN, "/images/folder_closed_gray.png");
		/** Record attribute holding the clone-of reference of a ware. */
		private static final String CLONEOF = "cloneof";
		/** Record attribute holding the rom-of reference of a ware. */
		private static final String ROMOF = "romof";
		/** Type-to-icon mapping for ware rows. */
		private static final Map<String, String> TYPE_ICONS = Map.of(
				"BIOS", "/images/icons/application_osx_terminal.png",
				"DEVICE", "/images/icons/computer.png",
				"MECHANICAL", "/images/icons/wrench.png",
				"STANDARD", "/images/icons/joystick.png");
		/** Whether the current list is a machine list (controls visibility of machine-specific fields). */
		private boolean ismachinelist = false;
		/** Index of a record to select once data arrives, or {@code null} if none. */
		private Integer toSelect = null;
		/** The datasource backing this grid. */
		private final DSAnywareList ds;

		/**
		 * Constructs the ware grid, binding its datasource, selection/click handlers, context menu, and fields.
		 */
		public AnywareList() //NOSONAR
		{
			ds = DSAnywareList.getInstance();
			setCanEdit(true);
			setDataProperties(buildServerResultSet());
			setCanRemoveRecords(false);
			setShowFilterEditor(true);
			setSelectionType(SelectionStyle.SINGLE);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setAllowFilterOperators(false);
			setContextMenu(buildAnywareListMenu());
			addSelectionChangedHandler(event -> {
				if (event.getState())
					anyware.reset(event.getRecord(), getDataSource());
			});
			addDataArrivedHandler(event -> {
				ds.setExtraData(Collections.emptyMap());
				if (getTotalRows() > 0) {
					if (toSelect != null) {
						selectSingleRecord(toSelect);
						toSelect = null;
					} else if (!Boolean.TRUE.equals(anySelected()))
						selectSingleRecord(0);
				}
				refreshFields();
			});
			addCellDoubleClickHandler(event -> {
				var field = getField(event.getColNum());
				var fieldName = field.getName();
				if (CLONEOF.equals(fieldName) || ROMOF.equals(fieldName)) {
					var rec = new Record(getCriteria().getValues());
					rec.setAttribute("find", event.getRecord().getAttribute(fieldName));
					ds.performCustomOperation("find", rec, (dsResponse, data, dsRequest) -> {
						try {
							var idx = Integer.valueOf(dsResponse.getAttribute("found"));
							var recrd = getRecordList().get(idx);
							if (recrd == null || recrd.getAttribute(NAME) == null)
								toSelect = idx;
							else
								selectSingleRecord(idx);
							scrollToRow(idx);
						} catch (NumberFormatException e) {
							// do nothing
						}
					});
				}
			});
			setDataSource(ds,
					buildStatusField(STATUS, Client.getSession().getMsg("MachineListRenderer.Status"), STATUS_ICONS),
					buildField(NAME, Client.getSession().getMsg("MachineListRenderer.Name"), 70, "15%", null, false, true),
					buildField("description", Client.getSession().getMsg("MachineListRenderer.Description"), 0, null, null, false, true),
					buildField("have", Client.getSession().getMsg("MachineListRenderer.Have"), 40, "8%", Alignment.CENTER, false, false),
					buildField(CLONEOF, Client.getSession().getMsg("MachineListRenderer.CloneOf"), 70, "15%", null, false, true),
					buildRomOfField(),
					buildSampleOfField(),
					buildSelectedField());
			getField("have").setCanSort(false);
		}

		/**
		 * Builds the context menu with collect-keywords and select-none/all/invert actions.
		 *
		 * @return the configured menu
		 */
		private Menu buildAnywareListMenu() {
			var menu = new Menu();
			var collectKeywords = new MenuItem(Client.getSession().getMsg("ProfileViewer.mntmCollectKeywords.text"));
			var selectNone = new MenuItem(Client.getSession().getMsg("ProfileViewer.mntmSelectNone.text"));
			selectNone.addClickHandler(event -> getDataSource().performCustomOperation("selectNone",
					new Record(getCriteria().getValues()), (dsResponse, data, dsRequest) -> refreshData()));
			var selectAll = new MenuItem(Client.getSession().getMsg("ProfileViewer.mntmSelectAll.text"));
			selectAll.addClickHandler(event -> getDataSource().performCustomOperation("selectAll",
					new Record(getCriteria().getValues()), (dsResponse, data, dsRequest) -> refreshData()));
			var selectInvert = new MenuItem(Client.getSession().getMsg("ProfileViewer.mntmSelectInvert.text"));
			selectInvert.addClickHandler(event -> getDataSource().performCustomOperation("selectInvert",
					new Record(getCriteria().getValues()), (dsResponse, data, dsRequest) -> refreshData()));
			menu.setItems(collectKeywords, selectNone, selectAll, selectInvert);
			return menu;
		}

		/**
		 * Builds a status field displaying only a value icon.
		 *
		 * @param name the field name
		 * @param title the field title
		 * @param icons the status-to-icon mapping
		 * @return the configured status field
		 */
		private ListGridField buildStatusField(String name, String title, Map<String, String> icons) {
			var field = new ListGridField(name, title, 24);
			field.setValueIcons(icons);
			field.setShowValueIconOnly(true);
			field.setAlign(Alignment.CENTER);
			field.setCanEdit(false);
			field.setCanFilter(false);
			return field;
		}

		/**
		 * Builds the rom-of field, visible only for machine lists.
		 *
		 * @return the configured rom-of field
		 */
		private ListGridField buildRomOfField() {
			var field = new ListGridField(ROMOF, Client.getSession().getMsg("MachineListRenderer.RomOf"));
			field.setMinWidth(70);
			field.setWidth("15%");
			field.setCanEdit(false);
			field.setShowIfCondition((grid, f, fieldNum) -> ismachinelist);
			return field;
		}

		/**
		 * Builds the sample-of field, visible only for machine lists.
		 *
		 * @return the configured sample-of field
		 */
		private ListGridField buildSampleOfField() {
			var field = new ListGridField("sampleof", Client.getSession().getMsg("MachineListRenderer.SampleOf"));
			field.setMinWidth(70);
			field.setWidth("15%");
			field.setCanEdit(false);
			field.setShowIfCondition((grid, f, fieldNum) -> ismachinelist);
			return field;
		}

		/**
		 * Builds the toggleable "selected" field used to mark wares for inclusion.
		 *
		 * @return the configured selected field
		 */
		private ListGridField buildSelectedField() {
			var field = new ListGridField("selected", Client.getSession().getMsg("MachineListRenderer.Selected"), 20);
			field.setAlign(Alignment.CENTER);
			field.setCanToggle(true);
			field.setCanSort(false);
			return field;
		}

		/**
		 * Returns the icon for a ware cell based on the field name and record attributes.
		 */
		@Override
		public String getValueIcon(ListGridField field, Object value, ListGridRecord recrd) {
			return switch (field.getName()) {
				case NAME -> TYPE_ICONS.get(recrd.getAttribute("type"));
				case CLONEOF -> STATUS_ICONS.get(recrd.getAttribute("cloneof_status"));
				case ROMOF -> STATUS_ICONS.get(recrd.getAttribute("romof_status"));
				case "sampleof" -> STATUS_ICONS.get(recrd.getAttribute("sampleof_status"));
				default -> super.getValueIcon(field, value, recrd);
			};
		}

		/**
		 * Resets the grid to display the wares of the given ware-list record, signaling a reset to the datasource.
		 *
		 * @param recrd the selected ware-list record
		 * @param ds the datasource of the ware-list grid
		 */
		public void reset(Record recrd, DataSource ds) {
			this.ds.setExtraData(Collections.singletonMap(RESET, "true"));
			ismachinelist = "*".equals(recrd.getAttribute(NAME));
			var criteria = new Criteria();
			criteria.addCriteria("list", recrd.getAttribute(NAME));
			if (Boolean.TRUE.equals(willFetchData(criteria)))
				fetchRelatedData(recrd, ds);
			else
				refreshData();
		}
	}

	/**
	 * List grid displaying the entries (ROMs/disks/samples) within the selected ware.
	 * <p>
	 * Provides a context menu to copy entry attributes (CRC, SHA-1, name) and to
	 * search the web for the selected entry.
	 *
	 * @since 2.5
	 */
	class Anyware extends ListGrid //NOSONAR
    {
		/** Status-to-icon mapping for entry rows. */
		private static final Map<String, String> STATUS_ICONS = Map.of(
				"OK", "/images/icons/bullet_green.png",
				"KO", "/images/icons/bullet_red.png",
				STATUS_UNKNOWN, "/images/icons/bullet_black.png");
		/** Dump-status-to-icon mapping for entry rows. */
		private static final Map<String, String> DUMPSTATUS_ICONS = Map.of(
				"verified", "/images/icons/star.png",
				"good", "/images/icons/tick.png",
				"baddump", "/images/icons/delete.png",
				"nodump", "/images/icons/error.png");
		/** Type-to-icon mapping for entry rows. */
		private static final Map<String, String> TYPE_ICONS = Map.of(
				"ROM", "/images/rom_small.png",
				"DISK", "/images/icons/drive.png",
				"SAMPLE", "/images/icons/sound.png");

		/** The datasource backing this grid. */
		private final DSAnyware ds;

		/**
		 * Constructs the entry grid, binding its datasource, context menu, and fields.
		 */
		public Anyware() {
			super();
			ds = DSAnyware.getInstance();
			setDataProperties(buildServerResultSet());
			setCanEdit(false);
			setCanRemoveRecords(false);
			setSelectionType(SelectionStyle.SINGLE);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setAlternateRecordStyles(true);
			setCanSort(false);
			setContextMenu(buildAnywareMenu());
			addDataArrivedHandler(event -> ds.setExtraData(Collections.emptyMap()));
			var codeFormatter = (com.smartgwt.client.widgets.grid.CellFormatter) (value, recrd, rowNum, colNum) -> {
				if (value != null)
					return "<code>" + value + "</code>";
				return null;
			};
			setDataSource(ds,
					buildStatusField(STATUS, Client.getSession().getMsg("AnywareRenderer.Status"), STATUS_ICONS),
					buildNameField(),
					buildSizeField(),
					buildCodeField("crc", 48, codeFormatter),
					buildCodeField("md5", 100, codeFormatter),
					buildCodeField("sha1", 160, codeFormatter),
					buildMergeField(),
					buildDumpStatusField());
		}

		/**
		 * Builds a status field displaying only a value icon.
		 *
		 * @param name the field name
		 * @param title the field title
		 * @param icons the status-to-icon mapping
		 * @return the configured status field
		 */
		private ListGridField buildStatusField(String name, String title, Map<String, String> icons) {
			var field = new ListGridField(name, title, 24);
			field.setValueIcons(icons);
			field.setShowValueIconOnly(true);
			field.setAlign(Alignment.CENTER);
			field.setCanEdit(false);
			return field;
		}

		/**
		 * Builds the name field, taking the remaining horizontal space.
		 *
		 * @return the configured name field
		 */
		private ListGridField buildNameField() {
			var field = new ListGridField("name", Client.getSession().getMsg("AnywareRenderer.Name"));
			field.setMinWidth(128);
			field.setWidth("*");
			return field;
		}

		/**
		 * Builds the size field, auto-fitting its width to the content.
		 *
		 * @return the configured size field
		 */
		private ListGridField buildSizeField() {
			var field = new ListGridField("size", Client.getSession().getMsg("AnywareRenderer.Size"));
			field.setMinWidth(48);
			field.setAutoFitWidth(true);
			return field;
		}

		/**
		 * Builds a hash code field (crc/md5/sha1) rendered in a monospace {@code <code>} element.
		 *
		 * @param name the field name
		 * @param minWidth the minimum width in pixels
		 * @param formatter the cell formatter wrapping the value in a {@code <code>} element
		 * @return the configured code field
		 */
		private ListGridField buildCodeField(String name, int minWidth, com.smartgwt.client.widgets.grid.CellFormatter formatter) {
			var field = new ListGridField(name);
			field.setMinWidth(minWidth);
			field.setAutoFitWidth(true);
			field.setCellFormatter(formatter);
			return field;
		}

		/**
		 * Builds the merge field, auto-fitting its width to the content.
		 *
		 * @return the configured merge field
		 */
		private ListGridField buildMergeField() {
			var field = new ListGridField("merge", Client.getSession().getMsg("AnywareRenderer.Merge"));
			field.setMinWidth(128);
			field.setAutoFitWidth(true);
			return field;
		}

		/**
		 * Builds the dump-status field displaying only a value icon.
		 *
		 * @return the configured dump-status field
		 */
		private ListGridField buildDumpStatusField() {
			var field = new ListGridField("dumpstatus", Client.getSession().getMsg("AnywareRenderer.DumpStatus"), 24);
			field.setValueIcons(DUMPSTATUS_ICONS);
			field.setShowValueIconOnly(true);
			field.setAlign(Alignment.CENTER);
			field.setCanEdit(false);
			return field;
		}

		/**
		 * Builds the context menu with copy-CRC/SHA-1/name and web-search actions.
		 *
		 * @return the configured menu
		 */
		private Menu buildAnywareMenu() {
			var menu = new Menu();
			var dialog = new Dialog();
			dialog.setWidth(350);
			menu.setItems(
					buildCopyMenuItem("Copy CRC", "crc", dialog),
					buildCopyMenuItem("Copy SHA1", "sha1", dialog),
					buildCopyMenuItem("Copy Name", "name", dialog),
					buildSearchMenuItem());
			return menu;
		}

		/**
		 * Builds a menu item that copies the given record attribute to the clipboard through a dialog.
		 *
		 * @param title the menu item title
		 * @param attr the record attribute to copy
		 * @param dialog the dialog used to prompt the user for the copy
		 * @return the configured menu item
		 */
		private MenuItem buildCopyMenuItem(String title, String attr, Dialog dialog) {
			var item = new MenuItem(title);
			item.addClickHandler(event -> {
				var recrd = getSelectedRecord();
				if (recrd != null)
					SC.askforValue("Copy", "Select and Copy the text below", recrd.getAttribute(attr), v -> {
					}, dialog);
			});
			item.setEnableIfCondition((target, menu, item1) -> getSelectedRecord() != null);
			return item;
		}

		/**
		 * Builds a menu item that searches the web for the selected entry's name and hash.
		 *
		 * @return the configured menu item
		 */
		private MenuItem buildSearchMenuItem() {
			var item = new MenuItem("Search on the Web");
			item.addClickHandler(event -> {
				var recrd = getSelectedRecord();
				if (recrd != null) {
					var name = recrd.getAttribute("name");
					var crc = recrd.getAttribute("crc");
					var sha1 = recrd.getAttribute("sha1");
					var hash = Optional.ofNullable(Optional.ofNullable(crc).orElse(sha1)).map(h -> '+' + h).orElse("");
					com.google.gwt.user.client.Window.open(
							"https://google.com/search?q=" + URL.encodeQueryString('"' + name + '"') + hash, "_blank", null);
				}
			});
			item.setEnableIfCondition((target, menu, item1) -> getSelectedRecord() != null);
			return item;
		}

		/**
		 * Returns the type icon for the name field, falling back to the default behavior otherwise.
		 */
		@Override
		public String getValueIcon(ListGridField field, Object value, ListGridRecord recrd) {
			if ("name".equals(field.getName()))
				return TYPE_ICONS.get(recrd.getAttribute("type"));
			return super.getValueIcon(field, value, recrd);
		}

		/**
		 * Resets the grid to display the entries of the given ware record, signaling a reset to the datasource.
		 *
		 * @param recrd the selected ware record
		 * @param ds the datasource of the ware grid
		 */
		public void reset(Record recrd, DataSource ds) {
			this.ds.setExtraData(Collections.singletonMap(RESET, "true"));
			var criteria = new Criteria();
			criteria.addCriteria("list", recrd.getAttribute("list"));
			criteria.addCriteria("ware", recrd.getAttribute("name"));
			if (Boolean.TRUE.equals(willFetchData(criteria)))
				fetchRelatedData(recrd, ds);
			else
				refreshData();
		}
	}

	/**
	 * Constructs the profile viewer window, builds its layout, shows it, and resets the ware-list grid.
	 */
	public ProfileViewer() {
		super();
		setTitle(Client.getSession().getMsg("ProfileViewer.this.title"));
		setWidth("80%");
		setHeight("80%");
		setAnimateMinimize(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		setHeaderIconDefaults(Map.of("width", 16, "height", 16, "src", "rom.png"));
		addCloseClickHandler(event -> markForDestroy());
		addItem(buildMainLayout());
		show();
		anywareListList.reset();
	}

	/**
	 * Registers this window with the {@link Client} child-window list before initialization.
	 */
	@Override
	protected void onInit() {
		Client.getChildWindows().add(this);
		super.onInit();
	}

	/**
	 * Builds the main vertical layout containing the top (lists) and bottom (entries) panels.
	 *
	 * @return the configured main layout
	 */
	private VLayout buildMainLayout() {
		var main = new VLayout();
		main.addMember(buildTopLayout());
		main.addMember(buildBottomLayout());
		return main;
	}

	/**
	 * Builds the top horizontal layout containing the ware-list and ware panels with a resize bar.
	 *
	 * @return the configured top layout
	 */
	private HLayout buildTopLayout() {
		var top = new HLayout();
		top.setHeight("60%");
		top.addMember(buildAnywareListListPanel());
		top.addMember(buildAnywareListPanel());
		top.setShowResizeBar(true);
		top.setResizeBarTarget("next");
		return top;
	}

	/**
	 * Builds the ware-list panel: the grid and its status filter toolbar.
	 *
	 * @return the configured panel
	 */
	private VLayout buildAnywareListListPanel() {
		var panel = new VLayout();
		var filterButtons = new ToolStripButton[4];
		panel.setWidth("30%");
		anywareListList = new AnywareListList();
		panel.addMember(anywareListList);
		var strip = new ToolStrip();
		filterButtons[0] = buildFilterButton(STATUS_UNKNOWN, "/images/disk_multiple_gray.png",
				"ProfileViewer.tglbtnUnknownWL.toolTipText");
		filterButtons[1] = buildFilterButton(STATUS_MISSING, "/images/disk_multiple_red.png",
				"ProfileViewer.tglbtnMissingWL.toolTipText");
		filterButtons[2] = buildFilterButton(STATUS_PARTIAL, "/images/disk_multiple_orange.png",
				"ProfileViewer.tglbtnPartialWL.toolTipText");
		filterButtons[3] = buildFilterButton(STATUS_COMPLETE, "/images/disk_multiple_green.png",
				"ProfileViewer.tglbtnCompleteWL.toolTipText");
		strip.addButton(filterButtons[0]);
		strip.addButton(filterButtons[1]);
		strip.addButton(filterButtons[2]);
		strip.addButton(filterButtons[3]);
		panel.addMember(strip);
		panel.setShowResizeBar(true);
		for (var btn : filterButtons)
			btn.addClickHandler(e -> updateListListFilter(filterButtons));
		return panel;
	}

	/**
	 * Updates the ware-list grid filter from the selected status toggle buttons.
	 *
	 * @param buttons the status filter toggle buttons
	 */
	private void updateListListFilter(ToolStripButton[] buttons) {
		var filter = Stream.of(buttons).filter(ToolStripButton::isSelected).map(ToolStripButton::getName)
				.collect(Collectors.joining(","));
		if (filter.isEmpty())
			filter = "NONE";
		var criteria = anywareListList.getCriteria();
		if (criteria != null)
			criteria.addCriteria(new Criteria(STATUS, filter));
		else
			criteria = new Criteria(STATUS, filter);
		anywareListList.filterData(criteria);
	}

	/**
	 * Builds the ware panel: the grid and its status filter toolbar.
	 *
	 * @return the configured panel
	 */
	private VLayout buildAnywareListPanel() {
		var panel = new VLayout();
		var filterButtons = new ToolStripButton[4];
		anywareList = new AnywareList();
		panel.addMember(anywareList);
		var strip = new ToolStrip();
		filterButtons[0] = buildFilterButton(STATUS_UNKNOWN, "/images/folder_closed_gray.png", null);
		filterButtons[1] = buildFilterButton(STATUS_MISSING, "/images/folder_closed_red.png",
				"ProfileViewer.tglbtnMissingW.toolTipText");
		filterButtons[2] = buildFilterButton(STATUS_PARTIAL, "/images/folder_closed_orange.png",
				"ProfileViewer.tglbtnPartialW.toolTipText");
		filterButtons[3] = buildFilterButton(STATUS_COMPLETE, "/images/folder_closed_green.png",
				"ProfileViewer.tglbtnCompleteW.toolTipText");
		strip.addButton(filterButtons[0]);
		strip.addButton(filterButtons[1]);
		strip.addButton(filterButtons[2]);
		strip.addButton(filterButtons[3]);
		panel.addMember(strip);
		for (var btn : filterButtons)
			btn.addClickHandler(e -> updateListFilter(filterButtons));
		return panel;
	}

	/**
	 * Updates the ware grid filter from the selected status toggle buttons.
	 *
	 * @param buttons the status filter toggle buttons
	 */
	private void updateListFilter(ToolStripButton[] buttons) {
		var filter = Stream.of(buttons).filter(ToolStripButton::isSelected).map(ToolStripButton::getName)
				.collect(Collectors.joining(","));
		if (filter.isEmpty())
			filter = "NONE";
		var criteria = anywareList.getCriteria();
		if (criteria != null)
			criteria.addCriteria(new Criteria(STATUS, filter));
		else
			criteria = new Criteria(STATUS, filter);
		anywareList.filterData(criteria);
	}

	/**
	 * Builds the bottom panel: the entry grid and its status filter toolbar.
	 *
	 * @return the configured bottom layout
	 */
	private VLayout buildBottomLayout() {
		var panel = new VLayout();
		var filterButtons = new ToolStripButton[3];
		anyware = new Anyware();
		panel.addMember(anyware);
		var strip = new ToolStrip();
		filterButtons[0] = buildFilterButton(STATUS_UNKNOWN, "/images/icons/bullet_black.png",
				"ProfileViewer.tglbtnUnknown.toolTipText");
		filterButtons[1] = buildFilterButton("KO", "/images/icons/bullet_red.png",
				"ProfileViewer.tglbtnBad.toolTipText");
		filterButtons[2] = buildFilterButton("OK", "/images/icons/bullet_green.png",
				"ProfileViewer.tglbtnOK.toolTipText");
		strip.addButton(filterButtons[0]);
		strip.addButton(filterButtons[1]);
		strip.addButton(filterButtons[2]);
		panel.addMember(strip);
		for (var btn : filterButtons)
			btn.addClickHandler(e -> updateAnywareFilter(filterButtons));
		return panel;
	}

	/**
	 * Updates the entry grid filter from the selected status toggle buttons.
	 *
	 * @param buttons the status filter toggle buttons
	 */
	private void updateAnywareFilter(ToolStripButton[] buttons) {
		var filter = Stream.of(buttons).filter(ToolStripButton::isSelected).map(ToolStripButton::getName)
				.collect(Collectors.joining(","));
		if (filter.isEmpty())
			filter = "NONE";
		var criteria = anyware.getCriteria();
		if (criteria != null)
			criteria.addCriteria(new Criteria(STATUS, filter));
		else
			criteria = new Criteria(STATUS, filter);
		anyware.filterData(criteria);
	}

	/**
	 * Builds a checkbox-style toggle filter button with the given name, icon, and optional tooltip.
	 *
	 * @param name the button name (used as the filter value)
	 * @param icon the button icon path
	 * @param promptKey the i18n message key for the tooltip, or {@code null} for no tooltip
	 * @return the configured toggle button
	 */
	private ToolStripButton buildFilterButton(String name, String icon, String promptKey) {
		var btn = new ToolStripButton();
		btn.setName(name);
		btn.setIcon(icon);
		btn.setActionType(SelectionType.CHECKBOX);
		btn.setShowFocused(false);
		btn.setSelected(true);
		if (promptKey != null)
			btn.setPrompt(Client.getSession().getMsg(promptKey));
		return btn;
	}

	/**
	 * Removes this window from the {@link Client} child-window list on destruction.
	 */
	@Override
	protected void onDestroy() {
		Client.getChildWindows().remove(this);
		super.onDestroy();
	}

	/**
	 * Flag guarding the static {@link #reset()} from running while a reset callback is in progress.
	 */
	public static boolean canResetPV = true;

	/**
	 * Callback executed within the guarded scope of {@link #reset(ResetCB)}.
	 *
	 * @since 2.5
	 */
	public interface ResetCB {
		/** Applies the reset action. */
		void apply();
	}

	/**
	 * Runs the given callback within a reset guard, then schedules a deferred reset of the ware-list grid.
	 *
	 * @param cb the callback to apply before resetting
	 */
	public static void reset(ResetCB cb) {
		canResetPV = false;
		cb.apply();
		canResetPV = true;
		reset();
	}

	/** Deferred timer used to coalesce reset requests. */
	private static Timer resetTimer = null;

	/**
	 * Schedules a deferred reset of the ware-list grid of the currently visible profile viewer, if any.
	 * <p>
	 * Repeated calls coalesce into a single reset one second after the last call.
	 */
	public static void reset() {
		if (resetTimer == null) {
			resetTimer = new Timer() {
				@Override
				public void run() {
					if (canResetPV && Client.getMainWindow().scannerPanel.profileViewer != null
							&& Client.getChildWindows().contains(Client.getMainWindow().scannerPanel.profileViewer))
						Client.getMainWindow().scannerPanel.profileViewer.anywareListList.reset();
				}
			};
		}
		resetTimer.cancel();
		resetTimer.schedule(1000);
	}

}
