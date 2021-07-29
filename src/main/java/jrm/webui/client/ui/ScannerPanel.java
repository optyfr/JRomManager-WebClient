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

public final class ScannerPanel extends VLayout
{
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

	public ScannerPanel()
	{
		super();
		setMembers(
			new ToolStrip() {{
				setWidth100();
				setAlign(Alignment.CENTER);
				addButton(
					new ToolStripButton() {{
						setAutoFit(true);
						setTitle(Client.getSession().getMsg("MainFrame.btnInfo.text"));
						setIcon("icons/information.png");
						addClickHandler(event->{
							if(profileViewer==null || !Client.getChildWindows().contains(profileViewer))
								profileViewer = new ProfileViewer();
							else if(profileViewer.isVisible())
								profileViewer.bringToFront();
							else
								profileViewer.show();
						});
					}}
				);
				addSeparator();
				addButton(
					btnScan = new ToolStripButton() {{
						setAutoFit(true);
						setTitle(Client.getSession().getMsg("MainFrame.btnScan.text"));
						setIcon("icons/magnifier.png");
						addClickHandler(event->{
							Client.sendMsg(JsonUtils.stringify(Q_Profile.Scan.instantiate()));
						});
					}}
				);
				addButton(
					new ToolStripButton() {{
						setAutoFit(true);
						setTitle(Client.getSession().getMsg("MainFrame.btnReport.text"));
						setIcon("icons/report.png");
						addClickHandler(event->{
							if(reportViewer==null || !Client.getChildWindows().contains(reportViewer))
								reportViewer = new ReportViewer();
							else if(reportViewer.isVisible())
								reportViewer.bringToFront();
							else
								reportViewer.show();
						});
					}}
				);
				addButton(
					btnFix = new ToolStripButton() {{
						setAutoFit(true);
						setTitle(Client.getSession().getMsg("MainFrame.btnFix.text"));
						setIcon("icons/tick.png");
						setDisabled(true);
						addClickHandler(event->{
							Client.sendMsg(JsonUtils.stringify(Q_Profile.Fix.instantiate()));
						});
					}}
				);
				addSeparator();
				addButton(
					new ToolStripButton() {{
						setAutoFit(true);
						setTitle("Import Settings");
						setIcon("icons/table_refresh.png");
						addClickHandler(event -> {
							new RemoteFileChooser("importSettings", null, path -> Client.sendMsg(JsonUtils.stringify(Q_Profile.ImportSettings.instantiate().setPath(path[0].path))));
						});
					}}
				);
				addButton(
					new ToolStripButton() {{
						setAutoFit(true);
						setTitle("Export Settings");
						setIcon("icons/table_save.png");
						addClickHandler(event -> {
							SC.askforValue("Export Preset", "Choose a preset name", s -> Client.sendMsg(JsonUtils.stringify(Q_Profile.ExportSettings.instantiate().setPath("%presets/" + s))));
						});
					}}
				);
			}},
			new TabSet() {{
				setPaneMargin(0);
				setTabs(
					new Tab() {{
						setTitle(Client.getSession().getMsg("MainFrame.scannerDirectories.title"));
						setIcon("icons/folder.png");
						setPane(new HLayout() {{
							addMember(new LayoutSpacer("5%","*"));
							addMember(new VLayout() {{
								setHeight100();
								addMember(new LayoutSpacer("100%","*"));
								addMember(scannerDirPanel = new ScannerDirPanel());
								addMember(new LayoutSpacer("100%","*"));
							}});
							addMember(new LayoutSpacer("5%","*"));
						}});
					}},
					new Tab() {{
						setTitle(Client.getSession().getMsg("MainFrame.scannerSettingsPanel.title"));
						setIcon("icons/cog.png");
						setPane(new HLayout() {{
							addMember(new LayoutSpacer("5%","*"));
							addMember(new VLayout() {{
								setHeight100();
								addMember(new LayoutSpacer("100%","*"));
								addMember(scannerSettingsPanel = new ScannerSettingsPanel());
								addMember(new LayoutSpacer("100%","*"));
							}});
							addMember(new LayoutSpacer("5%","*"));
						}});
					}},
					new Tab() {{
						setTitle(Client.getSession().getMsg("MainFrame.Filters"));
						setIcon("icons/arrow_join.png");
						setPane(scannerFiltersPanel = new ScannerFiltersPanel());
					}},
					new Tab() {{
						setTitle(Client.getSession().getMsg("MainFrame.AdvFilters"));
						setIcon("icons/arrow_in.png");
						setPane(scannerAdvFiltersPanel = new ScannerAdvFiltersPanel());
					}},
					new Tab() {{
						setTitle(Client.getSession().getMsg("MainFrame.Automation"));
						setIcon("icons/link.png");
						setPane(new HLayout() {{
							addMember(new LayoutSpacer("25%","*"));
							addMember(new VLayout() {

							{
								setHeight100();
								addMember(new LayoutSpacer("100%","*"));
								addMember(scannerAutomationPanel = new ScannerAutomationPanel());
								addMember(new LayoutSpacer("100%","*"));
							}});
							addMember(new LayoutSpacer("25%","*"));
						}});
					}}
				);
			}},
			lblProfileinfo = new Label() {{
				setWidth100();
				setHeight(20);
				setBackgroundColor("ActiveCaption");
				setBorder("1px inset");
			}}
		);
	}
}
