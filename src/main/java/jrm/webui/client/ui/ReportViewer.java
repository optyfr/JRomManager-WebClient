package jrm.webui.client.ui;

import java.util.HashMap;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Report;

/**
 * Full report viewer window for scanner results.
 *
 * @since 2.5
 */
public final class ReportViewer extends Window implements ReportStatus /* NOSONAR */ {
    /** The report tree displayed in this window. */
    private ReportTree tree;
    /** Copyable summary and missing/partial title list. */
    private TextAreaItem summaryItem;

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
        final var layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.addMember(buildToolStrip());
        layout.addMember(buildSummaryForm());
        layout.addMember(tree);
        addItem(layout);
        setShowFooter(true);
        setShowStatusBar(true);
        show();
    }

    private ToolStrip buildToolStrip() {
        final ToolStrip strip = new ToolStrip();
        strip.setWidth100();
        strip.addButton(buildCopyButton());
        strip.addButton(buildFixDatButton());
        return strip;
    }

    private ToolStripButton buildCopyButton() {
        final ToolStripButton btn = new ToolStripButton();
        btn.setAutoFit(true);
        btn.setTitle(Client.getSession().getMsg("Report.CopyReport"));
        btn.addClickHandler(event -> tree.copyReport());
        return btn;
    }

    private ToolStripButton buildFixDatButton() {
        final ToolStripButton btn = new ToolStripButton();
        btn.setAutoFit(true);
        btn.setTitle(Client.getSession().getMsg("Report.CreateFixDat"));
        btn.addClickHandler(event -> new RemoteFileChooser("exportFixDat", null,
                records -> Client.sendMsg(JsonUtils.stringify(Q_Report.CreateFixDat.instantiate().setPath(records[0].path)))));
        return btn;
    }

    private DynamicForm buildSummaryForm() {
        final DynamicForm form = new DynamicForm();
        form.setWidth100();
        form.setHeight(140);
        form.setNumCols(1);
        form.setColWidths("*");
        summaryItem = new TextAreaItem("summary");
        summaryItem.setShowTitle(false);
        summaryItem.setWidth("*");
        summaryItem.setHeight("*");
        summaryItem.setCanEdit(false);
        form.setItems(summaryItem);
        return form;
    }

    @Override
    public void setSummary(String summary) {
        if (summaryItem != null)
            summaryItem.setValue(summary != null ? summary : "");
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
