package jrm.webui.client.ui;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
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
import jrm.webui.client.protocol.Q_Compressor;
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.ui.RemoteFileChooser.PathInfo;

public class BatchCompressorPanel extends VLayout
{
	ListGrid fr;

	private final class Form extends DynamicForm
	{
		private Form()
		{
			setWidth100();
			setHeight(20);
			setNumCols(5);
			setColWidths("*",100,75,100,100);
			final var format = new SelectItem();
			format.setValueMap("ZIP","TZIP","SEVENZIP");
			format.setWidth(100);
			format.setTitle(Client.getSession().getMsg("BatchCompressorPanel.Format")); //$NON-NLS-1$
			format.setDefaultValue(Client.getSession().getSetting("compressor.format","TZIP"));
			format.addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("compressor.format", (String)e.getValue()))));
			final var force = new CheckboxItem("force");
			force.setTitle(Client.getSession().getMsg("BatchCompressorPanel.Force")); //$NON-NLS-1$
			force.setShowTitle(false);
			force.setWidth("*");
			force.setDefaultValue(Client.getSession().getSettingAsBoolean("compressor.force",false));
			force.addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("compressor.force", (Boolean)e.getValue()))));
			final var clear = new ButtonItem();
			clear.setTitle(Client.getSession().getMsg("BatchCompressorPanel.Clear")); //$NON-NLS-1$
			clear.setIcon("icons/bin.png");
			clear.setAlign(Alignment.RIGHT);
			clear.setStartRow(false);
			clear.setEndRow(false);
			clear.setWidth("*");
			clear.addClickHandler(e -> fr.getDataSource().performCustomOperation("clear", null, (dsResponse, data, dsRequest) -> fr.invalidateCache()));
			final var start = new ButtonItem();
			start.setTitle(Client.getSession().getMsg("BatchCompressorPanel.Start")); //$NON-NLS-1$
			start.setIcon("icons/bullet_go.png");
			start.setAlign(Alignment.RIGHT);
			start.setStartRow(false);
			start.setWidth("*");
			start.addClickHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Compressor.Start.instantiate())));
			setItems(format,force,clear,start);
		}
	}

	private static final class Grid extends ListGrid
	{
		private static final String RESULT = "result";

		private Grid()
		{
			setHeight100();
			setCanEdit(false);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.MULTIPLE);
			setCanSort(false);
			setAutoFitExpandField(RESULT);
			setAutoFitFieldsFillViewport(true);
			setAutoFetchData(true);
			final var menu = new Menu();
			final var add = new MenuItem();
			add.setTitle(Client.getSession().getMsg("BatchCompressorPanel.AddArchive")); //$NON-NLS-1$
			add.addClickHandler(e -> new RemoteFileChooser("addArc", Client.getSession().getSetting("dir.addArc", null), pi -> {
				RPCManager.startQueue();
				for(PathInfo p : pi)
				{
					final var rec = new Record();
					rec.setAttribute("file",p.path);
					Grid.this.addData(rec);
				}
				RPCManager.sendQueue();
			}));
			menu.addItem(add);
			final var delete = new MenuItem();
			delete.setTitle(Client.getSession().getMsg("BatchCompressorPanel.DeleteSelection")); //$NON-NLS-1$
			delete.setEnableIfCondition((target, mn, item)->Grid.this.getSelectedRecords().length>0);
			delete.addClickHandler(e -> Grid.this.removeSelectedData());
			menu.addItem(delete);
			setContextMenu(menu);
			final var ds = new RestDataSource();
			ds.setID("BatchCompressorFR");
			ds.setDataURL("/datasources/"+getID());
			ds.setDataFormat(DSDataFormat.XML);
			final var fetchop = new OperationBinding();
			fetchop.setOperationType(DSOperationType.FETCH);
			fetchop.setDataProtocol(DSProtocol.POSTXML);
			final var addop = new OperationBinding();
			addop.setOperationType(DSOperationType.ADD);
			addop.setDataProtocol(DSProtocol.POSTXML);
			final var removeop = new OperationBinding();
			removeop.setOperationType(DSOperationType.REMOVE);
			removeop.setDataProtocol(DSProtocol.POSTXML);
			final var updateop = new OperationBinding();
			updateop.setOperationType(DSOperationType.UPDATE);
			updateop.setDataProtocol(DSProtocol.POSTXML);
			final var customop = new OperationBinding();
			customop.setOperationType(DSOperationType.CUSTOM);
			customop.setDataProtocol(DSProtocol.POSTXML);
			ds.setOperationBindings(fetchop, addop, removeop, updateop, customop);
			final var id =new DataSourceTextField("id");
			id.setPrimaryKey(true);
			ds.setFields(id, new DataSourceTextField("file"), new DataSourceTextField(RESULT));
			setDataSource(ds);
			setFields(
				new ListGridField("file",Client.getSession().getMsg("BatchCompressorPanel.Archives")), //$NON-NLS-2$
				new ListGridField(RESULT,Client.getSession().getMsg("BatchCompressorPanel.Result")) //$NON-NLS-2$
			);
		}
	}

	public BatchCompressorPanel()
	{
		setHeight100();
		fr = new Grid();
		addMember(fr);
		addMember(new Form());
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
