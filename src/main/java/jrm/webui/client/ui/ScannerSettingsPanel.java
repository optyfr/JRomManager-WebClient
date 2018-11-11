package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.utils.EnhJSO;

public final class ScannerSettingsPanel extends DynamicForm
{
	private boolean hasSettings = false;
	
	@SuppressWarnings("serial")
	final static private Map<String,String> fname2name= new HashMap<String,String>() {{
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
	}};

	public ScannerSettingsPanel()
	{
		this(null);
	}
	
	@SuppressWarnings("serial")
	public ScannerSettingsPanel(EnhJSO settings)
	{
		super();
		hasSettings = settings!=null;
		setWidth100();
		setNumCols(4);
		setColWidths("*","*","*","*");
		setWrapItemTitles(false);
		setItems(
			new CheckboxItem("chckbxNeedSHA1", Client.session.getMsg("MainFrame.chckbxNeedSHA1.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "need_sha1_or_md5", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxCreateMissingSets", Client.session.getMsg("MainFrame.chckbxCreateMissingSets.text")) {{
				addChangedHandler(event->{
					setPropertyItemValue(getName(), "create_mode", (boolean)getValue());
					ScannerSettingsPanel.this.getItem("chckbxCreateOnlyComplete").setDisabled(!getValueAsBoolean());
				});
				setDefaultValue(true);
			}},
			new CheckboxItem("chckbxUseParallelism", Client.session.getMsg("MainFrame.chckbxUseParallelism.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "use_parallelism", (boolean)getValue()));
				setDefaultValue(true);
			}},
			new CheckboxItem("chckbxCreateOnlyComplete", Client.session.getMsg("MainFrame.chckbxCreateOnlyComplete.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "createfull_mode", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxIgnoreUnneededContainers", Client.session.getMsg("MainFrame.chckbxIgnoreUnneededContainers.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "ignore_unneeded_containers", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxIgnoreUnneededEntries", Client.session.getMsg("MainFrame.chckbxIgnoreUnneededEntries.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "ignore_unneeded_entries", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxIgnoreUnknownContainers", Client.session.getMsg("MainFrame.chckbxIgnoreUnknownContainers.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "ignore_unknown_containers", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxUseImplicitMerge", Client.session.getMsg("MainFrame.chckbxUseImplicitMerge.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "implicit_merge", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxIgnoreMergeNameRoms", Client.session.getMsg("MainFrame.chckbxIgnoreMergeName.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "ignore_merge_name_roms", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxIgnoreMergeNameDisks", Client.session.getMsg("MainFrame.chckbxIgnoreMergeName_1.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "ignore_merge_name_disks", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxExcludeGames", Client.session.getMsg("MainFrame.chckbxExcludeGames.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "exclude_games", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxExcludeMachines", Client.session.getMsg("MainFrame.chckbxExcludeMachines.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "exclude_machines", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxBackup", Client.session.getMsg("MainFrame.chckbxBackup.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "backup", (boolean)getValue()));
				setDefaultValue(true);
			}},
			new SelectItem("cbCompression", Client.session.getMsg("MainFrame.lblCompression.text")) {{
				setValueMap(new HashMap<String, String>() {{
					put("DIR", Client.session.getMsg("FormatOptions.Directories"));
					put("ZIP", Client.session.getMsg("FormatOptions.Zip"));
					put("ZIPE", Client.session.getMsg("FormatOptions.ZipExternal"));
					put("SEVENZIP", Client.session.getMsg("FormatOptions.SevenZip"));
					put("TZIP", Client.session.getMsg("FormatOptions.TorrentZip"));
				}});
				setDefaultValue("TZIP");
				addChangedHandler(event->setPropertyItemValue(getName(), "format", getValue().toString()));
				setColSpan(3);
				setWidth("*");
			}},
			new SelectItem("cbbxMergeMode", Client.session.getMsg("MainFrame.lblMergeMode.text")) {{
				setValueMap(new HashMap<String, String>() {{
					put("FULLMERGE", Client.session.getMsg("MergeOptions.FullMerge"));
					put("MERGE", Client.session.getMsg("MergeOptions.Merge"));
					put("SUPERFULLNOMERGE", Client.session.getMsg("MergeOptions.NoMergeInclBiosAndDevices"));
					put("FULLNOMERGE", Client.session.getMsg("MergeOptions.NoMergeInclBios"));
					put("NOMERGE", Client.session.getMsg("MergeOptions.NoMerge"));
					put("SPLIT", Client.session.getMsg("MergeOptions.Split"));
				}});
				setDefaultValue("SPLIT");
				addChangedHandler(event->{
					setPropertyItemValue(getName(), "merge_mode", getValue().toString());
					event.getForm().getItem("cbHashCollision").setDisabled(!(getValue().equals("MERGE") || getValue().equals("FULLMERGE")));
				});
				setColSpan(3);
				setPrompt(Client.session.getMsg("MainFrame.cbbxMergeMode.toolTipText"));
				setWidth("*");
			}},
			new SelectItem("cbHashCollision", Client.session.getMsg("MainFrame.lblHashCollision.text")) {{
				setValueMap(new HashMap<String, String>() {{
					put("SINGLEFILE", Client.session.getMsg("HashCollisionOptions.SingleFile"));
					put("SINGLECLONE", Client.session.getMsg("HashCollisionOptions.SingleClone"));
					put("ALLCLONES", Client.session.getMsg("HashCollisionOptions.AllClones"));
					put("HALFDUMB", Client.session.getMsg("HashCollisionOptions.AllClonesHalfDumb"));
					put("DUMB", Client.session.getMsg("HashCollisionOptions.AllClonesDumb"));
					put("DUMBER", Client.session.getMsg("HashCollisionOptions.AllClonesDumber"));
				}});
				setDefaultValue("SINGLEFILE");
				addChangedHandler(event->setPropertyItemValue(getName(), "hash_collision_mode", getValue().toString()));
				setColSpan(3);
				setWidth("*");
				setDisabled(true);
			}}
		);
		if(hasSettings)
			initPropertyItemValues(settings);
	}
	
	private void setPropertyItemValue(String field, String name, boolean value)
	{
		getItem(field).setValue(value);
		if(!hasSettings)
			Q_Profile.SetProperty.instantiate().setProperty(name, value).send();
	}

	private void setPropertyItemValue(String field, String name, String value)
	{
		getItem(field).setValue(value);
		if(!hasSettings)
			Q_Profile.SetProperty.instantiate().setProperty(name, value).send();
	}

	void initPropertyItemValue(String field, String name, EnhJSO jso)
	{
		if(jso.exists(name))
		{
			FormItem  formItem =  getItem(field);
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
	
	void initPropertyItemValues(EnhJSO settings)
	{
		fname2name.forEach((fn,n)->initPropertyItemValue(fn, n, settings));
	}

	Map<String,Object> getFilteredValues()
	{
		Map<String,Object> values = new HashMap<>();
		fname2name.forEach((fn,n)->{
			values.put(n, getValue(fn));
		});
		return values;
	}
}
