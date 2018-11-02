package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.A_CatVer;
import jrm.webui.client.protocol.A_NPlayers;
import jrm.webui.client.protocol.A_Profile;
import jrm.webui.client.protocol.A_Progress;
import jrm.webui.client.protocol.A_Report;
import jrm.webui.client.utils.EnhJSO;

public class MainWindow extends Window
{
	TabSet mainPane;
	ProfilePanel profilePanel;
	ScannerPanel scannerPanel;
	Label lblProfileinfo;
	private Progress progress = null;
	
	public MainWindow()
	{
		super();
		setTitle("JRomManager Web Client");
		setWidth(1000);
		setHeight(600);
		setAnimateMinimize(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowFooter(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		Map<String,Object> headerIconDefaults = new HashMap<>();
		headerIconDefaults.put("width", 16);
		headerIconDefaults.put("height", 16);
		headerIconDefaults.put("src", "rom.png");
		setHeaderIconDefaults(headerIconDefaults);
		setShowHeaderIcon(true);
		addCloseClickHandler(new CloseClickHandler()
		{
			@Override
			public void onCloseClick(CloseClickEvent event)
			{
				close();
			}
		});
		addItem(mainPane = new TabSet() {{
			setPaneMargin(0);
			setTabBarControls(
				TabBarControls.TAB_SCROLLER,
				TabBarControls.TAB_PICKER
			);
			addTab(new Tab() {{
				setIcon("icons/script.png");
				setTitle(Client.session.getMsg("MainFrame.Profiles"));
				setPane(profilePanel = new ProfilePanel());
			}});
			addTab(new Tab() {{
				setIcon("icons/drive_magnify.png");
				setTitle(Client.session.getMsg("MainFrame.Scanner"));
				setDisabled(true);
				setPane(scannerPanel = new ScannerPanel());
			}});
			addTab(new Tab() {{
				setIcon("icons/drive_go.png");
				setTitle(Client.session.getMsg("MainFrame.Dir2Dat"));
			}});
			addTab(new Tab() {{
				setIcon("icons/application_osx_terminal.png");
				setTitle(Client.session.getMsg("MainFrame.BatchTools"));
			}});
			addTab(new Tab() {{
				setIcon("icons/cog.png");
				setTitle(Client.session.getMsg("MainFrame.Settings"));
			}});
		}});
		setFooterControls(lblProfileinfo = new Label() {{setWidth100();}});
		centerInPage();
		show();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	public void update(A_Profile.Loaded params)
	{
		lblProfileinfo.setContents(params.getSuccess()?params.getName():null);
		scannerPanel.btnScan.setDisabled(!params.getSuccess());
		scannerPanel.btnFix.setDisabled(true);
		if(params.getSuccess())
		{
			mainPane.enableTab(1);
			mainPane.selectTab(1);
			EnhJSO settings = params.getSettings();
			scannerPanel.scannerDirPanel.getItem("tfRomsDest").setValue(settings.get("roms_dest_dir"));
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfRomsDest", "roms_dest_dir", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfDisksDestCbx", "disks_dest_dir_enabled", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfDisksDest", "disks_dest_dir", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfSWDestCbx", "swroms_dest_dir_enabled", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfSWDest", "swroms_dest_dir", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfSWDisksDestCbx", "swdisks_dest_dir_enabled", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfSWDisksDest", "swdisks_dest_dir", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfSamplesDestCbx", "samples_dest_dir_enabled", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("tfSamplesDest", "samples_dest_dir", settings);
			scannerPanel.scannerDirPanel.initPropertyItemValue("listSrcDir", "src_dir", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxNeedSHA1", "need_sha1_or_md5", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxUseParallelism", "use_parallelism", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxCreateMissingSets", "create_mode", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxCreateOnlyComplete", "createfull_mode", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxIgnoreUnneededContainers", "ignore_unneeded_containers", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxIgnoreUnneededEntries", "ignore_unneeded_entries", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxIgnoreUnknownContainers", "ignore_unknown_containers", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxUseImplicitMerge", "implicit_merge", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxIgnoreMergeNameRoms", "ignore_merge_name_roms", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxIgnoreMergeNameDisks", "ignore_merge_name_disks", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxExcludeGames", "exclude_games", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxExcludeMachines", "exclude_machines", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("chckbxBackup", "backup", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("cbCompression", "format", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("cbbxMergeMode", "merge_mode", settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValue("cbHashCollision", "hash_collision_mode", settings);
			scannerPanel.scannerFiltersPanel.systems.setData(Record.convertToRecordArray(params.getSystems()));
		}
		else
		{
			if(mainPane.getSelectedTabNumber()==1)
				mainPane.selectTab(0);
			mainPane.disableTab(1);
		}
		profilePanel.refreshListGrid();
	}
	
	public void update(A_Progress params)
	{
//		if(progress==null || progress.getDestroyed() || progress.getDestroying())
			progress = new Progress();
/*		else
		{
			progress.clearInfos();
			progress.show();
		}*/
	}

	
	public void update(A_Progress.Close params)
	{
		progress.close();
	}
	
	public void update(A_Progress.SetInfos params)
	{
		progress.setInfos(params.getThreadCnt(), params.getMultipleSubInfos());		
	}
	
	public void update(A_Progress.ClearInfos params)
	{
		progress.clearInfos();		
	}
	
	public void update(A_Progress.SetProgress params)
	{
		progress.setProgress(params.getOffset(), params.getMsg(), params.getVal(), params.getMax(), params.getSubMsg());
	}
	
	public void update(A_Progress.SetProgress2 params)
	{
		progress.setProgress2(params.getMsg(), params.getVal(), params.getMax());
	}
	
	public void update(A_CatVer.Loaded params)
	{
		scannerPanel.scannerAdvFiltersPanel.catver_path.setValue(params.getPath());
		scannerPanel.scannerAdvFiltersPanel.catver_tree.invalidateCache();
	}
	
	public void update(A_NPlayers.Loaded params)
	{
		scannerPanel.scannerAdvFiltersPanel.nplayers_path.setValue(params.getPath());
		scannerPanel.scannerAdvFiltersPanel.nplayers_list.invalidateCache();
	}

	public void update(A_Profile.Scanned params)
	{
		if(params.getSuccess())
		{
			scannerPanel.btnFix.setDisabled(params.getActions()==null || params.getActions()==0);
			if(Client.mainwindow.scannerPanel.profileViewer!=null && Client.childWindows.contains(Client.mainwindow.scannerPanel.profileViewer))
			{
				Client.mainwindow.scannerPanel.profileViewer.anywareListList.refreshData();
				Client.mainwindow.scannerPanel.profileViewer.anywareList.refreshData();
				Client.mainwindow.scannerPanel.profileViewer.anyware.refreshData();
			}
		}
	}

	public void update(A_Report.ApplyFilter params)
	{
		params.forEachParams((k, v) -> scannerPanel.reportViewer.applyFilter(k,v));
		scannerPanel.reportViewer.reload();
	}

}
