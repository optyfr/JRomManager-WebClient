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
		setColWidths(150, "*", 20, 20);
		final var threadCount = new SelectItem("cbThreadCount");
		threadCount.setTitle(Client.getSession().getMsg("SettingsGenPanel.lblThreading.text"));
		final var map = new LinkedHashMap<Integer, String>();
		map.put(-1, "Adaptive");
		map.put(0, "Max available");
		int maxthreadcount = Client.getSession().getSettingAsInteger("MaxThreadCount", 8);
		for (int i = 1; i <= maxthreadcount; i++)
			map.put(i, Integer.toString(i) + (i > 1 ? " threads" : " thread"));
		threadCount.setValueMap(map);
		threadCount.setWidth("*");
		threadCount.setColSpan(3);
		threadCount.addChangedHandler(event -> setGPropertyItemValue("cbThreadCount", fname2name.get("cbThreadCount"), (Integer) threadCount.getValue()));
		threadCount.setDefaultValue(Client.getSession().getSettingAsInteger(fname2name.get("cbThreadCount"), -1));
		final var backupDest = new TextItem("tfBackupGDest", Client.getSession().getMsg("MainFrame.lblBackupDest.text"));
		backupDest.setWidth("*");
		backupDest.setCanEdit(false);
		backupDest.setDisabled(true);
		backupDest.setEndRow(false);
		backupDest.setDisabled(!Client.getSession().getSettingAsBoolean(fname2name.get("tfBackupGDestEnabled"), false));
		backupDest.setDefaultValue(Client.getSession().getSetting(fname2name.get("tfBackupGDest"), "%work/backup"));
		final var backupDestBtn = new ButtonItem("tfBackupGDestBtn");
		backupDestBtn.setStartRow(false);
		backupDestBtn.setIcon("icons/disk.png");
		backupDestBtn.setTitle(null);
		backupDestBtn.setDisabled(true);
		backupDestBtn.setValueIconRightPadding(0);
		backupDestBtn.setEndRow(false);
		backupDestBtn.addClickHandler(event->new RemoteFileChooser("tfBackupDest", null, records->setGPropertyItemValue("tfBackupGDest", "backup_dest_dir", records[0].path)));
		backupDestBtn.setDisabled(!Client.getSession().getSettingAsBoolean(fname2name.get("tfBackupGDestEnabled"), false));
		final var backupDestEnabled = new CheckboxItem("tfBackupGDestEnabled");
		backupDestEnabled.setStartRow(false);
		backupDestEnabled.setShowLabel(false);
		backupDestEnabled.setShowTitle(false);
		backupDestEnabled.addChangedHandler(event->{
			boolean selected = (boolean)event.getValue();
			backupDestBtn.setDisabled(!selected);
			backupDest.setDisabled(!selected);
			setGPropertyItemValue("tfBackupGDestEnabled", fname2name.get("tfBackupGDestEnabled"), selected);
		});
		backupDestEnabled.setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get("tfBackupGDestEnabled"), false));
		setItems(threadCount, backupDest, backupDestBtn, backupDestEnabled);
	}
}
