package jrm.webui.client.ui;

import java.util.LinkedHashMap;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.fields.SelectItem;

import jrm.webui.client.Client;

final class SettingsGenPanel extends SettingsForm
{
	public SettingsGenPanel()
	{
		setWidth("80%");
		setLayoutAlign(Alignment.CENTER);
		setNumCols(2);
		setColWidths(150,"*");
		setItems(
			new SelectItem("cbThreadCount") {{
				setTitle(Client.session.getMsg("SettingsGenPanel.lblThreading.text"));
				final var map = new LinkedHashMap<Integer, String>();
				map.put(-1, "Adaptive");
				map.put(0, "Max available");
				int maxthreadcount = Client.session.getSettingAsInteger("MaxThreadCount", 8);
				for (int i = 1; i <= maxthreadcount; i++)
					map.put(i, Integer.toString(i) + (i > 1 ? " threads" : " thread"));
				setValueMap(map);
				setWidth("*");
				addChangedHandler(event -> setGPropertyItemValue(getName(), fname2name.get(getName()), (Integer) getValue()));
				setDefaultValue(Client.session.getSettingAsInteger(fname2name.get(getName()), -1));
			}}
		);
	}
}
