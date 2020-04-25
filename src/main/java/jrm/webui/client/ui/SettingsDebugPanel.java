package jrm.webui.client.ui;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Global;

final class SettingsDebugPanel extends SettingsForm
{
	public SettingsDebugPanel()
	{
		setWidth("80%");
		setLayoutAlign(Alignment.CENTER);
		setNumCols(3);
		setColWidths(150,"*",150);
		setItems(
			new SelectItem("cbDbgLevel") {{
				setColSpan(2);
				setTitle(Client.session.getMsg("MainFrame.lblLogLevel.text"));
				setValueMap("OFF","SEVERE","WARNING","INFO","CONFIG","FINE","FINER","FINEST","ALL");
				setWidth("*");
				addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)getValue()));
				setDefaultValue(Client.session.getSetting(fname2name.get(getName()), "INFO"));
			}},
			new TextItem("txtDbgMemory", Client.session.getMsg("MainFrame.lblMemory.text")) {{
				setWidth("*");
				setCanEdit(false);
				setShouldSaveValue(false);
			}},
			new ButtonItem() {{
				setTitle(Client.session.getMsg("MainFrame.btnGc.text"));
				setShouldSaveValue(false);
				setStartRow(false);
				setWidth(150);
				addClickHandler(e->Q_Global.GC.instantiate().send());
			}}
		);
		new Timer()
		{
			{
				Q_Global.GetMemory.instantiate().send();
			}

			@Override
			public void run()
			{
				Q_Global.GetMemory.instantiate().send();
			}
		}.scheduleRepeating(60000);
	}
}
