package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

import jrm.webui.client.Client;

public final class ReportLite extends Window implements ReportStatus
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
		final var map = new HashMap<String,Object>();
		map.put("width", 16);
		map.put("height", 16);
		map.put("src", "rom.png");
		setHeaderIconDefaults(map);
		setShowHeaderIcon(true);
		addCloseClickHandler(event->ReportLite.this.markForDestroy());
		tree = new ReportTree(src,this);
		addItem(tree);
		final var hlayout = new HLayout();
		hlayout.addMember(new LayoutSpacer("*",20));
		hlayout.addMember(new IButton("Close", e->ReportLite.this.markForDestroy()));
		addItem(hlayout);
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
	
	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}
