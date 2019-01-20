package jrm.webui.client.ui;

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
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;

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
					setTitle(Client.session.getMsg("MainFrame.AddSrcDir"));
					addClickHandler(e -> new RemoteFileChooser("addDatSrc", pi -> {
						for(PathInfo p : pi)
						{
							src.addData(new Record() {{
								setAttribute("name",p.path);
							}});
						}
					}));
				}});
				addItem(new MenuItem() {{
					setTitle(Client.session.getMsg("MainFrame.DelSrcDir"));
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
						setTitle(Client.session.getMsg("MainFrame.AddDat"));
						addClickHandler(e -> new RemoteFileChooser("addDat", pi -> {
							for(PathInfo p : pi)
							{
								sdr.addData(new Record() {{
									setAttribute("src",p.path);
								}});
							}
						}));
					}});
					addItem(new MenuItem() {{
						setTitle("Set Destination");
						setEnableIfCondition(new MenuItemIfFunction()
						{
							@Override
							public boolean execute(Canvas target, Menu menu, MenuItem item)
							{
								return sdr.getSelectedRecords().length==1;
							}
						});
						addClickHandler(e -> new RemoteFileChooser("updDat", pi -> {
							Record record = sdr.getSelectedRecord();
							for(PathInfo p : pi)
								record.setAttribute("dst", p.path);
							sdr.updateData(record);
						}));
					}});
					addItem(new MenuItem() {{
						setTitle(Client.session.getMsg("MainFrame.DelDat"));
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
						setTitle(Client.session.getMsg("MainFrame.Presets"));
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
								setTitle(Client.session.getMsg("MainFrame.Dir2DatMenu"));
								setSubmenu(new Menu() {{
									addItem(new MenuItem() {{
										setTitle(Client.session.getMsg("MainFrame.TZIP"));
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
												Client.socket.send(JsonUtils.stringify(settings.setProfile(record.getAttribute("src"))));
										});
									}});
									addItem(new MenuItem() {{
										setTitle(Client.session.getMsg("MainFrame.DIR"));
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
												Client.socket.send(JsonUtils.stringify(settings.setProfile(record.getAttribute("src"))));
										});
									}});
								}});
							}});
							addItem(new MenuItem() {{
								setTitle(Client.session.getMsg("BatchToolsDirUpd8rPanel.mntmCustom.text"));
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
					new ListGridField("src",Client.session.getMsg("BatchTableModel.SrcDats")) {{
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
					new ListGridField("dst",Client.session.getMsg("BatchTableModel.DstDirs")) {{
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
					new ListGridField("result",Client.session.getMsg("BatchTableModel.Result")) {{
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
									setCanEdit(false);
								}},
								new DataSourceIntegerField("have") {{
									setCanEdit(false);
								}},
								new DataSourceIntegerField("miss") {{
									setCanEdit(false);
								}},
								new DataSourceIntegerField("total") {{
									setCanEdit(false);
								}}
							);
						}});
						setFields(
							new ListGridField("src") {{
								setAlign(Alignment.RIGHT);
								setCellFormatter(new CellFormatter()
								{
									@Override
									public String format(Object value, ListGridRecord record, int rowNum, int colNum)
									{
										return "<div style='overflow:hidden;text-overflow:ellipsis;direction:rtl'>"+value+"</div>";
									}
								});
							}},
							new ListGridField("have") {{
								setWidth(70);
							}},
							new ListGridField("miss") {{
								setWidth(70);
							}},
							new ListGridField("total") {{
								setWidth(70);
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
				setItems(new CheckboxItem("dry_run", Client.session.getMsg("MainFrame.cbBatchToolsDat2DirDryRun.text")) {{
					setLabelAsTitle(true);
					setDefaultValue(Client.session.getSettingAsBoolean("dry_run",true));
					setShowLabel(false);
					addChangedHandler(e->Client.socket.send(JsonUtils.stringify(Q_Global.SetProperty.instantiate().setProperty("dry_run", (Boolean)e.getValue()))));
				}});
			}});
			addMember(new IButton(Client.session.getMsg("MainFrame.btnStart.text"), e->{
				Client.mainwindow.mainPane.disableTab(1);
				Client.socket.send(JsonUtils.stringify(Q_Dat2Dir.Start.instantiate()));
			}));
		}});
	}
	
	@SuppressWarnings("serial")
	class Settings extends Window
	{
		ScannerSettingsPanel settings_panel;

		Settings(EnhJSO settings, JsArrayString srcs)
		{
			Client.childWindows.add(this);
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
			Client.childWindows.remove(this);
			super.onDestroy();
		}
		
	}
	
	public void showSettings(EnhJSO settings, JsArrayString srcs)
	{
		new Settings(settings, srcs);
	}

}
