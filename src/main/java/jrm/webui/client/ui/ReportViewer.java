package jrm.webui.client.ui;

import java.util.HashMap;

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
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;

import jrm.webui.client.Client;

@SuppressWarnings("serial")
public class ReportViewer extends Window
{
	public ReportViewer()
	{
		super();
		Client.childWindows.add(this);
		setTitle(Client.session.getMsg("ReportFrame.title"));
		setWidth("60%");
		setHeight("80%");
		setAnimateMinimize(true);
		setAutoCenter(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		setHeaderIconDefaults(new HashMap<String,Object>() {{
			put("width", 16);
			put("height", 16);
			put("src", "rom.png");
		}});
		setShowHeaderIcon(true);
		addCloseClickHandler(event->ReportViewer.this.markForDestroy());
		addItem(new ReportTree());
		setShowFooter(true);
		setFooterControls(new Label() {{setWidth100();setBorder("2px inset");}});
		show();
	}

	class ReportTree extends TreeGrid
	{
		public ReportTree()
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
			setContextMenu(new Menu() {{
				setItems(
					new MenuItem() {{
						setTitle(Client.session.getMsg("ReportFrame.chckbxmntmShowOkEntries.text"));
					}},
					new MenuItem() {{
						setTitle(Client.session.getMsg("ReportFrame.chckbxmntmHideFullyMissing.text"));
					}}
				);
			}});
			setDataSource(
				new RestDataSource() {{
					setID("Report");
					setTitleField("title");
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
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
	}
	
	@Override
	protected void onDestroy()
	{
		Client.childWindows.remove(this);
		super.onDestroy();
	}
	
}
