package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.events.DataArrivedEvent;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Report;

final class ReportTree extends TreeGrid
{
	final HashMap<String, Boolean> filters = new HashMap<>();
	private static final String STATUS = "status";
	private static final String IS_FIXABLE = "isFixable";
	private static final String TITLE = "title";
	private static final String DETAIL = "detail";
	private static final String PARENT_ID = "ParentID";
	private static final String SELECT_AND_COPY_THE_TEXT_BELOW = "Select and Copy the text below";
	private static final String SHOWOK = "SHOWOK";
	private static final String HIDEMISSING = "HIDEMISSING";
	
	private final class ReportMenu extends Menu
	{
		private ReportMenu(String src)
		{
			Dialog dialog = new Dialog();
			dialog.setWidth(350);
			final var search = new MenuItem("Search on the Web");
			search.addClickHandler(event -> ReportTree.this.getDataSource().performCustomOperation(DETAIL, ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
				Record[] records = dsResponse.getData();
				if (records != null && records.length > 0)
				{
					final String name = records[0].getAttribute("Name");
					final String crc = records[0].getAttribute("CRC");
					final String sha1 = records[0].getAttribute("SHA1");
					final String hash = Optional.ofNullable(Optional.ofNullable(crc).orElse(sha1)).map(h -> '+' + h).orElse("");
					com.google.gwt.user.client.Window.open("https://google.com/search?q=" + URL.encodeQueryString('"' + name + '"') + hash, "_blank", null);
				}
			}));
			search.setEnableIfCondition((target, menu, item) -> Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r -> r.getAttributeAsInt(PARENT_ID)).orElse(0) != 0);
			final var copyName = new MenuItem("Copy Name");
			copyName.addClickHandler(event -> ReportTree.this.getDataSource().performCustomOperation(DETAIL, ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
				Record[] records = dsResponse.getData();
				if (records != null && records.length > 0)
					SC.askforValue("Copy", SELECT_AND_COPY_THE_TEXT_BELOW, records[0].getAttribute("Name"), v -> {
					}, dialog);
			}));
			copyName.setEnableIfCondition((target, menu, item) -> Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r -> r.getAttributeAsInt(PARENT_ID)).orElse(0) != 0);
			final var copySHA1 = new MenuItem("Copy SHA1");
			copySHA1.addClickHandler(event -> ReportTree.this.getDataSource().performCustomOperation(DETAIL, ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
				Record[] records = dsResponse.getData();
				if (records != null && records.length > 0)
					SC.askforValue("Copy", SELECT_AND_COPY_THE_TEXT_BELOW, records[0].getAttribute("SHA1"), v -> {
					}, dialog);
			}));
			copySHA1.setEnableIfCondition((target, menu, item) -> Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r -> r.getAttributeAsInt(PARENT_ID)).orElse(0) != 0);
			final var copyCRC = new MenuItem("Copy CRC");
			copyCRC.addClickHandler(event -> ReportTree.this.getDataSource().performCustomOperation(DETAIL, ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
				Record[] records = dsResponse.getData();
				if (records != null && records.length > 0)
					SC.askforValue("Copy", SELECT_AND_COPY_THE_TEXT_BELOW, records[0].getAttribute("CRC"), v -> {
					}, dialog);
			}));
			copyCRC.setEnableIfCondition((target, menu, item) -> Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r -> r.getAttributeAsInt(PARENT_ID)).orElse(0) != 0);
			final var detail = new MenuItem("Detail");
			detail.addClickHandler(event -> ReportTree.this.getDataSource().performCustomOperation(DETAIL, ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
				Record[] records = dsResponse.getData();
				if (records != null && records.length > 0)
					SC.say("<pre>" + records[0].getAttribute("Detail") + "</pre>");
			}));
			detail.setEnableIfCondition((target, menu, item) -> Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r -> r.getAttributeAsInt(PARENT_ID)).orElse(0) != 0);
			final var separator = new MenuItem();
			separator.setIsSeparator(true);
			final var hideMissing = new MenuItem();
			hideMissing.setTitle(Client.getSession().getMsg("ReportFrame.chckbxmntmHideFullyMissing.text"));
			hideMissing.addClickHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Report.SetFilter.instantiate(src!=null).setFilter(HIDEMISSING, !(filters.containsKey(HIDEMISSING)&&filters.get(HIDEMISSING))))));
			hideMissing.setCheckIfCondition((target, menu, item)->filters.containsKey(HIDEMISSING)&&filters.get(HIDEMISSING));
			final var showOK = new MenuItem();
			showOK.setTitle(Client.getSession().getMsg("ReportFrame.chckbxmntmShowOkEntries.text"));
			showOK.addClickHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Report.SetFilter.instantiate(src!=null).setFilter(SHOWOK,  !(filters.containsKey(SHOWOK)&&filters.get(SHOWOK))))));
			showOK.setCheckIfCondition((target, menu, item)->filters.containsKey(SHOWOK)&&filters.get(SHOWOK));
			setItems(showOK, hideMissing, separator, detail, copyCRC, copySHA1, copyName, search);
		}
	}

	class DS extends RestDataSource
	{
		private String status;

		DS(String src)
		{
			setID("Report");
			setTitleField(TITLE);
			if (src != null)
			{
				final var req = new DSRequest();
				req.setData(Collections.singletonMap("src", src));
				setRequestProperties(req);
			}
			final var fetch = new OperationBinding();
			fetch.setOperationType(DSOperationType.FETCH);
			fetch.setDataProtocol(DSProtocol.POSTXML);
			final var custom = new OperationBinding();
			custom.setOperationType(DSOperationType.CUSTOM);
			custom.setDataProtocol(DSProtocol.POSTXML);
			setOperationBindings(fetch, custom);
			setDataFormat(DSDataFormat.XML);
			setDataURL("/datasources/" + getID());
			DataSourceTextField nameField = new DataSourceTextField(TITLE);
			DataSourceIntegerField idField = new DataSourceIntegerField("ID");
			idField.setPrimaryKey(true);
			idField.setRequired(true);
			DataSourceIntegerField parentIDField = new DataSourceIntegerField(PARENT_ID);
			parentIDField.setRequired(true);
			parentIDField.setForeignKey(id + ".ID");
			parentIDField.setRootValue(0);
			DataSourceTextField classField = new DataSourceTextField("class");
			DataSourceTextField statusField = new DataSourceTextField(STATUS);
			DataSourceBooleanField hasNotesField = new DataSourceBooleanField("hasNotes");
			DataSourceBooleanField isFixableField = new DataSourceBooleanField(IS_FIXABLE);
			setFields(nameField, idField, parentIDField, classField, statusField, hasNotesField, isFixableField);
		}
		
		@Override
		protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data)
		{
			status = XMLTools.selectString(data, "/response/infos");
			super.transformResponse(dsResponse, dsRequest, data);
		}
	}
	
	public ReportTree(final String src, ReportStatus status)
	{
		super();
		setWidth100();
		setHeight100();
		setAutoFetchData(true);
		setShowConnectors(true);
		setShowOpener(true);
		setShowOpenIcons(true);
		setShowCustomIconOpen(true);
		setDataFetchMode(FetchMode.PAGED);
		DS ds = new DS(src);
		addDataArrivedHandler((DataArrivedEvent e) -> {
			if (status != null)
				status.setStatus(ds.status);
		});
		setDataSource(ds,new TreeGridField(TITLE));
		setContextMenu(new ReportMenu(src));
	}
	
	@Override
	protected String getIcon(Record node, boolean defaultState)
	{
		switch(node.getAttribute("class"))
		{
			case "RomSuspiciousCRC":
				return "/images/icons/information.png";
			case "ContainerUnknown":
			case "ContainerUnneeded":
				return "/images/icons/error.png";
			case "ContainerTZip":
				return "/images/icons/compress.png";
			case "EntryOK":
				return "/images/icons/bullet_green.png";
			case "EntryAdd":
				return "/images/icons/bullet_blue.png";
			case "EntryMissingDuplicate":
				return "/images/icons/bullet_purple.png";
			case "EntryMissing":
				return "/images/icons/bullet_red.png";
			case "EntryUnneeded":
				return "/images/icons/bullet_black.png";
			case "EntryWrongHash":
				return "/images/icons/bullet_orange.png";
			case "EntryWrongName":
				return "/images/icons/bullet_pink.png";
			case "SubjectSet":
			{
				if(Boolean.TRUE.equals(node.getAttributeAsBoolean("isFolder")))
				{
					String icon = "/images/folder";
					if(Boolean.TRUE.equals(node.getAttributeAsBoolean("isOpen")))
						icon += "_open";
					else
						icon += "_closed";
					switch(node.getAttribute(STATUS))
					{
						case "FOUND":
							if(Boolean.TRUE.equals(node.getAttributeAsBoolean("hasNotes")))
							{
								if(Boolean.TRUE.equals(node.getAttributeAsBoolean(IS_FIXABLE)))
									icon += "_purple";
								else
									icon += "_orange";
							}
							else
								icon += "_green";
							break;
						case "CREATE":
						case "CREATEFULL":
							if(Boolean.TRUE.equals(node.getAttributeAsBoolean(IS_FIXABLE)))
								icon += "_blue"; //$NON-NLS-1$
							else
								icon += "_orange"; //$NON-NLS-1$
							break;
						case "MISSING":
							icon += "_red";
							break;
						case "UNNEEDED":
							icon += "_gray";
							break;
						default:
							return super.getIcon(node, defaultState);
					}
					icon += ".png";
					return icon;
				}
				else if(node.getAttribute(STATUS).equals("FOUND"))
					return "/images/icons/bullet_green.png";
				else
					return super.getIcon(node, defaultState);
			}
			default:
				return super.getIcon(node, defaultState);
		}
	}
	
	void applyFilter(String name, Boolean value)
	{
		filters.put(name, value);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}