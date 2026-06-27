package jrm.webui.client.ui;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;

public final class ScannerPanel extends VLayout {
	private static final String ICON_INFORMATION = "icons/information.png";
	private static final String ICON_MAGNIFIER = "icons/magnifier.png";
	private static final String ICON_REPORT = "icons/report.png";
	private static final String ICON_TICK = "icons/tick.png";
	private static final String ICON_TABLE_REFRESH = "icons/table_refresh.png";
	private static final String ICON_TABLE_SAVE = "icons/table_save.png";
	private static final String ICON_FOLDER = "icons/folder.png";
	private static final String ICON_COG = "icons/cog.png";
	private static final String ICON_ARROW_JOIN = "icons/arrow_join.png";
	private static final String ICON_ARROW_IN = "icons/arrow_in.png";
	private static final String ICON_LINK = "icons/link.png";

	public ToolStripButton btnFix;
	public ToolStripButton btnScan;
	Label lblProfileinfo;
	ScannerDirPanel scannerDirPanel;
	ScannerSettingsPanel scannerSettingsPanel;
	ScannerFiltersPanel scannerFiltersPanel;
	ScannerAdvFiltersPanel scannerAdvFiltersPanel;
	ScannerAutomationPanel scannerAutomationPanel;
	ProfileViewer profileViewer;
	ReportViewer reportViewer;

	public ScannerPanel() {
		super();
		setMembers(buildToolStrip(), buildTabSet(), buildProfileInfoLabel());
	}

	private ToolStrip buildToolStrip() {
		var strip = new ToolStrip();
		strip.setWidth100();
		strip.setAlign(Alignment.CENTER);
		strip.addButton(buildInfoButton());
		strip.addSeparator();
		strip.addButton(btnScan = buildScanButton());
		strip.addButton(buildReportButton());
		strip.addButton(btnFix = buildFixButton());
		strip.addSeparator();
		strip.addButton(buildImportSettingsButton());
		strip.addButton(buildExportSettingsButton());
		return strip;
	}

	private ToolStripButton buildInfoButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle(Client.getSession().getMsg("MainFrame.btnInfo.text"));
		btn.setIcon(ICON_INFORMATION);
		btn.addClickHandler(e -> showProfileViewer());
		return btn;
	}

	private ToolStripButton buildScanButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle(Client.getSession().getMsg("MainFrame.btnScan.text"));
		btn.setIcon(ICON_MAGNIFIER);
		btn.addClickHandler(e -> Client.sendMsg(JsonUtils.stringify(Q_Profile.Scan.instantiate())));
		return btn;
	}

	private ToolStripButton buildReportButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle(Client.getSession().getMsg("MainFrame.btnReport.text"));
		btn.setIcon(ICON_REPORT);
		btn.addClickHandler(e -> showReportViewer());
		return btn;
	}

	private ToolStripButton buildFixButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle(Client.getSession().getMsg("MainFrame.btnFix.text"));
		btn.setIcon(ICON_TICK);
		btn.setDisabled(true);
		btn.addClickHandler(e -> Client.sendMsg(JsonUtils.stringify(Q_Profile.Fix.instantiate())));
		return btn;
	}

	private ToolStripButton buildImportSettingsButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle("Import Settings");
		btn.setIcon(ICON_TABLE_REFRESH);
		btn.addClickHandler(e -> new RemoteFileChooser("importSettings", null,
				path -> Client.sendMsg(JsonUtils.stringify(Q_Profile.ImportSettings.instantiate().setPath(path[0].path)))));
		return btn;
	}

	private ToolStripButton buildExportSettingsButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle("Export Settings");
		btn.setIcon(ICON_TABLE_SAVE);
		btn.addClickHandler(e -> SC.askforValue("Export Preset", "Choose a preset name",
				s -> Client.sendMsg(JsonUtils.stringify(Q_Profile.ExportSettings.instantiate().setPath("%presets/" + s)))));
		return btn;
	}

	private void showProfileViewer() {
		if (profileViewer == null || !Client.getChildWindows().contains(profileViewer))
			profileViewer = new ProfileViewer();
		else if (profileViewer.isVisible())
			profileViewer.bringToFront();
		else
			profileViewer.show();
	}

	private void showReportViewer() {
		if (reportViewer == null || !Client.getChildWindows().contains(reportViewer))
			reportViewer = new ReportViewer();
		else if (reportViewer.isVisible())
			reportViewer.bringToFront();
		else
			reportViewer.show();
	}

	private HLayout buildCenteredPanel(VLayout content, String leftSpacer, String rightSpacer) {
		var layout = new HLayout();
		layout.addMember(new LayoutSpacer(leftSpacer, "*"));
		layout.addMember(content);
		layout.addMember(new LayoutSpacer(rightSpacer, "*"));
		return layout;
	}

	private VLayout buildVerticalCenteredPanel() {
		var layout = new VLayout();
		layout.setHeight100();
		layout.addMember(new LayoutSpacer("100%", "*"));
		return layout;
	}

	private TabSet buildTabSet() {
		var tabSet = new TabSet();
		tabSet.setPaneMargin(0);
		tabSet.setTabs(
				buildDirectoriesTab(),
				buildSettingsTab(),
				buildFiltersTab(),
				buildAdvFiltersTab(),
				buildAutomationTab());
		return tabSet;
	}

	private Tab buildDirectoriesTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.scannerDirectories.title"));
		tab.setIcon(ICON_FOLDER);
		var content = buildVerticalCenteredPanel();
		content.addMember(scannerDirPanel = new ScannerDirPanel());
		content.addMember(new LayoutSpacer("100%", "*"));
		tab.setPane(buildCenteredPanel(content, "5%", "5%"));
		return tab;
	}

	private Tab buildSettingsTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.scannerSettingsPanel.title"));
		tab.setIcon(ICON_COG);
		var content = buildVerticalCenteredPanel();
		content.addMember(scannerSettingsPanel = new ScannerSettingsPanel());
		content.addMember(new LayoutSpacer("100%", "*"));
		tab.setPane(buildCenteredPanel(content, "5%", "5%"));
		return tab;
	}

	private Tab buildFiltersTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.Filters"));
		tab.setIcon(ICON_ARROW_JOIN);
		tab.setPane(scannerFiltersPanel = new ScannerFiltersPanel());
		return tab;
	}

	private Tab buildAdvFiltersTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.AdvFilters"));
		tab.setIcon(ICON_ARROW_IN);
		tab.setPane(scannerAdvFiltersPanel = new ScannerAdvFiltersPanel());
		return tab;
	}

	private Tab buildAutomationTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.Automation"));
		tab.setIcon(ICON_LINK);
		var content = buildVerticalCenteredPanel();
		content.addMember(scannerAutomationPanel = new ScannerAutomationPanel());
		content.addMember(new LayoutSpacer("100%", "*"));
		tab.setPane(buildCenteredPanel(content, "25%", "25%"));
		return tab;
	}

	private Label buildProfileInfoLabel() {
		lblProfileinfo = new Label();
		lblProfileinfo.setWidth100();
		lblProfileinfo.setHeight(20);
		lblProfileinfo.setBackgroundColor("#DDDDDD");
		lblProfileinfo.setBorder("1px inset");
		return lblProfileinfo;
	}
}
