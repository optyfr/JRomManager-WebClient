package jrm.webui.client.ui;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;
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
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.protocol.Q_TrntChk;

public class BatchTrrntChkPanel extends VLayout
{
	ListGrid sdr;
	
	public BatchTrrntChkPanel()
	{
		setHeight100();
		addMember(sdr = new ListGrid() {{
			setHeight100();;
			setCanEdit(true);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.MULTIPLE);
			setCanSort(false);
			setAutoFitExpandField("result");
			setAutoFitFieldsFillViewport(true);
			setAutoFetchData(true);
			setCanExpandRecords(true);
			setContextMenu(new Menu() {{
				addItem(new MenuItem() {{
					setTitle(Client.session.getMsg("BatchToolsTrrntChkPanel.mntmAddTorrent.text"));
					addClickHandler(e -> new RemoteFileChooser("addTrnt", path -> {
						sdr.addData(new Record() {{
							setAttribute("src",path);
						}});
					}));
				}});
				addItem(new MenuItem() {{
					setTitle("Set Destination");
					setEnableIfCondition((target, menu, item)->sdr.getSelectedRecords().length==1);
					addClickHandler(e -> new RemoteFileChooser("updTrnt", path -> {
						Record record = sdr.getSelectedRecord();
						record.setAttribute("dst", path);
						sdr.updateData(record);
					}));
				}});
				addItem(new MenuItem() {{
					setTitle(Client.session.getMsg("BatchToolsTrrntChkPanel.mntmDelTorrent.text"));
					setEnableIfCondition((target, menu, item)->sdr.getSelectedRecords().length>0);
					addClickHandler(e -> sdr.removeSelectedData());
				}});
			}});
			setDataSource(new RestDataSource() {{
				setID("BatchTrntChkSDR");
				setDataURL("/datasources/"+getID());
				setDataFormat(DSDataFormat.XML);
				setOperationBindings(
					new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}}
				);
				setFields(
					new DataSourceTextField("src") {{
						setPrimaryKey(true);
						setCanEdit(false);
					}},
					new DataSourceTextField("dst") {{
						setCanEdit(false);
					}},
					new DataSourceTextField("result") {{
						setCanEdit(false);
					}},
					new DataSourceBooleanField("selected") {{
					}}
				);
			}});
			setFields(
				new ListGridField("src",Client.session.getMsg("MainFrame.TorrentFiles")) {{
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
				new ListGridField("dst",Client.session.getMsg("MainFrame.DstDirs")) {{
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
				new ListGridField("result",Client.session.getMsg("MainFrame.Result")) {{
				}},
				new ListGridField("selected") {{
					setWidth(20);
					setAlign(Alignment.CENTER);
				}}
			);
		}});
		addMember(new DynamicForm() {{
			setWidth100();
			setHeight(20);
			setNumCols(9);
			setColWidths("*",100,10,"*",10,"*",10,"*","*");
			setItems(
				new SelectItem() {{
					setValueMap("FILENAME","FILESIZE","SHA1");
					setWidth(100);
					setTitle(Client.session.getMsg("BatchToolsTrrntChkPanel.lblCheckMode.text"));
					setDefaultValue(Client.session.getSetting("trntchk.mode","FILENAME"));
					addChangedHandler(e->{
						Client.socket.send(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.mode", (String)e.getValue())));
						e.getForm().getItem("remove_wrong_sized_files").setDisabled("FILENAME".equals(e.getValue()));
					});
				}},
				new CheckboxItem() {{
					setTitle(Client.session.getMsg("BatchTrrntChkPanel.chckbxDetectArchivedFolder.text"));
					setDefaultValue(Client.session.getSettingAsBoolean("trntchk.detect_archived_folders",true));
					addChangedHandler(e->Client.socket.send(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.detect_archived_folders", (Boolean)e.getValue()))));
				}},
				new CheckboxItem() {{
					setTitle(Client.session.getMsg("BatchToolsTrrntChkPanel.chckbxRemoveUnknownFiles.text"));
					setDefaultValue(Client.session.getSettingAsBoolean("trntchk.remove_unknown_files",false));
					addChangedHandler(e->Client.socket.send(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_unknown_files", (Boolean)e.getValue()))));
				}},
				new CheckboxItem("remove_wrong_sized_files") {{
					setTitle(Client.session.getMsg("BatchToolsTrrntChkPanel.chckbxRemoveWrongSized.text"));
					setDefaultValue(Client.session.getSettingAsBoolean("trntchk.remove_wrong_sized_files",false));
					setDisabled("FILENAME".equals(Client.session.getSetting("trntchk.mode","FILENAME")));
					addChangedHandler(e->Client.socket.send(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_wrong_sized_files", (Boolean)e.getValue()))));
				}},
				new ButtonItem() {{
					setTitle(Client.session.getMsg("BatchToolsTrrntChkPanel.TrntCheckStart.text"));
					setAlign(Alignment.RIGHT);
					setStartRow(false);
					addClickHandler(e->Client.socket.send(JsonUtils.stringify(Q_TrntChk.Start.instantiate())));
				}}
			);
		}});
	}

}
