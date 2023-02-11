package jrm.webui.client.ui;

import java.util.Collections;
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
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemStringFunction;
import com.smartgwt.client.widgets.tree.TreeGrid;

import jrm.webui.client.Client;
import jrm.webui.client.datasources.DSBatchTrntChkReportTree;
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
						setDynamicTitleFunction(new MenuItemStringFunction()
						{
							@Override
							public String execute(Canvas target, Menu menu, MenuItem item)
							{
								return Client.getSession().getMsg(sdr.getSelectedRecords().length==1?"BatchToolsTrrntChkPanel.mntmUpdTorrent.text":"BatchToolsTrrntChkPanel.mntmAddTorrent.text");
							}
						});
						addClickHandler(e -> new RemoteFileChooser("addTrnt", Client.getSession().getSetting("dir.addTrnt", null), new RemoteFileChooser.CallBack()
						{
							private void addData(PathInfo[] pi, int i)
							{
								if (i < pi.length)
								{
									Record record = new Record(Collections.singletonMap("src", pi[i].path));
									sdr.addData(record, (dsResponse, data, dsRequest) -> addData(pi, i + 1));
								}
							}
							
							private void updData(PathInfo[] pi, int start, int i)
							{
								if (i < pi.length)
								{
									if (start + i < sdr.getTotalRows())
									{
										Record record = sdr.getRecord(start + i);
										record.setAttribute("src", pi[i].path);
										sdr.updateData(record, (dsResponse, data, dsRequest) -> updData(pi, start, i + 1));
									}
									else
										addData(pi, i);
								}
							}
							
							@Override
							public void apply(PathInfo[] pi)
							{
								Record record = sdr.getSelectedRecord();
								if(record != null)
									updData(pi, sdr.getRecordIndex(record), 0);
								else
									addData(pi, 0);
							}
						}));
					}});
					addItem(new MenuItem() {{
						setTitle("Set Destination");
						setEnableIfCondition((target, menu, item)->sdr.getSelectedRecords().length==1);
						addClickHandler(e -> new RemoteFileChooser("updTrnt", Client.getSession().getSetting("dir.updTrnt", null), new RemoteFileChooser.CallBack()
						{
							private void updData(PathInfo[] pi, int start, int i)
							{
								if (i < pi.length)
								{
									if(start + i < sdr.getTotalRows())
									{
										Record record = sdr.getRecord(start + i);
										record.setAttribute("dst", pi[i].path);
										sdr.updateData(record, (dsResponse, data, dsRequest) -> updData(pi, start, i + 1));
									}
								}
							}
							
							@Override
							public void apply(PathInfo[] pi)
							{
								updData(pi, sdr.getRecordIndex(sdr.getSelectedRecord()), 0);
							}
						}));
					}});
					addItem(new MenuItem() {{
						setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.mntmDelTorrent.text"));
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
						new DataSourceTextField("id") {{
							setPrimaryKey(true);
						}},
						new DataSourceTextField("src"),
						new DataSourceTextField("dst"),
						new DataSourceTextField("result"),
						new DataSourceBooleanField("selected")
					);
				}});
				setFields(
					new ListGridField("src",Client.getSession().getMsg("MainFrame.TorrentFiles")) {{
						setWidth("35%");
						setCanEdit(false);
					}},
					new ListGridField("dst",Client.getSession().getMsg("MainFrame.DstDirs")) {{
						setCanEdit(false);
					}},
					new ListGridField("result",Client.getSession().getMsg("MainFrame.Result")) {{
						setWidth("35%");
						setCanEdit(false);
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
									setTitle(Client.getSession().getMsg("ReportFrame.chckbxmntmShowOkEntries.text"));
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
						final var ds = new DSBatchTrntChkReportTree();
						ds.setCB((data) -> {
							showok = Optional.ofNullable(XMLTools.selectString(data, "/response/showOK")).map(Boolean::valueOf).orElse(true);
						}).setExtraData(new HashMap<String, String>()
						{
							{
								put("src", record.getAttribute("src"));
								put("showOK", Boolean.toString(showok == null || showok == true));
							}
						});
						setDataSource(ds);
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
			setColWidths("*",90,10,"*",10,"*",10,"*",100);
			setItems(
				new SelectItem() {{
					setValueMap("FILENAME","FILESIZE","SHA1");
					setWidth(100);
					setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.lblCheckMode.text"));
					setDefaultValue(Client.getSession().getSetting("trntchk.mode","FILENAME"));
					addChangedHandler(e->{
						Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.mode", (String)e.getValue())));
						e.getForm().getItem("remove_wrong_sized_files").setDisabled("FILENAME".equals(e.getValue()));
					});
				}},
				new CheckboxItem() {{
					setTitle(Client.getSession().getMsg("BatchTrrntChkPanel.chckbxDetectArchivedFolder.text"));
					setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.detect_archived_folders",true));
					addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.detect_archived_folders", (Boolean)e.getValue()))));
				}},
				new CheckboxItem() {{
					setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.chckbxRemoveUnknownFiles.text"));
					setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.remove_unknown_files",false));
					addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_unknown_files", (Boolean)e.getValue()))));
				}},
				new CheckboxItem("remove_wrong_sized_files") {{
					setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.chckbxRemoveWrongSized.text"));
					setDefaultValue(Client.getSession().getSettingAsBoolean("trntchk.remove_wrong_sized_files",false));
					setDisabled("FILENAME".equals(Client.getSession().getSetting("trntchk.mode","FILENAME")));
					addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("trntchk.remove_wrong_sized_files", (Boolean)e.getValue()))));
				}},
				new ButtonItem() {{
					setTitle(Client.getSession().getMsg("BatchToolsTrrntChkPanel.TrntCheckStart.text"));
					setIcon("icons/bullet_go.png");
					setAlign(Alignment.RIGHT);
					setStartRow(false);
					setWidth("*");
					addClickHandler(e->Client.sendMsg(JsonUtils.stringify(Q_TrntChk.Start.instantiate())));
				}}
			);
		}});
	}

}
