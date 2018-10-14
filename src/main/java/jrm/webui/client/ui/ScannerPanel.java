package jrm.webui.client.ui;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import jrm.webui.client.Client;

public final class ScannerPanel extends VLayout
{
	public ToolStripButton btnFix;
	public ToolStripButton btnScan;
	ScannerDirPanel scannerDirPanel;
	ScannerSettingsPanel scannerSettingsPanel;
	ScannerFiltersPanel scannerFiltersPanel;

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
						setTitle(Client.session.getMsg("MainFrame.btnInfo.text"));
						setIcon("icons/information.png");
					}}
				);
				addButton(
					btnScan = new ToolStripButton() {{
						setAutoFit(true);
						setTitle(Client.session.getMsg("MainFrame.btnScan.text"));
						setIcon("icons/magnifier.png");
					}}
				);
				addButton(
					new ToolStripButton() {{
						setAutoFit(true);
						setTitle(Client.session.getMsg("MainFrame.btnReport.text"));
						setIcon("icons/report.png");
					}}
				);
				addButton(
					btnFix = new ToolStripButton() {{
						setAutoFit(true);
						setTitle(Client.session.getMsg("MainFrame.btnFix.text"));
						setIcon("icons/tick.png");
						setDisabled(true);
					}}
				);
			}},
			new TabSet() {{
				setPaneMargin(0);
				setTabs(
					new Tab() {{
						setTitle(Client.session.getMsg("MainFrame.scannerDirectories.title"));
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
						setTitle(Client.session.getMsg("MainFrame.scannerSettingsPanel.title"));
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
						setTitle(Client.session.getMsg("MainFrame.Filters"));
						setPane(scannerFiltersPanel = new ScannerFiltersPanel());
					}},
					new Tab() {{
						setTitle(Client.session.getMsg("MainFrame.AdvFilters"));
						setPane(new VLayout() {{
							setWidth100();
							setHeight100();
						}});
					}}
				);
			}}
		);
	}
}
