package jrm.webui.client.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.DataArrivedEvent;
import com.smartgwt.client.widgets.tree.events.DataArrivedHandler;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_CatVer;
import jrm.webui.client.protocol.Q_NPlayers;
import jrm.webui.client.protocol.Q_Profile;

public final class ScannerAdvFiltersPanel extends HLayout
{
	TextItem catver_path;
	TreeGrid catver_tree;
	TextItem nplayers_path;
	ListGrid nplayers_list;
	
	public ScannerAdvFiltersPanel()
	{
		super();
		setMembers(new VLayout() {{
			setShowResizeBar(true);
			setMembers(new DynamicForm() {{
				setCellPadding(0);
				setNumCols(2);
				setColWidths("*",26);
				setItems(nplayers_path = new TextItem() {{
					setShowTitle(false);
					setWidth("*");
					setCanEdit(false);
				}}, new ButtonItem() {{
					setStartRow(false);
					setIcon("icons/disk.png");
					setTitle(null);
					setValueIconRightPadding(0);
					setEndRow(false);
					addClickHandler(event->new RemoteFileChooser("NPlayers", path -> Client.sendMsg(JsonUtils.stringify(Q_NPlayers.Load.instantiate().setPath(path[0].path)))));
				}});
			}}, nplayers_list = new ListGrid() {{
				setSelectionAppearance(SelectionAppearance.CHECKBOX);
				setShowAllRecords(true);
				setShowSelectedStyle(false);
				setAutoFetchData(true);
				setSelectionProperty("isSelected");
				setAlternateRecordStyles(false);
				setCanEdit(false);
				setCanRemoveRecords(false);
				addSelectionChangedHandler(event -> Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("ID"), event.getState()))));
				setDataSource(new RestDataSource() {{
					setID("NPlayers");
					setDataURL("/datasources/"+getID());
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
					);
					setFields(
						new DataSourceTextField("Name",Client.session.getMsg("MainFrame.NPlayers")),
						new DataSourceTextField("ID") {{setPrimaryKey(true);setHidden(true);}},
						new DataSourceBooleanField("isSelected") {{setHidden(true);}},
						new DataSourceIntegerField("Cnt") {{setHidden(true);}}
					);
				}});
				setFields(new TreeGridField("Name") {{
					setCellFormatter((value, record, rowNum, colNum)->{
						return value + " ("+record.getAttribute("Cnt")+")";
					});
				}});
				setContextMenu(new Menu() {{
					setItems(
						new MenuItem(Client.session.getMsg("MainFrame.SelectAll")) {{
							addClickHandler(event->nplayers_list.selectAllRecords());
						}},
						new MenuItem(Client.session.getMsg("MainFrame.SelectNone")) {{
							addClickHandler(event->nplayers_list.deselectAllRecords());
						}},
						new MenuItem(Client.session.getMsg("MainFrame.InvertSelection")) {{
							addClickHandler(event->{
								ListGridRecord[] to_unselect = nplayers_list.getSelectedRecords();
								List<ListGridRecord> to_unselect_list = Arrays.asList(to_unselect);
								ListGridRecord[] to_select = Stream.of(nplayers_list.getRecords()).filter(r->!to_unselect_list.contains(r)).collect(Collectors.toList()).toArray(new ListGridRecord[0]);
								nplayers_list.deselectRecords(to_unselect);
								nplayers_list.selectRecords(to_select);
							});
						}},
						new MenuItem() {{setIsSeparator(true);}},
						new MenuItem(Client.session.getMsg("ScannerAdvFilterPanel.mntmClear_1.text")) {{
							addClickHandler(event->Client.sendMsg(JsonUtils.stringify(Q_NPlayers.Load.instantiate().setPath(null))));
						}}
					);
				}});
			}});
		}}, new VLayout() {{
			setMembers(new DynamicForm() {{
				setWidth100();
				setCellPadding(0);
				setNumCols(2);
				setColWidths("*",26);
				setItems(
					catver_path = new TextItem() {{
						setShowTitle(false);
						setWidth("*");
						setCanEdit(false);
					}},
					new ButtonItem() {{
						setStartRow(false);
						setIcon("icons/disk.png");
						setTitle(null);
						setValueIconRightPadding(0);
						setEndRow(false);
						addClickHandler(event -> new RemoteFileChooser("CatVer", path -> Client.sendMsg(JsonUtils.stringify(Q_CatVer.Load.instantiate().setPath(path[0].path)))));
					}}
				);
			}}, catver_tree = new TreeGrid() {{
				setShowAllRecords(true);
				setSelectionAppearance(SelectionAppearance.CHECKBOX);
				setShowSelectedStyle(false);
				setShowPartialSelection(true);
				setShowConnectors(true);
				setCascadeSelection(true);
				setCellPadding(0);
				setCanEdit(false);
				setCanRemoveRecords(false);
				setAutoFetchData(true);
//				setCellHeight(16);
				setDataProperties(new Tree() {{
					setModelType(TreeModelType.PARENT);
					setRootValue(1);
					setNameProperty("Name");
					setIdField("ID");
					setParentIdField("ParentID");
					setOpenProperty("isOpen");
					setIsFolderProperty("isFolder");
				}});
				setSelectionProperty("isSelected");
				setTreeFieldTitle(Client.session.getMsg("MainFrame.Categories"));
				setNodeIcon(null);
				setFolderIcon(null);
				addSelectionChangedHandler(event -> Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("ID"), !isPartiallySelected(event.getRecord()) && event.getState()))));
				addDataArrivedHandler(new DataArrivedHandler()
				{
					@Override
					public void onDataArrived(DataArrivedEvent event)
					{
						markForRedraw();
					}
				});
				setDataSource(new RestDataSource() {{
					setID("CatVer");
					setDataURL("/datasources/"+getID());
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
					);
					setFields(
						new DataSourceTextField("Name",Client.session.getMsg("MainFrame.Categories")),
						new DataSourceTextField("ID") {{setPrimaryKey(true);}},
						new DataSourceTextField("ParentID") {{setForeignKey("ID");setRootValue(1);}},
						new DataSourceBooleanField("isOpen") {{setHidden(true);}},
						new DataSourceBooleanField("isSelected") {{setHidden(true);}},
						new DataSourceBooleanField("isFolder") {{setHidden(true);}},
						new DataSourceIntegerField("Cnt") {{setHidden(true);}}
					);
				}});
				setFields(new TreeGridField("Name") {{
					setCellFormatter((value, record, rowNum, colNum)->{
						TreeNode node = Tree.nodeForRecord(record);
						if(node.getAttributeAsBoolean("isFolder"))
						{
							TreeNode[] children = getData().getDescendantLeaves(node);
							if(children!=null)
							{
								int count = 0;
								for(TreeNode child : children)
								{
									if(isSelected(child))
										count += Integer.parseInt(child.getAttribute("Cnt"));
								}
								return value + " ("+count+")";
							}
						}
						return value + " ("+record.getAttribute("Cnt")+")";
					});
				}});
				setContextMenu(new Menu() {{
					addItem(new MenuItem() {{
						setTitle(Client.session.getMsg("MainFrame.Select"));
						this.setSubmenu(new Menu() {{
							addItem(new MenuItem() {{
								setTitle(Client.session.getMsg("MainFrame.All"));
								addClickHandler(event->catver_tree.selectAllRecords());
							}});
							addItem(new MenuItem() {{
								setTitle(Client.session.getMsg("MainFrame.Mature"));
								addClickHandler(event->catver_tree.selectRecords(catver_tree.getData().findAll(new AdvancedCriteria("Name", OperatorId.ENDS_WITH, "* Mature *"))));
							}});
						}});
					}});
					addItem(new MenuItem() {{
						setTitle(Client.session.getMsg("MainFrame.Unselect"));
						this.setSubmenu(new Menu() {{
							addItem(new MenuItem() {{
								setTitle(Client.session.getMsg("MainFrame.All"));
								addClickHandler(event->catver_tree.deselectAllRecords());
							}});
							addItem(new MenuItem() {{
								setTitle(Client.session.getMsg("MainFrame.Mature"));
								addClickHandler(event->catver_tree.deselectRecords(catver_tree.getData().findAll(new AdvancedCriteria("Name", OperatorId.ENDS_WITH, "* Mature *"))));
							}});
						}});
					}});
					addItem(new MenuItem() {{setIsSeparator(true);}});
					addItem(new MenuItem(Client.session.getMsg("ScannerAdvFilterPanel.mntmClear.text")) {{
						addClickHandler(event->Client.sendMsg(JsonUtils.stringify(Q_CatVer.Load.instantiate().setPath(null))));
					}});
				}});
			}});
		}});
	}

}
