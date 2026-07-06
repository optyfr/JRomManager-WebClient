package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.utils.EnhJSO;

/**
 * Panel for configuring ROM scanner settings within the WebClient UI.
 * <p>
 * Exposes checkboxes and combo boxes that control scan behavior — merge mode,
 * compression format, hash-collision strategy, container/entry filtering, and
 * exclusion glob patterns — and provides a right-click context menu with
 * Pleasuredome presets for quick configuration.
 * <p>
 * Settings are persisted as key-value pairs through the parent
 * {@link SettingsForm} infrastructure.
 */
@SuppressWarnings("java:S110")
public final class ScannerSettingsPanel extends SettingsForm {

    /** Form-item name for the "Create Missing Sets" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_CREATE_MISSING_SETS = "chckbxCreateMissingSets";
    /** Form-item name for the "Create Only Complete" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}; disabled when {@link #CHCKBX_CREATE_MISSING_SETS} is unchecked. */
    private static final String CHCKBX_CREATE_ONLY_COMPLETE = "chckbxCreateOnlyComplete";
    /** Form-item name for the "Ignore Unneeded Containers" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_IGNORE_UNNEEDED_CONTAINERS = "chckbxIgnoreUnneededContainers";
    /** Form-item name for the "Ignore Unneeded Entries" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_IGNORE_UNNEEDED_ENTRIES = "chckbxIgnoreUnneededEntries";
    /** Form-item name for the "Ignore Unknown Containers" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_IGNORE_UNKNOWN_CONTAINERS = "chckbxIgnoreUnknownContainers";
    /** Form-item name for the "Use Implicit Merge" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_USE_IMPLICIT_MERGE = "chckbxUseImplicitMerge";
    /** Form-item name for the "Ignore Merge Name (Disks)" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_IGNORE_MERGE_NAME_DISKS = "chckbxIgnoreMergeNameDisks";
    /** Form-item name for the "Ignore Merge Name (ROMs)" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_IGNORE_MERGE_NAME_ROMS = "chckbxIgnoreMergeNameRoms";
    /** Form-item name for the output-compression {@link com.smartgwt.client.widgets.form.fields.SelectItem SelectItem}. */
    private static final String CB_COMPRESSION = "cbCompression";
    /** Form-item name for the merge-mode {@link com.smartgwt.client.widgets.form.fields.SelectItem SelectItem}. */
    private static final String CBBX_MERGE_MODE = "cbbxMergeMode";
    /** Form-item name for the hash-collision {@link com.smartgwt.client.widgets.form.fields.SelectItem SelectItem}; enabled only when {@link #CBBX_MERGE_MODE} is {@value #MERGE} or {@code FULLMERGE}. */
    private static final String CB_HASH_COLLISION = "cbHashCollision";
    /** Form-item name for the "Exclude Games" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_EXCLUDE_GAMES = "chckbxExcludeGames";
    /** Form-item name for the "Exclude Machines" {@link com.smartgwt.client.widgets.form.fields.CheckboxItem CheckboxItem}. */
    private static final String CHCKBX_EXCLUDE_MACHINES = "chckbxExcludeMachines";
    /** Merge-mode value that enables the {@link #CB_HASH_COLLISION} field. */
    private static final String MERGE = "MERGE";
    /** Merge-mode value representing a fully split ROM set layout. */
    private static final String SPLIT = "SPLIT";
    /** {@link com.smartgwt.client.widgets.grid.ListGridField ListGridField} name used in the destination-exclusion grid. */
    private static final String FILTER = "Filter";

    /**
     * Constructs a new {@code ScannerSettingsPanel} with no initial settings.
     * <p>
     * Delegates to {@link #ScannerSettingsPanel(EnhJSO)} with a {@code null}
     * argument, causing the panel to display default values only.
     */
    public ScannerSettingsPanel() {
        this(null);
    }

    /**
     * Constructs a new {@code ScannerSettingsPanel} with the given initial settings.
     *
     * @param settings the initial settings to apply
     */
    public ScannerSettingsPanel(EnhJSO settings) {
        super(settings);
        setID("ScannerSettingsPanel");
        setWidth100();
        setNumCols(6);
        setColWidths("*", "*", "*", "*", "*", "*");
        setWrapItemTitles(false);
        
        setupContextMenu();
        setupItems();
        
        if (hasSettings) {
            initPropertyItemValues(settings);
        }
    }

    /**
     * Builds and attaches the right-click context menu for this panel.
     * <p>
     * The menu contains a <em>Presets</em> submenu with Pleasuredome MAME
     * presets (merged, non-merged, and split) that batch-configure several
     * scanner options at once via {@link #applyPreset(String, String, String)}.
     */
    private void setupContextMenu() {
        Menu contextMenu = new Menu();
        
        MenuItem presetsItem = new MenuItem();
        presetsItem.setTitle(Client.getSession().getMsg("MainFrame.mnPresets.text"));
        
        Menu presetsSubmenu = new Menu();
        
        MenuItem pdMameItem = new MenuItem();
        pdMameItem.setTitle(Client.getSession().getMsg("MainFrame.mnPdMame.text"));
        
        Menu pdMameSubmenu = new Menu();
        
        MenuItem pdMameMergedItem = new MenuItem();
        pdMameMergedItem.setTitle(Client.getSession().getMsg("MainFrame.mntmPleasuredome.text"));
        pdMameMergedItem.addClickHandler(e -> applyPreset("TZIP", MERGE, "HALFDUMB"));
        pdMameSubmenu.addItem(pdMameMergedItem);
        
        MenuItem pdMameNonMergedItem = new MenuItem();
        pdMameNonMergedItem.setTitle(Client.getSession().getMsg("MainFrame.mntmPdMameNon.text"));
        pdMameNonMergedItem.addClickHandler(e -> applyPreset("TZIP", "SUPERFULLNOMERGE", null));
        pdMameSubmenu.addItem(pdMameNonMergedItem);
        
        MenuItem pdMameSplitItem = new MenuItem();
        pdMameSplitItem.setTitle(Client.getSession().getMsg("MainFrame.mntmPdMameSplit.text"));
        pdMameSplitItem.addClickHandler(e -> applyPreset("TZIP", SPLIT, null));
        pdMameSubmenu.addItem(pdMameSplitItem);
        
        pdMameItem.setSubmenu(pdMameSubmenu);
        presetsSubmenu.addItem(pdMameItem);
        
        presetsItem.setSubmenu(presetsSubmenu);
        contextMenu.addItem(presetsItem);
        
        setContextMenu(contextMenu);
    }

    /**
     * Applies a preset configuration by batch-setting scanner options.
     * <p>
     * All supported scanner properties are written in a single call and the
     * disabled state of dependent fields is refreshed afterwards.
     *
     * @param compression   the compression format identifier (e.g. {@code "TZIP"})
     * @param mergeMode     the merge mode identifier (e.g. {@code "MERGE"}, {@code "SPLIT"})
     * @param hashCollision the hash collision mode identifier, or {@code null} to leave it unchanged
     */
    private void applyPreset(String compression, String mergeMode, String hashCollision) {
        Map<String, Object> options = new HashMap<>();
        options.put(CHCKBX_CREATE_MISSING_SETS, true);
        options.put(CHCKBX_CREATE_ONLY_COMPLETE, false);
        options.put(CHCKBX_IGNORE_UNNEEDED_CONTAINERS, false);
        options.put(CHCKBX_IGNORE_UNNEEDED_ENTRIES, false);
        options.put(CHCKBX_IGNORE_UNKNOWN_CONTAINERS, true); // Don't remove _ReadMe_.txt
        options.put(CHCKBX_USE_IMPLICIT_MERGE, true);
        options.put(CHCKBX_IGNORE_MERGE_NAME_DISKS, true);
        options.put(CHCKBX_IGNORE_MERGE_NAME_ROMS, false);
        options.put(CB_COMPRESSION, compression);
        options.put(CBBX_MERGE_MODE, mergeMode);
        if (hashCollision != null) {
            options.put(CB_HASH_COLLISION, hashCollision);
        }
        options.put(CHCKBX_EXCLUDE_GAMES, false);
        options.put(CHCKBX_EXCLUDE_MACHINES, false);
        setPropertiesItemValue(options);
        updateDisabled();
    }

    /**
     * Creates all form items (checkboxes, combo boxes, and the exclusion grid)
     * and registers them with the underlying {@link SettingsForm}.
     * <p>
     * Each item is wired to a {@code changed} handler that persists its value
     * through {@link SettingsForm#setPropertyItemValue}.
     */
    private void setupItems() {
        CheckboxItem needSHA1 = new CheckboxItem("chckbxNeedSHA1", Client.getSession().getMsg("MainFrame.chckbxNeedSHA1.text"));
        needSHA1.addChangedHandler(event -> setPropertyItemValue(needSHA1.getName(), "need_sha1_or_md5", (boolean) needSHA1.getValue()));
        
        CheckboxItem createMissingSets = new CheckboxItem(CHCKBX_CREATE_MISSING_SETS, Client.getSession().getMsg("MainFrame.chckbxCreateMissingSets.text"));
        createMissingSets.addChangedHandler(event -> {
            setPropertyItemValue(createMissingSets.getName(), "create_mode", (boolean) createMissingSets.getValue());
            updateDisabled();
        });
        createMissingSets.setDefaultValue(true);
        
        CanvasItem gridExclusions = createGridExclusionsItem();
        
        CheckboxItem useParallelism = new CheckboxItem("chckbxUseParallelism", Client.getSession().getMsg("MainFrame.chckbxUseParallelism.text"));
        useParallelism.addChangedHandler(event -> setPropertyItemValue(useParallelism.getName(), "use_parallelism", (boolean) useParallelism.getValue()));
        useParallelism.setDefaultValue(true);
        
        CheckboxItem createOnlyComplete = new CheckboxItem(CHCKBX_CREATE_ONLY_COMPLETE, Client.getSession().getMsg("MainFrame.chckbxCreateOnlyComplete.text"));
        createOnlyComplete.addChangedHandler(event -> setPropertyItemValue(createOnlyComplete.getName(), "createfull_mode", (boolean) createOnlyComplete.getValue()));
        
        CheckboxItem ignoreUnneededContainers = new CheckboxItem(CHCKBX_IGNORE_UNNEEDED_CONTAINERS, Client.getSession().getMsg("MainFrame.chckbxIgnoreUnneededContainers.text"));
        ignoreUnneededContainers.addChangedHandler(event -> setPropertyItemValue(ignoreUnneededContainers.getName(), "ignore_unneeded_containers", (boolean) ignoreUnneededContainers.getValue()));
        
        CheckboxItem ignoreUnneededEntries = new CheckboxItem(CHCKBX_IGNORE_UNNEEDED_ENTRIES, Client.getSession().getMsg("MainFrame.chckbxIgnoreUnneededEntries.text"));
        ignoreUnneededEntries.addChangedHandler(event -> setPropertyItemValue(ignoreUnneededEntries.getName(), "ignore_unneeded_entries", (boolean) ignoreUnneededEntries.getValue()));
        
        CheckboxItem ignoreUnknownContainers = new CheckboxItem(CHCKBX_IGNORE_UNKNOWN_CONTAINERS, Client.getSession().getMsg("MainFrame.chckbxIgnoreUnknownContainers.text"));
        ignoreUnknownContainers.addChangedHandler(event -> setPropertyItemValue(ignoreUnknownContainers.getName(), "ignore_unknown_containers", (boolean) ignoreUnknownContainers.getValue()));
        
        CheckboxItem useImplicitMerge = new CheckboxItem(CHCKBX_USE_IMPLICIT_MERGE, Client.getSession().getMsg("MainFrame.chckbxUseImplicitMerge.text"));
        useImplicitMerge.addChangedHandler(event -> setPropertyItemValue(useImplicitMerge.getName(), "implicit_merge", (boolean) useImplicitMerge.getValue()));
        
        CheckboxItem ignoreMergeNameRoms = new CheckboxItem(CHCKBX_IGNORE_MERGE_NAME_ROMS, Client.getSession().getMsg("MainFrame.chckbxIgnoreMergeName.text"));
        ignoreMergeNameRoms.addChangedHandler(event -> setPropertyItemValue(ignoreMergeNameRoms.getName(), "ignore_merge_name_roms", (boolean) ignoreMergeNameRoms.getValue()));
        
        CheckboxItem ignoreMergeNameDisks = new CheckboxItem(CHCKBX_IGNORE_MERGE_NAME_DISKS, Client.getSession().getMsg("MainFrame.chckbxIgnoreMergeName_1.text"));
        ignoreMergeNameDisks.addChangedHandler(event -> setPropertyItemValue(ignoreMergeNameDisks.getName(), "ignore_merge_name_disks", (boolean) ignoreMergeNameDisks.getValue()));
        
        CheckboxItem excludeGames = new CheckboxItem(CHCKBX_EXCLUDE_GAMES, Client.getSession().getMsg("MainFrame.chckbxExcludeGames.text"));
        excludeGames.addChangedHandler(event -> setPropertyItemValue(excludeGames.getName(), "exclude_games", (boolean) excludeGames.getValue()));
        
        CheckboxItem excludeMachines = new CheckboxItem(CHCKBX_EXCLUDE_MACHINES, Client.getSession().getMsg("MainFrame.chckbxExcludeMachines.text"));
        excludeMachines.addChangedHandler(event -> setPropertyItemValue(excludeMachines.getName(), "exclude_machines", (boolean) excludeMachines.getValue()));
        
        CheckboxItem backup = new CheckboxItem("chckbxBackup", Client.getSession().getMsg("MainFrame.chckbxBackup.text"));
        backup.addChangedHandler(event -> setPropertyItemValue(backup.getName(), "backup", (boolean) backup.getValue()));
        backup.setDefaultValue(true);
        
        CheckboxItem zeroEntryMatters = new CheckboxItem("chckbxZeroEntryMatters", Client.getSession().getMsg("MainFrame.chckbxZeroEntryMatters.text"));
        zeroEntryMatters.addChangedHandler(event -> setPropertyItemValue(zeroEntryMatters.getName(), "zero_entry_matters", (boolean) zeroEntryMatters.getValue()));
        zeroEntryMatters.setDefaultValue(true);
        
        SelectItem cbCompression = createCompressionItem();
        SelectItem cbbxMergeMode = createMergeModeItem();
        SelectItem cbHashCollision = createHashCollisionItem();
        
        setItems(
            needSHA1, createMissingSets, gridExclusions, useParallelism, createOnlyComplete,
            ignoreUnneededContainers, ignoreUnneededEntries, ignoreUnknownContainers,
            useImplicitMerge, ignoreMergeNameRoms, ignoreMergeNameDisks, excludeGames,
            excludeMachines, backup, zeroEntryMatters, cbCompression, cbbxMergeMode, cbHashCollision
        );
    }

    /**
     * Creates a {@link CanvasItem} embedding an editable {@link ListGrid} for
     * destination-exclusion glob patterns.
     * <p>
     * The grid supports add, edit, and delete via a context menu. Patterns are
     * stored as a single pipe-separated ({@code |}) string.
     *
     * @return the configured {@link CanvasItem} wrapping the exclusion grid
     */
    private CanvasItem createGridExclusionsItem() {
        CanvasItem gridExclusions = new CanvasItem("gridExclusions");
        gridExclusions.setShowTitle(false);
        gridExclusions.setColSpan(2);
        gridExclusions.setRowSpan(10);
        gridExclusions.setAlign(Alignment.CENTER);
        
        ListGrid grid = new ListGrid();
        grid.setMinWidth(75);
        grid.setMaxWidth(150);
        grid.setWidth("*");
        grid.setHeight(250);
        
        Menu gridContextMenu = new Menu();
        
        MenuItem addItem = new MenuItem("Add");
        addItem.addClickHandler(event -> grid.startEditingNew());
        gridContextMenu.addItem(addItem);
        
        MenuItem editItem = new MenuItem("Edit");
        editItem.setEnableIfCondition((target, menu, item) -> grid.anySelected());
        editItem.addClickHandler(event -> grid.startEditing(grid.getRecordIndex(grid.getSelectedRecord())));
        gridContextMenu.addItem(editItem);
        
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.addClickHandler(event -> grid.removeSelectedData());
        gridContextMenu.addItem(deleteItem);
        
        grid.setContextMenu(gridContextMenu);
        grid.setFields(new ListGridField(FILTER, "Dst Exclude glob"));
        
        grid.addEditCompleteHandler(event -> {
            final var list = grid.getDataAsRecordList();
            final var value = Stream.of(list.toArray())
                .map(r -> r.getAttributeAsString(FILTER))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("|"));
            gridExclusions.storeValue(value);
        });
        
        gridExclusions.setCanvas(grid);
        
        gridExclusions.addShowValueHandler(event -> {
            final var value = (String) event.getDataValue();
            final var list = new RecordList();
            SC.logWarn("addShowValueHandler:" + value);
            if (value != null) {
                for (final var filter : value.split("\\|")) {
                    list.add(new Record(Collections.singletonMap(FILTER, filter)));
                }
            }
            grid.setData(list);
        });
        
        gridExclusions.addChangedHandler(event -> setPropertyItemValue(gridExclusions.getName(), "exclusion_glob_list", (String) gridExclusions.getValue()));
        
        return gridExclusions;
    }

    /**
     * Creates the combo box for selecting the output compression format.
     * <p>
     * Available formats include Zip, Zip External, 7-Zip, TorrentZip,
     * Directories, and Single File.
     *
     * @return the configured {@link SelectItem} bound to the {@code format} property
     */
    private SelectItem createCompressionItem() {
        SelectItem cbCompression = new SelectItem(CB_COMPRESSION, Client.getSession().getMsg("MainFrame.lblCompression.text"));
        Map<String, String> compressionMap = new HashMap<>();
        compressionMap.put("ZIP", Client.getSession().getMsg("FormatOptions.Zip"));
        compressionMap.put("ZIPE", Client.getSession().getMsg("FormatOptions.ZipExternal"));
        compressionMap.put("SEVENZIP", Client.getSession().getMsg("FormatOptions.SevenZip"));
        compressionMap.put("TZIP", Client.getSession().getMsg("FormatOptions.TorrentZip"));
        compressionMap.put("DIR", Client.getSession().getMsg("FormatOptions.Directories"));
        compressionMap.put("FAKE", Client.getSession().getMsg("FormatOptions.SingleFile"));
        cbCompression.setValueMap(compressionMap);
        cbCompression.setDefaultValue("ZIP");
        cbCompression.addChangedHandler(event -> setPropertyItemValue(cbCompression.getName(), "format", cbCompression.getValue().toString()));
        cbCompression.setColSpan(3);
        cbCompression.setWidth("*");
        return cbCompression;
    }

    /**
     * Creates the combo box for selecting the ROM merge mode.
     * <p>
     * Available modes include Full Merge, Merge, No Merge (with BIOS and
     * devices), No Merge (with BIOS), No Merge, and Split. Changing the
     * selection also triggers {@link #updateDisabled()} to refresh dependent
     * fields.
     *
     * @return the configured {@link SelectItem} bound to the {@code merge_mode} property
     */
    private SelectItem createMergeModeItem() {
        SelectItem cbbxMergeMode = new SelectItem(CBBX_MERGE_MODE, Client.getSession().getMsg("MainFrame.lblMergeMode.text"));
        Map<String, String> mergeModeMap = new HashMap<>();
        mergeModeMap.put("FULLMERGE", Client.getSession().getMsg("MergeOptions.FullMerge"));
        mergeModeMap.put(MERGE, Client.getSession().getMsg("MergeOptions.Merge"));
        mergeModeMap.put("SUPERFULLNOMERGE", Client.getSession().getMsg("MergeOptions.NoMergeInclBiosAndDevices"));
        mergeModeMap.put("FULLNOMERGE", Client.getSession().getMsg("MergeOptions.NoMergeInclBios"));
        mergeModeMap.put("NOMERGE", Client.getSession().getMsg("MergeOptions.NoMerge"));
        mergeModeMap.put(SPLIT, Client.getSession().getMsg("MergeOptions.Split"));
        cbbxMergeMode.setValueMap(mergeModeMap);
        cbbxMergeMode.setDefaultValue(SPLIT);
        cbbxMergeMode.addChangedHandler(event -> {
            setPropertyItemValue(cbbxMergeMode.getName(), "merge_mode", cbbxMergeMode.getValue().toString());
            updateDisabled();
        });
        cbbxMergeMode.setColSpan(3);
        cbbxMergeMode.setPrompt(Client.getSession().getMsg("MainFrame.cbbxMergeMode.toolTipText"));
        cbbxMergeMode.setWidth("*");
        return cbbxMergeMode;
    }

    /**
     * Creates the combo box for selecting the hash-collision resolution strategy.
     * <p>
     * The field is initially disabled and becomes enabled only when the merge
     * mode is set to {@value #MERGE} or {@code FULLMERGE} (see
     * {@link #updateDisabled()}).
     *
     * @return the configured {@link SelectItem} bound to the {@code hash_collision_mode} property
     */
    private SelectItem createHashCollisionItem() {
        SelectItem cbHashCollision = new SelectItem(CB_HASH_COLLISION, Client.getSession().getMsg("MainFrame.lblHashCollision.text"));
        Map<String, String> hashCollisionMap = new HashMap<>();
        hashCollisionMap.put("SINGLEFILE", Client.getSession().getMsg("HashCollisionOptions.SingleFile"));
        hashCollisionMap.put("SINGLECLONE", Client.getSession().getMsg("HashCollisionOptions.SingleClone"));
        hashCollisionMap.put("ALLCLONES", Client.getSession().getMsg("HashCollisionOptions.AllClones"));
        hashCollisionMap.put("HALFDUMB", Client.getSession().getMsg("HashCollisionOptions.AllClonesHalfDumb"));
        hashCollisionMap.put("DUMB", Client.getSession().getMsg("HashCollisionOptions.AllClonesDumb"));
        hashCollisionMap.put("DUMBER", Client.getSession().getMsg("HashCollisionOptions.AllClonesDumber"));
        cbHashCollision.setValueMap(hashCollisionMap);
        cbHashCollision.setDefaultValue("SINGLEFILE");
        cbHashCollision.addChangedHandler(event -> setPropertyItemValue(cbHashCollision.getName(), "hash_collision_mode", cbHashCollision.getValue().toString()));
        cbHashCollision.setColSpan(3);
        cbHashCollision.setWidth("*");
        cbHashCollision.setDisabled(true);
        return cbHashCollision;
    }

    /**
     * Updates the enabled/disabled state of dependent form items based on the
     * current selection.
     * <p>
     * The <em>Create Only Complete</em> checkbox is disabled when
     * <em>Create Missing Sets</em> is unchecked, and the <em>Hash Collision</em>
     * combo box is enabled only when the merge mode is {@value #MERGE} or
     * {@code FULLMERGE}.
     */
    @Override
    protected void updateDisabled() {
        getItem(CHCKBX_CREATE_ONLY_COMPLETE).setDisabled(getValue(CHCKBX_CREATE_MISSING_SETS).equals(false));
        getItem(CB_HASH_COLLISION).setDisabled(!(getValue(CBBX_MERGE_MODE).equals(MERGE) || getValue(CBBX_MERGE_MODE).equals("FULLMERGE")));
        super.updateDisabled();
    }
}
