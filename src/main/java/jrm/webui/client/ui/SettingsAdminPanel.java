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

/**
 * Admin panel for managing user accounts and roles.
 * <p>
 * Hosts an editable {@link AdminGrid} backed by the {@code /datasources/admin}
 * REST datasource, allowing administrators to add, edit and remove user
 * accounts and assign their roles.
 *
 * @since 2.5
 */
public class SettingsAdminPanel extends VLayout /* NOSONAR */ {

    /** Field name for the user login column. */
    private static final String LOGIN = "Login";
    /** Field name for the user password column. */
    private static final String PASSWORD = "Password";
    /** Field name for the user roles column. */
    private static final String ROLES = "Roles";
    /** Role value identifying an administrator. */
    private static final String ADMIN = "admin";

    /**
     * Builds the admin panel, filling its width with a new {@link AdminGrid}.
     */
    public SettingsAdminPanel() {
        super();
        setWidth100();
        addMember(new AdminGrid());
    }

    /**
     * Grid for displaying and editing admin user records.
     *
     * @since 2.5
     */
    class AdminGrid extends ListGrid /* NOSONAR */ {
        /**
         * Builds the admin grid: configures the REST datasource, columns,
         * operation bindings and context menu (add/remove users).
         */
        public AdminGrid() {
            setWidth100();
            final var ds = new RestDataSource();
            ds.setDataURL("/datasources/admin");
            ds.setDataFormat(DSDataFormat.XML);
            final var login = new DataSourceTextField(LOGIN);
            login.setPrimaryKey(true);
            login.setRequired(true);
            final var pw = new DataSourceTextField(PASSWORD);
            pw.setRequired(true);
            final var roles = new DataSourceTextField(ROLES);
            roles.setValueMap(ADMIN, "user");
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
            ListGridField logfinField = new ListGridField(LOGIN);
            logfinField.setWidth(160);
            ListGridField passwordField = new ListGridField(PASSWORD);
            passwordField.setWidth("*");
            ListGridField rolesField = new ListGridField(ROLES);
            rolesField.setWidth(80);
            setFields(logfinField, passwordField, rolesField);
            setAutoFetchData(true);
            setAutoFitFieldWidths(true);
            setCanEdit(true);
            final var contextMenu = new Menu();
            final var addItem = new MenuItem("Add");
            addItem.addClickHandler(e -> AdminGrid.this.startEditingNew(Collections.singletonMap(ROLES, ADMIN)));
            contextMenu.addItem(addItem);
            final var removeItem = new MenuItem("Remove");
            removeItem.setEnableIfCondition((target, menu, item) -> AdminGrid.this.getSelectedRecords().length > 0);
            removeItem.addClickHandler(e -> AdminGrid.this.removeSelectedData());
            contextMenu.addItem(removeItem);
            setContextMenu(contextMenu);
        }

        /**
         * Determines whether a given cell is editable.
         * <p>
         * The login column is read-only for existing records, and the roles
         * column is read-only for the built-in {@code admin} account.
         *
         * @param rowNum the row index
         * @param colNum the column index
         * @return {@code true} if the cell can be edited
         */
        @Override
        public boolean canEditCell(int rowNum, int colNum) {
            String field = getFieldName(colNum);
            ListGridRecord rec = getRecord(rowNum);
            if (field.equals(LOGIN) && rec != null)
                return false;
            if (field.equals(ROLES) && rec != null)
                return !rec.getAttribute(LOGIN).equals(ADMIN);
            return true;
        }
    }

}
