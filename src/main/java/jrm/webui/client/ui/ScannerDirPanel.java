package jrm.webui.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.core.Rectangle;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RowSpacerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.utils.EnhJSO;

/**
 * Dynamic form panel for the scanner directory configuration, exposing destination paths for ROMs, disks,
 * software, samples, and backups, as well as the list of source directories to scan.
 */
public final class ScannerDirPanel extends DynamicForm /* NOSONAR */ {
    /** Field name for the ROMs destination text item. */
    private static final String TF_ROMS_DEST = "tfRomsDest";
    /** Field name for the disks destination text item. */
    private static final String TF_DISKS_DEST = "tfDisksDest";
    /** Field name for the software ROMs destination text item. */
    private static final String TF_SW_DEST = "tfSWDest";
    /** Field name for the software disks destination text item. */
    private static final String TF_SW_DISKS_DEST = "tfSWDisksDest";
    /** Field name for the samples destination text item. */
    private static final String TF_SAMPLES_DEST = "tfSamplesDest";
    /** Field name for the backup destination text item. */
    private static final String TF_BACKUP_DEST = "tfBackupDest";
    /** Field name for the source directory select item. */
    private static final String LIST_SRC_DIR = "listSrcDir";
    /** Icon path for the destination directory chooser button. */
    private static final String DISK_ICON = "icons/disk.png";
    /** Property name for the ROMs destination directory. */
    private static final String ROMS_DEST_DIR = "roms_dest_dir";
    /** Property name for the source directory. */
    private static final String SRC_DIR = "src_dir";

    /**
     * Creates, configures, and returns an instance in a single expression, avoiding double-brace initialization.
     *
     * @param item        the instance to configure
     * @param initializer the configuration action applied to the instance
     * @param <T>         the instance type
     * @return the configured instance
     */
    private static <T> T init(T item, Consumer<T> initializer) {
        initializer.accept(item);
        return item;
    }

    /**
     * Constructs the scanner directory panel, building all destination text items, chooser buttons, enable
     * checkboxes, the source directory multi-select, and the context menu.
     */
    public ScannerDirPanel() {
        super();
        setWidth100();
        setCellPadding(1);
        setNumCols(4);
        setColWidths("200", "*", "22", "20");
        setWrapItemTitles(false);
        setItems(
                new RowSpacerItem(),
                init(new TextItem(TF_ROMS_DEST, Client.getSession().getMsg("MainFrame.lblRomsDest.text")), item -> {
                    item.setWidth("*");
                    item.setCanEdit(false);
                    item.setEndRow(false);
                }),
                init(new ButtonItem(), item -> {
                    item.setStartRow(false);
                    item.setIcon(DISK_ICON);
                    item.setTitle(null);
                    item.setValueIconRightPadding(0);
                    item.setEndRow(false);
                    item.addClickHandler(event -> new RemoteFileChooser(TF_ROMS_DEST, null, records -> setPropertyItemValue(TF_ROMS_DEST, ROMS_DEST_DIR, records[0].path)));
                }),
                new SpacerItem(),
                init(new TextItem(TF_DISKS_DEST, Client.getSession().getMsg("MainFrame.lblDisksDest.text")), item -> {
                    item.setWidth("*");
                    item.setCanEdit(false);
                    item.setDisabled(true);
                    item.setEndRow(false);
                }),
                init(new ButtonItem("tfDisksDestBtn"), item -> {
                    item.setStartRow(false);
                    item.setIcon(DISK_ICON);
                    item.setTitle(null);
                    item.setDisabled(true);
                    item.setValueIconRightPadding(0);
                    item.setEndRow(false);
                    item.addClickHandler(event -> new RemoteFileChooser(TF_DISKS_DEST, null, records -> setPropertyItemValue(TF_DISKS_DEST, "disks_dest_dir", records[0].path)));
                }),
                init(new CheckboxItem("tfDisksDestCbx"), item -> {
                    item.setStartRow(false);
                    item.setShowLabel(false);
                    item.setShowTitle(false);
                    item.addChangedHandler(event -> {
                        boolean selected = (boolean) event.getValue();
                        ScannerDirPanel.this.getField("tfDisksDestBtn").setDisabled(!selected);
                        ScannerDirPanel.this.getField(TF_DISKS_DEST).setDisabled(!selected);
                        Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("disks_dest_dir_enabled", selected)));
                    });
                }),
                init(new TextItem(TF_SW_DEST, Client.getSession().getMsg("MainFrame.chckbxSoftwareDest.text")), item -> {
                    item.setWidth("*");
                    item.setCanEdit(false);
                    item.setDisabled(true);
                    item.setEndRow(false);
                }),
                init(new ButtonItem("tfSWDestBtn"), item -> {
                    item.setStartRow(false);
                    item.setIcon(DISK_ICON);
                    item.setTitle(null);
                    item.setDisabled(true);
                    item.setValueIconRightPadding(0);
                    item.setEndRow(false);
                    item.addClickHandler(event -> new RemoteFileChooser(TF_SW_DEST, null, records -> setPropertyItemValue(TF_SW_DEST, "swroms_dest_dir", records[0].path)));
                }),
                init(new CheckboxItem("tfSWDestCbx"), item -> {
                    item.setStartRow(false);
                    item.setShowLabel(false);
                    item.setShowTitle(false);
                    item.addChangedHandler(event -> {
                        boolean selected = (boolean) event.getValue();
                        ScannerDirPanel.this.getField("tfSWDestBtn").setDisabled(!selected);
                        ScannerDirPanel.this.getField(TF_SW_DEST).setDisabled(!selected);
                        Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("swroms_dest_dir_enabled", selected)));
                    });
                }),
                init(new TextItem(TF_SW_DISKS_DEST, Client.getSession().getMsg("MainFrame.chckbxSwdisksdest.text")), item -> {
                    item.setWidth("*");
                    item.setCanEdit(false);
                    item.setDisabled(true);
                    item.setEndRow(false);
                }),
                init(new ButtonItem("tfSWDisksDestBtn"), item -> {
                    item.setStartRow(false);
                    item.setIcon(DISK_ICON);
                    item.setTitle(null);
                    item.setDisabled(true);
                    item.setValueIconRightPadding(0);
                    item.setEndRow(false);
                    item.addClickHandler(
                            event -> new RemoteFileChooser(TF_SW_DISKS_DEST, null, records -> setPropertyItemValue(TF_SW_DISKS_DEST, "swdisks_dest_dir", records[0].path)));
                }),
                init(new CheckboxItem("tfSWDisksDestCbx"), item -> {
                    item.setStartRow(false);
                    item.setShowLabel(false);
                    item.setShowTitle(false);
                    item.addChangedHandler(event -> {
                        boolean selected = (boolean) event.getValue();
                        ScannerDirPanel.this.getField("tfSWDisksDestBtn").setDisabled(!selected);
                        ScannerDirPanel.this.getField(TF_SW_DISKS_DEST).setDisabled(!selected);
                        Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("swdisks_dest_dir_enabled", selected)));
                    });
                }),
                init(new TextItem(TF_SAMPLES_DEST, Client.getSession().getMsg("MainFrame.lblSamplesDest.text")), item -> {
                    item.setWidth("*");
                    item.setCanEdit(false);
                    item.setDisabled(true);
                    item.setEndRow(false);
                }),
                init(new ButtonItem("tfSamplesDestBtn"), item -> {
                    item.setStartRow(false);
                    item.setIcon(DISK_ICON);
                    item.setTitle(null);
                    item.setDisabled(true);
                    item.setValueIconRightPadding(0);
                    item.setEndRow(false);
                    item.addClickHandler(
                            event -> new RemoteFileChooser(TF_SAMPLES_DEST, null, records -> setPropertyItemValue(TF_SAMPLES_DEST, "samples_dest_dir", records[0].path)));
                }),
                init(new CheckboxItem("tfSamplesDestCbx"), item -> {
                    item.setStartRow(false);
                    item.setShowLabel(false);
                    item.setShowTitle(false);
                    item.addChangedHandler(event -> {
                        boolean selected = (boolean) event.getValue();
                        ScannerDirPanel.this.getField("tfSamplesDestBtn").setDisabled(!selected);
                        ScannerDirPanel.this.getField(TF_SAMPLES_DEST).setDisabled(!selected);
                        Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("samples_dest_dir_enabled", selected)));
                    });
                }),
                init(new TextItem(TF_BACKUP_DEST, Client.getSession().getMsg("MainFrame.lblBackupDest.text")), item -> {
                    item.setWidth("*");
                    item.setCanEdit(false);
                    item.setDisabled(true);
                    item.setEndRow(false);
                }),
                init(new ButtonItem("tfBackupDestBtn"), item -> {
                    item.setStartRow(false);
                    item.setIcon(DISK_ICON);
                    item.setTitle(null);
                    item.setDisabled(true);
                    item.setValueIconRightPadding(0);
                    item.setEndRow(false);
                    item.addClickHandler(event -> new RemoteFileChooser(TF_BACKUP_DEST, null, records -> setPropertyItemValue(TF_BACKUP_DEST, "backup_dest_dir", records[0].path)));
                }),
                init(new CheckboxItem("tfBackupDestCbx"), item -> {
                    item.setStartRow(false);
                    item.setShowLabel(false);
                    item.setShowTitle(false);
                    item.addChangedHandler(event -> {
                        boolean selected = (boolean) event.getValue();
                        ScannerDirPanel.this.getField("tfBackupDestBtn").setDisabled(!selected);
                        ScannerDirPanel.this.getField(TF_BACKUP_DEST).setDisabled(!selected);
                        Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty("backup_dest_dir_enabled", selected)));
                    });
                }),
                init(new SelectItem(LIST_SRC_DIR, Client.getSession().getMsg("MainFrame.lblSrcDir.text")), item -> {
                    item.setWidth("*");
                    item.setID(LIST_SRC_DIR);
                    item.setHeight(200);
                    item.setEndRow(false);
                    item.setColSpan(2);
                    item.setRowSpan(3);
                    item.setMultiple(true);
                    item.setValueMap();
                    item.setMultipleAppearance(MultipleAppearance.GRID);
                    item.addChangedHandler(event -> event.getForm().getItem("delSrcDirBtn").setDisabled(item.getValues().length == 0));
                }),
                init(new ButtonItem(), item -> {
                    item.setStartRow(false);
                    item.setEndRow(true);
                    item.setHeight(20);
                    item.setVAlign(VerticalAlignment.TOP);
                    item.setIcon("icons/folder_add.png");
                    item.setPrompt(Client.getSession().getMsg("MainFrame.mntmAddDirectory.text"));
                    item.setTitle(null);
                    item.addClickHandler(event -> addSrcDir());
                }),
                init(new ButtonItem("delSrcDirBtn"), item -> {
                    item.setStartRow(false);
                    item.setEndRow(true);
                    item.setHeight(20);
                    item.setVAlign(VerticalAlignment.TOP);
                    item.setIcon("icons/folder_delete.png");
                    item.setPrompt(Client.getSession().getMsg("MainFrame.mntmDeleteSelected.text"));
                    item.setTitle(null);
                    item.addClickHandler(event -> delSrcDir());
                    item.setDisabled(true);
                    item.setShouldSaveValue(true);
                }),
                init(new SpacerItem(), item -> item.setHeight(160)));
        setContextMenu(init(new Menu(), ctxMenu -> {
            ctxMenu.addShowContextMenuHandler(event -> {
                SelectItem selectitem = ((SelectItem) ScannerDirPanel.this.getItem(LIST_SRC_DIR));
                if (event.getX() > selectitem.getPageLeft() && event.getY() > selectitem.getPageTop()) {
                    Rectangle rect = selectitem.getPageRect();
                    if (event.getX() < (rect.getLeft() + rect.getWidth()) && event.getY() < (rect.getTop() + rect.getHeight()))
                        return;
                }
                event.cancel();
            });
            ctxMenu.addItem(init(new MenuItem(Client.getSession().getMsg("MainFrame.mntmAddDirectory.text")), mi -> {
                mi.setIcon("icons/folder_add.png");
                mi.addClickHandler(event -> addSrcDir());
            }));
            ctxMenu.addItem(init(new MenuItem(Client.getSession().getMsg("MainFrame.mntmDeleteSelected.text")), mi -> {
                mi.setIcon("icons/folder_delete.png");
                mi.setEnableIfCondition((Canvas target, Menu menu, MenuItem item) -> ((SelectItem) ScannerDirPanel.this.getItem(LIST_SRC_DIR)).getValues().length > 0);
                mi.addClickHandler(event -> delSrcDir());
            }));
        }));
    }

    /**
     * Opens a remote file chooser and appends the selected path to the source directory list, then notifies
     * the server of the updated list.
     */
    private void addSrcDir() {
        new RemoteFileChooser(LIST_SRC_DIR, null, path -> {
            SelectItem selectItem = (SelectItem) ScannerDirPanel.this.getItem(LIST_SRC_DIR);
            String[] values = selectItem.getValueMapAsArray();
            List<String> lvalues = new ArrayList<>(Arrays.asList(values));
            lvalues.addAll(Stream.of(path).map(p -> p.path).toList());
            Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(SRC_DIR, lvalues.stream().collect(Collectors.joining("|")))));
            selectItem.setValueMap(lvalues.toArray(new String[0]));
        });
    }

    /**
     * Removes the currently selected entries from the source directory list and notifies the server of the
     * updated list.
     */
    private void delSrcDir() {
        SelectItem selectItem = (SelectItem) ScannerDirPanel.this.getItem(LIST_SRC_DIR);
        String[] values = selectItem.getValueMapAsArray();
        List<String> lvalues = new ArrayList<>(Arrays.asList(values));
        lvalues.removeAll(Arrays.asList(selectItem.getValues()));
        Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(SRC_DIR, lvalues.stream().collect(Collectors.joining("|")))));
        selectItem.setValueMap(lvalues.toArray(new String[0]));
    }

    /**
     * Sets the value of a form item and sends the corresponding property update to the server.
     *
     * @param field the form item field name
     * @param name  the server-side property name
     * @param value the value to set
     */
    private void setPropertyItemValue(String field, String name, String value) {
        getItem(field).setValue(value);
        Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(name, value)));
    }

    /**
     * Initializes a single form item from a JSON settings object, dispatching on the value type (boolean or
     * string) and the form item kind (checkbox, text, or select).
     *
     * @param field the form item field name
     * @param name  the property name to read from the settings object
     * @param jso   the JSON settings object
     */
    void initPropertyItemValue(String field, String name, EnhJSO jso) {
        if (!jso.exists(name))
            return;
        FormItem formItem = getItem(field);
        if (jso.isBoolean(name)) {
            if (formItem instanceof CheckboxItem cbitem) {
                cbitem.setValue(jso.getBool(name));
                cbitem.fireEvent(new ChangedEvent(cbitem.getJsObj()) {
                    @Override
                    public Object getValue() {
                        return cbitem.getValue();
                    }
                });
            }
        } else if (jso.isString(name)) {
            if (formItem instanceof TextItem)
                formItem.setValue(jso.get(name));
            else if (formItem instanceof SelectItem selitem) {
                if (Boolean.TRUE.equals(selitem.isMultiple())) {
                    selitem.setValueMap();
                    Optional.of(jso.getString(name, false)).ifPresent(strs -> selitem.setValueMap(strs.split("\\|")));
                } else {
                    selitem.setValue(jso.get(name));
                }
            }
        }
    }

    /**
     * Initializes all destination and source directory form items from the given JSON settings object.
     *
     * @param settings the JSON settings object containing all scanner directory properties
     */
    void initPropertyItemValues(EnhJSO settings) {
        getItem(TF_ROMS_DEST).setValue(settings.get(ROMS_DEST_DIR));
        initPropertyItemValue(TF_ROMS_DEST, ROMS_DEST_DIR, settings);
        initPropertyItemValue("tfDisksDestCbx", "disks_dest_dir_enabled", settings);
        initPropertyItemValue(TF_DISKS_DEST, "disks_dest_dir", settings);
        initPropertyItemValue("tfSWDestCbx", "swroms_dest_dir_enabled", settings);
        initPropertyItemValue(TF_SW_DEST, "swroms_dest_dir", settings);
        initPropertyItemValue("tfSWDisksDestCbx", "swdisks_dest_dir_enabled", settings);
        initPropertyItemValue(TF_SW_DISKS_DEST, "swdisks_dest_dir", settings);
        initPropertyItemValue("tfSamplesDestCbx", "samples_dest_dir_enabled", settings);
        initPropertyItemValue(TF_SAMPLES_DEST, "samples_dest_dir", settings);
        initPropertyItemValue("tfBackupDestCbx", "backup_dest_dir_enabled", settings);
        initPropertyItemValue(TF_BACKUP_DEST, "backup_dest_dir", settings);
        initPropertyItemValue(LIST_SRC_DIR, SRC_DIR, settings);
    }
}
