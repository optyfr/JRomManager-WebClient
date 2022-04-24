package jrm.webui.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window.Location;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
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
		final var deflist = new ListGrid();
		deflist.setEmptyMessage(Client.getSession().getMsg("MainWindow.NoItemsToShow")); //$NON-NLS-1$
		deflist.setLoadingDataMessage(Client.getSession().getMsg("MainWindow.LoadingData")); //$NON-NLS-1$
		ListGrid.setDefaultProperties(deflist);
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
		addCloseClickHandler(event -> {
			RPCRequest request = new RPCRequest();
			String logout = Location.getProtocol() + "//logout:logout@" + Location.getHost() + Location.getPath(); //$NON-NLS-1$
			request.setActionURL(logout);
			request.setSendNoQueue(true);
			request.setHttpHeaders(Collections.singletonMap("Authorization", "Basic AAAAAAAAAAAAAAAAAAA=")); //$NON-NLS-1$ //$NON-NLS-2$
			request.setWillHandleError(true);
			RPCManager.sendRequest(request,(response, rawData, request1) -> {
				Cookies.removeCookie("JSESSIONID"); //$NON-NLS-1$
				String logout2 = Location.getHref();
				Location.replace(logout);
				Location.replace(logout2);
			});
			close();
		});
		mainPane = new TabSet();
		mainPane.setPaneMargin(0);
		mainPane.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER);
		mainPane.addTab(getProfileTab());
		mainPane.addTab(getScannerTab());
		mainPane.addTab(getDir2DatTab());
		mainPane.addTab(getBatchTab());
		mainPane.addTab(getSettingsTab());
		addItem(mainPane);
		centerInPage();
		show();
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
			scannerPanel.scannerFiltersPanel.sources.setData(Record.convertToRecordArray(params.getSources()));
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
		progress = new Progress();
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
		batchDirUpd8rPanel.sdr.refreshData((dsResponse, data, dsRequest) -> {
			ListGridRecord[] records = batchDirUpd8rPanel.sdr.getExpandedRecords();
			batchDirUpd8rPanel.sdr.collapseRecords(records);
			batchDirUpd8rPanel.sdr.expandRecords(records);
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
		batchTrrntChkPanel.sdr.refreshData((dsResponse, data, dsRequest) -> {
			ListGridRecord[] records = batchTrrntChkPanel.sdr.getExpandedRecords();
			batchTrrntChkPanel.sdr.collapseRecords(records);
			batchTrrntChkPanel.sdr.expandRecords(records);
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
		Record rec = new Record();
		rec.setAttribute("Src", params.getPath()); //$NON-NLS-1$
		rec.setAttribute("Parent", params.getParent()); //$NON-NLS-1$
		rec.setAttribute("File", params.getName()); //$NON-NLS-1$
		profilePanel.listgrid.addData(rec, (dsResponse, data, dsRequest) -> profilePanel.listgrid.selectRecord(rec));
	}

	/**
	 * @return
	 */
	private Tab getSettingsTab()
	{
		final var tab = new Tab();
		tab.setIcon("icons/cog.png"); //$NON-NLS-1$
		tab.setTitle(Client.getSession().getMsg("MainFrame.Settings")); //$NON-NLS-1$
		final var pane = new SectionStack();
		pane.setOverflow(Overflow.AUTO);
		pane.setVisibilityMode(VisibilityMode.MULTIPLE);
		final var gen = new SectionStackSection("General");
		gen.setCanClose(false);
		gen.setExpanded(true);
		settingsGenPanel = new SettingsGenPanel();
		gen.addItem(settingsGenPanel);
		pane.addSection(gen);
		final var compressors = new SectionStackSection(Client.getSession().getMsg("MainFrame.Compressors"));
		compressors.setCanClose(false);
		compressors.setExpanded(true);
		settingsCompressorPanel = new SettingsCompressorPanel();
		compressors.addItem(settingsCompressorPanel);
		pane.addSection(compressors);
		final var bug = new SectionStackSection(Client.getSession().getMsg("MainFrame.Debug"));
		bug.setCanClose(false);
		bug.setExpanded(true);
		settingsDebugPanel = new SettingsDebugPanel();
		bug.addItem(settingsDebugPanel);
		pane.addSection(bug);
		if(Client.getSession().isAdmin())
		{
			final var admin = new SectionStackSection(Client.getSession().getMsg("MainWindow.Admin"));
			admin.setCanClose(false);
			admin.setExpanded(true);
			admin.addItem(new SettingsAdminPanel());
			pane.addSection(admin);
		}
		tab.setPane(pane);
		return tab;
	}

	/**
	 * @return
	 */
	private Tab getBatchTab()
	{
		return new Tab() {{
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
		}};
	}

	/**
	 * @return
	 */
	private Tab getDir2DatTab()
	{
		return new Tab() {{
			setIcon("icons/drive_go.png"); //$NON-NLS-1$
			setTitle(Client.getSession().getMsg("MainFrame.Dir2Dat")); //$NON-NLS-1$
			setPane(dir2datPanel = new Dir2DatPanel());
		}};
	}

	/**
	 * @return
	 */
	private Tab getScannerTab()
	{
		return new Tab() {{
			setName("scanner");
			setIcon("icons/drive_magnify.png"); //$NON-NLS-1$
			setTitle(Client.getSession().getMsg("MainFrame.Scanner")); //$NON-NLS-1$
			setDisabled(true);
			setPane(scannerPanel = new ScannerPanel());
		}};
	}

	/**
	 * @return
	 */
	private Tab getProfileTab()
	{
		return new Tab() {{
			setIcon("icons/script.png"); //$NON-NLS-1$
			setTitle(Client.getSession().getMsg("MainFrame.Profiles")); //$NON-NLS-1$
			setPane(profilePanel = new ProfilePanel());
		}};
	}
}
