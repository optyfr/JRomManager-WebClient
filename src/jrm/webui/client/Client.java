package jrm.webui.client;

import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;

public class Client implements EntryPoint
{

	public Client()
	{
		super();
	}

	@Override
	public void onModuleLoad()
	{
		SC.logInfo("Hello");
		new Label() {{
			setWidth100();
	//		setAutoDraw(true);
			setAlign(Alignment.CENTER);
			setContents("<span style='font:bold 24px arial'>JRomManager</span>");
		}}.draw();
		Page.setTitle("JRomManager");
		new MainWindow();
	}

}
