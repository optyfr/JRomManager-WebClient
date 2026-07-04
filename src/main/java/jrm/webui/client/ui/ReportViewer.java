package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.Window;

import jrm.webui.client.Client;

/**
 * Full report viewer window for scanner results.
 *
 * @since 2.5
 */
public final class ReportViewer extends Window implements ReportStatus /* NOSONAR */ {
    /** The report tree displayed in this window. */
    private ReportTree tree;

    /**
     * Constructs the full report viewer window and shows the default report tree.
     */
    public ReportViewer() {
        super();
        Client.getChildWindows().add(this);
        setTitle(Client.getSession().getMsg("ReportFrame.title"));
        setWidth("60%");
        setHeight("80%");
        setAnimateMinimize(true);
        setAutoCenter(true);
        setCanDragReposition(true);
        setCanDragResize(true);
        setShowHeaderIcon(true);
        setShowMaximizeButton(true);
        final var map = new HashMap<String, Object>();
        map.put("width", 16);
        map.put("height", 16);
        map.put("src", "rom.png");
        setHeaderIconDefaults(map);
        setShowHeaderIcon(true);
        addCloseClickHandler(event -> ReportViewer.this.markForDestroy());
        tree = new ReportTree(null, this);
        addItem(tree);
        setShowFooter(true);
        setShowStatusBar(true);
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
