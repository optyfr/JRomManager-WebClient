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

	public BatchCompressorPanel()
	{
		setHeight100();
		addMember(fr = new ListGrid() {
			{
				setHeight100();;
				setCanEdit(false);
				setCanHover(true);
				setHoverAutoFitWidth(true);
				setHoverAutoFitMaxWidth("50%");
				setSelectionType(SelectionStyle.MULTIPLE);
				setCanSort(false);
				setAutoFitExpandField("result");
				setAutoFitFieldsFillViewport(true);
				setAutoFetchData(true);
				setContextMenu(new Menu() {{
					addItem(new MenuItem() {{
						setTitle(Client.session.getMsg("BatchCompressorPanel.AddArchive")); //$NON-NLS-1$
						addClickHandler(e -> new RemoteFileChooser("addArc", null, pi -> {
							RPCManager.startQueue();
							for(PathInfo p : pi)
								fr.addData(new Record() {{
									setAttribute("file",p.path);
								}});
							RPCManager.sendQueue();
						}));
					}});
					addItem(new MenuItem() {{
						setTitle(Client.session.getMsg("BatchCompressorPanel.DeleteSelection")); //$NON-NLS-1$
						setEnableIfCondition((target, menu, item)->fr.getSelectedRecords().length>0);
						addClickHandler(e -> fr.removeSelectedData());
					}});
				}});
				setDataSource(new RestDataSource() {{
					setID("BatchCompressorFR");
					setDataURL("/datasources/"+getID());
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.CUSTOM);setDataProtocol(DSProtocol.POSTXML);}}
					);
					setFields(
						new DataSourceTextField("id") {{
							setPrimaryKey(true);
						}},
						new DataSourceTextField("file"),
						new DataSourceTextField("result")
					);
				}});
				setFields(
					new ListGridField("file",Client.session.getMsg("BatchCompressorPanel.Archives")), //$NON-NLS-2$
					new ListGridField("result",Client.session.getMsg("BatchCompressorPanel.Result")) //$NON-NLS-2$
				);
			}
		});
		addMember(new DynamicForm() {{
			setWidth100();
			setHeight(20);
			setNumCols(5);
			setColWidths("*",100,75,100,100);
			setItems(
				new SelectItem() {{
					setValueMap("ZIP","TZIP","SEVENZIP");
					setWidth(100);
					setTitle(Client.session.getMsg("BatchCompressorPanel.Format")); //$NON-NLS-1$
					setDefaultValue(Client.session.getSetting("compressor.format","TZIP"));
					addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("compressor.format", (String)e.getValue()))));
				}},
				new CheckboxItem("force") {{
					setTitle(Client.session.getMsg("BatchCompressorPanel.Force")); //$NON-NLS-1$
					setShowTitle(false);
					setWidth("*");
					setDefaultValue(Client.session.getSettingAsBoolean("compressor.force",false));
					addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("compressor.force", (Boolean)e.getValue()))));
				}},
				new ButtonItem() {{
					setTitle(Client.session.getMsg("BatchCompressorPanel.Clear")); //$NON-NLS-1$
					setIcon("icons/bin.png");
					setAlign(Alignment.RIGHT);
					setStartRow(false);
					setEndRow(false);
					setWidth("*");
					addClickHandler(e -> fr.getDataSource().performCustomOperation("clear", null, (dsResponse, data, dsRequest) -> fr.invalidateCache()));
				}},
				new ButtonItem() {{
					setTitle(Client.session.getMsg("BatchCompressorPanel.Start")); //$NON-NLS-1$
					setIcon("icons/bullet_go.png");
					setAlign(Alignment.RIGHT);
					setStartRow(false);
					setWidth("*");
					addClickHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Compressor.Start.instantiate())));
				}}
			);
		}});
	}
}
