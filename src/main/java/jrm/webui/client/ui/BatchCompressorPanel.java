package jrm.webui.client.ui;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.datasources.DSBatchCompressorFR;
import jrm.webui.client.protocol.Q_Compressor;
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.ui.RemoteFileChooser.PathInfo;

/**
 * SmartGWT panel for the batch compressor UI.
 * <p>
 * Displays a list grid of archives to (re)compress and a control form offering
 * the output format, a force flag, a clear action, and a start action. Archives
 * can be added or removed via the grid's context menu.
 *
 * @since 2.5
 */
public class BatchCompressorPanel extends VLayout //NOSONAR
{

	/** Name of the grid column holding the archive file path. */
	private static final String FILE = "file";
	/** Name of the grid column holding the operation result. */
	private static final String RESULT = "result";
	/** Icon path for the clear (trash bin) action. */
	private static final String ICON_BIN = "icons/bin.png";
	/** Icon path for the start (go) action. */
	private static final String ICON_BULLET_GO = "icons/bullet_go.png";

	/** The grid listing the archives selected for batch compression. */
	ListGrid fr;

	/**
	 * Constructs the batch compressor panel, building its grid and control form.
	 */
	public BatchCompressorPanel() {
		setHeight100();
		fr = buildGrid();
		addMember(fr);
		addMember(buildForm());
	}

	/**
	 * Builds the control form hosting the format, force, clear and start items.
	 *
	 * @return the configured dynamic form
	 */
	private DynamicForm buildForm() {
		DynamicForm form = new DynamicForm();
		form.setWidth100();
		form.setHeight(20);
		form.setNumCols(5);
		form.setColWidths("*", 100, 75, 100, 100);

		SelectItem format = buildFormatItem();
		CheckboxItem force = buildForceItem();
		ButtonItem clear = buildClearButton();
		ButtonItem start = buildStartButton();
		form.setItems(format, force, clear, start);
		return form;
	}

	/**
	 * Builds the archive format selector (ZIP / TZIP / SEVENZIP).
	 * <p>
	 * Persists the chosen value on the server through a global property update.
	 *
	 * @return the configured select item
	 */
	private SelectItem buildFormatItem() {
		SelectItem format = new SelectItem();
		format.setValueMap("ZIP", "TZIP", "SEVENZIP");
		format.setWidth(100);
		format.setTitle(Client.getSession().getMsg("BatchCompressorPanel.Format")); //$NON-NLS-1$
		format.setDefaultValue(Client.getSession().getSetting("compressor.format", "TZIP"));
		format.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("compressor.format", (String) e.getValue()))));
		return format;
	}

	/**
	 * Builds the "force compression" checkbox.
	 * <p>
	 * Persists the chosen value on the server through a global property update.
	 *
	 * @return the configured checkbox item
	 */
	private CheckboxItem buildForceItem() {
		CheckboxItem force = new CheckboxItem("force");
		force.setTitle(Client.getSession().getMsg("BatchCompressorPanel.Force")); //$NON-NLS-1$
		force.setShowTitle(false);
		force.setWidth("*");
		force.setDefaultValue(Client.getSession().getSettingAsBoolean("compressor.force", false));
		force.addChangedHandler(e -> Client.sendMsg(JsonUtils.stringify(
				Q_Global.SetProperty.instantiate().setProperty("compressor.force", (Boolean) e.getValue()))));
		return force;
	}

	/**
	 * Builds the "clear" button which removes all entries from the grid.
	 *
	 * @return the configured button item
	 */
	private ButtonItem buildClearButton() {
		ButtonItem clear = new ButtonItem();
		clear.setTitle(Client.getSession().getMsg("BatchCompressorPanel.Clear")); //$NON-NLS-1$
		clear.setIcon(ICON_BIN);
		clear.setAlign(Alignment.RIGHT);
		clear.setStartRow(false);
		clear.setEndRow(false);
		clear.setWidth("*");
		clear.addClickHandler(e -> fr.getDataSource().performCustomOperation("clear", null,
				(dsResponse, data, dsRequest) -> fr.invalidateCache()));
		return clear;
	}

	/**
	 * Builds the "start" button which triggers the batch compression on the server.
	 *
	 * @return the configured button item
	 */
	private ButtonItem buildStartButton() {
		ButtonItem start = new ButtonItem();
		start.setTitle(Client.getSession().getMsg("BatchCompressorPanel.Start")); //$NON-NLS-1$
		start.setIcon(ICON_BULLET_GO);
		start.setAlign(Alignment.RIGHT);
		start.setStartRow(false);
		start.setWidth("*");
		start.addClickHandler(e -> Client.sendMsg(JsonUtils.stringify(Q_Compressor.Start.instantiate())));
		return start;
	}

	/**
	 * Builds the archives/results grid with its context menu and data source.
	 *
	 * @return the configured list grid
	 */
	private ListGrid buildGrid() {
		ListGrid grid = new ListGrid();
		grid.setHeight100();
		grid.setCanEdit(false);
		grid.setCanHover(true);
		grid.setHoverAutoFitWidth(true);
		grid.setHoverAutoFitMaxWidth("50%");
		grid.setSelectionType(SelectionStyle.MULTIPLE);
		grid.setCanSort(false);
		grid.setAutoFitExpandField(RESULT);
		grid.setAutoFitFieldsFillViewport(true);
		grid.setAutoFetchData(true);
		grid.setContextMenu(buildGridContextMenu());
		grid.setDataSource(DSBatchCompressorFR.getInstance());
		grid.setFields(
				new ListGridField(FILE, Client.getSession().getMsg("BatchCompressorPanel.Archives")), //$NON-NLS-1$
				new ListGridField(RESULT, Client.getSession().getMsg("BatchCompressorPanel.Result")) //$NON-NLS-1$
		);
		return grid;
	}

	/**
	 * Builds the context menu of the grid, offering add and delete actions.
	 *
	 * @return the configured menu
	 */
	private Menu buildGridContextMenu() {
		Menu menu = new Menu();
		menu.addItem(buildAddMenuItem());
		menu.addItem(buildDeleteMenuItem());
		return menu;
	}

	/**
	 * Builds the "add archive" menu item which opens a remote file chooser.
	 *
	 * @return the configured menu item
	 */
	private MenuItem buildAddMenuItem() {
		MenuItem add = new MenuItem();
		add.setTitle(Client.getSession().getMsg("BatchCompressorPanel.AddArchive")); //$NON-NLS-1$
		add.addClickHandler(e -> new RemoteFileChooser("addArc",
				Client.getSession().getSetting("dir.addArc", null), pi -> {
					RPCManager.startQueue();
					for (PathInfo p : pi) {
						Record rec = new Record();
						rec.setAttribute(FILE, p.path);
						fr.addData(rec);
					}
					RPCManager.sendQueue();
				}));
		return add;
	}

	/**
	 * Builds the "delete selection" menu item, enabled only when rows are selected.
	 *
	 * @return the configured menu item
	 */
	private MenuItem buildDeleteMenuItem() {
		MenuItem delete = new MenuItem();
		delete.setTitle(Client.getSession().getMsg("BatchCompressorPanel.DeleteSelection")); //$NON-NLS-1$
		delete.setEnableIfCondition((target, mn, item) -> fr.getSelectedRecords().length > 0);
		delete.addClickHandler(e -> fr.removeSelectedData());
		return delete;
	}

	/**
	 * Indicates whether some other object is equal to this panel.
	 * <p>
	 * Delegates to the super implementation only when the other object is also a
	 * {@link BatchCompressorPanel}; otherwise returns {@code false}.
	 *
	 * @param obj
	 *            the reference object with which to compare
	 * @return {@code true} if the other object is the same panel instance, {@code false} otherwise
	 */
	@Override
	public boolean equals(Object obj) {
        if(obj instanceof BatchCompressorPanel)
            return super.equals(obj);
		return false;
	}

	/**
	 * Returns the hash code for this panel, delegating to the super implementation.
	 *
	 * @return the hash code value
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
