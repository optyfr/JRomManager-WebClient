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

public class BatchDirUpd8rPanel extends VLayout //NOSONAR
{

	private static final String SRC = "src";
	private static final String DST = "dst";
	private static final String RESULT = "result";
	private static final String SELECTED = "selected";
	private static final String ICON_BULLET_GO = "icons/bullet_go.png";

	private final ListGrid srcGrid;
	final ListGrid sdr;
	ReportLite report;

	public BatchDirUpd8rPanel() {
		setHeight100();
		srcGrid = buildSrcList();
		addMember(srcGrid);
		sdr = buildSdrList();
		addMember(sdr);
		addMember(buildBottomLayout());
	}

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

	private IButton buildStartButton() {
		IButton start = new IButton(Client.getSession().getMsg("MainFrame.btnStart.text"), e -> {
			Client.getMainWindow().mainPane.disableTab(1);
			Client.sendMsg(JsonUtils.stringify(Q_Dat2Dir.Start.instantiate()));
		});
		start.setIcon(ICON_BULLET_GO);
		return start;
	}

	private ListGrid buildSdrList() {
		return new SDRList();
	}

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

	final class SDRList extends ListGrid //NOSONAR
    {
		final class SDRExpList extends ListGrid //NOSONAR
        {
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

			private ListGridField buildExpSrcField() {
				ListGridField field = new ListGridField(SRC);
				field.setAlign(Alignment.RIGHT);
				field.setCanEdit(false);
				return field;
			}

			private ListGridField buildExpHaveField() {
				ListGridField field = new ListGridField("have");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			private ListGridField buildExpCreateField() {
				ListGridField field = new ListGridField("create");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			private ListGridField buildExpFixField() {
				ListGridField field = new ListGridField("fix");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			private ListGridField buildExpMissField() {
				ListGridField field = new ListGridField("miss");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			private ListGridField buildExpTotalField() {
				ListGridField field = new ListGridField("total");
				field.setWidth(70);
				field.setCanEdit(false);
				return field;
			}

			private ListGridField buildExpReportField() {
				ListGridField field = new ListGridField("report");
				field.setAlign(Alignment.CENTER);
				field.setDefaultWidth(70);
				field.setAutoFitWidth(true);
				field.setCanEdit(false);
				return field;
			}

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

		private ListGridField buildSdrSrcField() {
			ListGridField field = new ListGridField(SRC, Client.getSession().getMsg("BatchTableModel.SrcDats"));
			field.setCanEdit(false);
			field.setWidth("35%");
			return field;
		}

		private ListGridField buildSdrDstField() {
			ListGridField field = new ListGridField(DST, Client.getSession().getMsg("BatchTableModel.DstDirs"));
			field.setCanEdit(false);
			return field;
		}

		private ListGridField buildSdrResultField() {
			ListGridField field = new ListGridField(RESULT, Client.getSession().getMsg("BatchTableModel.Result"));
			field.setCanEdit(false);
			field.setWidth("35%");
			return field;
		}

		private ListGridField buildSdrSelectedField() {
			ListGridField field = new ListGridField(SELECTED);
			field.setWidth(20);
			field.setAlign(Alignment.CENTER);
			return field;
		}

		@Override
		protected Canvas getExpansionComponent(ListGridRecord rec) {
			return new SDRExpList(rec);
		}

		private Menu buildSdrContextMenu() {
			Menu contextMenu = new Menu();
			contextMenu.addItem(buildAddOrUpdateDatMenuItem());
			contextMenu.addItem(buildSetDestMenuItem());
			contextMenu.addItem(buildDelDatMenuItem());
			contextMenu.addItem(buildPresetsMenuItem());
			return contextMenu;
		}

		private MenuItem buildAddOrUpdateDatMenuItem() {
			MenuItem item = new MenuItem();
			item.setDynamicTitleFunction((target, menu, menuItem) -> Client.getSession().getMsg(
					sdr.getSelectedRecords().length == 1 ? "MainFrame.UpdDat" : "MainFrame.AddDat"));
			item.addClickHandler(e -> doAddOrUpdateDat());
			return item;
		}

		private MenuItem buildSetDestMenuItem() {
			MenuItem item = new MenuItem();
			item.setTitle("Set Destination");
			item.setEnableIfCondition((target, menu, menuItem) -> sdr.getSelectedRecords().length == 1);
			item.addClickHandler(e -> doSetDest());
			return item;
		}

		private MenuItem buildDelDatMenuItem() {
			MenuItem item = new MenuItem();
			item.setTitle(Client.getSession().getMsg("MainFrame.DelDat"));
			item.setEnableIfCondition((target, menu, menuItem) -> sdr.getSelectedRecords().length == 1);
			item.addClickHandler(e -> sdr.removeSelectedData());
			return item;
		}

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

		private MenuItem buildCustomMenuItem() {
			MenuItem item = new MenuItem();
			item.setTitle(Client.getSession().getMsg("BatchToolsDirUpd8rPanel.mntmCustom.text"));
			item.addClickHandler(e -> settingsCustom());
			return item;
		}

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

		private RemoteFileChooser doSetDest() {
			return new RemoteFileChooser("updDat", Client.getSession().getSetting("dir.updDat", null),
					pi -> updDataRecursive(sdr, pi, sdr.getRecordIndex(sdr.getSelectedRecord()), 0, DST, null));
		}

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

		private void settingsCustom() {
			final List<String> srcs = Stream.of(sdr.getSelectedRecords())
					.map(n -> n.getAttribute(SRC)).toList();
			Q_Dat2Dir.Settings.instantiate().setSrcs(srcs).send();
		}
	}

	private static void addDataRecursive(ListGrid grid, PathInfo[] pi, int i, String attr) {
		if (i < pi.length) {
			Record rec = new Record(Collections.singletonMap(attr, pi[i].path));
			grid.addData(rec, (dsResponse, data, dsRequest) -> addDataRecursive(grid, pi, i + 1, attr));
		}
	}

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

	class Settings extends Window //NOSONAR
    {
		ScannerSettingsPanel settingsPanel;

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

		private HLayout buildSettingsBottomLayout(JsArrayString srcs) {
			HLayout layout = new HLayout();
			layout.addMember(new LayoutSpacer("*", 20));
			layout.addMember(buildOKButton(srcs));
			layout.addMember(new IButton("Cancel", e -> Settings.this.markForDestroy()));
			return layout;
		}

		@Override
		protected void onDestroy() {
			Client.getChildWindows().remove(this);
			super.onDestroy();
		}

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

	public void showSettings(EnhJSO settings, JsArrayString srcs) {
		Client.getChildWindows().add(new Settings(settings, srcs));
	}

	private Menu buildSrcContextMenu() {
		Menu menu = new Menu();
		menu.addItem(buildAddSrcDirMenuItem());
		menu.addItem(buildDelSrcDirMenuItem());
		return menu;
	}

	private MenuItem buildAddSrcDirMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.AddSrcDir"));
		item.addClickHandler(e -> doAddDatSrc());
		return item;
	}

	private MenuItem buildDelSrcDirMenuItem() {
		MenuItem item = new MenuItem();
		item.setTitle(Client.getSession().getMsg("MainFrame.DelSrcDir"));
		item.setEnableIfCondition((target, m, menuItem) -> srcGrid.getSelectedRecords().length > 0);
		item.addClickHandler(e -> srcGrid.removeSelectedData());
		return item;
	}

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
