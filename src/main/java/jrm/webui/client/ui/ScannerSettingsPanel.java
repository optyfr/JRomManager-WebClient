package jrm.webui.client.ui;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;

import jrm.webui.client.Client;

public final class ScannerSettingsPanel extends DynamicForm
{
	public ScannerSettingsPanel()
	{
		super();
		setWidth100();
		setHeight100();
		setNumCols(4);
		setColWidths("20","*","20","*");
		setItems(
			new CheckboxItem("chckbxNeedSHA1", Client.session.getMsg("MainFrame.chckbxNeedSHA1.text")),
			new CheckboxItem("chckbxCreateMissingSets", Client.session.getMsg("MainFrame.chckbxCreateMissingSets.text")),
			new CheckboxItem("chckbxUseParallelism", Client.session.getMsg("MainFrame.chckbxUseParallelism.text")),
			new CheckboxItem("chckbxCreateOnlyComplete", Client.session.getMsg("MainFrame.chckbxCreateOnlyComplete.text"))
		);
	}
}
