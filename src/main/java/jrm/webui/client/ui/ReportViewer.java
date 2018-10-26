package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
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
			setDataSource(
				new RestDataSource() {{
					setID("Report");
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
			        setFields(nameField, IDField, parentIDField);  
				}},
				new TreeGridField("title")
			);
		}
	}
	
}
