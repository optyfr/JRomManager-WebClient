package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.*;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

import jrm.webui.client.Client;

public class ProfileViewer extends Window
{
	AnywareListList anywareListList;
	AnywareList anywareList;
	Anyware anyware;
	
	@SuppressWarnings("serial")
	class AnywareListList extends ListGrid
	{
		private HashMap<String,String> status_icons = new HashMap<String,String>() {{
			put("COMPLETE","/images/disk_multiple_green.png");
			put("MISSING","/images/disk_multiple_red.png");
			put("PARTIAL","/images/disk_multiple_orange.png");
			put("UNKNOWN","/images/disk_multiple_gray.png");
		}};

		public AnywareListList()
		{
			super();
			setCanEdit(false);
			setCanRemoveRecords(false);
			setSelectionType(SelectionStyle.SINGLE);
			addSelectionChangedHandler(event -> {
				anywareList.reset(event.getRecord(), getDataSource());
			});
			setDataProperties(new ResultSet() {{setUseClientFiltering(false);}});
			addDataArrivedHandler(event->{
				getDataSource().setRequestProperties(new DSRequest());
				if(getTotalRows()>0 && !anySelected())
					selectSingleRecord(0);
			});
			setDataSource(
				new RestDataSource() {{
					setID("AnywareListList");
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
				new ListGridField("name") {{
					setMinWidth(80);
					setWidth("25%");
				}},
				new ListGridField("description") {{
					setMinWidth(20);									
				}},
				new ListGridField("have") {{
					setAlign(Alignment.CENTER);
					setMinWidth(80);
					setWidth("25%");
				}}
			);
		}
		
		@Override
		public String getValueIcon(ListGridField field, Object value, ListGridRecord record)
		{
			switch(field.getName())
			{
				case "name":
					return status_icons.get(record.getAttribute("status"));
			}
			return super.getValueIcon(field, value, record);
		};
		
		public void reset()
		{
			getDataSource().setRequestProperties(new DSRequest() {{
				setBypassCache(true);
				setData(new HashMap<String,String>(){{
					put("reset","true");
				}});
			}});
			if(willFetchData(null))
				fetchData();
			else
				refreshData();
		}
	}
	
	@SuppressWarnings("serial")
	class AnywareList extends ListGrid
	{
		private HashMap<String,String> status_icons = new HashMap<String,String>() {{
			put("COMPLETE","/images/folder_closed_green.png");
			put("MISSING","/images/folder_closed_red.png");
			put("PARTIAL","/images/folder_closed_orange.png");
			put("UNKNOWN","/images/folder_closed_gray.png");
		}};
		private HashMap<String,String> type_icons = new HashMap<String,String>() {{
			put("BIOS","/images/icons/application_osx_terminal.png");
			put("DEVICE","/images/icons/computer.png");
			put("MECHANICAL","/images/icons/wrench.png");
			put("STANDARD","/images/icons/joystick.png");
		}};
		private boolean ismachinelist=false;
		
		public AnywareList()
		{
			setCanEdit(true);
			setCanRemoveRecords(false);
			setSelectionType(SelectionStyle.SINGLE);
			//setShowRowNumbers(true);
			//setAutoFetchData(true);
			addSelectionChangedHandler(event -> {
				anyware.reset(event.getRecord(), getDataSource());
			});
			addDataArrivedHandler(event->{
				getDataSource().setRequestProperties(new DSRequest());
				if(getTotalRows()>0 && !anySelected())
					selectSingleRecord(0);
				refreshFields();
			});
			setDataSource(
				new RestDataSource()
				{
					{
						setID("AnywareList");
						setDataURL("/datasources/"+getID());
						setDataFormat(DSDataFormat.XML);
						setOperationBindings(
							new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
							new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}}
						);
						setFields(
							new DataSourceTextField("status"),
							new DataSourceTextField("list") {{
								setPrimaryKey(true);
								setHidden(true);
								setForeignKey("AnywareListList.name");
							}},
							new DataSourceTextField("name") {{
								setPrimaryKey(true);
							}},
							new DataSourceTextField("type"),
							new DataSourceTextField("description"),
							new DataSourceTextField("have"),
							new DataSourceTextField("cloneof"),
							new DataSourceTextField("cloneof_status"),
							new DataSourceTextField("romof"),
							new DataSourceTextField("romof_status"),
							new DataSourceTextField("sampleof"),
							new DataSourceTextField("sampleof_status"),
							new DataSourceBooleanField("selected")
						);
					}
				}, 
				new ListGridField("status",Client.session.getMsg("MachineListRenderer.Status"),24) {{
					setValueIcons(status_icons);
					setShowValueIconOnly(true);
					setAlign(Alignment.CENTER);
					setCanEdit(false);
				}},
				new ListGridField("name",Client.session.getMsg("MachineListRenderer.Name")) {{
					setMinWidth(70);
					setWidth("15%");
					setCanEdit(false);
				}},
				new ListGridField("description",Client.session.getMsg("MachineListRenderer.Description")) {{
					setCanEdit(false);
				}},
				new ListGridField("have",Client.session.getMsg("MachineListRenderer.Have")) {{
					setMinWidth(40);
					setWidth("8%");
					setAlign(Alignment.CENTER);
					setCanEdit(false);
				}},
				new ListGridField("cloneof",Client.session.getMsg("MachineListRenderer.CloneOf")) {{
					setMinWidth(70);
					setWidth("15%");
					setCanEdit(false);
				}},
				new ListGridField("romof",Client.session.getMsg("MachineListRenderer.RomOf")) {{
					setMinWidth(70);
					setWidth("15%");
					setCanEdit(false);
					setShowIfCondition((grid, field, fieldNum)->ismachinelist);
				}},
				new ListGridField("sampleof",Client.session.getMsg("MachineListRenderer.SampleOf")) {{
					setMinWidth(70);
					setWidth("15%");
					setCanEdit(false);
					setShowIfCondition((grid, field, fieldNum)->ismachinelist);
				}},
				new ListGridField("selected",Client.session.getMsg("MachineListRenderer.Selected"),20) {{
					setAlign(Alignment.CENTER);
					setCanToggle(true);
				}}
			);
		}
		
		@Override
		public String getValueIcon(ListGridField field, Object value, ListGridRecord record)
		{
			switch(field.getName())
			{
				case "name":
					return type_icons.get(record.getAttribute("type"));
				case "cloneof":
					return status_icons.get(record.getAttribute("cloneof_status"));
				case "romof":
					return status_icons.get(record.getAttribute("romof_status"));
				case "sampleof":
					return status_icons.get(record.getAttribute("sampleof_status"));
			}
			return super.getValueIcon(field, value, record);
		};
		
		public void reset(Record record, DataSource ds)
		{
			getDataSource().setRequestProperties(new DSRequest() {{
				setData(new HashMap<String,String>(){{
					put("reset","true");
				}});
			}});
			ismachinelist =  "*".equals(record.getAttribute("list"));
			fetchRelatedData(record, ds);
			refreshData();
		}
	}
	
	private class Anyware extends ListGrid
	{
		public Anyware()
		{
			super();
			setCanEdit(false);
			setCanRemoveRecords(false);
			setSelectionType(SelectionStyle.NONE);
			addDataArrivedHandler(event->{
				getDataSource().setRequestProperties(new DSRequest());
			});
			setDataSource(
				new RestDataSource() {{
					setID("Anyware");
					setDataURL("/datasources/"+getID());
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
					);
					setFields(
						new DataSourceTextField("list") {{
							setPrimaryKey(true);
							setHidden(true);
							setForeignKey("AnywareList.list");
						}},
						new DataSourceTextField("ware") {{
							setPrimaryKey(true);
							setHidden(true);
							setForeignKey("AnywareList.name");
						}},
						new DataSourceTextField("name") {{
							setPrimaryKey(true);
						}}
					);
				}}, 
				new ListGridField("name") {{
					setAutoFitWidth(true);
				}}
			);
		}

		
		@SuppressWarnings("serial")
		public void reset(Record record, DataSource ds)
		{
			getDataSource().setRequestProperties(new DSRequest() {{
				setData(new HashMap<String,String>(){{
					put("reset","true");
				}});
			}});
			fetchRelatedData(record, ds);
		}
	}
	
	public ProfileViewer()
	{
		super();
		Client.childWindows.add(this);
		setTitle(Client.session.getMsg("ProfileViewer.this.title"));
		setWidth("80%");
		setHeight("80%");
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
				setHeight("60%");
				addMember(new VLayout() {{
					setWidth("30%");
					addMember(anywareListList = new AnywareListList());
					addMember(new ToolStrip() {{}});
					setShowResizeBar(true);
				}});
				addMember(new VLayout() {{
					addMember(anywareList = new AnywareList());
					addMember(new ToolStrip() {{}});
				}});
				setShowResizeBar(true);
				setResizeBarTarget("next");
			}});
			addMember(new VLayout() {{
				addMember(anyware = new Anyware());
				addMember(new ToolStrip() {{}});
			}});
		}});
		show();
		anywareListList.reset();
	}

	@Override
	protected void onDestroy()
	{
		Client.childWindows.remove(this);
		super.onDestroy();
	}
	
}
