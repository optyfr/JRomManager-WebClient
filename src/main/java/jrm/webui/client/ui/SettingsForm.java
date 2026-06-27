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

class SettingsForm extends DynamicForm {
    protected boolean hasSettings = false;

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

    public SettingsForm() {
        this(null);
    }

    public SettingsForm(EnhJSO settings) {
        super();
        hasSettings = settings != null;
    }

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

    protected void setPropertyItemValue(String field, String name, boolean value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Profile.SetProperty.instantiate().setProperty(name, value).send();
    }

    protected void setPropertyItemValue(String field, String name, String value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Profile.SetProperty.instantiate().setProperty(name, value).send();
    }

    public void setGPropertyItemValue(String field, String name, boolean value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Global.SetProperty.instantiate().setProperty(name, value).send();
    }

    public void setGPropertyItemValue(String field, String name, int value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Global.SetProperty.instantiate().setProperty(name, value).send();
    }

    public void setGPropertyItemValue(String field, String name, String value) {
        getItem(field).setValue(value);
        if (!hasSettings)
            Q_Global.SetProperty.instantiate().setProperty(name, value).send();
    }

    protected void initPropertyItemValue(String field, String name, EnhJSO jso) {
        if (jso.exists(name)) {
            FormItem formItem = getItem(field);
            if (formItem != null) {
                if (jso.isBoolean(name)) {
                    if (formItem instanceof CheckboxItem) {
                        CheckboxItem cbitem = (CheckboxItem) formItem;
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
                    else if (formItem instanceof RadioGroupItem)
                        formItem.setValue(jso.get(name));
                    else if (formItem instanceof CanvasItem)
                        formItem.setValue(jso.get(name));
                    else if (formItem instanceof SelectItem) {
                        SelectItem selitem = (SelectItem) formItem;
                        if (Boolean.TRUE.equals(selitem.isMultiple()))
                            selitem.setValueMap(jso.get(name).split("\\|"));
                        else
                            selitem.setValue(jso.get(name));
                    }
                } else if (jso.isVoid(name) && (formItem instanceof TextItem || formItem instanceof CanvasItem))
                    formItem.clearValue();
            }
        } else {
            FormItem formItem = getItem(field);
            if (formItem != null && (formItem instanceof TextItem || formItem instanceof CanvasItem))
                formItem.clearValue();
        }
    }

    protected void initPropertyItemValues(EnhJSO settings) {
        fname2name.forEach((fn, n) -> initPropertyItemValue(fn, n, settings));
        updateDisabled();
    }

    Map<String, Object> getFilteredValues() {
        Map<String, Object> values = new HashMap<>();
        fname2name.forEach((fn, n) -> {
            if (getItem(fn) != null)
                values.put(n, getValue(fn));
        });
        return values;
    }

    protected void updateDisabled() {

    }

}
