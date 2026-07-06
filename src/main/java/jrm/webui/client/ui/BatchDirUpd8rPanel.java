package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.datasources.DSBatchDat2DirResult;
import jrm.webui.client.datasources.DSBatchDat2DirSDR;
import jrm.webui.client.datasources.DSBatchDat2DirSrc;
import jrm.webui.client.protocol.Q_Dat2Dir;
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.ui.RemoteFileChooser.PathInfo;
import jrm.webui.client.utils.EnhJSO;

/**
 * SmartGWT panel for the batch Dir2Dat (directory updat8r) UI.
 * <p>
 * Shows a source-archives grid, an SDR (source/destination/result) grid with
 * expandable per-row details, a dry-run checkbox, and a start button. The SDR
 * grid context menu lets the user add/update DATs, set destinations, delete
 * entries, and apply TZIP / DIR / custom scanner settings presets.
 *
 * @since 2.5
 */
public class BatchDirUpd8rPanel extends VLayout //NOSONAR
{

	/** Name of the column holding the source DAT path. */
	private static final String SRC = "src";
	/** Name of the column holding the destination directory path. */
	private static final String DST = "dst";
	/** Name of the column holding the operation result. */
	private static final String RESULT = "result";
	/** Name of the column holding the selection flag. */
	private static final String SELECTED = "selected";
	/** Icon path for the start action. */
	private static final String ICON_BULLET_GO = "icons/bullet_go.png";

	/** The grid listing the source DAT directories. */
	private final ListGrid srcGrid;
	/** The SDR (source/destination/result) grid with expandable detail rows. */
	final ListGrid sdr;
	/** The currently displayed report, if any. */
	ReportLite report;

	/**
	 * Constructs the panel, building the source grid, the SDR grid, and the
	 * bottom control layout.
	 */
	public BatchDirUpd8rPanel() {
		setHeight100();
		srcGrid = buildSrcList();
		addMember(srcGrid);
		sdr = buildSdrList();
		addMember(sdr);
		addMember(buildBottomLayout());
	}

	/**
	 * Builds the bottom layout holding the dry-run checkbox and the start button.
	 *
	 * @return the configured horizontal layout
	 */
	private HLayout buildBottomLayout() {
		HLayout hLayout = new HLayout();
		hLayout.setHeight(20);
		hLayout.addMember(new LayoutSpacer("*", 20));

		DynamicForm form = new DynamicForm();
		form.setColWidths(100, 50);
		form.setWrapItemTitles(false);
		CheckboxItem cbxDryRun = buildDryRunCheckbox();
		form.setItems(cbxDryRun);
		hLayout.addMember(form);

		IButton start = buildStartButton();
		hLayout.addMember(start);
		return hLayout;
	}

	/**
	 * Builds the "dry run" checkbox, persisted as a global property.
	 *
	 * @return the configured checkbox item
	 */
	private CheckboxItem buildDryRunCheckbox() {
		CheckboxItem cbxDryRun = new CheckboxItem("dry_run",
				Client.getSession().getMsg("MainFrame.cbBatchToolsDat2DirDryRun.text"));
		cbxDryRun.setLabelAsTitle(true);
		cbxDryRun.setDefaultValue(Client.getSession().getSettingAsBoolean("dat2dir.dry_run", true));
		cbxDryRun.setShowLabel(false);
		cbxDryRun.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("dat2dir.dry_run", (Boolean) e.getValue()))));
		return cbxDryRun;
	}

	/**
	 * Builds the start button which disables the batch tab and sends a start request.
	 *
	 * @return the configured button
	 */
	private IButton buildStartButton() {
		IButton start = new IButton(Client.getSession().getMsg("MainFrame.btnStart.text"), e -> {
			Client.getMainWindow().mainPane.disableTab(1);
			Client.sendMsg(JsonUtils.stringify(Q_Dat2Dir.Start.instantiate()));
		});
		start.setIcon(ICON_BULLET_GO);
		return start;
	}

	/**
	 * Builds the SDR (source/destination/result) list grid.
	 *
	 * @return the configured SDR list grid
	 */
	private ListGrid buildSdrList() {
		return new SDRList();
	}

	/**
	 * Builds the source DAT directories grid.
	 *
	 * @return the configured source grid
	 */
	private ListGrid buildSrcList() {
		ListGrid grid = new ListGrid();
		grid.setHeight("30%");
		grid.setShowResizeBar(true);
		grid.setCanEdit(false);
		grid.setCanHover(true);
		grid.setHoverAutoFitWidth(true);
		grid.setHoverAutoFitMaxWidth("50%");
		grid.setSelectionType(SelectionStyle.MULTIPLE);
		grid.setCanSort(false);
		grid.setAutoFetchData(true);
		grid.setContextMenu(buildSrcContextMenu());
		grid.setDataSource(DSBatchDat2DirSrc.getInstance());
		return grid;
	}

	/**
	 * Inner list grid showing one row per SDR (source DAT / destination dir /
	 * result), with expandable per-row detail grids.
	 */
	final class SDRList extends ListGrid //NOSONAR
    {
		/**
		 * Expansion grid shown when a SDR row is expanded, listing per-source
		 * have/create/fix/miss/total counts and a report button.
		 */
		final class SDRExpList extends ListGrid //NOSONAR
        {
			/**
			 * Builds the expansion grid and fetches related data for the given SDR record.
			 *
			 * @param rec
			 *            the parent SDR record to display details for
			 */
			SDRExpList(ListGridRecord rec) {
				setHeight(200);
				setCanEdit(true);
				setCanHover(true);
				setHoverAutoFitWidth(true);
				setHoverAutoFitMaxWidth("50%");
				setSelectionType(SelectionStyle.NONE);
				setCanSort(false);
				setShowRecordComponents(true);
				setShowRecordComponentsByCell(true);
				setAutoFitExpandField(SRC);
				setAutoFitFieldsFillViewport(true);
				setDataSource(DSBatchDat2DirResult.getInstance(sdr.getDataSource().getID() + ".src"));
				setFields(
						buildExpSrcField(),
						buildExpHaveField(),
						buildExpCreateField(),
						buildExpFixField(),
						buildExpMissField(),
						buildExpTotalField(),
						buildExpReportField());
				fetchRelatedData(rec, sdr.getDataSource());
			}

			/**
			 * Builds the "src" column field of the expansion grid.
			 *
			 * @return the configured list grid field
			 */
			private ListGridField buildExpSrcField() {
				ListGridField field = new ListGridField(SRC);
				field.setAlign(Alignment.RIGHT);
				field.setCanEdit(false);
				return field;
			}

			/** Builds the "have" count column field. */
			private ListGridField buildExpHaveField() {
				ListGridField field = new ListGridField("have");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			/** Builds the "create" count column field. */
			private ListGridField buildExpCreateField() {
				ListGridField field = new ListGridField("create");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			/** Builds the "fix" count column field. */
			private ListGridField buildExpFixField() {
				ListGridField field = new ListGridField("fix");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			/** Builds the "miss" count column field. */
			private ListGridField buildExpMissField() {
				ListGridField field = new ListGridField("miss");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			/** Builds the "total" count column field. */
			private ListGridField buildExpTotalField() {
				ListGridField field = new ListGridField("total");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			/** Builds the "report" column field hosting a per-row report button. */
			private ListGridField buildExpReportField() {
				ListGridField field = new ListGridField("report");
				field.setAlign(Alignment.CENTER);
				field.setDefaultWidth(70);
				field.setAutoFitWidth(true);
				field.setCanEdit(false);
				return field;
			}

			/**
			 * Creates the per-cell component for the expansion grid; renders a
			 * "Report" button in the report column, defers to the super
			 * implementation otherwise.
			 *
			 * @param rec
			 *            the record being rendered
			 * @param colNum
			 *            the column index being rendered
			 * @return the canvas component for the cell
			 */
			@Override
			protected Canvas createRecordComponent(ListGridRecord rec, Integer colNum) {
				String fieldName = getFieldName(colNum);
				if ("report".equals(fieldName)) {
					IButton iButton = new IButton("Report", e -> report = new ReportLite(rec.getAttribute(SRC)));
					iButton.setAutoFit(true);
					return iButton;
				}
				return super.createRecordComponent(rec, colNum);
			}
		}

		/**
		 * Builds the SDR list grid with its fields and context menu.
		 */
		SDRList() {
			setHeight("70%");
			setCanEdit(true);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.MULTIPLE);
			setCanSort(false);
			setAutoFitExpandField(RESULT);
			setAutoFitFieldsFillViewport(true);
			setAutoFetchData(true);
			setCanExpandRecords(true);
			setContextMenu(buildSdrContextMenu());
			setDataSource(DSBatchDat2DirSDR.getInstance());
			setFields(
					buildSdrSrcField(),
					buildSdrDstField(),
					buildSdrResultField(),
					buildSdrSelectedField());
		}

		/** Builds the SDR "src" column field. */
		private ListGridField buildSdrSrcField() {
			ListGridField field = new ListGridField(SRC, Client.getSession().getMsg("BatchTableModel.SrcDats"));
			field.setCanEdit(false);
			field.setWidth("35%");
			return field;
		}

		/** Builds the SDR "dst" column field. */
		private ListGridField buildSdrDstField() {
			ListGridField field = new ListGridField(DST, Client.getSession().getMsg("BatchTableModel.DstDirs"));
			field.setCanEdit(false);
			return field;
		}

		/** Builds the SDR "result" column field. */
		private ListGridField buildSdrResultField() {
			ListGridField field = new ListGridField(RESULT, Client.getSession().getMsg("BatchTableModel.Result"));
			field.setCanEdit(false);
			field.setWidth("35%");
			return field;
		}

		/** Builds the SDR "selected" column field. */
		private ListGridField buildSdrSelectedField() {
			ListGridField field = new ListGridField(SELECTED);
			field.setWidth(20);
			field.setAlign(Alignment.CENTER);
			return field;
		}

		/**
		 * Returns the expansion component for the given SDR row.
		 *
		 * @param rec
		 *            the parent SDR record
		 * @return the expansion grid component
		 */
		@Override
		protected Canvas getExpansionComponent(ListGridRecord rec) {
			return new SDRExpList(rec);
		}

		/** Builds the SDR grid context menu. */
		private Menu buildSdrContextMenu() {
			Menu contextMenu = new Menu();
			contextMenu.addItem(buildAddOrUpdateDatMenuItem());
			contextMenu.addItem(buildSetDestMenuItem());
			contextMenu.addItem(buildDelDatMenuItem());
			contextMenu.addItem(buildPresetsMenuItem());
			return contextMenu;
		}

		/** Builds the "add/update DAT" menu item. */
		private MenuItem buildAddOrUpdateDatMenuItem() {
			MenuItem item = new MenuItem();
			item.setDynamicTitleFunction((target, menu, menuItem) -> Client.getSession().getMsg(
					sdr.getSelectedRecords().length == 1 ? "MainFrame.UpdDat" : "MainFrame.AddDat"));
			item.addClickHandler(e -> doAddOrUpdateDat());
			return item;
		}

		/** Builds the "set destination" menu item, enabled only on single selection. */
		private MenuItem buildSetDestMenuItem() {
			MenuItem item = new MenuItem();
			item.setTitle("Set Destination");
			item.setEnableIfCondition((target, menu, menuItem) -> sdr.getSelectedRecords().length == 1);
			item.addClickHandler(e -> doSetDest());
			return item;
		}

		/** Builds the "delete DAT" menu item, enabled only on single selection. */
		private MenuItem buildDelDatMenuItem() {
			MenuItem item = new MenuItem();
			item.setTitle(Client.getSession().getMsg("MainFrame.DelDat"));
			item.setEnableIfCondition((target, menu, menuItem) -> sdr.getSelectedRecords().length == 1);
			item.addClickHandler(e -> sdr.removeSelectedData());
			return item;
		}

		/** Builds the "presets" menu item hosting the TZIP / DIR / custom submenus. */
		private MenuItem buildPresetsMenuItem() {
			MenuItem itemPresets = new MenuItem();
			itemPresets.setTitle(Client.getSession().getMsg("MainFrame.Presets"));
			itemPresets.setEnableIfCondition((target, menu, menuItem) -> sdr.anySelected());

			Menu submenuPresets = new Menu();
			submenuPresets.addItem(buildDir2DatMenuItem());
			submenuPresets.addItem(buildCustomMenuItem());
			itemPresets.setSubmenu(submenuPresets);
			return itemPresets;
		}

		/** Builds the "Dir2Dat" submenu item with TZIP and DIR settings presets. */
		private MenuItem buildDir2DatMenuItem() {
			MenuItem itemDir2Dat = new MenuItem();
			itemDir2Dat.setTitle(Client.getSession().getMsg("MainFrame.Dir2DatMenu"));

			Menu submenuDir2Dat = new Menu();
			MenuItem itemSettingsTZIP = new MenuItem();
			itemSettingsTZIP.setTitle(Client.getSession().getMsg("MainFrame.TZIP"));
			itemSettingsTZIP.addClickHandler(e -> settingsTZIP());
			submenuDir2Dat.addItem(itemSettingsTZIP);

			MenuItem itemSettingsDIR = new MenuItem();
			itemSettingsDIR.setTitle(Client.getSession().getMsg("MainFrame.DIR"));
			itemSettingsDIR.addClickHandler(e -> settingsDIR());
			submenuDir2Dat.addItem(itemSettingsDIR);

			itemDir2Dat.setSubmenu(submenuDir2Dat);
			return itemDir2Dat;
		}

		/** Builds the "custom" settings menu item. */
		private MenuItem buildCustomMenuItem() {
			MenuItem item = new MenuItem();
			item.setTitle(Client.getSession().getMsg("BatchToolsDirUpd8rPanel.mntmCustom.text"));
			item.addClickHandler(e -> settingsCustom());
			return item;
		}

		/**
		 * Opens a remote file chooser to add or update the selected DAT source.
		 *
		 * @return the remote file chooser
		 */
		private RemoteFileChooser doAddOrUpdateDat() {
			return new RemoteFileChooser("addDat", Client.getSession().getSetting("dir.addDat", null), pi -> {
				Record rec = sdr.getSelectedRecord();
				if (rec != null) {
					updDataRecursive(sdr, pi, sdr.getRecordIndex(rec), 0, SRC,
							() -> addDataRecursive(sdr, pi, 0, SRC));
				} else {
					addDataRecursive(sdr, pi, 0, SRC);
				}
			});
		}

		/**
		 * Opens a remote file chooser to set the destination directory of the
		 * selected SDR row.
		 *
		 * @return the remote file chooser
		 */
		private RemoteFileChooser doSetDest() {
			return new RemoteFileChooser("updDat", Client.getSession().getSetting("dir.updDat", null),
					pi -> updDataRecursive(sdr, pi, sdr.getRecordIndex(sdr.getSelectedRecord()), 0, DST, null));
		}

		/** Applies the TZIP preset to each selected SDR row. */
		private void settingsTZIP() {
			Q_Profile.SetProperty settings = Q_Profile.SetProperty.instantiate();
			settings.setProperty("need_sha1_or_md5", false); //$NON-NLS-1$
			settings.setProperty("use_parallelism", true); //$NON-NLS-1$
			settings.setProperty("create_mode", true); //$NON-NLS-1$
			settings.setProperty("createfull_mode", false); //$NON-NLS-1$
			settings.setProperty("ignore_unneeded_containers", false); //$NON-NLS-1$
			settings.setProperty("ignore_unneeded_entries", false); //$NON-NLS-1$
			settings.setProperty("ignore_unknown_containers", true); //$NON-NLS-1$
			settings.setProperty("implicit_merge", false); //$NON-NLS-1$
			settings.setProperty("ignore_merge_name_roms", false); //$NON-NLS-1$
			settings.setProperty("ignore_merge_name_disks", false); //$NON-NLS-1$
			settings.setProperty("exclude_games", false); //$NON-NLS-1$
			settings.setProperty("exclude_machines", false); //$NON-NLS-1$
			settings.setProperty("backup", true); //$NON-NLS-1$
			settings.setProperty("format", "TZIP"); //$NON-NLS-1$
			settings.setProperty("merge_mode", "NOMERGE"); //$NON-NLS-1$
			settings.setProperty("archives_and_chd_as_roms", false); //$NON-NLS-1$
			for (ListGridRecord rec : sdr.getSelectedRecords())
				Client.sendMsg(JsonUtils.stringify(settings.setProfile(rec.getAttribute(SRC))));
		}

		/** Applies the DIR preset to each selected SDR row. */
		private void settingsDIR() {
			Q_Profile.SetProperty settings = Q_Profile.SetProperty.instantiate();
			settings.setProperty("need_sha1_or_md5", false); //$NON-NLS-1$
			settings.setProperty("use_parallelism", true); //$NON-NLS-1$
			settings.setProperty("create_mode", true); //$NON-NLS-1$
			settings.setProperty("createfull_mode", false); //$NON-NLS-1$
			settings.setProperty("ignore_unneeded_containers", false); //$NON-NLS-1$
			settings.setProperty("ignore_unneeded_entries", false); //$NON-NLS-1$
			settings.setProperty("ignore_unknown_containers", true); //$NON-NLS-1$
			settings.setProperty("implicit_merge", false); //$NON-NLS-1$
			settings.setProperty("ignore_merge_name_roms", false); //$NON-NLS-1$
			settings.setProperty("ignore_merge_name_disks", false); //$NON-NLS-1$
			settings.setProperty("exclude_games", false); //$NON-NLS-1$
			settings.setProperty("exclude_machines", false); //$NON-NLS-1$
			settings.setProperty("backup", true); //$NON-NLS-1$
			settings.setProperty("format", "DIR"); //$NON-NLS-1$
			settings.setProperty("merge_mode", "NOMERGE"); //$NON-NLS-1$
			settings.setProperty("archives_and_chd_as_roms", true); //$NON-NLS-1$
			for (ListGridRecord rec : sdr.getSelectedRecords())
				Client.sendMsg(JsonUtils.stringify(settings.setProfile(rec.getAttribute(SRC))));
		}

		/** Opens the custom scanner settings window for the selected SDR rows. */
		private void settingsCustom() {
			final List<String> srcs = Stream.of(sdr.getSelectedRecords())
					.map(n -> n.getAttribute(SRC)).toList();
			Q_Dat2Dir.Settings.instantiate().setSrcs(srcs).send();
		}
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
	private static void addDataRecursive(ListGrid grid, PathInfo[] pi, int i, String attr) {
		if (i < pi.length) {
			Record rec = new Record(Collections.singletonMap(attr, pi[i].path));
			grid.addData(rec, (dsResponse, data, dsRequest) -> addDataRecursive(grid, pi, i + 1, attr));
		}
	}

	/**
	 * Recursively updates records in the given grid with the selected paths,
	 * optionally falling back to adding new records when the grid runs out of rows.
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
	 * @param fallbackAdd
	 *            optional runnable invoked when more paths remain than grid rows,
	 *            or {@code null} for no fallback
	 */
	private static void updDataRecursive(ListGrid grid, PathInfo[] pi, int start, int i, String attr,
			Runnable fallbackAdd) {
		if (i < pi.length) {
			if (start + i < grid.getTotalRows()) {
				Record rec = grid.getRecord(start + i);
				rec.setAttribute(attr, pi[i].path);
				grid.updateData(rec,
						(dsResponse, data, dsRequest) -> updDataRecursive(grid, pi, start, i + 1, attr, fallbackAdd));
			} else if (fallbackAdd != null) {
				fallbackAdd.run();
			}
		}
	}

	/**
	 * Modal window hosting a {@link ScannerSettingsPanel} for editing custom
	 * scanner settings of one or more source DATs.
	 */
	class Settings extends Window //NOSONAR
    {
		/** The embedded scanner settings panel. */
		ScannerSettingsPanel settingsPanel;

		/**
		 * Builds the settings window for the given source DATs.
		 *
		 * @param settings
		 *            the current settings as a JSO
		 * @param srcs
		 *            the source DAT paths the settings apply to
		 */
		Settings(EnhJSO settings, JsArrayString srcs) {
			setAnimateMinimize(true);
			setIsModal(true);
			setShowModalMask(true);
			setCanDragReposition(true);
			setCanDragResize(true);
			setShowHeaderIcon(true);
			setShowMaximizeButton(true);
			final var headerIconDefaults = new HashMap<String, Object>();
			headerIconDefaults.put("width", 16);
			headerIconDefaults.put("height", 16);
			headerIconDefaults.put("src", "rom.png");
			setHeaderIconDefaults(headerIconDefaults);
			setShowHeaderIcon(true);
			addCloseClickHandler(event -> Settings.this.markForDestroy());
			addItem(buildMainLayout(settings));
			addItem(buildSettingsBottomLayout(srcs));
			setAutoCenter(true);
			setWidth("50%");
			setHeight("50%");
			show();
			redraw();
		}

		/**
		 * Builds the main layout wrapping the scanner settings panel.
		 *
		 * @param settings
		 *            the current settings as a JSO
		 * @return the configured horizontal layout
		 */
		private HLayout buildMainLayout(EnhJSO settings) {
			HLayout hLayout = new HLayout();
			hLayout.setHeight100();
			hLayout.addMember(new LayoutSpacer("5%", "*"));
			VLayout component = new VLayout();
			component.setHeight100();
			component.addMember(new LayoutSpacer("100%", "*"));
			settingsPanel = new ScannerSettingsPanel(settings);
			component.addMember(settingsPanel);
			component.addMember(new LayoutSpacer("100%", "*"));
			hLayout.addMember(component);
			hLayout.addMember(new LayoutSpacer("5%", "*"));
			return hLayout;
		}

		/**
		 * Builds the bottom layout with OK and Cancel buttons.
		 *
		 * @param srcs
		 *            the source DAT paths the settings apply to
		 * @return the configured horizontal layout
		 */
		private HLayout buildSettingsBottomLayout(JsArrayString srcs) {
			HLayout layout = new HLayout();
			layout.addMember(new LayoutSpacer("*", 20));
			layout.addMember(buildOKButton(srcs));
			layout.addMember(new IButton("Cancel", e -> Settings.this.markForDestroy()));
			return layout;
		}

		/**
		 * Removes this settings window from the client's child window list before
		 * delegating destruction to the super implementation.
		 */
		@Override
		protected void onDestroy() {
			Client.getChildWindows().remove(this);
			super.onDestroy();
		}

		/**
		 * Builds the OK button which applies the edited settings to each source
		 * DAT and destroys the window.
		 *
		 * @param srcs
		 *            the source DAT paths the settings apply to
		 * @return the configured button
		 */
		private IButton buildOKButton(JsArrayString srcs) {
			return new IButton("OK", e -> {
				Q_Profile.SetProperty props = Q_Profile.SetProperty.instantiate();
				Map<String, Object> values = settingsPanel.getFilteredValues();
				values.forEach((k, v) -> {
					if (v != null)
						props.setProperty(k, v);
				});
				for (int i = 0; i < srcs.length(); i++) {
					SC.logWarn(i + ":" + srcs.get(i));
					props.setProfile(srcs.get(i)).send();
				}
				Settings.this.markForDestroy();
			});
		}
	}

	/**
	 * Opens the custom scanner settings window for the given source DATs.
	 *
	 * @param settings
	 *            the current settings as a JSO
	 * @param srcs
	 *            the source DAT paths the settings apply to
	 */
	public void showSettings(EnhJSO settings, JsArrayString srcs) {
		Client.getChildWindows().add(new Settings(settings, srcs));
	}

	/** Builds the source grid context menu. */
	private Menu buildSrcContextMenu() {
		Menu menu = new Menu();
		menu.addItem(buildAddSrcDirMenuItem());
		menu.addItem(buildDelSrcDirMenuItem());
		return menu;
	}

	/** Builds the "add source directory" menu item. */
	private MenuItem buildAddSrcDirMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.AddSrcDir"));
		item.addClickHandler(e -> doAddDatSrc());
		return item;
	}

	/** Builds the "delete source directory" menu item, enabled only on selection. */
	private MenuItem buildDelSrcDirMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.DelSrcDir"));
		item.setEnableIfCondition((target, m, menuItem) -> srcGrid.getSelectedRecords().length > 0);
		item.addClickHandler(e -> srcGrid.removeSelectedData());
		return item;
	}

	/**
	 * Opens a remote file chooser to add one or more source directories.
	 *
	 * @return the remote file chooser
	 */
	private RemoteFileChooser doAddDatSrc() {
		return new RemoteFileChooser("addDatSrc", null, pi -> {
			for (PathInfo p : pi) {
				Record rec = new Record();
				rec.setAttribute("name", p.path);
				srcGrid.addData(rec);
			}
		});
	}
}
