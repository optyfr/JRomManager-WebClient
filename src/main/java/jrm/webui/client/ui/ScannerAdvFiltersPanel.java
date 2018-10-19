package jrm.webui.client.ui;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_CatVer;
import jrm.webui.client.protocol.Q_Profile;

public final class ScannerAdvFiltersPanel extends HLayout
{
	TextItem catver_path;
	TreeGrid catver_tree;
	
	public ScannerAdvFiltersPanel()
	{
		super();
		setMembers(new VLayout() {{
			setShowResizeBar(true);
			setMembers(new DynamicForm() {{
				setCellPadding(0);
				setNumCols(2);
				setColWidths("*",24);
				setItems(new TextItem() {{
					setShowTitle(false);
					setWidth("*");
					setCanEdit(false);
				}}, new ButtonItem() {{
					setStartRow(false);
					setIcon("icons/disk.png");
					setTitle(null);
					setValueIconRightPadding(0);
					setEndRow(false);
					addClickHandler(event->new RemoteFileChooser("NPlayers", null));
				}});
			}}, new ListGrid() {{
				setID("NPlayers");
			}});
		}}, new VLayout() {{
			setMembers(new DynamicForm() {{
				setCellPadding(0);
				setNumCols(2);
				setColWidths("*",24);
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
						addClickHandler(event -> new RemoteFileChooser("CatVer", path -> Client.socket.send(JsonUtils.stringify(Q_CatVer.Load.instantiate().setPath(path[0])))));
					}}
				);
			}}, catver_tree = new TreeGrid() {{
				setSelectionAppearance(SelectionAppearance.CHECKBOX);
				setShowSelectedStyle(false);
				setShowPartialSelection(true);
				setCascadeSelection(true);
				setAutoFetchData(true);
				setDataProperties(new Tree() {{
					setModelType(TreeModelType.PARENT);
					setRootValue(1);
					setNameProperty("Name");
					setIdField("ID");
					setParentIdField("ParentID");
					setOpenProperty("isOpen");
					setSelectionProperty("isSelected");
				}});
				addSelectionChangedHandler(event->Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("ID"), event.getState()))));
				setDataSource(new RestDataSource() {{
					setID("CatVer");
					setDataURL("/datasources/"+getID());
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
					);
					setFields(
						new DataSourceTextField("Name"),
						new DataSourceTextField("ID") {{setPrimaryKey(true);}},
						new DataSourceTextField("ParentID") {{setForeignKey("ID");setRootValue(1);}},
//						new DataSourceBooleanField("isOpen"),
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
			}});
		}});
	}

}
