package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import jrm.webui.client.Client;

/**
 * Lightweight report viewer window for batch operations.
 *
 * @since 2.5
 */
public final class ReportLite extends Window implements ReportStatus /* NOSONAR */ {
    /** The report tree displayed in this window. */
    private ReportTree tree;
    /** Copyable summary and missing/partial title list. */
    private TextAreaItem summaryItem;

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
        final var layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();
        layout.addMember(buildToolStrip());
        layout.addMember(buildSummaryForm());
        layout.addMember(tree);
        addItem(layout);
        final var hlayout = new HLayout();
        hlayout.addMember(new LayoutSpacer("*", 20));
        hlayout.addMember(new IButton("Close", e -> ReportLite.this.markForDestroy()));
        addItem(hlayout);
        show();
    }

    private ToolStrip buildToolStrip() {
        final ToolStrip strip = new ToolStrip();
        strip.setWidth100();
        final ToolStripButton copy = new ToolStripButton();
        copy.setAutoFit(true);
        copy.setTitle(Client.getSession().getMsg("Report.CopyReport"));
        copy.addClickHandler(event -> tree.copyReport());
        strip.addButton(copy);
        return strip;
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
