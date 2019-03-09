package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.form.fields.SelectItem;

import jrm.webui.client.Client;
import jrm.webui.client.utils.EnhJSO;

public class ScannerAutomationPanel extends SettingsForm
{

	public ScannerAutomationPanel()
	{
		this(null);
	}

	@SuppressWarnings("serial")
	public ScannerAutomationPanel(EnhJSO settings)
	{
		super(settings);
		setWidth100();
		setNumCols(2);
		setColWidths("25%","*");
		setWrapItemTitles(false);
		setItems(
			new SelectItem("cbCompression", Client.session.getMsg("ScannerAutomationPanel.OnScanAction")) {{
				setValueMap(new HashMap<String, String>() {{
					put("SCAN", Client.session.getMsg("ScanAutomation.Scan"));
					put("SCAN_REPORT", Client.session.getMsg("ScanAutomation.ScanReport"));
					put("SCAN_REPORT_FIX", Client.session.getMsg("ScanAutomation.ScanReportFix"));
					put("SCAN_REPORT_FIX_SCAN", Client.session.getMsg("ScanAutomation.ScanReportFixScan"));
					put("SCAN_FIX", Client.session.getMsg("ScanAutomation.ScanFix"));
				}});
				setDefaultValue("SCAN");
				addChangedHandler(event->setPropertyItemValue(getName(), "automation.scan", getValue().toString()));
				setWidth("*");
			}}
		);
	}

}
