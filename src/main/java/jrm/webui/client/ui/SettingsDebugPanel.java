package jrm.webui.client.ui;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Global;

final class SettingsDebugPanel extends SettingsForm	//NOSONAR
{
	private static final class DebugTimer extends Timer
	{
		private DebugTimer() 
		{
			Q_Global.GetMemory.instantiate().send();
		}

		@Override
		public void run()
		{
			Q_Global.GetMemory.instantiate().send();
		}
	}

	public SettingsDebugPanel()
	{
		setWidth("80%");
		setLayoutAlign(Alignment.CENTER);
		setNumCols(3);
		setColWidths(150,"*",150);
		final var selectItem = new SelectItem("cbDbgLevel");
		selectItem.setColSpan(2);
		selectItem.setTitle(Client.getSession().getMsg("MainFrame.lblLogLevel.text"));
		selectItem.setValueMap("OFF","SEVERE","WARNING","INFO","CONFIG","FINE","FINER","FINEST","ALL");
		selectItem.setWidth("*");
		selectItem.addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)selectItem.getValue()));
		selectItem.setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "INFO"));
		final var textItem = new TextItem("txtDbgMemory", Client.getSession().getMsg("MainFrame.lblMemory.text"));
		textItem.setWidth("*");
		textItem.setCanEdit(false);
		textItem.setShouldSaveValue(false);
		final var buttonItem = new ButtonItem();
		buttonItem.setTitle(Client.getSession().getMsg("MainFrame.btnGc.text"));
		buttonItem.setShouldSaveValue(false);
		buttonItem.setStartRow(false);
		buttonItem.setWidth(150);
		buttonItem.addClickHandler(e->Q_Global.GC.instantiate().send());
		setItems(selectItem, textItem, buttonItem);
		new DebugTimer().scheduleRepeating(60000);
	}
}
