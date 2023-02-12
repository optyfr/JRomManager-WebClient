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

public class BatchTrrntChkPanel extends VLayout
{
	private static final String TRNTCHK_MODE = "trntchk.mode";
	private static final String FILENAME = "FILENAME";

	private final class SdrGrid extends ListGrid
	{
		private SdrGrid()
		{
			setHeight100();
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
			Menu contextMenu = new Menu();
			MenuItem updateOrAdditem = new MenuItem();
			updateOrAdditem.setDynamicTitleFunction((target, menu, item) -> Client.getSession().getMsg(sdr.getSelectedRecords().length==1?"BatchToolsTrrntChkPanel.mntmUpdTorrent.text":"BatchToolsTrrntChkPanel.mntmAddTorrent.text"));
			updateOrAdditem.addClickHandler(e -> new RemoteFileChooser("addTrnt", Client.getSession().getSetting("dir.addTrnt", null), updateOrAddCB()));
			contextMenu.addItem(updateOrAdditem);
			MenuItem setDestItem = new MenuItem();
			setDestItem.setTitle("Set Destination");
			setDestItem.setEnableIfCondition((target, menu, item)->sdr.getSelectedRecords().length==1);
			setDestItem.addClickHandler(e -> new RemoteFileChooser("updTrnt", Client.getSession().getSetting("dir.updTrnt", null), setDestCB()));
			contextMenu.addItem(setDestItem);
			MenuItem delItem = new MenuItem();
			delItem.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.mntmDelTorrent.text"));
			delItem.setEnableIfCondition((target, menu, item)->sdr.getSelectedRecords().length>0);
			delItem.addClickHandler(e -> sdr.removeSelectedData());
			contextMenu.addItem(delItem);
			setContextMenu(contextMenu);
			setDataSource(DSBatchTrntChkSDR.getInstance());
			ListGridField srcField = new ListGridField("src",Client.getSession().getMsg("MainFrame.TorrentFiles"));
			srcField.setWidth("35%");
			srcField.setCanEdit(false);
			ListGridField dstField = new ListGridField("dst",Client.getSession().getMsg("MainFrame.DstDirs"));
			dstField.setCanEdit(false);
			ListGridField resultField = new ListGridField("result",Client.getSession().getMsg("MainFrame.Result"));
			resultField.setWidth("35%");
			resultField.setCanEdit(false);
			ListGridField selectedField = new ListGridField("selected");
			selectedField.setWidth(20);
			selectedField.setAlign(Alignment.CENTER);
			setFields(srcField, dstField, resultField, selectedField);
		}

		private CallBack setDestCB()
		{
			return new RemoteFileChooser.CallBack()
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
			};
		}

		private CallBack updateOrAddCB()
		{
			return new RemoteFileChooser.CallBack()
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
			};
		}

		@Override
		protected Canvas getExpansionComponent(ListGridRecord rcrd)
		{
			return new ExpansionGrid(rcrd);
		}
	}

	private final class ExpansionGrid extends TreeGrid
	{
		private static final String TITLE = "title";
		private Boolean showok = null;
		
		private ExpansionGrid(ListGridRecord rcrd)
		{
			TreeGrid grid = this;
			setHeight(200);
			setCanEdit(false);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.NONE);
			setCanSort(false);
			setShowRecordComponents(true);
			setShowRecordComponentsByCell(true);
			setAutoFitExpandField(TITLE);
			setAutoFitFieldsFillViewport(true);
			setAutoFetchData(true);
			setShowConnectors(true);
			setShowOpener(true);
			setShowOpenIcons(true);
			setShowCustomIconOpen(true);
			setDataFetchMode(FetchMode.PAGED);
			Menu contextMenu = new Menu();
			MenuItem showokItem = new MenuItem();
			showokItem.setTitle(Client.getSession().getMsg("ReportFrame.chckbxmntmShowOkEntries.text"));
			showokItem.addClickHandler(e->{
				final var map = new HashMap<String,String>();
				map. put("src", rcrd.getAttribute("src"));
				map. put("showOK", Boolean.toString(!(showok==null||showok)));
				grid.getDataSource().getRequestProperties().setData(map);
				grid.invalidateCache();
			});
			showokItem.setCheckIfCondition((target, menu, item)->showok==null||showok);
			contextMenu.setItems(showokItem);
			setContextMenu(contextMenu);
			final var ds = DSBatchTrntChkReportTree.getInstance();
			final var extradata = new HashMap<String, String>();
			extradata.put("src", rcrd.getAttribute("src"));
			extradata.put("showOK", Boolean.toString(showok == null || showok));
			ds.setCB(data -> showok = Optional.ofNullable(XMLTools.selectString(data, "/response/showOK")).map(Boolean::valueOf).orElse(true)).setExtraData(extradata);
			setDataSource(ds);
			ListGridField statusField = new ListGridField("status");
			statusField.setWidth(100);
			statusField.setCellFormatter((Object value, ListGridRecord rec, int rowNum, int colNum)->statusFormatter(value));
			ListGridField titleField = new ListGridField(TITLE);
			titleField.setHoverCustomizer(new HoverCustomizer()
			{
				@Override
				public String hoverHTML(Object value, ListGridRecord rec, int rowNum, int colNum)
				{
					return rcrd.getAttribute(TITLE);
				}
			});
			ListGridField lengthField = new ListGridField("length");
			lengthField.setWidth(100);
			lengthField.setHoverCustomizer(new HoverCustomizer()
			{
				@Override
				public String hoverHTML(Object value, ListGridRecord rec, int rowNum, int colNum)
				{
					return Optional.ofNullable(rec.getAttributeAsLong("length")).map(l->readableFileSize(l)).orElse(null);
				}
			});
			setFields(
				titleField,
				lengthField,
				statusField
			);
		}

		private String statusFormatter(Object value)
		{
			if(value==null)
				return null;
			switch(value.toString())
			{
				case "OK":
					return "<b style='color:green'>"+value+"</b>";
				case "SIZE":
					return "<b style='color:red'>"+value+"</b>";
				case "SHA1":
					return "<b style='color:red'>"+value+"</b>";
				case "MISSING":
					return "<span style='color:red'>"+value+"</span>";
				case "SKIPPED":
					return "<span style='color:orange'>"+value+"</span>";
				case "UNKNWON":
					return "<i style='color:gray'>"+value+"</i>";
				default:
					return value.toString();
			}
		}

		public String readableFileSize(long size)
		{
		    if(size <= 0) return "0";
		    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		    return NumberFormat.getFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
		}
	}

	ListGrid sdr;
	
	public BatchTrrntChkPanel()
	{
		setHeight100();
		sdr = new SdrGrid();
		addMember(sdr);
		DynamicForm form = new DynamicForm();
		form.setWidth100();
		form.setHeight(20);
		form.setNumCols(9);
		form.setColWidths("*",90,10,"*",10,"*",10,"*",100);
		SelectItem checkModeItem = new SelectItem();
		checkModeItem.setValueMap(FILENAME,"FILESIZE","SHA1");
		checkModeItem.setWidth(100);
		checkModeItem.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.lblCheckMode.text"));
		checkModeItem.setDefaultValue(Client.getSession().getSetting(TRNTCHK_MODE,FILENAME));
		checkModeItem.addChangedHandler(e->{
			Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty(TRNTCHK_MODE, (String)e.getValue())));
			e.getForm().getItem("remove_wrong_sized_files").setDisabled(FILENAME.equals(e.getValue()));
		});
		CheckboxItem detectArchivedFolderItem = new CheckboxItem();
		detectArchivedFolderItem.setTitle(Client.getSession().getMsg("BatchTrrntChkPanel.chckbxDetectArchivedFolder.text"));
		detectArchivedFolderItem.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.detect_archived_folders",true));
		detectArchivedFolderItem.addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.detect_archived_folders", (Boolean)e.getValue()))));
		CheckboxItem removeUnknownFilesItem = new CheckboxItem();
		removeUnknownFilesItem.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.chckbxRemoveUnknownFiles.text"));
		removeUnknownFilesItem.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.remove_unknown_files",false));
		removeUnknownFilesItem.addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_unknown_files", (Boolean)e.getValue()))));
		CheckboxItem removeWrongSizedItem = new CheckboxItem("remove_wrong_sized_files");
		removeWrongSizedItem.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.chckbxRemoveWrongSized.text"));
		removeWrongSizedItem.setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.remove_wrong_sized_files",false));
		removeWrongSizedItem.setDisabled(FILENAME.equals(Client.getSession().getSetting(TRNTCHK_MODE,FILENAME)));
		removeWrongSizedItem.addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_wrong_sized_files", (Boolean)e.getValue()))));
		ButtonItem startItem = new ButtonItem();
		startItem.setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.TrntCheckStart.text"));
		startItem.setIcon("icons/bullet_go.png");
		startItem.setAlign(Alignment.RIGHT);
		startItem.setStartRow(false);
		startItem.setWidth("*");
		startItem.addClickHandler(e->Client.sendMsg(JsonUtils.stringify(Q_TrntChk.Start.instantiate())));
		form.setItems(
			checkModeItem,
			detectArchivedFolderItem,
			removeUnknownFilesItem,
			removeWrongSizedItem,
			startItem
		);
		addMember(form);
	}

}
