package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

import jrm.webui.client.Client;

public class ProfileViewer extends Window
{
	@SuppressWarnings("serial")
	public ProfileViewer()
	{
		super();
		setTitle(Client.session.getMsg("ProfileViewer.this.title"));
		setWidth(800);
		setHeight(600);
		setAnimateMinimize(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		Map<String,Object> headerIconDefaults = new HashMap<>();
		headerIconDefaults.put("width", 16);
		headerIconDefaults.put("height", 16);
		headerIconDefaults.put("src", "rom.png");
		setHeaderIconDefaults(headerIconDefaults);
		setShowHeaderIcon(true);
		addCloseClickHandler(event->ProfileViewer.this.markForDestroy());
		addItem(new VLayout() {{
			addMember(new HLayout() {{
				addMember(new VLayout() {{
					setWidth("25%");
					addMember(new ListGrid() {{
						setAutoFetchData(true);
						//setShowAllRecords(true);
						setCanEdit(false);
						setCanRemoveRecords(false);
						addSelectionChangedHandler(event -> {});
						setDataSource(
							new RestDataSource() {{
								setID("MachineListList");
								setDataURL("/datasources/"+getID());
								setDataFormat(DSDataFormat.XML);
								setOperationBindings(
									new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
								);
								setFields(
									new DataSourceTextField("status"),
									new DataSourceTextField("name",Client.session.getMsg("SoftwareListListRenderer.Name")) {{
										setPrimaryKey(true);
									}},
									new DataSourceTextField("description", Client.session.getMsg("SoftwareListListRenderer.Description")),
									new DataSourceTextField("have", Client.session.getMsg("SoftwareListListRenderer.Have"))
								);
							}}, 
							new ListGridField("status",24) {{
								setValueIcons(new HashMap<String,String>() {{
									put("COMPLETE","/images/disk_multiple_green.png");
									put("MISSING","/images/disk_multiple_red.png");
									put("PARTIAL","/images/disk_multiple_orange.png");
									put("UNKNOWN","/images/disk_multiple_gray.png");
								}});
								setShowValueIconOnly(true);
								setAlign(Alignment.CENTER);
							}},
							new ListGridField("name") {{
								setAutoFitWidth(true);
							}},
							new ListGridField("description"),
							new ListGridField("have") {{
								setAutoFitWidth(true);
								setAlign(Alignment.CENTER);
							}}
						);
					}});
					addMember(new ToolStrip() {{
						
					}});
					setShowResizeBar(true);
				}});
				addMember(new VLayout() {{
					addMember(new ListGrid() {{
						
					}});
					addMember(new ToolStrip() {{
						
					}});
				}});
				setShowResizeBar(true);
			}});
			addMember(new VLayout() {{
				addMember(new ListGrid() {{
					
				}});
				addMember(new ToolStrip() {{
					
				}});
			}});
		}});
		show();
	}
}
