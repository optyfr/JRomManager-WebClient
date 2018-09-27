package jrm.webui.client;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.Toolbar;
import com.smartgwt.client.widgets.tree.TreeGrid;

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
				setPane(new VLayout() {{
					addMembers(
						new SplitPane() {{
							setNavigationPane(new TreeGrid());
							setListPane(new ListGrid() {{
								setShowFilterEditor(false);
							}});
						}},
						new ToolStrip() {{
							setAlign(Alignment.CENTER);
							addMembers(
								new IButton("Import Dat"),
								new IButton("Import Software List")
							);
						}}
					);
				}});
			}});
			addTab(new Tab() {{
				setTitle("Scanner");
				setDisabled(true);
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
		//centerInPage();
		show();
	}

}
