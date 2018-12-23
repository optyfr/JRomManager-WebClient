package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Optional;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.TreeGrid;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.protocol.Q_TrntChk;
import jrm.webui.client.ui.RemoteFileChooser.PathInfo;

public class BatchTrrntChkPanel extends VLayout
{
	ListGrid sdr;
	
	public BatchTrrntChkPanel()
	{
		setHeight100();
		addMember(sdr = new ListGrid() {
			{
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
						addClickHandler(e -> new RemoteFileChooser("addTrnt", pi -> {
							for(PathInfo p : pi)
								sdr.addData(new Record() {{
									setAttribute("src",p.path);
								}});
						}));
					}});
					addItem(new MenuItem() {{
						setTitle("Set Destination");
						setEnableIfCondition((target, menu, item)->sdr.getSelectedRecords().length==1);
						addClickHandler(e -> new RemoteFileChooser("updTrnt", pi -> {
							Record record = sdr.getSelectedRecord();
							for(PathInfo p : pi)
								record.setAttribute("dst", p.path);
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
			}

			@SuppressWarnings("serial")
			@Override
			protected Canvas getExpansionComponent(ListGridRecord record)
			{
				return new TreeGrid()
				{
					Boolean showok = null;
					
					{
						TreeGrid grid = this;
						setHeight(200);
						setCanEdit(false);
						setCanHover(true);
						setHoverAutoFitWidth(true);
						setHoverAutoFitMaxWidth("50%");
						setSelectionType(SelectionStyle.NONE);
						setCanSort(false);
						setShowRecordComponents(true);
						setShowRecordComponentsByCell(true);
						setAutoFitExpandField("title");
						setAutoFitFieldsFillViewport(true);
						setAutoFetchData(true);
						setShowConnectors(true);
						setShowOpener(true);
						setShowOpenIcons(true);
						setShowCustomIconOpen(true);
						setDataFetchMode(FetchMode.PAGED);
						setContextMenu(new Menu() {{
							setItems(
								new MenuItem() {{
									setTitle(Client.session.getMsg("ReportFrame.chckbxmntmShowOkEntries.text"));
									addClickHandler(e->{
										grid.getDataSource().getRequestProperties().setData(new HashMap<String,String>() {{
											put("src", record.getAttribute("src"));
											put("showOK", Boolean.toString(!(showok==null||showok==true)));
										}});
										grid.invalidateCache();
									});
									setCheckIfCondition((target, menu, item)->showok==null||showok==true);
								}}
							);
						}});
						setDataSource(new RestDataSource() {
							{
								setID("BatchTrntChkReportTree");
								setDataURL("/datasources/"+getID());
								setDataFormat(DSDataFormat.XML);
								setRequestProperties(new DSRequest() {{
									setData(new HashMap<String,String>() {{
										put("src", record.getAttribute("src"));
										put("showOK", Boolean.toString(showok==null||showok==true));
									}});
								}});
								setOperationBindings(
									new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
								);
								setFields(
									new DataSourceIntegerField("ID") {{
										setPrimaryKey(true);
										setRequired(true);
									}},
									new DataSourceIntegerField("ParentID") {{
								        setRequired(true);  
								        setForeignKey(id + ".ID");  
								        setRootValue(0);
									}},
									new DataSourceTextField("title"),
									new DataSourceIntegerField("length"),
									new DataSourceTextField("status")
								);
							}
							
							protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data)
							{
								if(dsResponse.getStatus()==0)
									showok = Optional.ofNullable(XMLTools.selectString(data, "/response/showOK")).map(Boolean::valueOf).orElse(true);
								super.transformResponse(dsResponse, dsRequest, data);
							};
						});
						setFields(
							new ListGridField("title") {{
								setHoverCustomizer(new HoverCustomizer()
								{
									@Override
									public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum)
									{
										return record.getAttribute("title");
									}
								});
							}},
							new ListGridField("length") {{
								setWidth(100);
								setHoverCustomizer(new HoverCustomizer()
								{
									@Override
									public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum)
									{
										return Optional.ofNullable(record.getAttributeAsLong("length")).map(l->readableFileSize(l)).orElse(null);
									}
								});
							}},
							new ListGridField("status") {{
								setWidth(100);
								setCellFormatter((Object value, ListGridRecord record, int rowNum, int colNum)->{
									if(value==null)
										return null;
									switch(value.toString())
									{
										case "OK":
											return "<b style='color:green'>"+value+"</b>";
										case "SIZE":
											return "<b style='color:red'>"+value+"</b>";
										case "SHA1":
											return "<b style='color:red'>"+value+"</b>";
										case "MISSING":
											return "<span style='color:red'>"+value+"</span>";
										case "SKIPPED":
											return "<span style='color:orange'>"+value+"</span>";
										case "UNKNWON":
											return "<i style='color:gray'>"+value+"</i>";
										default:
											return value.toString();
									}
								});
							}}
						);
					}
					
					public String readableFileSize(long size)
					{
					    if(size <= 0) return "0";
					    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
					    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
					    return NumberFormat.getFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
					}

				};
			}
		});
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
