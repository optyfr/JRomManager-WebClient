package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.form.fields.SelectItem;

import jrm.webui.client.Client;
import jrm.webui.client.utils.EnhJSO;

public final class ScannerAutomationPanel extends SettingsForm
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
			new SelectItem("cbCompression", Client.getSession().getMsg("ScannerAutomationPanel.OnScanAction")) {{
				setValueMap(new HashMap<String, String>() {{
					put("SCAN", Client.getSession().getMsg("ScanAutomation.Scan"));
					put("SCAN_REPORT", Client.getSession().getMsg("ScanAutomation.ScanReport"));
					put("SCAN_REPORT_FIX", Client.getSession().getMsg("ScanAutomation.ScanReportFix"));
					put("SCAN_REPORT_FIX_SCAN", Client.getSession().getMsg("ScanAutomation.ScanReportFixScan"));
					put("SCAN_FIX", Client.getSession().getMsg("ScanAutomation.ScanFix"));
				}});
				setDefaultValue("SCAN");
				addChangedHandler(event->setPropertyItemValue(getName(), "automation.scan", getValue().toString()));
				setWidth("*");
			}}
		);
	}

}
