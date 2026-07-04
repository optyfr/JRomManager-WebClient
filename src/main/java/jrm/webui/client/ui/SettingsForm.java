package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;

import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.utils.EnhJSO;

/**
 * Base form class for settings panels with property persistence.
 * <p>
 * Subclasses build SmartGWT form items whose names map, through
 * {@link #fname2name}, to the property keys used by the server. Depending on
 * whether the form was given explicit settings, value changes are either
 * applied locally only or also persisted to the server (profile or global
 * settings) via the {@code Q_Profile} / {@code Q_Global} protocol messages.
 *
 * @since 2.5
 */
class SettingsForm extends DynamicForm /* NOSONAR */ {
    /**
     * {@code true} when the form was built with explicit settings and should
     * therefore apply values locally only (no server persistence).
     */
    protected boolean hasSettings = false;

    /**
     * Immutable mapping from form-item field names to the server-side property
     * keys they persist.
     */
    public static final Map<String, String> fname2name = Map.ofEntries(
            Map.entry("chckbxNeedSHA1", "need_sha1_or_md5"),
            Map.entry("chckbxUseParallelism", "use_parallelism"),
            Map.entry("chckbxCreateMissingSets", "create_mode"),
            Map.entry("chckbxCreateOnlyComplete", "createfull_mode"),
            Map.entry("chckbxIgnoreUnneededContainers", "ignore_unneeded_containers"),
            Map.entry("chckbxIgnoreUnneededEntries", "ignore_unneeded_entries"),
            Map.entry("chckbxIgnoreUnknownContainers", "ignore_unknown_containers"),
            Map.entry("chckbxUseImplicitMerge", "implicit_merge"),
            Map.entry("chckbxIgnoreMergeNameRoms", "ignore_merge_name_roms"),
            Map.entry("chckbxIgnoreMergeNameDisks", "ignore_merge_name_disks"),
            Map.entry("chckbxExcludeGames", "exclude_games"),
            Map.entry("chckbxExcludeMachines", "exclude_machines"),
            Map.entry("chckbxBackup", "backup"),
            Map.entry("chckbxZeroEntryMatters", "zero_entry_matters"),
            Map.entry("cbCompression", "format"),
            Map.entry("cbbxMergeMode", "merge_mode"),
            Map.entry("cbHashCollision", "hash_collision_mode"),
            Map.entry("chckbxIncludeClones", "filter.InclClones"),
            Map.entry("chckbxIncludeDisks", "filter.InclDisks"),
            Map.entry("chckbxIncludeSamples", "filter.InclSamples"),
            Map.entry("cbMachineType", "filter.CabinetType"),
            Map.entry("cbOrientation", "filter.DisplayOrientation"),
            Map.entry("cbDriverStatus", "filter.DriverStatus"),
            Map.entry("cbSwMinSupport", "filter.MinSoftwareSupportedLevel"),
            Map.entry("cbYearMin", "filter.YearMin"),
            Map.entry("cbYearMax", "filter.YearMax"),
            Map.entry("chckbxScanSubfolders", "dir2dat.scan_subfolders"),
            Map.entry("chckbxDeepScan", "dir2dat.deep_scan"),
            Map.entry("chckbxAddMD5", "dir2dat.add_md5"),
            Map.entry("chckbxAddSHA1", "dir2dat.add_sha1"),
            Map.entry("chckbxJunkFolders", "dir2dat.junk_folders"),
            Map.entry("chckbxDoNotScanArchives", "dir2dat.do_not_scan_archives"),
            Map.entry("chckbxMatchProfile", "dir2dat.match_profile"),
            Map.entry("chckbxIncludeEmptyDirs", "dir2dat.include_empty_dirs"),
            Map.entry("txtSrcDir", "dir2dat_src_dir"),
            Map.entry("txtDstDat", "dir2dat_dst_file"),
            Map.entry("rgFormat", "dir2dat_format"),
            Map.entry("tfDir2DatName", "dir2dat.name"),
            Map.entry("tfDir2DatDescription", "dir2dat.description"),
            Map.entry("tfDir2DatVersion", "dir2dat.version"),
            Map.entry("tfDir2DatAuthor", "dir2dat.author"),
            Map.entry("tfDir2DatComment", "dir2dat.comment"),
            Map.entry("tfDir2DatCategory", "dir2dat.category"),
            Map.entry("tfDir2DatDate", "dir2dat.date"),
            Map.entry("tfDir2DatEMail", "dir2dat.email"),
            Map.entry("tfDir2DatHomepage", "dir2dat.homepage"),
            Map.entry("tfDir2DatURL", "dir2dat.url"),
            Map.entry("cbZipTempThreshold", "zip_temp_threshold"),
            Map.entry("cbZipLevel", "zip_compression_level"),
            Map.entry("cbZipELevel", "zip_level"),
            Map.entry("cb7ZLevel", "7z_level"),
            Map.entry("txt7ZThreads", "7z_threads"),
            Map.entry("chkbx7ZSolid", "7z_solid"),
            Map.entry("cbDbgLevel", "debug_level"),
            Map.entry("cbThreadCount", "thread_count"),
            Map.entry("tfBackupGDest", "backup_dest_dir"),
            Map.entry("tfBackupGDestEnabled", "backup_dest_dir_enabled"),
            Map.entry("gridExclusions", "exclusion_glob_list")
    );

    /**
     * Creates a settings form not backed by explicit settings (values are
     * persisted to the server on change).
     */
    public SettingsForm() {
        this(null);
    }

    /**
     * Creates a settings form, optionally backed by explicit settings.
     *
     * @param settings the explicit settings to apply, or {@code null} to enable
     *                 server-side persistence on change
     */
    public SettingsForm(EnhJSO settings) {
        super();
        hasSettings = settings != null;
    }

    /**
     * Applies a batch of property values to the form and, when not backed by
     * explicit settings, persists them to the server as profile properties.
     *
     * @param propvalues the field-name to value mappings to apply
     */
    protected void setPropertiesItemValue(Map<String, ?> propvalues) {
        setValues(propvalues);
        if (!hasSettings) {
            final var properties = Q_Profile.SetProperty.instantiate();
            propvalues.forEach((in, v) -> {
                if (fname2name.containsKey(in)) {
                    if (v instanceof Boolean)
                        properties.setProperty(fname2name.get(in), (boolean) v);
                    else
                        properties.setProperty(fname2name.get(in), (String) v);
                }
            });
            properties.send();
        }
    }

    /**
     * Sets a boolean profile property on the form and persists it to the server
     * when not backed by explicit settings.
     *
     * @param field the form-item field name
     * @param name  the server-side property key
     * @param value the boolean value to apply
     */
    protected void setPropertyItemValue(String field, String name, boolean value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Profile.SetProperty.instantiate().setProperty(name, value).send();
    }

    /**
     * Sets a string profile property on the form and persists it to the server
     * when not backed by explicit settings.
     *
     * @param field the form-item field name
     * @param name  the server-side property key
     * @param value the string value to apply
     */
    protected void setPropertyItemValue(String field, String name, String value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Profile.SetProperty.instantiate().setProperty(name, value).send();
    }

    /**
     * Sets a boolean global property on the form and persists it to the server
     * when not backed by explicit settings.
     *
     * @param field the form-item field name
     * @param name  the server-side property key
     * @param value the boolean value to apply
     */
    public void setGPropertyItemValue(String field, String name, boolean value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Global.SetProperty.instantiate().setProperty(name, value).send();
    }

    /**
     * Sets an integer global property on the form and persists it to the server
     * when not backed by explicit settings.
     *
     * @param field the form-item field name
     * @param name  the server-side property key
     * @param value the integer value to apply
     */
    public void setGPropertyItemValue(String field, String name, int value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Global.SetProperty.instantiate().setProperty(name, value).send();
    }

    /**
     * Sets a string global property on the form and persists it to the server
     * when not backed by explicit settings.
     *
     * @param field the form-item field name
     * @param name  the server-side property key
     * @param value the string value to apply
     */
    public void setGPropertyItemValue(String field, String name, String value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Global.SetProperty.instantiate().setProperty(name, value).send();
    }

    /**
     * Initializes a single form item from a settings object, applying the value
     * when present or clearing it otherwise.
     *
     * @param field the form-item field name
     * @param name  the server-side property key
     * @param jso   the settings object to read from
     */
    protected void initPropertyItemValue(String field, String name, EnhJSO jso) {
        FormItem formItem = getItem(field);
        if (formItem == null) {
            return;
        }
        if (jso.exists(name)) {
            applyPropertyValue(formItem, name, jso);
        } else {
            clearIfTextOrCanvas(formItem);
        }
    }

    /**
     * Dispatches the value of a property to the appropriate typed applier based
     * on its JSON type.
     *
     * @param formItem the form item to update
     * @param name     the server-side property key
     * @param jso      the settings object to read from
     */
    private void applyPropertyValue(FormItem formItem, String name, EnhJSO jso) {
        if (jso.isBoolean(name)) {
            applyBooleanValue(formItem, name, jso);
        } else if (jso.isString(name)) {
            applyStringValue(formItem, name, jso);
        } else if (jso.isVoid(name)) {
            clearIfTextOrCanvas(formItem);
        }
    }

    /**
     * Applies a boolean value to a {@link CheckboxItem}, firing a changed event
     * so dependent handlers run.
     *
     * @param formItem the form item to update (must be a {@link CheckboxItem})
     * @param name     the server-side property key
     * @param jso      the settings object to read from
     */
    private void applyBooleanValue(FormItem formItem, String name, EnhJSO jso) {
        if (formItem instanceof CheckboxItem cbitem) {
            cbitem.setValue(jso.getBool(name));
            cbitem.fireEvent(new ChangedEvent(cbitem.getJsObj()) {
                @Override
                public Object getValue() {
                    return cbitem.getValue();
                }
            });
        }
    }

    /**
     * Applies a string value to a text, radio-group, canvas or select item,
     * splitting pipe-separated values for multi-select items.
     *
     * @param formItem the form item to update
     * @param name     the server-side property key
     * @param jso      the settings object to read from
     */
    private void applyStringValue(FormItem formItem, String name, EnhJSO jso) {
        if (formItem instanceof TextItem || formItem instanceof RadioGroupItem || formItem instanceof CanvasItem) {
            formItem.setValue(jso.get(name));
        } else if (formItem instanceof SelectItem selitem) {
            if (Boolean.TRUE.equals(selitem.isMultiple()))
                selitem.setValueMap(jso.get(name).split("\\|"));
            else
                selitem.setValue(jso.get(name));
        }
    }

    /**
     * Clears the value of a form item if it is a {@link TextItem} or
     * {@link CanvasItem}.
     *
     * @param formItem the form item to clear
     */
    private static void clearIfTextOrCanvas(FormItem formItem) {
        if (formItem instanceof TextItem || formItem instanceof CanvasItem)
            formItem.clearValue();
    }

    /**
     * Initializes all known form items from the given settings object, then
     * refreshes the disabled state of dependent items.
     *
     * @param settings the settings object to read from
     */
    protected void initPropertyItemValues(EnhJSO settings) {
        fname2name.forEach((fn, n) -> initPropertyItemValue(fn, n, settings));
        updateDisabled();
    }

    /**
     * Returns the current form values filtered to the known field names and
     * keyed by their server-side property names.
     *
     * @return a map of property name to value for the known fields
     */
    Map<String, Object> getFilteredValues() {
        Map<String, Object> values = new HashMap<>();
        fname2name.forEach((fn, n) -> {
            if (getItem(fn) != null)
                values.put(n, getValue(fn));
        });
        return values;
    }

    /**
     * Updates the disabled state of form items based on current values.
     * <p>
     * Intentionally empty in the base class; overridden by subclasses as
     * needed.
     */
    // Subclasses override this to update disabled state of form items based on current values
    protected void updateDisabled() {
        // intentionally empty - overridden by subclasses
    }

}
