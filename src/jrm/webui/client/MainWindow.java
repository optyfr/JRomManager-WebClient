package jrm.webui.client;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class MainWindow extends Window
{
	public MainWindow()
	{
		super();
		setTitle("JRomManager");
		setWidth(640);
		setHeight(400);
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
		addItem(new TabSet() {{
			setTabBarControls(
				TabBarControls.TAB_SCROLLER,
				TabBarControls.TAB_PICKER
			);
			addTab(new Tab() {{
				setTitle("Profiles");
			}});
			addTab(new Tab() {{
				setTitle("Scanner");
			}});
			addTab(new Tab() {{
				setTitle("Dir2Dat");
			}});
			addTab(new Tab() {{
				setTitle("Batch Tools");
			}});
			addTab(new Tab() {{
				setTitle("Settings");
			}});
		}});
		centerInPage();
		show();
	}

}
