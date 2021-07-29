package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

import jrm.webui.client.Client;

@SuppressWarnings("serial")
final public class ReportLite extends Window implements ReportStatus
{
	private ReportTree tree;
	
	public ReportLite(String src)
	{
		super();
		Client.getChildWindows().add(this);
		setTitle(Client.getSession().getMsg("ReportFrame.Title")+" - "+src);
		setWidth("60%");
		setHeight("80%");
		setAnimateMinimize(true);
		setIsModal(true);
		setShowModalMask(true);
		setAutoCenter(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		setShowStatusBar(true);
		setShowFooter(true);
		setHeaderIconDefaults(new HashMap<String,Object>() {{
			put("width", 16);
			put("height", 16);
			put("src", "rom.png");
		}});
		setShowHeaderIcon(true);
		addCloseClickHandler(event->ReportLite.this.markForDestroy());
		addItem(tree = new ReportTree(src,this));
		addItem(new HLayout() {{
			addMember(new LayoutSpacer("*",20));
			addMember(new IButton("Close", e->ReportLite.this.markForDestroy()));
		}});
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
		Client.getChildWindows().remove(this);
		super.onDestroy();
	}
	
}
