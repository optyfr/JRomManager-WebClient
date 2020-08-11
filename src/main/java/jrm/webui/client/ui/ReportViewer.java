package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.Window;

import jrm.webui.client.Client;

@SuppressWarnings("serial")
final public class ReportViewer extends Window implements ReportStatus
{
	private ReportTree tree;
	
	public ReportViewer()
	{
		super();
		Client.childWindows.add(this);
		setTitle(Client.session.getMsg("ReportFrame.title"));
		setWidth("60%");
		setHeight("80%");
		setAnimateMinimize(true);
		setAutoCenter(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		setHeaderIconDefaults(new HashMap<String,Object>() {{
			put("width", 16);
			put("height", 16);
			put("src", "rom.png");
		}});
		setShowHeaderIcon(true);
		addCloseClickHandler(event->ReportViewer.this.markForDestroy());
		addItem(tree = new ReportTree(null, this));
		setShowFooter(true);
		setShowStatusBar(true);
		show();
	}

	void applyFilter(String name, Boolean value)
	{
		tree.applyFilter(name, value);
	}
	
	void reload()
	{
		tree.invalidateCache();
	}
	
	@Override
	protected void onDestroy()
	{
		Client.childWindows.remove(this);
		super.onDestroy();
	}
	
}
