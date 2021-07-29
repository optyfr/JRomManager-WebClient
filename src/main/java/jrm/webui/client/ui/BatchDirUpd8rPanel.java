package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;
import com.smartgwt.client.widgets.menu.MenuItemStringFunction;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Dat2Dir;
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.ui.RemoteFileChooser.PathInfo;
import jrm.webui.client.utils.EnhJSO;

public class BatchDirUpd8rPanel extends VLayout
{
	ListGrid src;
	ListGrid sdr;
	ReportLite report;
	
	public BatchDirUpd8rPanel()
	{
		setHeight100();
		addMember(src = new ListGrid() {{
			setHeight("30%");
			setShowResizeBar(true);
			setCanEdit(false);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.MULTIPLE);
			setCanSort(false);
			setAutoFetchData(true);
			setContextMenu(new Menu() {{
				addItem(new MenuItem() {{
					setTitle(Client.getSession().getMsg("MainFrame.AddSrcDir"));
					addClickHandler(e -> new RemoteFileChooser("addDatSrc", null, pi -> {
						for(PathInfo p : pi)
						{
							src.addData(new Record() {{
								setAttribute("name",p.path);
							}});
						}
					}));
				}});
				addItem(new MenuItem() {{
					setTitle(Client.getSession().getMsg("MainFrame.DelSrcDir"));
					setEnableIfCondition((target, menu, item) ->src.getSelectedRecords().length>0);
					addClickHandler(e -> src.removeSelectedData());
				}});
			}});
			setDataSource(new RestDataSource() {{
				setID("BatchDat2DirSrc");
				setDataURL("/datasources/"+getID());
				setDataFormat(DSDataFormat.XML);
				setOperationBindings(
					new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}}
				);
				setFields(
					new DataSourceTextField("name") {{
						setPrimaryKey(true);
					}}
				);
			}});
		}});
		addMember(sdr = new ListGrid() {
			{
				setHeight("70%");
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
								return Client.getSession().getMsg(sdr.getSelectedRecords().length==1?"MainFrame.UpdDat":"MainFrame.AddDat");
							}
						});
						addClickHandler(e -> new RemoteFileChooser("addDat", Client.getSession().getSetting("dir.addDat", null), new RemoteFileChooser.CallBack()
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
						addClickHandler(e -> new RemoteFileChooser("updDat", Client.getSession().getSetting("dir.updDat", null), new RemoteFileChooser.CallBack()
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
						setTitle(Client.getSession().getMsg("MainFrame.DelDat"));
						setEnableIfCondition(new MenuItemIfFunction()
						{
							@Override
							public boolean execute(Canvas target, Menu menu, MenuItem item)
							{
								return sdr.getSelectedRecords().length==1;
							}
						});
						addClickHandler(e -> sdr.removeSelectedData());
					}});
					addItem(new MenuItem() {{
						setTitle(Client.getSession().getMsg("MainFrame.Presets"));
						setEnableIfCondition(new MenuItemIfFunction()
						{
							@Override
							public boolean execute(Canvas target, Menu menu, MenuItem item)
							{
								return sdr.anySelected();
							}
						});
						setSubmenu(new Menu() {{
							addItem(new MenuItem() {{
								setTitle(Client.getSession().getMsg("MainFrame.Dir2DatMenu"));
								setSubmenu(new Menu() {{
									addItem(new MenuItem() {{
										setTitle(Client.getSession().getMsg("MainFrame.TZIP"));
										addClickHandler(e -> {
											Q_Profile.SetProperty settings = Q_Profile.SetProperty.instantiate();
											settings.setProperty("need_sha1_or_md5", false); //$NON-NLS-1$
											settings.setProperty("use_parallelism", true); //$NON-NLS-1$
											settings.setProperty("create_mode", true); //$NON-NLS-1$
											settings.setProperty("createfull_mode", false); //$NON-NLS-1$
											settings.setProperty("ignore_unneeded_containers", false); //$NON-NLS-1$
											settings.setProperty("ignore_unneeded_entries", false); //$NON-NLS-1$
											settings.setProperty("ignore_unknown_containers", true); //$NON-NLS-1$
											settings.setProperty("implicit_merge", false); //$NON-NLS-1$
											settings.setProperty("ignore_merge_name_roms", false); //$NON-NLS-1$
											settings.setProperty("ignore_merge_name_disks", false); //$NON-NLS-1$
											settings.setProperty("exclude_games", false); //$NON-NLS-1$
											settings.setProperty("exclude_machines", false); //$NON-NLS-1$
											settings.setProperty("backup", true); //$NON-NLS-1$
											settings.setProperty("format", "TZIP"); //$NON-NLS-1$
											settings.setProperty("merge_mode", "NOMERGE"); //$NON-NLS-1$
											settings.setProperty("archives_and_chd_as_roms", false); //$NON-NLS-1$
											for(ListGridRecord record : sdr.getSelectedRecords())
												Client.sendMsg(JsonUtils.stringify(settings.setProfile(record.getAttribute("src"))));
										});
									}});
									addItem(new MenuItem() {{
										setTitle(Client.getSession().getMsg("MainFrame.DIR"));
										addClickHandler(e -> {
											Q_Profile.SetProperty settings = Q_Profile.SetProperty.instantiate();
											settings.setProperty("need_sha1_or_md5", false); //$NON-NLS-1$
											settings.setProperty("use_parallelism", true); //$NON-NLS-1$
											settings.setProperty("create_mode", true); //$NON-NLS-1$
											settings.setProperty("createfull_mode", false); //$NON-NLS-1$
											settings.setProperty("ignore_unneeded_containers", false); //$NON-NLS-1$
											settings.setProperty("ignore_unneeded_entries", false); //$NON-NLS-1$
											settings.setProperty("ignore_unknown_containers", true); //$NON-NLS-1$
											settings.setProperty("implicit_merge", false); //$NON-NLS-1$
											settings.setProperty("ignore_merge_name_roms", false); //$NON-NLS-1$
											settings.setProperty("ignore_merge_name_disks", false); //$NON-NLS-1$
											settings.setProperty("exclude_games", false); //$NON-NLS-1$
											settings.setProperty("exclude_machines", false); //$NON-NLS-1$
											settings.setProperty("backup", true); //$NON-NLS-1$
											settings.setProperty("format", "DIR"); //$NON-NLS-1$
											settings.setProperty("merge_mode", "NOMERGE"); //$NON-NLS-1$
											settings.setProperty("archives_and_chd_as_roms", true); //$NON-NLS-1$
											for(ListGridRecord record : sdr.getSelectedRecords())
												Client.sendMsg(JsonUtils.stringify(settings.setProfile(record.getAttribute("src"))));
										});
									}});
								}});
							}});
							addItem(new MenuItem() {{
								setTitle(Client.getSession().getMsg("BatchToolsDirUpd8rPanel.mntmCustom.text"));
								addClickHandler(e -> {
									final List<String> srcs = Stream.of(sdr.getSelectedRecords()).map(n->n.getAttribute("src")).collect(Collectors.toList());
									Q_Dat2Dir.Settings.instantiate().setSrcs(srcs).send();
								});
							}});
						}});
					}});
				}});
				setDataSource(new RestDataSource() {{
					setID("BatchDat2DirSDR");
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
					new ListGridField("src",Client.getSession().getMsg("BatchTableModel.SrcDats")) {{
						setCanEdit(false);
						setWidth("35%");
					}},
					new ListGridField("dst",Client.getSession().getMsg("BatchTableModel.DstDirs")) {{
						setCanEdit(false);
					}},
					new ListGridField("result",Client.getSession().getMsg("BatchTableModel.Result")) {{
						setCanEdit(false);
						setWidth("35%");
					}},
					new ListGridField("selected") {{
						setWidth(20);
						setAlign(Alignment.CENTER);
					}}
				);
			}
			
			@Override
			protected Canvas getExpansionComponent(ListGridRecord record)
			{
				return new ListGrid()
				{
					{
						setHeight(200);
						setCanEdit(true);
						setCanHover(true);
						setHoverAutoFitWidth(true);
						setHoverAutoFitMaxWidth("50%");
						setSelectionType(SelectionStyle.NONE);
						setCanSort(false);
						setShowRecordComponents(true);
						setShowRecordComponentsByCell(true);
						setAutoFitExpandField("src");
						setAutoFitFieldsFillViewport(true);
						setDataSource(new RestDataSource() {{
							setID("BatchDat2DirResult");
							setDataURL("/datasources/"+getID());
							setDataFormat(DSDataFormat.XML);
							setOperationBindings(
								new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
							);
							setFields(
								new DataSourceTextField("src","Dat/XML") {{
									setPrimaryKey(true);
									setForeignKey(sdr.getDataSource().getID()+".src");
								}},
								new DataSourceIntegerField("have"),
								new DataSourceIntegerField("create"),
								new DataSourceIntegerField("fix"),
								new DataSourceIntegerField("miss"),
								new DataSourceIntegerField("total")
							);
						}});
						setFields(
							new ListGridField("src") {{
								setAlign(Alignment.RIGHT);
								setCanEdit(false);
							}},
							new ListGridField("have") {{
								setWidth(70);
								setCanEdit(false);
							}},
							new ListGridField("create") {{
								setWidth(70);
								setCanEdit(false);
							}},
							new ListGridField("fix") {{
								setWidth(70);
								setCanEdit(false);
							}},
							new ListGridField("miss") {{
								setWidth(70);
								setCanEdit(false);
							}},
							new ListGridField("total") {{
								setWidth(70);
								setCanEdit(false);
							}},
							new ListGridField("report") {{
								setAlign(Alignment.CENTER);
								setDefaultWidth(70);
								setAutoFitWidth(true);
								setCanEdit(false);
							}}
						);
						fetchRelatedData(record, sdr.getDataSource());
					}
					
					@Override
					protected Canvas createRecordComponent(ListGridRecord record, Integer colNum)
					{
						switch(getFieldName(colNum))
						{
							case "report":
							{
								return new IButton("Report", e -> report = new ReportLite(record.getAttribute("src"))) {{
									setAutoFit(true);
								}};
							}
						}
						return super.createRecordComponent(record, colNum);
					}
				};
			}
		});
		addMember(new HLayout() {{
			setHeight(20);
			addMember(new LayoutSpacer("*",20));
			addMember(new DynamicForm() {{
				setColWidths(100,50);
				setWrapItemTitles(false);
				setItems(new CheckboxItem("dry_run", Client.getSession().getMsg("MainFrame.cbBatchToolsDat2DirDryRun.text")) {{
					setLabelAsTitle(true);
					setDefaultValue(Client.getSession().getSettingAsBoolean("dat2dir.dry_run",true));
					setShowLabel(false);
					addChangedHandler(e->Client.sendMsg(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("dat2dir.dry_run", (Boolean)e.getValue()))));
				}});
			}});
			IButton start = new IButton(Client.getSession().getMsg("MainFrame.btnStart.text"), e->{
				Client.getMainWindow().mainPane.disableTab(1);
				Client.sendMsg(JsonUtils.stringify(Q_Dat2Dir.Start.instantiate()));
			});
			start.setIcon("icons/bullet_go.png");
			addMember(start);
		}});
	}
	
	@SuppressWarnings("serial")
	class Settings extends Window
	{
		ScannerSettingsPanel settings_panel;

		Settings(EnhJSO settings, JsArrayString srcs)
		{
			Client.getChildWindows().add(this);
			setAnimateMinimize(true);
			setIsModal(true);
			setShowModalMask(true);
			setCanDragReposition(true);
			setCanDragResize(true);
			setShowHeaderIcon(true);
			setShowMaximizeButton(true);
			setHeaderIconDefaults(new HashMap<String,Object>() {{
				put("width", 16);
				put("height", 16);
				put("src", "rom.png");
			}});
			setShowHeaderIcon(true);
			addCloseClickHandler(event->Settings.this.markForDestroy());
			addItem(new HLayout() {{
				setHeight100();
				addMember(new LayoutSpacer("5%","*"));
				addMember(new VLayout() {{
					setHeight100();
					addMember(new LayoutSpacer("100%","*"));
					addMember(settings_panel=new ScannerSettingsPanel(settings));
					addMember(new LayoutSpacer("100%","*"));
				}});
				addMember(new LayoutSpacer("5%","*"));
			}});
			addItem(new HLayout() {{
				addMember(new LayoutSpacer("*",20));
				addMember(new IButton("OK", e-> {
					Q_Profile.SetProperty props = Q_Profile.SetProperty.instantiate();
					Map<String, Object> values = settings_panel.getFilteredValues();
					values.forEach((k, v) -> {
						if (v != null)
							props.setProperty(k, v);
					});
					for(int i = 0; i < srcs.length(); i++)
					{
						SC.logWarn(i+":"+srcs.get(i));
						props.setProfile(srcs.get(i)).send();
					}
					Settings.this.markForDestroy();
				}));
				addMember(new IButton("Cancel", e->Settings.this.markForDestroy()));
			}});
			setAutoCenter(true);
			setWidth("50%");
			setHeight("50%");
			show();
			redraw();
		}

		@Override
		protected void onDestroy()
		{
			Client.getChildWindows().remove(this);
			super.onDestroy();
		}
		
	}
	
	public void showSettings(EnhJSO settings, JsArrayString srcs)
	{
		new Settings(settings, srcs);
	}

}
