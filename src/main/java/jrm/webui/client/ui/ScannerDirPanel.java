package jrm.webui.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ShowContextMenuEvent;
import com.smartgwt.client.widgets.events.ShowContextMenuHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.*;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.utils.EnhJSO;

public final class ScannerDirPanel extends DynamicForm
{
	public ScannerDirPanel()
	{
		super();
		setWidth100();
		setCellPadding(1);
		setNumCols(4);
		setColWidths("200","*","22","20");
		setWrapItemTitles(false);
		setItems(
			new ButtonItem() {{
				setTitle("Manager files uploads");
				setColSpan(4);
				setAlign(Alignment.CENTER);
				addClickHandler(new ClickHandler()
				{
					@Override
					public void onClick(ClickEvent event)
					{
						new RemoteFileChooser("manageUploads", null);
					}
				});
			}},
			new RowSpacerItem(),
			new TextItem("tfRomsDest",Client.session.getMsg("MainFrame.lblRomsDest.text")) {{
				setWidth("*");
				setCanEdit(false);
				setEndRow(false);
			}},
			new ButtonItem() {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle(null);
				setValueIconRightPadding(0);
				setEndRow(false);
				addClickHandler(event->new RemoteFileChooser("tfRomsDest", records->setPropertyItemValue("tfRomsDest", "roms_dest_dir", records[0])));
			}},
			new SpacerItem(),
			new TextItem("tfDisksDest",Client.session.getMsg("MainFrame.lblDisksDest.text")) {{
				setWidth("*");
				setCanEdit(false);
				setDisabled(true);
				setEndRow(false);
			}},
			new ButtonItem("tfDisksDestBtn") {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle(null);
				setDisabled(true);
				setValueIconRightPadding(0);
				setEndRow(false);
				addClickHandler(event->new RemoteFileChooser("tfDisksDest", records->setPropertyItemValue("tfDisksDest", "disks_dest_dir", records[0])));
			}},
			new CheckboxItem("tfDisksDestCbx") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(event->{
					boolean selected = (boolean)event.getValue();
					ScannerDirPanel.this.getField("tfDisksDestBtn").setDisabled(!selected);
					ScannerDirPanel.this.getField("tfDisksDest").setDisabled(!selected);
					Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("disks_dest_dir_enabled", selected)));
				});
			}},
			new TextItem("tfSWDest",Client.session.getMsg("MainFrame.chckbxSoftwareDest.text")) {{
				setWidth("*");
				setCanEdit(false);
				setDisabled(true);
				setEndRow(false);
			}},
			new ButtonItem("tfSWDestBtn") {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle(null);
				setDisabled(true);
				setValueIconRightPadding(0);
				setEndRow(false);
				addClickHandler(event->new RemoteFileChooser("tfSWDest", records->setPropertyItemValue("tfSWDest", "swroms_dest_dir", records[0])));
			}},
			new CheckboxItem("tfSWDestCbx") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(event->{
					boolean selected = (boolean)event.getValue();
					ScannerDirPanel.this.getField("tfSWDestBtn").setDisabled(!selected);
					ScannerDirPanel.this.getField("tfSWDest").setDisabled(!selected);
					Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("swroms_dest_dir_enabled", selected)));
				});
			}},
			new TextItem("tfSWDisksDest",Client.session.getMsg("MainFrame.chckbxSwdisksdest.text")) {{
				setWidth("*");
				setCanEdit(false);
				setDisabled(true);
				setEndRow(false);
			}},
			new ButtonItem("tfSWDisksDestBtn") {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle(null);
				setDisabled(true);
				setValueIconRightPadding(0);
				setEndRow(false);
				addClickHandler(event->new RemoteFileChooser("tfSWDisksDest", records->setPropertyItemValue("tfSWDisksDest", "swdisks_dest_dir", records[0])));
			}},
			new CheckboxItem("tfSWDisksDestCbx") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(event->{
					boolean selected = (boolean)event.getValue();
					ScannerDirPanel.this.getField("tfSWDisksDestBtn").setDisabled(!selected);
					ScannerDirPanel.this.getField("tfSWDisksDest").setDisabled(!selected);
					Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("swdisks_dest_dir_enabled", selected)));
				});
			}},
			new TextItem("tfSamplesDest",Client.session.getMsg("MainFrame.lblSamplesDest.text")) {{
				setWidth("*");
				setCanEdit(false);
				setDisabled(true);
				setEndRow(false);
			}},
			new ButtonItem("tfSamplesDestBtn") {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle(null);
				setDisabled(true);
				setValueIconRightPadding(0);
				setEndRow(false);
				addClickHandler(event->new RemoteFileChooser("tfSamplesDest", records->setPropertyItemValue("tfSamplesDest", "samples_dest_dir", records[0])));
			}},
			new CheckboxItem("tfSamplesDestCbx") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(event->{
					boolean selected = (boolean)event.getValue();
					ScannerDirPanel.this.getField("tfSamplesDestBtn").setDisabled(!selected);
					ScannerDirPanel.this.getField("tfSamplesDest").setDisabled(!selected);
					Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("samples_dest_dir_enabled", selected)));
				});
			}},
			new SelectItem("listSrcDir",Client.session.getMsg("MainFrame.lblSrcDir.text")) {{
				setWidth("*");
				setID("listSrcDir");
				setHeight(200);
				setEndRow(false);
				setColSpan(2);
				setRowSpan(3);
				setMultiple(true);
				setValueMap();
				setMultipleAppearance(MultipleAppearance.GRID);
				addChangedHandler(event->event.getForm().getItem("delSrcDirBtn").setDisabled(this.getValues().length==0));
			}},
			new ButtonItem() {{
				setStartRow(false);
				setEndRow(true);
				setHeight(20);
				setVAlign(VerticalAlignment.TOP);
				setIcon("icons/folder_add.png");
				setPrompt(Client.session.getMsg("MainFrame.mntmAddDirectory.text"));
				setTitle(null);
				addClickHandler(event->addSrcDir());
			}},
			new ButtonItem("delSrcDirBtn") {{
				setStartRow(false);
				setEndRow(true);
				setHeight(20);
				setVAlign(VerticalAlignment.TOP);
				setIcon("icons/folder_delete.png");
				setPrompt(Client.session.getMsg("MainFrame.mntmDeleteSelected.text"));
				setTitle(null);
				addClickHandler(event->delSrcDir());
				setDisabled(true);
				setShouldSaveValue(true);
			}},
			new SpacerItem() {{
				setHeight(160);
			}}
		);
		setContextMenu(new Menu() {{
			addShowContextMenuHandler(new ShowContextMenuHandler()
			{
				@Override
				public void onShowContextMenu(ShowContextMenuEvent event)
				{
					SelectItem selectitem = ((SelectItem)ScannerDirPanel.this.getItem("listSrcDir"));
					if(event.getX() > selectitem.getPageLeft() && event.getY() > selectitem.getPageTop())
					{
						Rectangle rect = selectitem.getPageRect();
						if(event.getX() < (rect.getLeft() + rect.getWidth()) && event.getY() < (rect.getTop() + rect.getHeight()))
							return;
					}
					event.cancel();
				}
			});
			addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAddDirectory.text")) {{
				setIcon("icons/folder_add.png");
				addClickHandler(event->addSrcDir());
			}});
			addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmDeleteSelected.text")) {{
				setIcon("icons/folder_delete.png");
				setEnableIfCondition(new MenuItemIfFunction()
				{
					@Override
					public boolean execute(Canvas target, Menu menu, MenuItem item)
					{
						if(((SelectItem)ScannerDirPanel.this.getItem("listSrcDir")).getValues().length>0)
							return true;
						return false;
					}
				});
				addClickHandler(event->delSrcDir());
			}});
		}});
	}
	
	private void addSrcDir()
	{
		new RemoteFileChooser("listSrcDir", (value)-> {
			SelectItem selectItem = (SelectItem)ScannerDirPanel.this.getItem("listSrcDir");
			String[] values = selectItem.getValueMapAsArray();
			List<String> lvalues = new ArrayList<>(Arrays.asList(values));
			lvalues.addAll(Arrays.asList(value));
			Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("src_dir", lvalues.stream().collect(Collectors.joining("|")))));
			selectItem.setValueMap(lvalues.toArray(new String[0]));
		});		
	}
	
	private void delSrcDir()
	{
		SelectItem selectItem = (SelectItem)ScannerDirPanel.this.getItem("listSrcDir");
		String[] values = selectItem.getValueMapAsArray();
		List<String> lvalues = new ArrayList<>(Arrays.asList(values));
		lvalues.removeAll(Arrays.asList(selectItem.getValues()));
		Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("src_dir", lvalues.stream().collect(Collectors.joining("|")))));
		selectItem.setValueMap(lvalues.toArray(new String[0]));
	}
	
	private void setPropertyItemValue(String field, String name, String value)
	{
		getItem(field).setValue(value);
		Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(name, value)));
	}

	void initPropertyItemValue(String field, String name, EnhJSO jso)
	{
		if(jso.exists(name))
		{
			FormItem  formItem =  getItem(field);
			if(jso.isBoolean(name))
			{
				if(formItem instanceof CheckboxItem)
				{
					CheckboxItem cbitem = (CheckboxItem)formItem;
					cbitem.setValue(jso.getBool(name));
					cbitem.fireEvent(new ChangedEvent(cbitem.getJsObj()){
						@Override
						public Object getValue() {
							return cbitem.getValue();
						}
					});
				}
			}
			else if(jso.isString(name))
			{
				if(formItem instanceof TextItem)
					formItem.setValue(jso.get(name));
				else if(formItem instanceof SelectItem)
				{
					SelectItem selitem = (SelectItem)formItem;
					if(selitem.isMultiple())
					{
						selitem.setValueMap();
						Optional.of(jso.getString(name, false)).ifPresent(strs->selitem.setValueMap(strs.split("\\|")));
					}
					else
						selitem.setValue(jso.get(name));
				}
			}
		}
	}
}
