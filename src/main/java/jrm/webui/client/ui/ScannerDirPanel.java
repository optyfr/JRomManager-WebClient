package jrm.webui.client.ui;

import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ShowContextMenuEvent;
import com.smartgwt.client.widgets.events.ShowContextMenuHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.*;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;

import jrm.webui.client.Client;

public final class ScannerDirPanel extends DynamicForm
{
	public ScannerDirPanel()
	{
		super();
		setWidth100();
		setHeight100();
		setCellPadding(1);
		setNumCols(4);
		setColWidths("200","*","22","20");
		setWrapItemTitles(false);
		setItems(
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
				addClickHandler(new ClickHandler()
				{
					@Override
					public void onClick(ClickEvent event)
					{
						new RemoteFileChooser("tfRomsDest");
					}
				});
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
			}},
			new CheckboxItem("tfDisksDestCbx") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(new ChangedHandler()
				{
					@Override
					public void onChanged(ChangedEvent event)
					{
						event.getForm().getField("tfDisksDestBtn").setDisabled(!(boolean)event.getValue());
						event.getForm().getField("tfDisksDest").setDisabled(!(boolean)event.getValue());
					}
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
			}},
			new CheckboxItem("tfSWDestCbx") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(new ChangedHandler()
				{
					@Override
					public void onChanged(ChangedEvent event)
					{
						event.getForm().getField("tfSWDestBtn").setDisabled(!(boolean)event.getValue());
						event.getForm().getField("tfSWDest").setDisabled(!(boolean)event.getValue());
					}
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
			}},
			new CheckboxItem("tfSWDisksDestCbx") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(new ChangedHandler()
				{
					@Override
					public void onChanged(ChangedEvent event)
					{
						event.getForm().getField("tfSWDisksDestBtn").setDisabled(!(boolean)event.getValue());
						event.getForm().getField("tfSWDisksDest").setDisabled(!(boolean)event.getValue());
					}
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
			}},
			new CheckboxItem("tfSamplesDestCbx") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(new ChangedHandler()
				{
					@Override
					public void onChanged(ChangedEvent event)
					{
						event.getForm().getField("tfSamplesDestBtn").setDisabled(!(boolean)event.getValue());
						event.getForm().getField("tfSamplesDest").setDisabled(!(boolean)event.getValue());
					}
				});
			}},
			new SelectItem("listSrcDir",Client.session.getMsg("MainFrame.lblSrcDir.text")) {{
				setWidth("*");
				setID("listSrcDir");
				setHeight(200);
				setEndRow(true);
				setColSpan(3);
				setMultiple(true);
				setMultipleAppearance(MultipleAppearance.GRID);
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
			}});
			addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmDeleteSelected.text")) {{
				setIcon("icons/folder_delete.png");
				setEnableIfCondition(new MenuItemIfFunction()
				{
					@Override
					public boolean execute(Canvas target, Menu menu, MenuItem item)
					{
						if(((SelectItem)ScannerDirPanel.this.getItem("listSrcDir")).getSelectedRecords().length>0)
							return true;
						return false;
					}
				});
			}});
			
		}});
	}
}
