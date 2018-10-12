package jrm.webui.client.ui;

import java.util.HashMap;

import com.google.gwt.core.client.JsonUtils;
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
	@SuppressWarnings("serial")
	public ScannerSettingsPanel()
	{
		super();
		setWidth100();
		setHeight100();
		setNumCols(4);
		setColWidths("15%","35%","15%","35%");
		setItems(
			new CheckboxItem("chckbxNeedSHA1", Client.session.getMsg("MainFrame.chckbxNeedSHA1.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "need_sha1_or_md5", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxCreateMissingSets", Client.session.getMsg("MainFrame.chckbxCreateMissingSets.text")) {{
				addChangedHandler(event->{
					setPropertyItemValue(getName(), "create_mode", (boolean)getValue());
					event.getForm().getItem("chckbxCreateOnlyComplete").setDisabled(!getValueAsBoolean());
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
	}
	
	private void setPropertyItemValue(String field, String name, boolean value)
	{
		getItem(field).setValue(value);
		Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(name, value)));
	}

	private void setPropertyItemValue(String field, String name, String value)
	{
		getItem(field).setValue(value);
		Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(name, value)));
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

}
