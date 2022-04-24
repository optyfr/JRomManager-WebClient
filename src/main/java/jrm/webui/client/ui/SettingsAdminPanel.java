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
		addMember(new AdminGrid());
	}

	class AdminGrid extends ListGrid
	{
		public AdminGrid()
		{
			setWidth100();
			final var ds = new RestDataSource();
			ds.setDataURL("/datasources/admin");
			ds.setDataFormat(DSDataFormat.XML);
			final var login = new DataSourceTextField("Login");
			login.setPrimaryKey(true);
			login.setRequired(true);
			final var pw = new DataSourceTextField("Password");
			pw.setRequired(true);
			final var roles = new DataSourceTextField("Roles");
			roles.setValueMap("admin", "user");
			ds.setAutoDeriveTitles(false);
			ds.setFields(login, pw, roles);
			final var fetchOp = new OperationBinding();
			fetchOp.setOperationType(DSOperationType.FETCH);
			fetchOp.setDataProtocol(DSProtocol.POSTXML);
			final var addOp = new OperationBinding();
			addOp.setOperationType(DSOperationType.ADD);
			addOp.setDataProtocol(DSProtocol.POSTXML);
			final var updateOp = new OperationBinding();
			updateOp.setOperationType(DSOperationType.UPDATE);
			updateOp.setDataProtocol(DSProtocol.POSTXML);
			final var removeOp = new OperationBinding();
			removeOp.setOperationType(DSOperationType.REMOVE);
			removeOp.setDataProtocol(DSProtocol.POSTXML);
			ds.setOperationBindings(fetchOp, addOp, updateOp, removeOp);
			setDataSource(ds);
			ListGridField logfinField = new ListGridField("Login");
			logfinField.setWidth(160);
			ListGridField passwordField = new ListGridField("Password");
			passwordField.setWidth("*");
			ListGridField rolesField = new ListGridField("Roles");
			rolesField.setWidth(80);
			setFields(logfinField, passwordField, rolesField);
			setAutoFetchData(true);
			setAutoFitFieldWidths(true);
			setCanEdit(true);
			final var contextMenu = new Menu();
			final var addItem = new MenuItem("Add");
			addItem.addClickHandler(e -> AdminGrid.this.startEditingNew(Collections.singletonMap("Roles", "admin")));
			contextMenu.addItem(addItem);
			final var removeItem = new MenuItem("Remove");
			removeItem.setEnableIfCondition((target, menu, item) -> AdminGrid.this.getSelectedRecords().length > 0);
			removeItem.addClickHandler(e -> AdminGrid.this.removeSelectedData());
			contextMenu.addItem(removeItem);
			setContextMenu(contextMenu);
		}
		
		@Override
		public boolean canEditCell(int rowNum, int colNum)
		{
			String field = getFieldName(colNum);
			ListGridRecord rec = getRecord(rowNum);
			if (field.equals("Login") && rec != null)
				return false;
			if (field.equals("Roles") && rec != null)
				return !rec.getAttribute("Login").equals("admin");
			return true;
		}
	}

}
