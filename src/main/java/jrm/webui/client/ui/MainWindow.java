package jrm.webui.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window.Location;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.*;
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
	SettingsCompressorPanel settingsCompressorPanel;
	SettingsDebugPanel settingsDebugPanel;
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
				if(Client.session.isAuthenticated())
				{
					RPCRequest request = new RPCRequest();
					String logout = Location.getProtocol() + "//logout:logout@" + Location.getHost() + Location.getPath();
					request.setActionURL(logout);
					request.setSendNoQueue(true);
					request.setHttpHeaders(Collections.singletonMap("Authorization", "Basic AAAAAAAAAAAAAAAAAAA="));
					request.setWillHandleError(true);
					RPCManager.sendRequest(request,new RPCCallback()
					{
						@Override
						public void execute(RPCResponse response, Object rawData, RPCRequest request)
						{
							Cookies.removeCookie("JSESSIONID");
							String logout2 = Location.getHref();
							Location.replace(logout);
							Location.replace(logout2);
						}
					});
				}
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
				setPane(dir2datPanel = new Dir2DatPanel());
			}});
			addTab(new Tab() {{
				setIcon("icons/application_osx_terminal.png");
				setTitle(Client.session.getMsg("MainFrame.BatchTools"));
				setPane(new TabSet() {{
					setPaneMargin(0);
					setTabBarControls(
							TabBarControls.TAB_SCROLLER,
							TabBarControls.TAB_PICKER
						);
					addTab(new Tab() {{
						setTitle(Client.session.getMsg("MainFrame.panelBatchToolsDat2Dir.title"));
						setPane(batchDirUpd8rPanel = new BatchDirUpd8rPanel());
					}});
					addTab(new Tab() {{
						setTitle(Client.session.getMsg("MainFrame.panelBatchToolsDir2Torrent.title"));
						setPane(batchTrrntChkPanel = new BatchTrrntChkPanel());
					}});
					addTab(new Tab() {{
						setTitle(Client.session.getMsg("BatchPanel.Compressor"));
						setPane(batchCompressorPanel = new BatchCompressorPanel());
					}});
				}});
			}});
			addTab(new Tab() {{
				setIcon("icons/cog.png");
				setTitle(Client.session.getMsg("MainFrame.Settings"));
				setPane(new TabSet() {{
					setPaneMargin(0);
					setTabBarControls(
							TabBarControls.TAB_SCROLLER,
							TabBarControls.TAB_PICKER
						);
					addTab(new Tab() {{
						setIcon("icons/compress.png");
						setTitle(Client.session.getMsg("MainFrame.Compressors"));
						setPane(settingsCompressorPanel = new SettingsCompressorPanel());
					}});
					addTab(new Tab() {{
						setIcon("icons/bug.png");
						setTitle(Client.session.getMsg("MainFrame.Debug"));
						setPane(new VLayout() {{
							addMember(new LayoutSpacer("*","*"));
							addMember(settingsDebugPanel = new SettingsDebugPanel());
							addMember(new LayoutSpacer("*","*"));
						}});
					}});
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
			mainPane.enableTab(1);
			mainPane.selectTab(1);
			EnhJSO settings = params.getSettings();
			scannerPanel.scannerDirPanel.initPropertyItemValues(settings);
			scannerPanel.scannerSettingsPanel.initPropertyItemValues(settings);
			scannerPanel.scannerFiltersPanel.systems.setData(Record.convertToRecordArray(params.getSystems()));
			JsArrayString jsarrstr = params.getYears();
			ArrayList<String> arrlststr = new ArrayList<>();
			for(int i = 0; i < jsarrstr.length(); i++)
				arrlststr.add(jsarrstr.get(i));
			String[] arrstr = arrlststr.toArray(new String[0]);
			scannerPanel.scannerFiltersPanel.filterForm.getItem("cbYearMin").setValueMap(arrstr);
			scannerPanel.scannerFiltersPanel.filterForm.getItem("cbYearMin").setDefaultValue(arrstr[0]);
			scannerPanel.scannerFiltersPanel.filterForm.getItem("cbYearMax").setValueMap(arrstr);
			scannerPanel.scannerFiltersPanel.filterForm.getItem("cbYearMax").setDefaultValue(arrstr[arrstr.length-1]);
			scannerPanel.scannerFiltersPanel.filterForm.initPropertyItemValues(settings);
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
	
	public void update(A_Progress.CanCancel params)
	{
		progress.canCancel(params.canCancel());		
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
			if(params.hasReport())
			{
				if(scannerPanel.reportViewer==null || !Client.childWindows.contains(scannerPanel.reportViewer))
					scannerPanel.reportViewer = new ReportViewer();
				else if(scannerPanel.reportViewer.isVisible())
					scannerPanel.reportViewer.bringToFront();
				else
					scannerPanel.reportViewer.show();
				
			}
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


	public void update(A_ReportLite.ApplyFilter params)
	{
		params.forEachParams((k, v) -> batchDirUpd8rPanel.report.applyFilter(k,v));
		batchDirUpd8rPanel.report.reload();
	}

	public void update(A_Dat2Dir.ClearResults params)
	{
		RecordList list = batchDirUpd8rPanel.sdr.getResultSet().getAllCachedRows();
		for(int i = 0; i < list.getLength(); i++)
			batchDirUpd8rPanel.sdr.setEditValue(i, 3, "");
	}

	public void update(A_Dat2Dir.UpdateResult params)
	{
		final int row = params.getRow();
		final String result = params.getResult();
		batchDirUpd8rPanel.sdr.setEditValue(row, 3, result);
	}

	public void update(A_Dat2Dir.End params)
	{
		batchDirUpd8rPanel.sdr.cancelEditing();
		batchDirUpd8rPanel.sdr.invalidateCache();
	}


	public void update(A_Dat2Dir.ShowSettings params)
	{
		batchDirUpd8rPanel.showSettings(params.getSettings(),params.getSrcs());
	}

	public void update(A_TrntChk.ClearResults params)
	{
		RecordList list = batchTrrntChkPanel.sdr.getResultSet().getAllCachedRows();
		for(int i = 0; i < list.getLength(); i++)
			batchTrrntChkPanel.sdr.setEditValue(i, 3, "");
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
		batchTrrntChkPanel.sdr.invalidateCache();
	}


	public void update(A_Compressor.ClearResults params)
	{
		for(int i = 0; i < batchCompressorPanel.fr.getTotalRows(); i++)
			batchCompressorPanel.fr.setEditValue(i, 1, "");
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
		settingsDebugPanel.getItem("txtDbgMemory").setValue(params.getMsg());
	}

	
	public void update(A_Profile.Imported params)
	{
		Record record = new Record() {{
			setAttribute("Src", params.getPath());
			setAttribute("Parent", params.getParent());
			setAttribute("File", params.getName());
		}};
		profilePanel.listgrid.addData(record, (dsResponse, data, dsRequest) -> profilePanel.listgrid.selectRecord(record));
	}
}
