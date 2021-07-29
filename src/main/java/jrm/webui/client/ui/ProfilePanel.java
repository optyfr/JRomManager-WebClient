package jrm.webui.client.ui;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.PreserveOpenState;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.ui.RemoteFileChooser.PathInfo;

public class ProfilePanel extends VLayout
{
	ListGrid listgrid;
	TreeGrid treegrid;
	String parent;
	
	public ProfilePanel()
	{
		super();
		addMembers(
			new SplitPane() {{
				listgrid = new ListGrid() {{
					setShowFilterEditor(false);
					setShowHover(true);
					setCanHover(true);
					setHoverWidth(200);
					setEditEvent(ListGridEditEvent.NONE);
					addRecordDoubleClickHandler(new RecordDoubleClickHandler()
					{
						@Override
						public void onRecordDoubleClick(RecordDoubleClickEvent event)
						{
							Q_Profile.Load.instantiate().setPath(event.getRecord().getAttribute("Parent"),event.getRecord().getAttribute("File")).send();
						}
					});
					setContextMenu(new Menu() {{
						setItems(
							new MenuItem() {{
								setTitle(Client.getSession().getMsg("MainFrame.btnImportDat.text"));
								setIcon("icons/script_go.png");
								addClickHandler(e->new RemoteFileChooser("importDat", null, path->{
									for(PathInfo p : path)
									{
										listgrid.addData(new Record() {{
											setAttribute("Src", p.path);
											setAttribute("Parent", parent);
											setAttribute("File", p.name);
										}});
									}
								}));
							}},
							new MenuItem() {{
								setIsSeparator(true);
							}},
							new MenuItem() {{
								setTitle(Client.getSession().getMsg("MainFrame.mntmDeleteProfile.text"));
								setIcon("icons/script_delete.png");
								setEnableIfCondition((Canvas target, Menu menu, MenuItem item)->listgrid.anySelected());
								addClickHandler(e->listgrid.removeSelectedData());
							}},
							new MenuItem() {{
								setTitle(Client.getSession().getMsg("MainFrame.mntmRenameProfile.text"));
								setIcon("icons/script_edit.png");
								setEnableIfCondition((Canvas target, Menu menu, MenuItem item)->listgrid.getSelectedRecords().length==1);
								addClickHandler(e->listgrid.startEditing(listgrid.getRecordIndex(listgrid.getSelectedRecord())));
							}},
							new MenuItem() {{
								setTitle(Client.getSession().getMsg("MainFrame.mntmDropCache.text"));
								setIcon("icons/bin.png");
								setEnableIfCondition((Canvas target, Menu menu, MenuItem item)->listgrid.getSelectedRecords().length==1);
								addClickHandler(e->listgrid.getDataSource().performCustomOperation("DropCache",listgrid.getSelectedRecord()));
							}}
						);
					}});
					setDataSource(new RestDataSource() {
						{
							setID("profilesList");
							setDataFormat(DSDataFormat.XML);
							setDataURL("/datasources/"+getID());
							setOperationBindings(
								new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
								new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
								new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}},
								new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}},
								new OperationBinding(){{setOperationType(DSOperationType.CUSTOM);setDataProtocol(DSProtocol.POSTXML);}}
							);
							DataSourceTextField nameField = new DataSourceTextField("Name", Client.getSession().getMsg("FileTableModel.Profile"));
							nameField.setCanEdit(true);
							DataSourceTextField parentField = new DataSourceTextField("Parent");
							parentField.setHidden(true);
							parentField.setPrimaryKey(true);
							DataSourceTextField fileField = new DataSourceTextField("File");
							fileField.setHidden(true);
							fileField.setPrimaryKey(true);
							DataSourceTextField verField = new DataSourceTextField("version", Client.getSession().getMsg("FileTableModel.Version"));
							DataSourceTextField haveSetsField = new DataSourceTextField("haveSets", Client.getSession().getMsg("FileTableModel.HaveSets"));
							DataSourceTextField haveRomsField = new DataSourceTextField("haveRoms", Client.getSession().getMsg("FileTableModel.HaveRoms"));
							DataSourceTextField haveDisksField = new DataSourceTextField("haveDisks", Client.getSession().getMsg("FileTableModel.HaveDisks"));
							DataSourceTextField createdField = new DataSourceTextField("created", Client.getSession().getMsg("FileTableModel.Created"));
							DataSourceTextField scannedField = new DataSourceTextField("scanned", Client.getSession().getMsg("FileTableModel.Scanned"));
							DataSourceTextField fixedField = new DataSourceTextField("fixed", Client.getSession().getMsg("FileTableModel.Fixed"));
							setFields(nameField, parentField, fileField, verField, haveSetsField, haveRomsField, haveDisksField, createdField, scannedField, fixedField);
						}
						
						@Override
						protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data)
						{
							if (dsResponse.getStatus() == 0)
								parent = XMLTools.selectString(data, "/response/parent");
							super.transformResponse(dsResponse, dsRequest, data);
						};
					});
				}};
				setNavigationPane(treegrid=new TreeGrid() {{
					setShowRoot(true);
					setAutoFetchData(true);
					setLoadDataOnDemand(false);
					setAutoFitFieldWidths(true);
					setAutoPreserveOpenState(PreserveOpenState.ALWAYS);
					setIndentSize(10);
					setExtraIconGap(0);
					setShowHover(true);
					setHoverWidth(200);
					setCanHover(true);
					setContextMenu(new Menu() {{
						setItems(
							new MenuItem() {{
								setTitle(Client.getSession().getMsg("MainFrame.mntmCreateFolder.text"));
								setIcon("icons/folder_add.png");
								addClickHandler(e->{
									TreeNode node = treegrid.getSelectedRecord();
									treegrid.getDataSource().addData(new Record() {{
											setAttribute("ParentID", node.getAttribute("ID"));
											setAttribute("title", Client.getSession().getMsg("MainFrame.NewFolder"));
											setAttribute("Path", node.getAttribute("Path"));
										}}, 
										(dsResponse, data, dsRequest) -> treegrid.startEditing(treegrid.getRecordIndex(dsResponse.getData()[0]))
									);
								});
								setEnableIfCondition((Canvas target, Menu menu, MenuItem item)->treegrid.anySelected());
							}},
							new MenuItem() {{
								setTitle(Client.getSession().getMsg("MainFrame.mntmDeleteFolder.text"));
								setIcon("icons/folder_delete.png");
								addClickHandler(e->{
									TreeNode node = treegrid.getSelectedRecord();
									String ParentID = node.getAttribute("ParentID");
									treegrid.removeSelectedData((DSResponse dsResponse, Object data, DSRequest dsRequest)->{
										treegrid.selectSingleRecord(new Record() {{setAttribute("ID", ParentID);}});
										Record record = treegrid.getSelectedRecord();
										String path = record.getAttribute("Path");
										listgrid.setCriteria(path==null?null:new Criteria() {{addCriteria("Parent", path);}});
										listgrid.invalidateCache();
									});
								});
								setEnableIfCondition((Canvas target, Menu menu, MenuItem item)->treegrid.anySelected());
							}}
						);
					}});
					addDataArrivedHandler((DataArrivedEvent event)->{
						selectSingleRecord(0);
						listgrid.setCriteria(null);
						if(listgrid.willFetchData(listgrid.getCriteria()))
							listgrid.fetchData();
						else
							listgrid.invalidateCache();
					});
					addRecordClickHandler(event->{
						String path = event.getRecord().getAttribute("Path");
						listgrid.setCriteria(path==null?null:new Criteria() {{addCriteria("Parent", path);}});
						listgrid.invalidateCache();
					});
					//setShowConnectors(true);
					setDataSource(new RestDataSource() {{
						setID("profilesTree");
						setOperationBindings(
							new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
							new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
							new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}},
							new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}}
						);
						setDataFormat(DSDataFormat.XML);
						setDataURL("/datasources/"+getID());
				        DataSourceTextField nameField = new DataSourceTextField("title");
				        DataSourceTextField pathField = new DataSourceTextField("Path");
				        pathField.setHidden(true);
				        DataSourceIntegerField IDField = new DataSourceIntegerField("ID");  
				        IDField.setPrimaryKey(true);  
				        IDField.setRequired(true);  
				        DataSourceIntegerField parentIDField = new DataSourceIntegerField("ParentID");  
				        parentIDField.setRequired(true);  
				        parentIDField.setForeignKey(id + ".ID");  
				        parentIDField.setRootValue("0");
				        setFields(nameField, pathField, IDField, parentIDField);  
					}});
					setFields(new ListGridField("title") {{
						setHoverCustomizer(new HoverCustomizer()
						{
							@Override
							public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum)
							{
								return record.getAttribute("title");
							}
						});
					}});
				}});
				setNavigationPaneWidth(200);
				setShowNavigationBar(true);
				setShowDetailToolStrip(true);
				setDetailToolButtons(
					new IButton("Manage files uploads") {{
						setAutoFit(true);
						setIcon("icons/page_add.png");
						addClickHandler(e->new RemoteFileChooser("manageUploads", null, null));
					}},
					new IButton(Client.getSession().getMsg("MainFrame.btnImportDat.text")) {{
						setAutoFit(true);
						setIcon("icons/script_go.png");
						addClickHandler(e->new RemoteFileChooser("importDat", null, path->{
							for(PathInfo p : path)
							{
								Record record = new Record() {{
									setAttribute("Src", p.path);
									setAttribute("Parent", parent);
									setAttribute("File", p.name);
								}};
								listgrid.addData(record, (dsResponse, data, dsRequest) -> listgrid.selectRecord(record));
							}
						}));
					}},
					new IMenuButton() {{
						setTitle(Canvas.imgHTML("icons/application_go.png")+" Import from Mame");
						setAutoFit(true);
						setMenu(new Menu() {{
							addItem(new MenuItem() {{
								setTitle("without Software list");
								addClickHandler(e->Q_Profile.Import.instantiate().setParent(parent).setSL(false).send());
							}});
							addItem(new MenuItem() {{
								setTitle("with Software list");
								addClickHandler(e->Q_Profile.Import.instantiate().setParent(parent).setSL(true).send());
							}});
						}});
					}}
				);
				setDetailPane(listgrid);
			}}
		);
	}
	
	void refreshListGrid()
	{
		String selection = listgrid.getSelectedState();
		listgrid.refreshData(new DSCallback()
		{
			@Override
			public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
			{
				 listgrid.setSelectedState(selection);
			}
		});
		
	}
}
