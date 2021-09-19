package jrm.webui.client.ui;

import java.util.LinkedHashMap;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

import jrm.webui.client.Client;

final class SettingsGenPanel extends SettingsForm
{
	public SettingsGenPanel()
	{
		setWidth("80%");
		setLayoutAlign(Alignment.CENTER);
		setNumCols(4);
		setColWidths(150,"*",20,20);
		setItems(
			new SelectItem("cbThreadCount") {{
				setTitle(Client.getSession().getMsg("SettingsGenPanel.lblThreading.text"));
				final var map = new LinkedHashMap<Integer, String>();
				map.put(-1, "Adaptive");
				map.put(0, "Max available");
				int maxthreadcount = Client.getSession().getSettingAsInteger("MaxThreadCount", 8);
				for (int i = 1; i <= maxthreadcount; i++)
					map.put(i, Integer.toString(i) + (i > 1 ? " threads" : " thread"));
				setValueMap(map);
				setWidth("*");
				setColSpan(3);
				addChangedHandler(event -> setGPropertyItemValue(getName(), fname2name.get(getName()), (Integer) getValue()));
				setDefaultValue(Client.getSession().getSettingAsInteger(fname2name.get(getName()), -1));
			}},
			new TextItem("tfBackupGDest",Client.getSession().getMsg("MainFrame.lblBackupDest.text")) {{
				setWidth("*");
				setCanEdit(false);
				setDisabled(true);
				setEndRow(false);
				setDisabled(!Client.getSession().getSettingAsBoolean(fname2name.get("tfBackupGDestEnabled"), false));
				setDefaultValue(Client.getSession().getSetting(fname2name.get("tfBackupGDest"), "%work/backup"));
			}},
			new ButtonItem("tfBackupGDestBtn") {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle(null);
				setDisabled(true);
				setValueIconRightPadding(0);
				setEndRow(false);
				addClickHandler(event->new RemoteFileChooser("tfBackupDest", null, records->setGPropertyItemValue("tfBackupGDest", "backup_dest_dir", records[0].path)));
				setDisabled(!Client.getSession().getSettingAsBoolean(fname2name.get("tfBackupGDestEnabled"), false));
			}},
			new CheckboxItem("tfBackupGDestEnabled") {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
				addChangedHandler(event->{
					boolean selected = (boolean)event.getValue();
					SettingsGenPanel.this.getField("tfBackupGDestBtn").setDisabled(!selected);
					SettingsGenPanel.this.getField("tfBackupGDest").setDisabled(!selected);
					setGPropertyItemValue(getName(), fname2name.get(getName()), selected);
				});
				setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get("tfBackupGDestEnabled"), false));
			}}
		);
	}
}
