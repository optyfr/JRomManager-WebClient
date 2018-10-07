package jrm.webui.client;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class MainWindow extends Window
{
	TabSet mainPane;
	ProfilePanel profilePanel;
	ScannerPanel scannerPanel;
	Label lblProfileinfo;
	
	public MainWindow()
	{
		super();
		setTitle("JRomManager Web Client");
		setWidth(800);
		setHeight(600);
		setAnimateMinimize(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowFooter(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		Map<String,Object> headerIconDefaults = new HashMap<>();
		headerIconDefaults.put("width", 16);
		headerIconDefaults.put("height", 16);
		headerIconDefaults.put("src", "rom.png");
		setHeaderIconDefaults(headerIconDefaults);
		setShowHeaderIcon(true);
		addCloseClickHandler(new CloseClickHandler()
		{
			@Override
			public void onCloseClick(CloseClickEvent event)
			{
				close();
			}
		});
		addItem(mainPane = new TabSet() {{
			setPaneMargin(0);
			setTabBarControls(
				TabBarControls.TAB_SCROLLER,
				TabBarControls.TAB_PICKER
			);
			addTab(new Tab() {{
				setIcon("icons/script.png");
				setTitle(Client.session.getMsg("MainFrame.Profiles"));
				setPane(profilePanel = new ProfilePanel());
			}});
			addTab(new Tab() {{
				setIcon("icons/drive_magnify.png");
				setTitle(Client.session.getMsg("MainFrame.Scanner"));
				setDisabled(true);
				setPane(scannerPanel = new ScannerPanel());
			}});
			addTab(new Tab() {{
				setIcon("icons/drive_go.png");
				setTitle(Client.session.getMsg("MainFrame.Dir2Dat"));
			}});
			addTab(new Tab() {{
				setIcon("icons/application_osx_terminal.png");
				setTitle(Client.session.getMsg("MainFrame.BatchTools"));
			}});
			addTab(new Tab() {{
				setIcon("icons/cog.png");
				setTitle(Client.session.getMsg("MainFrame.Settings"));
			}});
		}});
		setFooterControls(lblProfileinfo = new Label() {{setWidth100();}});
		centerInPage();
		show();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
