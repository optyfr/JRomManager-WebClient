package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.widgets.form.fields.SelectItem;

import jrm.webui.client.Client;
import jrm.webui.client.utils.EnhJSO;

/**
 * Panel for configuring scan automation actions.
 *
 * @since 2.5
 */
public final class ScannerAutomationPanel extends SettingsForm /* NOSONAR */ {

    /**
     * Constructs the scanner automation panel with no initial settings.
     */
    public ScannerAutomationPanel() {
        this(null);
    }

    /**
     * Constructs the scanner automation panel with the given initial settings.
     *
     * @param settings the initial settings to apply, or {@code null} for defaults
     */
    public ScannerAutomationPanel(EnhJSO settings) {
        super(settings);
        setWidth100();
        setNumCols(2);
        setColWidths("25%", "*");
        setWrapItemTitles(false);

        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("SCAN", Client.getSession().getMsg("ScanAutomation.Scan"));
        valueMap.put("SCAN_REPORT", Client.getSession().getMsg("ScanAutomation.ScanReport"));
        valueMap.put("SCAN_REPORT_FIX", Client.getSession().getMsg("ScanAutomation.ScanReportFix"));
        valueMap.put("SCAN_REPORT_FIX_SCAN", Client.getSession().getMsg("ScanAutomation.ScanReportFixScan"));
        valueMap.put("SCAN_FIX", Client.getSession().getMsg("ScanAutomation.ScanFix"));

        SelectItem scanAction = new SelectItem("cbCompression", Client.getSession().getMsg("ScannerAutomationPanel.OnScanAction"));
        scanAction.setValueMap(valueMap);
        scanAction.setDefaultValue("SCAN");
        scanAction.addChangedHandler(event -> setPropertyItemValue(scanAction.getName(), "automation.scan", scanAction.getValue().toString()));
        scanAction.setWidth("*");

        setItems(scanAction);
    }

}
