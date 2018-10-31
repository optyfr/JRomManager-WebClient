package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
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
/*			setDataProperties(new Tree() {{
				setModelType(TreeModelType.PARENT);
				setNameProperty("title");
				setIdField("ID");
				setParentIdField("ParentID");
				setRootValue(0);
			}});*/
			setDataFetchMode(FetchMode.PAGED);
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
			        DataSourceIntegerField IDField = new DataSourceIntegerField("ID");  
			        IDField.setPrimaryKey(true);  
			        IDField.setRequired(true);  
			        DataSourceIntegerField parentIDField = new DataSourceIntegerField("ParentID");  
			        parentIDField.setRequired(true);  
			        parentIDField.setForeignKey(id + ".ID");  
			        parentIDField.setRootValue(0);
			        DataSourceTextField classField = new DataSourceTextField("class");
			        DataSourceTextField statusField = new DataSourceTextField("status");
			        setFields(nameField, IDField, parentIDField, classField, statusField);  
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
				case "SubjectSet":
				{
					String icon = "/images/folder";
/*					if(is)
						icon += "_open";
					else
						icon += "_closed";*/
					switch(node.getAttribute("status"))
					{
/*						case "MISSING":
							icon += "_red";
							break;*/
						default:
							return super.getIcon(node, defaultState);
					}
				//	icon += ".png";
				//	return icon;
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
