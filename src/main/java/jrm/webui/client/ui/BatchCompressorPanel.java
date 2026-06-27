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

public class BatchCompressorPanel extends VLayout //NOSONAR
{

	private static final String FILE = "file";
	private static final String RESULT = "result";
	private static final String ICON_BIN = "icons/bin.png";
	private static final String ICON_BULLET_GO = "icons/bullet_go.png";

	ListGrid fr;

	public BatchCompressorPanel() {
		setHeight100();
		fr = buildGrid();
		addMember(fr);
		addMember(buildForm());
	}

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

	private Menu buildGridContextMenu() {
		Menu menu = new Menu();
		menu.addItem(buildAddMenuItem());
		menu.addItem(buildDeleteMenuItem());
		return menu;
	}

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

	private MenuItem buildDeleteMenuItem() {
		MenuItem delete = new MenuItem();
		delete.setTitle(Client.getSession().getMsg("BatchCompressorPanel.DeleteSelection")); //$NON-NLS-1$
		delete.setEnableIfCondition((target, mn, item) -> fr.getSelectedRecords().length > 0);
		delete.addClickHandler(e -> fr.removeSelectedData());
		return delete;
	}

	@Override
	public boolean equals(Object obj) {
        if(obj instanceof BatchCompressorPanel)
            return super.equals(obj);
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
