package jrm.webui.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window.Location;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.A_CatVer;
import jrm.webui.client.protocol.A_Compressor;
import jrm.webui.client.protocol.A_Dat2Dir;
import jrm.webui.client.protocol.A_Global;
import jrm.webui.client.protocol.A_NPlayers;
import jrm.webui.client.protocol.A_Profile;
import jrm.webui.client.protocol.A_Progress;
import jrm.webui.client.protocol.A_Report;
import jrm.webui.client.protocol.A_ReportLite;
import jrm.webui.client.protocol.A_TrntChk;
import jrm.webui.client.utils.EnhJSO;

public class MainWindow extends Window
{
	TabSet mainPane;
	ProfilePanel profilePanel;
	ScannerPanel scannerPanel;
	BatchDirUpd8rPanel batchDirUpd8rPanel;
	BatchTrrntChkPanel batchTrrntChkPanel;
	BatchCompressorPanel batchCompressorPanel;
	Dir2DatPanel dir2datPanel;
	SettingsGenPanel settingsGenPanel;
	SettingsCompressorPanel settingsCompressorPanel;
	SettingsDebugPanel settingsDebugPanel;
	private Progress progress = null;
	
	public MainWindow()
	{
		super();
		ListGrid.setDefaultProperties(new ListGrid() {{
			setEmptyMessage(Client.getSession().getMsg("MainWindow.NoItemsToShow")); //$NON-NLS-1$
			setLoadingDataMessage(Client.getSession().getMsg("MainWindow.LoadingData")); //$NON-NLS-1$
		}});
		setTitle(Client.getSession().getMsg("MainWindow.Title")); //$NON-NLS-1$
		setWidth(1000);
		setHeight(600);
		setAnimateMinimize(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		Map<String,Object> headerIconDefaults = new HashMap<>();
		headerIconDefaults.put("width", 16); //$NON-NLS-1$
		headerIconDefaults.put("height", 16); //$NON-NLS-1$
		headerIconDefaults.put("src", "rom.png"); //$NON-NLS-1$ //$NON-NLS-2$
		setHeaderIconDefaults(headerIconDefaults);
		setShowHeaderIcon(true);
		addCloseClickHandler(new CloseClickHandler()
		{
			@Override
			public void onCloseClick(CloseClickEvent event)
			{
				RPCRequest request = new RPCRequest();
				String logout = Location.getProtocol() + "//logout:logout@" + Location.getHost() + Location.getPath(); //$NON-NLS-1$
				request.setActionURL(logout);
				request.setSendNoQueue(true);
				request.setHttpHeaders(Collections.singletonMap("Authorization", "Basic AAAAAAAAAAAAAAAAAAA=")); //$NON-NLS-1$ //$NON-NLS-2$
				request.setWillHandleError(true);
				RPCManager.sendRequest(request,new RPCCallback()
				{
					@Override
					public void execute(RPCResponse response, Object rawData, RPCRequest request)
					{
						Cookies.removeCookie("JSESSIONID"); //$NON-NLS-1$
						String logout2 = Location.getHref();
						Location.replace(logout);
						Location.replace(logout2);
					}
				});
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
				setIcon("icons/script.png"); //$NON-NLS-1$
				setTitle(Client.getSession().getMsg("MainFrame.Profiles")); //$NON-NLS-1$
				setPane(profilePanel = new ProfilePanel());
			}});
			addTab(new Tab() {{
				setName("scanner");
				setIcon("icons/drive_magnify.png"); //$NON-NLS-1$
				setTitle(Client.getSession().getMsg("MainFrame.Scanner")); //$NON-NLS-1$
				setDisabled(true);
				setPane(scannerPanel = new ScannerPanel());
			}});
			addTab(new Tab() {{
				setIcon("icons/drive_go.png"); //$NON-NLS-1$
				setTitle(Client.getSession().getMsg("MainFrame.Dir2Dat")); //$NON-NLS-1$
				setPane(dir2datPanel = new Dir2DatPanel());
			}});
			addTab(new Tab() {{
				setIcon("icons/application_osx_terminal.png"); //$NON-NLS-1$
				setTitle(Client.getSession().getMsg("MainFrame.BatchTools")); //$NON-NLS-1$
				setPane(new TabSet() {{
					setPaneMargin(0);
					setTabBarControls(
							TabBarControls.TAB_SCROLLER,
							TabBarControls.TAB_PICKER
						);
					addTab(new Tab() {{
						setTitle(Client.getSession().getMsg("MainFrame.panelBatchToolsDat2Dir.title")); //$NON-NLS-1$
						setIcon("icons/application_cascade.png"); //$NON-NLS-1$
						setPane(batchDirUpd8rPanel = new BatchDirUpd8rPanel());
					}});
					addTab(new Tab() {{
						setTitle(Client.getSession().getMsg("MainFrame.panelBatchToolsDir2Torrent.title")); //$NON-NLS-1$
						setIcon("icons/drive_web.png"); //$NON-NLS-1$
						setPane(batchTrrntChkPanel = new BatchTrrntChkPanel());
					}});
					addTab(new Tab() {{
						setTitle(Client.getSession().getMsg("BatchPanel.Compressor")); //$NON-NLS-1$
						setIcon("icons/compress.png"); //$NON-NLS-1$
						setPane(batchCompressorPanel = new BatchCompressorPanel());
					}});
				}});
			}});
			addTab(new Tab() {{
				setIcon("icons/cog.png"); //$NON-NLS-1$
				setTitle(Client.getSession().getMsg("MainFrame.Settings")); //$NON-NLS-1$
				setPane(new TabSet() {{
					setPaneMargin(0);
					setTabBarControls(
							TabBarControls.TAB_SCROLLER,
							TabBarControls.TAB_PICKER
						);
					addTab(new Tab() {{
						setIcon("icons/cog.png"); //$NON-NLS-1$
						setTitle("General"); //$NON-NLS-1$
						setPane(new VLayout() {{
							addMember(new LayoutSpacer("*","*")); //$NON-NLS-1$ //$NON-NLS-2$
							addMember(settingsGenPanel = new SettingsGenPanel());
							addMember(new LayoutSpacer("*","*")); //$NON-NLS-1$ //$NON-NLS-2$
						}});
					}});
					addTab(new Tab() {{
						setIcon("icons/compress.png"); //$NON-NLS-1$
						setTitle(Client.getSession().getMsg("MainFrame.Compressors")); //$NON-NLS-1$
						setPane(settingsCompressorPanel = new SettingsCompressorPanel());
					}});
					addTab(new Tab() {{
						setIcon("icons/bug.png"); //$NON-NLS-1$
						setTitle(Client.getSession().getMsg("MainFrame.Debug")); //$NON-NLS-1$
						setPane(new VLayout() {{
							addMember(new LayoutSpacer("*","*")); //$NON-NLS-1$ //$NON-NLS-2$
							addMember(settingsDebugPanel = new SettingsDebugPanel());
							addMember(new LayoutSpacer("*","*")); //$NON-NLS-1$ //$NON-NLS-2$
						}});
					}});
					if(Client.getSession().isAdmin())
					{
						addTab(new Tab() {{
							setTitle(Client.getSession().getMsg("MainWindow.Admin")); //$NON-NLS-1$
							setIcon("icons/user.png"); //$NON-NLS-1$
							setPane(new SettingsAdminPanel());
						}});
					}
				}});
			}});
		}});
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
		scannerPanel.lblProfileinfo.setContents(params.getSuccess()?params.getName():null);
		scannerPanel.btnScan.setDisabled(!params.getSuccess());
		scannerPanel.btnFix.setDisabled(true);
		if(params.getSuccess())
		{
			mainPane.enableTab("scanner");
			mainPane.selectTab("scanner");
			EnhJSO settings = params.getSettings();
			scannerPanel.scannerDirPanel.initPropertyItemValues(settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValues(settings);
			scannerPanel.scannerFiltersPanel.systems.setData(Record.convertToRecordArray(params.getSystems()));
			JsArrayString jsarrstr = params.getYears();
			ArrayList<String> arrlststr = new ArrayList<>();
			for(int i = 0; i < jsarrstr.length(); i++)
				arrlststr.add(jsarrstr.get(i));
			String[] arrstr = arrlststr.toArray(new String[0]);
			scannerPanel.scannerFiltersPanel.filterForm.getItem("cbYearMin").setValueMap(arrstr); //$NON-NLS-1$
			scannerPanel.scannerFiltersPanel.filterForm.getItem("cbYearMin").setDefaultValue(arrstr[0]); //$NON-NLS-1$
			scannerPanel.scannerFiltersPanel.filterForm.getItem("cbYearMax").setValueMap(arrstr); //$NON-NLS-1$
			scannerPanel.scannerFiltersPanel.filterForm.getItem("cbYearMax").setDefaultValue(arrstr[arrstr.length-1]); //$NON-NLS-1$
			scannerPanel.scannerFiltersPanel.filterForm.initPropertyItemValues(settings);
		}
		else
		{
			if(mainPane.getSelectedTabNumber()==1)
				mainPane.selectTab(0);
			mainPane.disableTab("scanner");
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
		if(params.hasErrors())
			SC.warn(params.getErrors().stream().map(str->"<pre>"+str+"</pre>").collect(Collectors.joining()));
	}
	
	public void update(A_Progress.SetInfos params)
	{
		progress.setInfos(params.getThreadCnt(), params.getMultipleSubInfos());		
	}
	
	public void update(A_Progress.CanCancel params)
	{
		progress.canCancel(params.canCancel());		
	}
	
	public void update(A_Progress.ClearInfos params)
	{
		progress.clearInfos();		
	}
	
	public void update(A_Progress.SetFullProgress params)
	{
		progress.setFullProgress(params.getParams());
	}
	
	public void update(A_CatVer.Loaded params)
	{
		scannerPanel.scannerAdvFiltersPanel.catver_path.setValue(params.getPath());
		scannerPanel.scannerAdvFiltersPanel.catver_tree.enableEvents = false;
		scannerPanel.scannerAdvFiltersPanel.catver_tree.invalidateCache();
	}
	
	public void update(A_NPlayers.Loaded params)
	{
		scannerPanel.scannerAdvFiltersPanel.nplayers_path.setValue(params.getPath());
		scannerPanel.scannerAdvFiltersPanel.nplayers_list.enableEvents = false;
		scannerPanel.scannerAdvFiltersPanel.nplayers_list.invalidateCache();
	}

	public void update(A_Profile.Scanned params)
	{
		if(params.getSuccess())
		{
			scannerPanel.btnFix.setDisabled(params.getActions()==null || params.getActions()==0);
			if(params.hasReport())
			{
				if(scannerPanel.reportViewer==null || !Client.getChildWindows().contains(scannerPanel.reportViewer))
					scannerPanel.reportViewer = new ReportViewer();
				else if(scannerPanel.reportViewer.isVisible())
					scannerPanel.reportViewer.bringToFront();
				else
					scannerPanel.reportViewer.show();
				
			}
			if(Client.getMainWindow().scannerPanel.profileViewer!=null && Client.getChildWindows().contains(Client.getMainWindow().scannerPanel.profileViewer))
			{
				Client.getMainWindow().scannerPanel.profileViewer.anywareListList.refreshData();
				Client.getMainWindow().scannerPanel.profileViewer.anywareList.refreshData();
				Client.getMainWindow().scannerPanel.profileViewer.anyware.refreshData();
			}
		}
	}

	public void update(A_Profile.Fixed params)
	{
		if(params.getSuccess())
		{
			scannerPanel.btnFix.setDisabled(params.getActions()==null || params.getActions()==0);
			if(Client.getMainWindow().scannerPanel.profileViewer!=null && Client.getChildWindows().contains(Client.getMainWindow().scannerPanel.profileViewer))
			{
				Client.getMainWindow().scannerPanel.profileViewer.anywareListList.refreshData();
				Client.getMainWindow().scannerPanel.profileViewer.anywareList.refreshData();
				Client.getMainWindow().scannerPanel.profileViewer.anyware.refreshData();
			}
		}
	}

	public void update(A_Report.ApplyFilter params)
	{
		params.forEachParams((k, v) -> scannerPanel.reportViewer.applyFilter(k,v));
		scannerPanel.reportViewer.reload();
	}


	public void update(A_ReportLite.ApplyFilter params)
	{
		params.forEachParams((k, v) -> batchDirUpd8rPanel.report.applyFilter(k,v));
		batchDirUpd8rPanel.report.reload();
	}

	public void update(A_Dat2Dir.ClearResults params)
	{
		RecordList list = batchDirUpd8rPanel.sdr.getResultSet().getAllCachedRows();
		for(int i = 0; i < list.getLength(); i++)
			batchDirUpd8rPanel.sdr.setEditValue(i, 3, ""); //$NON-NLS-1$
	}

	public void update(A_Dat2Dir.UpdateResult params)
	{
		final int row = params.getRow();
		final String result = params.getResult();
		batchDirUpd8rPanel.sdr.setEditValue(row, 3, result);
	}

	public void update(A_Dat2Dir.End params)
	{
		mainPane.disableTab("scanner");
		batchDirUpd8rPanel.sdr.cancelEditing();
		batchDirUpd8rPanel.sdr.refreshData(new DSCallback()
		{

			@Override
			public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
			{
				ListGridRecord[] records = batchDirUpd8rPanel.sdr.getExpandedRecords();
				batchDirUpd8rPanel.sdr.collapseRecords(records);
				batchDirUpd8rPanel.sdr.expandRecords(records);
			}
		});
	}


	public void update(A_Dat2Dir.ShowSettings params)
	{
		batchDirUpd8rPanel.showSettings(params.getSettings(),params.getSrcs());
	}

	public void update(A_TrntChk.ClearResults params)
	{
		RecordList list = batchTrrntChkPanel.sdr.getResultSet().getAllCachedRows();
		for(int i = 0; i < list.getLength(); i++)
			batchTrrntChkPanel.sdr.setEditValue(i, 3, ""); //$NON-NLS-1$
	}

	public void update(A_TrntChk.UpdateResult params)
	{
		final int row = params.getRow();
		final String result = params.getResult();
		batchTrrntChkPanel.sdr.setEditValue(row, 3, result);
	}

	public void update(A_TrntChk.End params)
	{
		batchTrrntChkPanel.sdr.cancelEditing();
		batchTrrntChkPanel.sdr.refreshData(new DSCallback()
		{

			@Override
			public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
			{
				ListGridRecord[] records = batchTrrntChkPanel.sdr.getExpandedRecords();
				batchTrrntChkPanel.sdr.collapseRecords(records);
				batchTrrntChkPanel.sdr.expandRecords(records);
			}
		});
	}


	public void update(A_Compressor.ClearResults params)
	{
		for(int i = 0; i < batchCompressorPanel.fr.getTotalRows(); i++)
			batchCompressorPanel.fr.setEditValue(i, 1, ""); //$NON-NLS-1$
	}

	public void update(A_Compressor.UpdateResult params)
	{
		final int row = params.getRow();
		final String result = params.getResult();
		batchCompressorPanel.fr.setEditValue(row, 1, result);
	}

	public void update(A_Compressor.UpdateFile params)
	{
		final int row = params.getRow();
		final String file = params.getFile();
		batchCompressorPanel.fr.setEditValue(row, 0, file);
	}

	public void update(A_Compressor.End params)
	{
		batchCompressorPanel.fr.discardAllEdits();
		batchCompressorPanel.fr.refreshData();
	}
	
	public void update(A_Global.SetMemory params)
	{
		settingsDebugPanel.getItem("txtDbgMemory").setValue(params.getMsg()); //$NON-NLS-1$
	}

	
	public void update(A_Profile.Imported params)
	{
		Record record = new Record() {{
			setAttribute("Src", params.getPath()); //$NON-NLS-1$
			setAttribute("Parent", params.getParent()); //$NON-NLS-1$
			setAttribute("File", params.getName()); //$NON-NLS-1$
		}};
		profilePanel.listgrid.addData(record, (dsResponse, data, dsRequest) -> profilePanel.listgrid.selectRecord(record));
	}
}
