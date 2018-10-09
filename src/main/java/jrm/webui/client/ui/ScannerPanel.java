package jrm.webui.client.ui;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

import jrm.webui.client.Client;

public class ScannerPanel extends VLayout
{
	public IButton btnFix;
	public IButton btnScan;

	public ScannerPanel()
	{
		super();
		setMembers(
			new ToolStrip() {{
				setWidth100();
				setAlign(Alignment.CENTER);
				setMembers(
					new IButton() {{
						setAutoFit(true);
						setTitle(Client.session.getMsg("MainFrame.btnInfo.text"));
						setIcon("icons/information.png");
					}},
					btnScan = new IButton() {{
						setAutoFit(true);
						setTitle(Client.session.getMsg("MainFrame.btnScan.text"));
						setIcon("icons/magnifier.png");
					}},
					new IButton() {{
						setAutoFit(true);
						setTitle(Client.session.getMsg("MainFrame.btnReport.text"));
						setIcon("icons/report.png");
					}},
					btnFix = new IButton() {{
						setAutoFit(true);
						setTitle(Client.session.getMsg("MainFrame.btnFix.text"));
						setIcon("icons/tick.png");
						setDisabled(true);
					}}
				);
			}},
			new TabSet() {{
				setTabs(
					new Tab() {{
						setTitle(Client.session.getMsg("MainFrame.scannerDirectories.title"));
						setPane(new ScannerDirPanel());
					}},
					new Tab() {{
						setTitle(Client.session.getMsg("MainFrame.scannerSettingsPanel.title"));
						setPane(new VLayout() {{
							setWidth100();
							setHeight100();
						}});
					}},
					new Tab() {{
						setTitle(Client.session.getMsg("MainFrame.Filters"));
						setPane(new VLayout() {{
							setWidth100();
							setHeight100();
						}});
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
