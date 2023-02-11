package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

public class BatchDirUpd8rPanel extends VLayout
{
	private final ListGrid src;
	final ListGrid sdr;
	ReportLite report;
	
	public BatchDirUpd8rPanel()
	{
		setHeight100();
		src = buildSrcList();
		addMember(src);
		sdr = buildSdrList();
		addMember(sdr);
		addMember(buildBottomLayout());
	}

	private HLayout buildBottomLayout()
	{
		HLayout hLayout = new HLayout();
		hLayout.setHeight(20);
		hLayout.addMember(new LayoutSpacer("*",20));
		DynamicForm form = new DynamicForm();
		form.setColWidths(100,50);
		form.setWrapItemTitles(false);
		CheckboxItem cbxDryRun = new CheckboxItem("dry_run", Client.getSession().getMsg("MainFrame.cbBatchToolsDat2DirDryRun.text"));
		cbxDryRun.setLabelAsTitle(true);
		cbxDryRun.setDefaultValue(Client.getSession().getSettingAsBoolean("dat2dir.dry_run",true));
		cbxDryRun.setShowLabel(false);
		cbxDryRun.addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("dat2dir.dry_run", (Boolean)e.getValue()))));
		form.setItems(cbxDryRun);
		hLayout.addMember(form);
		IButton start = new IButton(Client.getSession().getMsg("MainFrame.btnStart.text"), e->{
			Client.getMainWindow().mainPane.disableTab(1);
			Client.sendMsg(JsonUtils.stringify(Q_Dat2Dir.Start.instantiate()));
		});
		start.setIcon("icons/bullet_go.png");
		hLayout.addMember(start);
		return hLayout;
	}

	private ListGrid buildSdrList()
	{
		return new SDRList();
	}

	private ListGrid buildSrcList()
	{
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
	
	final class SDRList extends ListGrid
	{
		final class SDRExpList extends ListGrid
		{
			SDRExpList(ListGridRecord rec)
			{
				setHeight(200);
				setCanEdit(true);
				setCanHover(true);
				setHoverAutoFitWidth(true);
				setHoverAutoFitMaxWidth("50%");
				setSelectionType(SelectionStyle.NONE);
				setCanSort(false);
				setShowRecordComponents(true);
				setShowRecordComponentsByCell(true);
				setAutoFitExpandField("src");
				setAutoFitFieldsFillViewport(true);
				setDataSource(DSBatchDat2DirResult.getInstance(sdr.getDataSource().getID()+".src"));
				ListGridField srcField = new ListGridField("src");
				srcField.setAlign(Alignment.RIGHT);
				srcField.setCanEdit(false);
				ListGridField haveField = new ListGridField("have");
				haveField.setWidth(70);
				haveField.setCanEdit(false);
				ListGridField createField = new ListGridField("create");
				createField.setWidth(70);
				createField.setCanEdit(false);
				ListGridField fixField = new ListGridField("fix");
				fixField.setWidth(70);
				fixField.setCanEdit(false);
				ListGridField missField = new ListGridField("miss");
				missField.setWidth(70);
				missField.setCanEdit(false);
				ListGridField totalField = new ListGridField("total");
				totalField.setWidth(70);
				totalField.setCanEdit(false);
				ListGridField reportField = new ListGridField("report");
				reportField.setAlign(Alignment.CENTER);
				reportField.setDefaultWidth(70);
				reportField.setAutoFitWidth(true);
				reportField.setCanEdit(false);
				setFields(srcField, haveField, createField, fixField, missField, totalField, reportField);
				fetchRelatedData(rec, sdr.getDataSource());
			}

			@Override
			protected Canvas createRecordComponent(ListGridRecord rec, Integer colNum)
			{
				String fieldName = getFieldName(colNum);
				if ("report".equals(fieldName))
				{
					IButton iButton = new IButton("Report", e -> report = new ReportLite(rec.getAttribute("src")));
					iButton.setAutoFit(true);
					return iButton;
				}
				return super.createRecordComponent(rec, colNum);
			}
		}

		SDRList()
		{
			setHeight("70%");
			setCanEdit(true);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.MULTIPLE);
			setCanSort(false);
			setAutoFitExpandField("result");
			setAutoFitFieldsFillViewport(true);
			setAutoFetchData(true);
			setCanExpandRecords(true);
			setContextMenu(buildSdrContextMenu());
			setDataSource(DSBatchDat2DirSDR.getInstance());
			ListGridField srcField = new ListGridField("src",Client.getSession().getMsg("BatchTableModel.SrcDats"));
			srcField.setCanEdit(false);
			srcField.setWidth("35%");
			ListGridField dstField = new ListGridField("dst",Client.getSession().getMsg("BatchTableModel.DstDirs"));
			dstField.setCanEdit(false);
			ListGridField resultField = new ListGridField("result",Client.getSession().getMsg("BatchTableModel.Result"));
			resultField.setCanEdit(false);
			resultField.setWidth("35%");
			ListGridField selectedField = new ListGridField("selected");
			selectedField.setWidth(20);
			selectedField.setAlign(Alignment.CENTER);
			setFields(srcField, dstField, resultField, selectedField);
		}

		@Override
		protected Canvas getExpansionComponent(ListGridRecord rec)
		{
			return new SDRExpList(rec);
		}

		private Menu buildSdrContextMenu()
		{
			Menu contextMenu = new Menu();
			MenuItem itemAddOrUpdateDat = new MenuItem();
			itemAddOrUpdateDat.setDynamicTitleFunction((target, menu, item) -> Client.getSession().getMsg(sdr.getSelectedRecords().length==1?"MainFrame.UpdDat":"MainFrame.AddDat"));
			itemAddOrUpdateDat.addClickHandler(e -> doAddOrUpdateDat());
			contextMenu.addItem(itemAddOrUpdateDat);
			MenuItem itemSetDest = new MenuItem();
			itemSetDest.setTitle("Set Destination");
			itemSetDest.setEnableIfCondition((target, menu, item)->sdr.getSelectedRecords().length==1);
			itemSetDest.addClickHandler(e -> doSetDest());
			contextMenu.addItem(itemSetDest);
			MenuItem itemDelDat = new MenuItem();
			itemDelDat.setTitle(Client.getSession().getMsg("MainFrame.DelDat"));
			itemDelDat.setEnableIfCondition((target, menu, item) -> sdr.getSelectedRecords().length==1);
			itemDelDat.addClickHandler(e -> sdr.removeSelectedData());
			contextMenu.addItem(itemDelDat);
			MenuItem itemPresets = new MenuItem();
			itemPresets.setTitle(Client.getSession().getMsg("MainFrame.Presets"));
			itemPresets.setEnableIfCondition((target, menu, item) -> sdr.anySelected());
			Menu submenuPresets = new Menu();
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
			submenuPresets.addItem(itemDir2Dat);
			MenuItem itemSettingsCustom = new MenuItem();
			itemSettingsCustom.setTitle(Client.getSession().getMsg("BatchToolsDirUpd8rPanel.mntmCustom.text"));
			itemSettingsCustom.addClickHandler(e -> settingsCustom());
			submenuPresets.addItem(itemSettingsCustom);
			itemPresets.setSubmenu(submenuPresets);
			contextMenu.addItem(itemPresets);
			return contextMenu;
		}
		private RemoteFileChooser doAddOrUpdateDat()
		{
			return new RemoteFileChooser("addDat", Client.getSession().getSetting("dir.addDat", null), new RemoteFileChooser.CallBack()
			{
				private void addData(PathInfo[] pi, int i)
				{
					if (i < pi.length)
					{
						Record rec = new Record(Collections.singletonMap("src", pi[i].path));
						sdr.addData(rec, (dsResponse, data, dsRequest) -> addData(pi, i + 1));
					}
				}
				
				private void updData(PathInfo[] pi, int start, int i)
				{
					if (i < pi.length)
					{
						if (start + i < sdr.getTotalRows())
						{
							Record rec = sdr.getRecord(start + i);
							rec.setAttribute("src", pi[i].path);
							sdr.updateData(rec, (dsResponse, data, dsRequest) -> updData(pi, start, i + 1));
						}
						else
							addData(pi, i);
					}
				}
				
				@Override
				public void apply(PathInfo[] pi)
				{
					Record rec = sdr.getSelectedRecord();
					if(rec != null)
						updData(pi, sdr.getRecordIndex(rec), 0);
					else
						addData(pi, 0);
				}
			});
		}

		private RemoteFileChooser doSetDest()
		{
			return new RemoteFileChooser("updDat", Client.getSession().getSetting("dir.updDat", null), new RemoteFileChooser.CallBack()
			{
				private void updData(PathInfo[] pi, int start, int i)
				{
					if (i < pi.length && start + i < sdr.getTotalRows())
					{
						Record rec = sdr.getRecord(start + i);
						rec.setAttribute("dst", pi[i].path);
						sdr.updateData(rec, (dsResponse, data, dsRequest) -> updData(pi, start, i + 1));
					}
				}
				
				@Override
				public void apply(PathInfo[] pi)
				{
					updData(pi, sdr.getRecordIndex(sdr.getSelectedRecord()), 0);
				}
			});
		}

		private void settingsTZIP()
		{
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
			for(ListGridRecord rec : sdr.getSelectedRecords())
				Client.sendMsg(JsonUtils.stringify(settings.setProfile(rec.getAttribute("src"))));
		}

		private void settingsDIR()
		{
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
			for(ListGridRecord rec : sdr.getSelectedRecords())
				Client.sendMsg(JsonUtils.stringify(settings.setProfile(rec.getAttribute("src"))));
		}

		private void settingsCustom()
		{
			final List<String> srcs = Stream.of(sdr.getSelectedRecords()).map(n->n.getAttribute("src")).collect(Collectors.toList());
			Q_Dat2Dir.Settings.instantiate().setSrcs(srcs).send();
		}
	}

	class Settings extends Window
	{
		ScannerSettingsPanel settingsPanel;

		Settings(EnhJSO settings, JsArrayString srcs)
		{
			Client.getChildWindows().add(this);
			setAnimateMinimize(true);
			setIsModal(true);
			setShowModalMask(true);
			setCanDragReposition(true);
			setCanDragResize(true);
			setShowHeaderIcon(true);
			setShowMaximizeButton(true);
			final var headerIconDefaults = new HashMap<String,Object>();
			headerIconDefaults.put("width", 16);
			headerIconDefaults.put("height", 16);
			headerIconDefaults.put("src", "rom.png");
			setHeaderIconDefaults(headerIconDefaults);
			setShowHeaderIcon(true);
			addCloseClickHandler(event->Settings.this.markForDestroy());
			addItem(buildMainLayout(settings));
			addItem(buildBottomLayout(srcs));
			setAutoCenter(true);
			setWidth("50%");
			setHeight("50%");
			show();
			redraw();
		}

		private HLayout buildMainLayout(EnhJSO settings)
		{
			HLayout hLayout = new HLayout();
			hLayout.setHeight100();
			hLayout.addMember(new LayoutSpacer("5%","*"));
			VLayout component = new VLayout();
			component.setHeight100();
			component.addMember(new LayoutSpacer("100%","*"));
			settingsPanel=new ScannerSettingsPanel(settings);
			component.addMember(settingsPanel);
			component.addMember(new LayoutSpacer("100%","*"));
			hLayout.addMember(component);
			hLayout.addMember(new LayoutSpacer("5%","*"));
			return hLayout;
		}

		private HLayout buildBottomLayout(JsArrayString srcs)
		{
			HLayout layout = new HLayout();
			layout.addMember(new LayoutSpacer("*",20));
			layout.addMember(buildOKButton(srcs));
			layout.addMember(new IButton("Cancel", e->Settings.this.markForDestroy()));
			return layout;
		}

		@Override
		protected void onDestroy()
		{
			Client.getChildWindows().remove(this);
			super.onDestroy();
		}

		private IButton buildOKButton(JsArrayString srcs)
		{
			return new IButton("OK", e-> {
				Q_Profile.SetProperty props = Q_Profile.SetProperty.instantiate();
				Map<String, Object> values = settingsPanel.getFilteredValues();
				values.forEach((k, v) -> {
					if (v != null)
						props.setProperty(k, v);
				});
				for(int i = 0; i < srcs.length(); i++)
				{
					SC.logWarn(i+":"+srcs.get(i));
					props.setProfile(srcs.get(i)).send();
				}
				Settings.this.markForDestroy();
			});
		}

		private BatchDirUpd8rPanel getEnclosingInstance()
		{
			return BatchDirUpd8rPanel.this;
		}
		
	}
	
	public void showSettings(EnhJSO settings, JsArrayString srcs)
	{
		new Settings(settings, srcs);
	}


	

	private Menu buildSrcContextMenu()
	{
		Menu menu = new Menu();
		MenuItem addsrcdir = new MenuItem();
		addsrcdir.setTitle(Client.getSession().getMsg("MainFrame.AddSrcDir"));
		addsrcdir.addClickHandler(e -> doAddDatSrc());
		menu.addItem(addsrcdir);
		MenuItem delsrcdir = new MenuItem();
		delsrcdir.setTitle(Client.getSession().getMsg("MainFrame.DelSrcDir"));
		delsrcdir.setEnableIfCondition((target, m, item) ->src.getSelectedRecords().length>0);
		delsrcdir.addClickHandler(e -> src.removeSelectedData());
		menu.addItem(delsrcdir);
		return menu;
	}

	private RemoteFileChooser doAddDatSrc()
	{
		return new RemoteFileChooser("addDatSrc", null, pi -> {
			for(PathInfo p : pi)
			{
				Record rec = new Record();
				rec.setAttribute("name",p.path);
				src.addData(rec);
			}
		});
	}

}
