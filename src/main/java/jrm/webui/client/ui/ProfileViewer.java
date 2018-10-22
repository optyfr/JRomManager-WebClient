package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
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
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.SINGLE);
			addSelectionChangedHandler(event -> {
				if(event.getState())
					anywareList.reset(event.getRecord(), getDataSource());
			});
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
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			addSelectionChangedHandler(event -> {
				if(event.getState())
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
			ismachinelist =  "*".equals(record.getAttribute("name"));
			if(willFetchData(new Criteria() {{addCriteria("list", record.getAttribute("name"));}}))
				fetchRelatedData(record, ds);
			else
				refreshData();
		}
	}
	
	@SuppressWarnings("serial")
	private class Anyware extends ListGrid
	{
		private HashMap<String,String> status_icons = new HashMap<String,String>() {{
			put("COMPLETE","/images/icons/bullet_green.png");
			put("MISSING","/images/icons/bullet_red.png");
			put("UNKNOWN","/images/icons/bullet_black.png");
		}};
		private HashMap<String,String> dumpstatus_icons = new HashMap<String,String>() {{
			put("verified","/images/icons/star.png");
			put("good","/images/icons/tick.png");
			put("baddump","/images/icons/delete.png");
			put("nodump","/images/icons/error.png");
		}};
		private HashMap<String,String> type_icons = new HashMap<String,String>() {{
			put("ROM","/images/rom_small.png");
			put("DISK","/images/icons/drive.png");
			put("SAMPLE","/images/icons/sound.png");
		}};
		
		public Anyware()
		{
			super();
			setCanEdit(false);
			setCanRemoveRecords(false);
			setSelectionType(SelectionStyle.NONE);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
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
						}},
						new DataSourceTextField("status"),
						new DataSourceIntegerField("size"),
						new DataSourceTextField("crc"),
						new DataSourceTextField("md5"),
						new DataSourceTextField("sha1")
					);
				}}, 
				new ListGridField("status",Client.session.getMsg("AnywareRenderer.Status"),24) {{
					setValueIcons(status_icons);
					setShowValueIconOnly(true);
					setAlign(Alignment.CENTER);
					setCanEdit(false);
				}},
				new ListGridField("name",Client.session.getMsg("AnywareRenderer.Name")) {{
					setMinWidth(128);
					setWidth("*");
				}},
				new ListGridField("size",Client.session.getMsg("AnywareRenderer.Size")) {{
					setMinWidth(48);
					setAutoFitWidth(true);
				}},
				new ListGridField("crc") {{
					setMinWidth(48);
					setAutoFitWidth(true);
					setCellFormatter((value, record, rowNum, colNum)->{
						if(value!=null)
							return "<code>"+value+"</code>";
						return null;
					});
				}},
				new ListGridField("md5") {{
					setMinWidth(100);
					setAutoFitWidth(true);
					setCellFormatter((value, record, rowNum, colNum)->{
						if(value!=null)
							return "<code>"+value+"</code>";
						return null;
					});
				}},
				new ListGridField("sha1") {{
					setMinWidth(160);
					setAutoFitWidth(true);
					setCellFormatter((value, record, rowNum, colNum)->{
						if(value!=null)
							return "<code>"+value+"</code>";
						return null;
					});
				}},
				new ListGridField("merge",Client.session.getMsg("AnywareRenderer.Merge")) {{
					setMinWidth(128);
					setAutoFitWidth(true);
				}},
				new ListGridField("dumpstatus",Client.session.getMsg("AnywareRenderer.DumpStatus"),24) {{
					setValueIcons(dumpstatus_icons);
					setShowValueIconOnly(true);
					setAlign(Alignment.CENTER);
					setCanEdit(false);
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
			if(willFetchData(new Criteria() {{addCriteria("list", record.getAttribute("list"));addCriteria("ware", record.getAttribute("name"));}}))
				fetchRelatedData(record, ds);
			else
				refreshData();
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
