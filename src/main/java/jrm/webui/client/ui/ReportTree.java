package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
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

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Report;

final class ReportTree extends TreeGrid
{
	final HashMap<String, Boolean> filters = new HashMap<>();
	
	public ReportTree(final String src)
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
		setDataSource(
			new RestDataSource() {{
				setID("Report");
				setTitleField("title");
				if(src!=null)
					setRequestProperties(new DSRequest() {{setData(Collections.singletonMap("src", src));}});
				setOperationBindings(
					new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.CUSTOM);setDataProtocol(DSProtocol.POSTXML);}}
				);
				setDataFormat(DSDataFormat.XML);
				setDataURL("/datasources/"+getID());
		        DataSourceTextField nameField = new DataSourceTextField("title");
		        DataSourceIntegerField IDField = new DataSourceIntegerField("ID") {{
			        setPrimaryKey(true);  
			        setRequired(true);
		        }};  
		        DataSourceIntegerField parentIDField = new DataSourceIntegerField("ParentID") {{
			        setRequired(true);  
			        setForeignKey(id + ".ID");  
			        setRootValue(0);
		        }};  
		        DataSourceTextField classField = new DataSourceTextField("class");
		        DataSourceTextField statusField = new DataSourceTextField("status");
		        DataSourceBooleanField hasNotesField = new DataSourceBooleanField("hasNotes");
		        DataSourceBooleanField isFixableField = new DataSourceBooleanField("isFixable");
		        setFields(nameField, IDField, parentIDField, classField, statusField, hasNotesField, isFixableField);  
			}},
			new TreeGridField("title")
		);
		setContextMenu(new Menu() {{
			Dialog dialog = new Dialog();
			dialog.setWidth(350);
			setItems(
				new MenuItem() {{
					setTitle(Client.session.getMsg("ReportFrame.chckbxmntmShowOkEntries.text"));
					addClickHandler(e->Client.socket.send(JsonUtils.stringify(Q_Report.SetFilter.instantiate(src!=null).setFilter("SHOWOK",  !(filters.containsKey("SHOWOK")&&filters.get("SHOWOK"))))));
					setCheckIfCondition((target, menu, item)->filters.containsKey("SHOWOK")&&filters.get("SHOWOK"));
				}},
				new MenuItem() {{
					setTitle(Client.session.getMsg("ReportFrame.chckbxmntmHideFullyMissing.text"));
					addClickHandler(e->Client.socket.send(JsonUtils.stringify(Q_Report.SetFilter.instantiate(src!=null).setFilter("HIDEMISSING", !(filters.containsKey("HIDEMISSING")&&filters.get("HIDEMISSING"))))));
					setCheckIfCondition((target, menu, item)->filters.containsKey("HIDEMISSING")&&filters.get("HIDEMISSING"));
				}},
				new MenuItem() {{
					setIsSeparator(true);
				}},
				new MenuItem("Detail") {{
					addClickHandler(event->{
						ReportTree.this.getDataSource().performCustomOperation("detail", ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
							Record[] records = dsResponse.getData();
							if(records!=null && records.length>0)
								SC.say("<pre>"+records[0].getAttribute("Detail")+"</pre>");
						});
					});
					setEnableIfCondition((target, menu, item)->Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r->r.getAttributeAsInt("ParentID")).orElse(0)!=0);
				}},
				new MenuItem("Copy CRC") {{
					addClickHandler(event->{
						ReportTree.this.getDataSource().performCustomOperation("detail", ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
							Record[] records = dsResponse.getData();
							if(records!=null && records.length>0)
								SC.askforValue("Copy", "Select and Copy the text below", records[0].getAttribute("CRC"), v->{}, dialog);
						});
					});
					setEnableIfCondition((target, menu, item)->Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r->r.getAttributeAsInt("ParentID")).orElse(0)!=0);
				}},
				new MenuItem("Copy SHA1") {{
					addClickHandler(event->{
						ReportTree.this.getDataSource().performCustomOperation("detail", ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
							Record[] records = dsResponse.getData();
							if(records!=null && records.length>0)
								SC.askforValue("Copy", "Select and Copy the text below", records[0].getAttribute("SHA1"), v->{}, dialog);
						});
					});
					setEnableIfCondition((target, menu, item)->Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r->r.getAttributeAsInt("ParentID")).orElse(0)!=0);
				}},
				new MenuItem("Copy Name") {{
					addClickHandler(event->{
						ReportTree.this.getDataSource().performCustomOperation("detail", ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
							Record[] records = dsResponse.getData();
							if(records!=null && records.length>0)
								SC.askforValue("Copy", "Select and Copy the text below", records[0].getAttribute("Name"), v->{}, dialog);
						});
					});
					setEnableIfCondition((target, menu, item)->Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r->r.getAttributeAsInt("ParentID")).orElse(0)!=0);
				}},
				new MenuItem("Search on the Web") {{
					addClickHandler(event->{
						ReportTree.this.getDataSource().performCustomOperation("detail", ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
							Record[] records = dsResponse.getData();
							if (records != null && records.length > 0)
							{
								final String name = records[0].getAttribute("Name");
								final String crc = records[0].getAttribute("CRC");
								final String sha1 = records[0].getAttribute("SHA1");
								final String hash = Optional.ofNullable(Optional.ofNullable(crc).orElse(sha1)).map(h -> '+' + h).orElse("");
								com.google.gwt.user.client.Window.open("https://google.com/search?q=" + URL.encodeQueryString('"' + name + '"') + hash, "_blank", null);
							}
						});
					});
					setEnableIfCondition((target, menu, item)->Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r->r.getAttributeAsInt("ParentID")).orElse(0)!=0);
				}}
			);
		}});
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
				if(node.getAttributeAsBoolean("isFolder"))
				{
					String icon = "/images/folder";
					if(node.getAttributeAsBoolean("isOpen"))
						icon += "_open";
					else
						icon += "_closed";
					switch(node.getAttribute("status"))
					{
						case "FOUND":
							if(node.getAttributeAsBoolean("hasNotes"))
							{
								if(node.getAttributeAsBoolean("isFixable"))
									icon += "_purple";
								else
									icon += "_orange";
							}
							else
								icon += "_green";
							break;
						case "CREATE":
						case "CREATEFULL":
							if(node.getAttributeAsBoolean("isFixable"))
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
				else if(node.getAttribute("status").equals("FOUND"))
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
	
}