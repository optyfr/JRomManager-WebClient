package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.types.FetchMode;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;

import jrm.webui.client.Client;
import jrm.webui.client.datasources.DSReportTree;
import jrm.webui.client.protocol.Q_Report;

/**
 * Tree grid displaying scan report results as a hierarchical view of software/ROM sets, containers, and entries.
 * <p>
 * Each node is rendered with a status-specific icon (see {@link #getIcon(Record, boolean)}) and supports a
 * context menu ({@link ReportMenu}) for filtering the view, inspecting entry details, copying identifying
 * attributes (name, CRC, SHA-1), and searching the web for a selected entry.
 * </p>
 */
final class ReportTree extends TreeGrid /* NOSONAR: must extend Smart GWT TreeGrid (framework-imposed inheritance depth) */ {
    /** Active report filters keyed by filter name (e.g. {@link #SHOWOK}, {@link #HIDEMISSING}) with their current boolean state. */
    final HashMap<String, Boolean> filters = new HashMap<>();
    /** Record attribute holding the status of a subject set node. */
    private static final String STATUS = "status";
    /** Record attribute indicating whether a node is fixable. */
    private static final String IS_FIXABLE = "isFixable";
    /** Record attribute/field holding the display title of a node. */
    private static final String TITLE = "title";
    /** Custom data-source operation name used to fetch the detail of a selected record. */
    private static final String DETAIL = "detail";
    /** Record attribute holding the parent identifier of a node, used to distinguish child entries. */
    private static final String PARENT_ID = "ParentID";
    /** Prompt text shown when copying a value to the clipboard via a dialog. */
    private static final String SELECT_AND_COPY_THE_TEXT_BELOW = "Select and Copy the text below";
    /** Filter name controlling whether OK entries are shown. */
    private static final String SHOWOK = "SHOWOK";
    /** Filter name controlling whether fully missing entries are hidden. */
    private static final String HIDEMISSING = "HIDEMISSING";

    /**
     * Context menu for a {@link ReportTree} node, providing filtering toggles, detail inspection, attribute copying, and web search.
     */
    private final class ReportMenu extends Menu /* NOSONAR: must extend Smart GWT Menu (framework-imposed inheritance depth) */ { 
        /**
         * Builds the report context menu.
         *
         * @param src the report source identifier, or {@code null} for the default report
         */
        private ReportMenu(String src) {
            Dialog dialog = new Dialog();
            dialog.setWidth(350);
            final var search = new MenuItem("Search on the Web");
            search.addClickHandler(event -> fetchDetail(rec -> {
                final String name = rec.getAttribute("Name");
                final String crc = rec.getAttribute("CRC");
                final String sha1 = rec.getAttribute("SHA1");
                final String hash = Optional.ofNullable(Optional.ofNullable(crc).orElse(sha1)).map(h -> '+' + h).orElse("");
                com.google.gwt.user.client.Window.open("https://google.com/search?q=" + URL.encodeQueryString('"' + name + '"') + hash, "_blank", null);
            }));
            search.setEnableIfCondition((target, menu, item) -> isSelectedRecordChild());
            final var copyName = createCopyMenuItem("Copy Name", "Name", dialog);
            final var copySHA1 = createCopyMenuItem("Copy SHA1", "SHA1", dialog);
            final var copyCRC = createCopyMenuItem("Copy CRC", "CRC", dialog);
            final var detail = new MenuItem("Detail");
            detail.addClickHandler(event -> fetchDetail(rec -> SC.say("<pre>" + rec.getAttribute("Detail") + "</pre>")));
            detail.setEnableIfCondition((target, menu, item) -> isSelectedRecordChild());
            final var separator = new MenuItem();
            separator.setIsSeparator(true);
            final var hideMissing = new MenuItem();
            hideMissing.setTitle(Client.getSession().getMsg("ReportFrame.chckbxmntmHideFullyMissing.text"));
            hideMissing.addClickHandler(e -> Client.sendMsg(
                    JsonUtils.stringify(Q_Report.SetFilter.instantiate(src != null).setFilter(HIDEMISSING, !(filters.containsKey(HIDEMISSING) && filters.get(HIDEMISSING))))));
            hideMissing.setCheckIfCondition((target, menu, item) -> filters.containsKey(HIDEMISSING) && filters.get(HIDEMISSING));
            final var showOK = new MenuItem();
            showOK.setTitle(Client.getSession().getMsg("ReportFrame.chckbxmntmShowOkEntries.text"));
            showOK.addClickHandler(
                    e -> Client.sendMsg(JsonUtils.stringify(Q_Report.SetFilter.instantiate(src != null).setFilter(SHOWOK, !(filters.containsKey(SHOWOK) && filters.get(SHOWOK))))));
            showOK.setCheckIfCondition((target, menu, item) -> filters.containsKey(SHOWOK) && filters.get(SHOWOK));
            setItems(showOK, hideMissing, separator, detail, copyCRC, copySHA1, copyName, search);
        }

        /**
         * Creates a menu item that copies a given record attribute to the clipboard through a dialog.
         *
         * @param title the menu item title
         * @param attribute the record attribute to copy (e.g. {@code "Name"}, {@code "SHA1"}, {@code "CRC"})
         * @param dialog the dialog used to prompt the user for the copy
         * @return the configured menu item
         */
        private MenuItem createCopyMenuItem(String title, String attribute, Dialog dialog) {
            final var item = new MenuItem(title);
            item.addClickHandler(event -> fetchDetail(rec -> SC.askforValue("Copy", SELECT_AND_COPY_THE_TEXT_BELOW, rec.getAttribute(attribute), v -> {
            }, dialog)));
            item.setEnableIfCondition((target, menu, ctx) -> isSelectedRecordChild());
            return item;
        }

        /**
         * Determines whether the currently selected record is a child entry (i.e. has a non-zero parent identifier).
         *
         * @return {@code true} if the selected record is a child entry, {@code false} otherwise
         */
        private boolean isSelectedRecordChild() {
            return Optional.ofNullable(ReportTree.this.getSelectedRecord()).map(r -> r.getAttributeAsInt(PARENT_ID)).orElse(0) != 0;
        }

        /**
         * Fetches the detail of the currently selected record from the data source and forwards it to the given handler.
         *
         * @param handler the callback invoked with the first returned record
         */
        private void fetchDetail(Consumer<Record> handler) {
            ReportTree.this.getDataSource().performCustomOperation(DETAIL, ReportTree.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> {
                Record[] records = dsResponse.getData();
                if (records != null && records.length > 0)
                    handler.accept(records[0]);
            });
        }
    }

    /**
     * Constructs the report tree, configures its layout and data source, and attaches the context menu.
     *
     * @param src the report source identifier, or {@code null} for the default report
     * @param status the callback receiving report status information extracted from the loaded data
     */
    public ReportTree(final String src, ReportStatus status) {
        super();
        setWidth100();
        setHeight100();
        setAutoFetchData(true);
        setShowConnectors(true);
        setShowOpener(true);
        setShowOpenIcons(true);
        setShowCustomIconOpen(true);
        setDataFetchMode(FetchMode.PAGED);
        final var ds = DSReportTree.getInstance(src);
        ds.setCB(data -> status.setStatus(XMLTools.selectString(data, "/response/infos")));
        setDataSource(ds, new TreeGridField(TITLE));
        setContextMenu(new ReportMenu(src));
    }

    /**
     * Returns the icon path for a tree node based on its reported class.
     *
     * @param node the tree node
     * @param defaultState the default icon state flag
     * @return the icon path, or the result of {@code super.getIcon(node, defaultState)} for unknown classes
     */
    @Override
    protected String getIcon(Record node, boolean defaultState) {
        return switch (node.getAttribute("class")) {
            case "RomSuspiciousCRC" -> "/images/icons/information.png";
            case "ContainerUnknown", "ContainerUnneeded" -> "/images/icons/error.png";
            case "ContainerTZip" -> "/images/icons/compress.png";
            case "EntryOK" -> "/images/icons/bullet_green.png";
            case "EntryAdd" -> "/images/icons/bullet_blue.png";
            case "EntryMissingDuplicate" -> "/images/icons/bullet_purple.png";
            case "EntryMissing" -> "/images/icons/bullet_red.png";
            case "EntryUnneeded" -> "/images/icons/bullet_black.png";
            case "EntryWrongHash" -> "/images/icons/bullet_orange.png";
            case "EntryWrongName" -> "/images/icons/bullet_pink.png";
            case "SubjectSet" -> getSubjectSetIcon(node, defaultState);
            default -> super.getIcon(node, defaultState);
        };
    }

    /**
     * Resolves the icon for a {@code SubjectSet} node, distinguishing folder and leaf nodes and applying status-based color suffixes.
     *
     * @param node the tree node
     * @param defaultState the default icon state flag
     * @return the subject set icon path, or the result of {@code super.getIcon(node, defaultState)} when no specific icon applies
     */
    private String getSubjectSetIcon(Record node, boolean defaultState) {
        if (!Boolean.TRUE.equals(node.getAttributeAsBoolean("isFolder")))
            return "FOUND".equals(node.getAttribute(STATUS)) ? "/images/icons/bullet_green.png" : super.getIcon(node, defaultState);
        final String suffix = getSubjectSetFolderSuffix(node);
        if (suffix == null)
            return super.getIcon(node, defaultState);
        return "/images/folder" + (Boolean.TRUE.equals(node.getAttributeAsBoolean("isOpen")) ? "_open" : "_closed") + suffix + ".png";
    }

    /**
     * Computes the color suffix for a {@code SubjectSet} folder icon based on the node's status and fixability.
     *
     * @param node the tree node
     * @return the folder icon color suffix (e.g. {@code "_green"}, {@code "_red"}), or {@code null} when no specific suffix applies
     */
    private String getSubjectSetFolderSuffix(Record node) {
        return switch (node.getAttribute(STATUS)) {
            case "FOUND" -> {
                if (Boolean.TRUE.equals(node.getAttributeAsBoolean("hasNotes")))
                    yield Boolean.TRUE.equals(node.getAttributeAsBoolean(IS_FIXABLE)) ? "_purple" : "_orange";
                yield "_green";
            }
            case "CREATE", "CREATEFULL" -> Boolean.TRUE.equals(node.getAttributeAsBoolean(IS_FIXABLE)) ? "_blue" : "_orange"; //$NON-NLS-1$
            case "MISSING" -> "_red";
            case "UNNEEDED" -> "_gray";
            default -> null;
        };
    }

    /**
     * Records the current state of a report filter so that context-menu check states stay synchronized.
     *
     * @param name the filter name
     * @param value the filter value
     */
    void applyFilter(String name, Boolean value) {
        filters.put(name, value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}