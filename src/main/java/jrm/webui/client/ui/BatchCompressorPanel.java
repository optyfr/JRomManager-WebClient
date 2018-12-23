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
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
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
						setTitle("Add archive");
						addClickHandler(e -> new RemoteFileChooser("addArc", pi -> {
							RPCManager.startQueue();
							for(PathInfo p : pi)
							{
								fr.addData(new Record() {{
									setAttribute("file",p.path);
								}});
							}
							RPCManager.sendQueue();
						}));
					}});
					addItem(new MenuItem() {{
						setTitle("Delete selection");
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
						new DataSourceTextField("file") {{
						}},
						new DataSourceTextField("result") {{
						}}
					);
				}});
				setFields(
					new ListGridField("file","Archives") {{
						setAlign(Alignment.RIGHT);
						setCellFormatter(new CellFormatter()
						{
							@Override
							public String format(Object value, ListGridRecord record, int rowNum, int colNum)
							{
								if(value!=null)
									return "<div style='overflow:hidden;text-overflow:ellipsis;direction:rtl'>"+value+"</div>";
								return null;
							}
						});
					}},
					new ListGridField("result","Result") {{
					}}
				);
			}
		});
		addMember(new DynamicForm() {{
			setWidth100();
			setHeight(20);
			setNumCols(5);
			setColWidths("*",100,75,75,75);
			setItems(
				new SelectItem() {{
					setValueMap("ZIP","TZIP","SEVENZIP");
					setWidth(100);
					setTitle("Format");
					setDefaultValue(Client.session.getSetting("compressor.format","TZIP"));
					addChangedHandler(e->Client.socket.send(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("compressor.format", (String)e.getValue()))));
				}},
				new CheckboxItem("force") {{
					setTitle("Force");
					setShowTitle(false);
					setWidth("*");
					setDefaultValue(Client.session.getSettingAsBoolean("compressor.force",false));
					addChangedHandler(e->Client.socket.send(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("compressor.force", (Boolean)e.getValue()))));
				}},
				new ButtonItem() {{
					setTitle("Clear");
					setAlign(Alignment.RIGHT);
					setStartRow(false);
					setEndRow(false);
					setWidth("*");
					addClickHandler(e -> fr.getDataSource().performCustomOperation("clear", null, (dsResponse, data, dsRequest) -> fr.invalidateCache()));
				}},
				new ButtonItem() {{
					setTitle("Start");
					setAlign(Alignment.RIGHT);
					setStartRow(false);
					setWidth("*");
					addClickHandler(e->Client.socket.send(JsonUtils.stringify(Q_Compressor.Start.instantiate())));
				}}
			);
		}});
	}
}
