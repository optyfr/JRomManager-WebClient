package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

import jrm.webui.client.Client;

/**
 * Lightweight report viewer window for batch operations.
 *
 * @since 2.5
 */
public final class ReportLite extends Window implements ReportStatus /* NOSONAR */ {
    /** The report tree displayed in this window. */
    private ReportTree tree;

    /**
     * Constructs the lightweight report viewer window for the given report source.
     *
     * @param src the report source identifier
     */
    public ReportLite(String src) {
        super();
        Client.getChildWindows().add(this);
        setTitle(Client.getSession().getMsg("ReportFrame.Title") + " - " + src);
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
        final var map = new HashMap<String, Object>();
        map.put("width", 16);
        map.put("height", 16);
        map.put("src", "rom.png");
        setHeaderIconDefaults(map);
        setShowHeaderIcon(true);
        addCloseClickHandler(event -> ReportLite.this.markForDestroy());
        tree = new ReportTree(src, this);
        addItem(tree);
        final var hlayout = new HLayout();
        hlayout.addMember(new LayoutSpacer("*", 20));
        hlayout.addMember(new IButton("Close", e -> ReportLite.this.markForDestroy()));
        addItem(hlayout);
        show();
    }

    /**
     * Applies a report filter state to the underlying tree.
     *
     * @param name the filter name
     * @param value the filter value
     */
    void applyFilter(String name, Boolean value) {
        tree.applyFilter(name, value);
    }

    /** Reloads the report tree by invalidating its cache. */
    void reload() {
        tree.invalidateCache();
    }

    /**
     * Removes this window from the {@link Client} child-window list on destruction.
     */
    @Override
    protected void onDestroy() {
        Client.getChildWindows().remove(this);
        super.onDestroy();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
