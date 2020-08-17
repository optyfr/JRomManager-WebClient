package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;

import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.utils.EnhJSO;

abstract class SettingsForm extends DynamicForm
{
	protected boolean hasSettings = false;

	@SuppressWarnings("serial")
	final static protected Map<String,String> fname2name= new HashMap<String,String>() {{
		put("chckbxNeedSHA1","need_sha1_or_md5");
		put("chckbxUseParallelism","use_parallelism");
		put("chckbxCreateMissingSets","create_mode");
		put("chckbxCreateOnlyComplete","createfull_mode");
		put("chckbxIgnoreUnneededContainers","ignore_unneeded_containers");
		put("chckbxIgnoreUnneededEntries","ignore_unneeded_entries");
		put("chckbxIgnoreUnknownContainers","ignore_unknown_containers");
		put("chckbxUseImplicitMerge","implicit_merge");
		put("chckbxIgnoreMergeNameRoms","ignore_merge_name_roms");
		put("chckbxIgnoreMergeNameDisks","ignore_merge_name_disks");
		put("chckbxExcludeGames","exclude_games");
		put("chckbxExcludeMachines","exclude_machines");
		put("chckbxBackup","backup");
		put("cbCompression","format");
		put("cbbxMergeMode","merge_mode");
		put("cbHashCollision","hash_collision_mode");
		put("chckbxIncludeClones", "filter.InclClones");
		put("chckbxIncludeDisks", "filter.InclDisks");
		put("chckbxIncludeSamples", "filter.InclSamples");
		put("cbMachineType", "filter.CabinetType");
		put("cbOrientation", "filter.DisplayOrientation");
		put("cbDriverStatus", "filter.DriverStatus");
		put("cbSwMinSupport", "filter.MinSoftwareSupportedLevel");
		put("cbYearMin", "filter.YearMin");
		put("cbYearMax", "filter.YearMax");
		put("chckbxScanSubfolders", "dir2dat.scan_subfolders");
		put("chckbxDeepScan", "dir2dat.deep_scan");
		put("chckbxAddMD5", "dir2dat.add_md5");
		put("chckbxAddSHA1", "dir2dat.add_sha1");
		put("chckbxJunkFolders", "dir2dat.junk_folders");
		put("chckbxDoNotScanArchives", "dir2dat.do_not_scan_archives");
		put("chckbxMatchProfile", "dir2dat.match_profile");
		put("chckbxIncludeEmptyDirs", "dir2dat.include_empty_dirs");
		put("txtSrcDir","dir2dat_src_dir");
		put("txtDstDat","dir2dat_dst_file");
		put("rgFormat","dir2dat_format");
		put("tfDir2DatName", "dir2dat.name"); //$NON-NLS-1$
		put("tfDir2DatDescription", "dir2dat.description"); //$NON-NLS-1$
		put("tfDir2DatVersion", "dir2dat.version"); //$NON-NLS-1$
		put("tfDir2DatAuthor", "dir2dat.author"); //$NON-NLS-1$
		put("tfDir2DatComment", "dir2dat.comment"); //$NON-NLS-1$
		put("tfDir2DatCategory", "dir2dat.category"); //$NON-NLS-1$
		put("tfDir2DatDate", "dir2dat.date"); //$NON-NLS-1$
		put("tfDir2DatEMail", "dir2dat.email"); //$NON-NLS-1$
		put("tfDir2DatHomepage", "dir2dat.homepage"); //$NON-NLS-1$
		put("tfDir2DatURL", "dir2dat.url"); //$NON-NLS-1$
		put("cbZipTempThreshold", "zip_temp_threshold");
		put("cbZipLevel", "zip_compression_level");
		put("cbZipELevel", "zip_level");
		put("cb7ZLevel", "7z_level");
		put("txt7ZThreads", "7z_threads");
		put("chkbx7ZSolid", "7z_solid");
		put("cbDbgLevel", "debug_level");
	}};

	public SettingsForm()
	{
		this(null);
	}

	public SettingsForm(EnhJSO settings)
	{
		super();
		hasSettings = settings!=null;
	}

	protected void setPropertiesItemValue(Map<String,?> propvalues)
	{
		setValues(propvalues);
		if(!hasSettings)
		{
			final var properties = Q_Profile.SetProperty.instantiate();
			propvalues.forEach((in,v)->{
				if(fname2name.containsKey(in))
				{
					if(v instanceof Boolean)
						properties.setProperty(fname2name.get(in), (boolean)v);
					else
						properties.setProperty(fname2name.get(in), (String)v);
				}
			});
			properties.send();
		}
	}


	protected void setPropertyItemValue(String field, String name, boolean value)
	{
		getItem(field).setValue(value);
		if(!hasSettings)
			Q_Profile.SetProperty.instantiate().setProperty(name, value).send();
	}

	protected void setPropertyItemValue(String field, String name, String value)
	{
		getItem(field).setValue(value);
		if(!hasSettings)
			Q_Profile.SetProperty.instantiate().setProperty(name, value).send();
	}

	protected void setGPropertyItemValue(String field, String name, boolean value)
	{
		getItem(field).setValue(value);
		if(!hasSettings)
			Q_Global.SetProperty.instantiate().setProperty(name, value).send();
	}

	protected void setGPropertyItemValue(String field, String name, int value)
	{
		getItem(field).setValue(value);
		if(!hasSettings)
			Q_Global.SetProperty.instantiate().setProperty(name, value).send();
	}

	protected void setGPropertyItemValue(String field, String name, String value)
	{
		getItem(field).setValue(value);
		if(!hasSettings)
			Q_Global.SetProperty.instantiate().setProperty(name, value).send();
	}

	protected void initPropertyItemValue(String field, String name, EnhJSO jso)
	{
		if(jso.exists(name))
		{
			FormItem  formItem =  getItem(field);
			if (formItem != null)
			{
				if(jso.isBoolean(name))
				{
					if(formItem instanceof CheckboxItem)
					{
						CheckboxItem cbitem = (CheckboxItem)formItem;
						cbitem.setValue(jso.getBool(name));
						cbitem.fireEvent(new ChangedEvent(cbitem.getJsObj()){
							@Override
							public Object getValue() {
								return cbitem.getValue();
							}
						});
					}
				}
				else if(jso.isString(name))
				{
					if(formItem instanceof TextItem)
						formItem.setValue(jso.get(name));
					else if(formItem instanceof RadioGroupItem)
						formItem.setValue(jso.get(name));
					else if(formItem instanceof SelectItem)
					{
						SelectItem selitem = (SelectItem)formItem;
						if(selitem.isMultiple())
							selitem.setValueMap(jso.get(name).split("\\|"));
						else
							selitem.setValue(jso.get(name));
					}
				}
			}
		}
	}
	
	protected void initPropertyItemValues(EnhJSO settings)
	{
		fname2name.forEach((fn,n)->initPropertyItemValue(fn, n, settings));
		updateDisabled();
	}

	Map<String,Object> getFilteredValues()
	{
		Map<String,Object> values = new HashMap<>();
		fname2name.forEach((fn,n)->{
			if(getItem(fn)!=null)
				values.put(n, getValue(fn));
		});
		return values;
	}
	
	protected void updateDisabled()
	{
		
	}

}
