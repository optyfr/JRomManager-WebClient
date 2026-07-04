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

/**
 * Main scanner panel containing toolbar, tabs, and profile info.
 *
 * @since 2.5
 */
public final class ScannerPanel extends VLayout /* NOSONAR */ {
	/** Icon for the "info" (profile viewer) button. */
	private static final String ICON_INFORMATION = "icons/information.png";
	/** Icon for the "scan" button. */
	private static final String ICON_MAGNIFIER = "icons/magnifier.png";
	/** Icon for the "report" button. */
	private static final String ICON_REPORT = "icons/report.png";
	/** Icon for the "fix" button. */
	private static final String ICON_TICK = "icons/tick.png";
	/** Icon for the "import settings" button. */
	private static final String ICON_TABLE_REFRESH = "icons/table_refresh.png";
	/** Icon for the "export settings" button. */
	private static final String ICON_TABLE_SAVE = "icons/table_save.png";
	/** Icon for the directories tab. */
	private static final String ICON_FOLDER = "icons/folder.png";
	/** Icon for the settings tab. */
	private static final String ICON_COG = "icons/cog.png";
	/** Icon for the filters tab. */
	private static final String ICON_ARROW_JOIN = "icons/arrow_join.png";
	/** Icon for the advanced filters tab. */
	private static final String ICON_ARROW_IN = "icons/arrow_in.png";
	/** Icon for the automation tab. */
	private static final String ICON_LINK = "icons/link.png";

	/** Toolbar button triggering a fix operation; disabled until a scan has been run. */
	public ToolStripButton btnFix;
	/** Toolbar button triggering a scan operation. */
	public ToolStripButton btnScan;
	/** Label displaying the current profile information at the bottom of the panel. */
	Label lblProfileinfo;
	/** The scanner directory configuration panel. */
	ScannerDirPanel scannerDirPanel;
	/** The scanner settings panel. */
	ScannerSettingsPanel scannerSettingsPanel;
	/** The scanner filters panel. */
	ScannerFiltersPanel scannerFiltersPanel;
	/** The scanner advanced filters panel. */
	ScannerAdvFiltersPanel scannerAdvFiltersPanel;
	/** The scanner automation panel. */
	ScannerAutomationPanel scannerAutomationPanel;
	/** The profile viewer window, lazily created. */
	ProfileViewer profileViewer;
	/** The report viewer window, lazily created. */
	ReportViewer reportViewer;

	/**
	 * Constructs the scanner panel with its toolbar, tab set, and profile info label.
	 */
	public ScannerPanel() {
		super();
		setMembers(buildToolStrip(), buildTabSet(), buildProfileInfoLabel());
	}

	/**
	 * Builds the toolbar with info, scan, report, fix, import, and export buttons.
	 *
	 * @return the configured toolbar
	 */
	private ToolStrip buildToolStrip() {
		var strip = new ToolStrip();
		strip.setWidth100();
		strip.setAlign(Alignment.CENTER);
		strip.addButton(buildInfoButton());
		strip.addSeparator();
		btnScan = buildScanButton();
		strip.addButton(btnScan);
		strip.addButton(buildReportButton());
		btnFix = buildFixButton();
		strip.addButton(btnFix);
		strip.addSeparator();
		strip.addButton(buildImportSettingsButton());
		strip.addButton(buildExportSettingsButton());
		return strip;
	}

	/**
	 * Builds the "info" button that opens the profile viewer.
	 *
	 * @return the configured toolbar button
	 */
	private ToolStripButton buildInfoButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle(Client.getSession().getMsg("MainFrame.btnInfo.text"));
		btn.setIcon(ICON_INFORMATION);
		btn.addClickHandler(e -> showProfileViewer());
		return btn;
	}

	/**
	 * Builds the "scan" button that sends a scan request to the server.
	 *
	 * @return the configured toolbar button
	 */
	private ToolStripButton buildScanButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle(Client.getSession().getMsg("MainFrame.btnScan.text"));
		btn.setIcon(ICON_MAGNIFIER);
		btn.addClickHandler(e -> Client.sendMsg(JsonUtils.stringify(Q_Profile.Scan.instantiate())));
		return btn;
	}

	/**
	 * Builds the "report" button that opens the report viewer.
	 *
	 * @return the configured toolbar button
	 */
	private ToolStripButton buildReportButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle(Client.getSession().getMsg("MainFrame.btnReport.text"));
		btn.setIcon(ICON_REPORT);
		btn.addClickHandler(e -> showReportViewer());
		return btn;
	}

	/**
	 * Builds the "fix" button that sends a fix request to the server; initially disabled.
	 *
	 * @return the configured toolbar button
	 */
	private ToolStripButton buildFixButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle(Client.getSession().getMsg("MainFrame.btnFix.text"));
		btn.setIcon(ICON_TICK);
		btn.setDisabled(true);
		btn.addClickHandler(e -> Client.sendMsg(JsonUtils.stringify(Q_Profile.Fix.instantiate())));
		return btn;
	}

	/**
	 * Builds the "Import Settings" button that opens a remote file chooser to import a settings preset.
	 *
	 * @return the configured toolbar button
	 */
	private ToolStripButton buildImportSettingsButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle("Import Settings");
		btn.setIcon(ICON_TABLE_REFRESH);
		btn.addClickHandler(e -> new RemoteFileChooser("importSettings", null,
				path -> Client.sendMsg(JsonUtils.stringify(Q_Profile.ImportSettings.instantiate().setPath(path[0].path)))));
		return btn;
	}

	/**
	 * Builds the "Export Settings" button that prompts for a preset name and exports the current settings.
	 *
	 * @return the configured toolbar button
	 */
	private ToolStripButton buildExportSettingsButton() {
		var btn = new ToolStripButton();
		btn.setAutoFit(true);
		btn.setTitle("Export Settings");
		btn.setIcon(ICON_TABLE_SAVE);
		btn.addClickHandler(e -> SC.askforValue("Export Preset", "Choose a preset name",
				s -> Client.sendMsg(JsonUtils.stringify(Q_Profile.ExportSettings.instantiate().setPath("%presets/" + s)))));
		return btn;
	}

	/**
	 * Shows the profile viewer, creating it if necessary, or bringing it to front if already visible.
	 */
	private void showProfileViewer() {
		if (profileViewer == null || !Client.getChildWindows().contains(profileViewer))
			profileViewer = new ProfileViewer();
		else if (profileViewer.isVisible())
			profileViewer.bringToFront();
		else
			profileViewer.show();
	}

	/**
	 * Shows the report viewer, creating it if necessary, or bringing it to front if already visible.
	 */
	private void showReportViewer() {
		if (reportViewer == null || !Client.getChildWindows().contains(reportViewer))
			reportViewer = new ReportViewer();
		else if (reportViewer.isVisible())
			reportViewer.bringToFront();
		else
			reportViewer.show();
	}

	/**
	 * Builds a horizontally centered panel wrapping the given content with left and right spacers.
	 *
	 * @param content the content to center
	 * @param leftSpacer the left spacer width
	 * @param rightSpacer the right spacer width
	 * @return the configured horizontal layout
	 */
	private HLayout buildCenteredPanel(VLayout content, String leftSpacer, String rightSpacer) {
		var layout = new HLayout();
		layout.addMember(new LayoutSpacer(leftSpacer, "*"));
		layout.addMember(content);
		layout.addMember(new LayoutSpacer(rightSpacer, "*"));
		return layout;
	}

	/**
	 * Builds a vertically centered container with a top spacer taking the remaining height.
	 *
	 * @return the configured vertical layout
	 */
	private VLayout buildVerticalCenteredPanel() {
		var layout = new VLayout();
		layout.setHeight100();
		layout.addMember(new LayoutSpacer("100%", "*"));
		return layout;
	}

	/**
	 * Builds the tab set containing the directories, settings, filters, advanced filters, and automation tabs.
	 *
	 * @return the configured tab set
	 */
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

	/**
	 * Builds the directories tab containing the {@link ScannerDirPanel}.
	 *
	 * @return the configured tab
	 */
	private Tab buildDirectoriesTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.scannerDirectories.title"));
		tab.setIcon(ICON_FOLDER);
		var content = buildVerticalCenteredPanel();
		scannerDirPanel = new ScannerDirPanel();
		content.addMember(scannerDirPanel);
		content.addMember(new LayoutSpacer("100%", "*"));
		tab.setPane(buildCenteredPanel(content, "5%", "5%"));
		return tab;
	}

	/**
	 * Builds the settings tab containing the {@link ScannerSettingsPanel}.
	 *
	 * @return the configured tab
	 */
	private Tab buildSettingsTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.scannerSettingsPanel.title"));
		tab.setIcon(ICON_COG);
		var content = buildVerticalCenteredPanel();
		scannerSettingsPanel = new ScannerSettingsPanel();
		content.addMember(scannerSettingsPanel);
		content.addMember(new LayoutSpacer("100%", "*"));
		tab.setPane(buildCenteredPanel(content, "5%", "5%"));
		return tab;
	}

	/**
	 * Builds the filters tab containing the {@link ScannerFiltersPanel}.
	 *
	 * @return the configured tab
	 */
	private Tab buildFiltersTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.Filters"));
		tab.setIcon(ICON_ARROW_JOIN);
		scannerFiltersPanel = new ScannerFiltersPanel();
		tab.setPane(scannerFiltersPanel);
		return tab;
	}

	/**
	 * Builds the advanced filters tab containing the {@link ScannerAdvFiltersPanel}.
	 *
	 * @return the configured tab
	 */
	private Tab buildAdvFiltersTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.AdvFilters"));
		tab.setIcon(ICON_ARROW_IN);
		scannerAdvFiltersPanel = new ScannerAdvFiltersPanel();
		tab.setPane(scannerAdvFiltersPanel);
		return tab;
	}

	/**
	 * Builds the automation tab containing the {@link ScannerAutomationPanel}.
	 *
	 * @return the configured tab
	 */
	private Tab buildAutomationTab() {
		var tab = new Tab();
		tab.setTitle(Client.getSession().getMsg("MainFrame.Automation"));
		tab.setIcon(ICON_LINK);
		var content = buildVerticalCenteredPanel();
		scannerAutomationPanel = new ScannerAutomationPanel();
		content.addMember(scannerAutomationPanel);
		content.addMember(new LayoutSpacer("100%", "*"));
		tab.setPane(buildCenteredPanel(content, "25%", "25%"));
		return tab;
	}

	/**
	 * Builds the profile information label displayed at the bottom of the panel.
	 *
	 * @return the configured label
	 */
	private Label buildProfileInfoLabel() {
		lblProfileinfo = new Label();
		lblProfileinfo.setWidth100();
		lblProfileinfo.setHeight(20);
		lblProfileinfo.setBackgroundColor("#DDDDDD");
		lblProfileinfo.setBorder("1px inset");
		return lblProfileinfo;
	}
}
