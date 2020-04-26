package jrm.webui.client.ui;

import java.util.Collections;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

public class SettingsAdminPanel extends VLayout
{

	public SettingsAdminPanel()
	{
		super();
		setWidth100();
		setHeight100();
		addMember(new AdminGrid());
	}

	class AdminGrid extends ListGrid
	{
		public AdminGrid()
		{
			setWidth100();
			setHeight100();
			setDataSource(new RestDataSource() {{
				setDataURL("/datasources/admin");
				setDataFormat(DSDataFormat.XML);
				DataSourceTextField username = new DataSourceTextField("Login");
				username.setPrimaryKey(true);
				username.setRequired(true);
				DataSourceTextField password = new DataSourceTextField("Password");
				password.setRequired(true);
				DataSourceTextField roles = new DataSourceTextField("Roles");
				roles.setValueMap("admin","user");
				setAutoDeriveTitles(false);
				setFields(username, password, roles);
				setOperationBindings(
					new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}}
				);
			}});
			ListGridField username = new ListGridField("Login");
			username.setWidth(160);
			ListGridField password = new ListGridField("Password");
			password.setWidth("*");
			ListGridField roles = new ListGridField("Roles");
			roles.setWidth(80);
			setFields(username, password, roles);
			setAutoFetchData(true);
			setAutoFitFieldWidths(true);
			setCanEdit(true);
			setContextMenu(new Menu() {{
				addItem(new MenuItem("Add") {{
					addClickHandler(e->AdminGrid.this.startEditingNew(Collections.singletonMap("Roles", "admin")));
				}});
				addItem(new MenuItem("Remove") {{
					setEnableIfCondition((target,menu,item)->AdminGrid.this.getSelectedRecords().length>0);
					addClickHandler(e->AdminGrid.this.removeSelectedData());
				}});
			}});
		}
		
		@Override
		public boolean canEditCell(int rowNum, int colNum)
		{
			String field = getFieldName(colNum);
			ListGridRecord record = getRecord(rowNum);
			if(field.equals("Login") && record != null)
				return false;
			if(field.equals("Roles") && record != null)
				return !record.getAttribute("Login").equals("admin");
			return true;
		}
	}

}
