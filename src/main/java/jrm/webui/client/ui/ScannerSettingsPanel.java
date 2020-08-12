package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.utils.EnhJSO;

public final class ScannerSettingsPanel extends SettingsForm
{
	public ScannerSettingsPanel()
	{
		this(null);
	}
	
	@SuppressWarnings("serial")
	public ScannerSettingsPanel(EnhJSO settings)
	{
		super(settings);
		setID("ScannerSettingsPanel");
		setWidth100();
		setNumCols(4);
		setColWidths("*","*","*","*");
		setWrapItemTitles(false);
		setContextMenu(new Menu() {{
			addItem(new MenuItem() {{
				setTitle(Client.session.getMsg("MainFrame.mnPresets.text"));
				setSubmenu(new Menu() {{
					addItem(new MenuItem() {{
						setTitle(Client.session.getMsg("MainFrame.mnPdMame.text"));
						setSubmenu(new Menu() {{
							addItem(new MenuItem() {{	// merged
								setTitle(Client.session.getMsg("MainFrame.mntmPleasuredome.text"));
								addClickHandler(e->{
									HashMap<String,Object> options = new HashMap<>();
									options.put("chckbxCreateMissingSets", true);
									options.put("chckbxCreateOnlyComplete", false);
									options.put("chckbxIgnoreUnneededContainers", false);
									options.put("chckbxIgnoreUnneededEntries", false);
									options.put("chckbxIgnoreUnknownContainers", true); // Don't remove _ReadMe_.txt
									options.put("chckbxUseImplicitMerge", true);
									options.put("chckbxIgnoreMergeNameDisks", true);
									options.put("chckbxIgnoreMergeNameRoms", false);
									options.put("cbCompression", "TZIP");
									options.put("cbbxMergeMode", "MERGE");
									options.put("cbHashCollision", "HALFDUMB");
									options.put("chckbxExcludeGames", false);
									options.put("chckbxExcludeMachines", false);
									setPropertiesItemValue(options);
									updateDisabled();
								});
							}});
							addItem(new MenuItem() {{	// non-merged
								setTitle(Client.session.getMsg("MainFrame.mntmPdMameNon.text"));
								addClickHandler(e->{
									HashMap<String,Object> options = new HashMap<>();
									options.put("chckbxCreateMissingSets", true);
									options.put("chckbxCreateOnlyComplete", false);
									options.put("chckbxIgnoreUnneededContainers", false);
									options.put("chckbxIgnoreUnneededEntries", false);
									options.put("chckbxIgnoreUnknownContainers", true); // Don't remove _ReadMe_.txt
									options.put("chckbxUseImplicitMerge", true);
									options.put("chckbxIgnoreMergeNameDisks", true);
									options.put("chckbxIgnoreMergeNameRoms", false);
									options.put("cbCompression", "TZIP");
									options.put("cbbxMergeMode", "SUPERFULLNOMERGE");
									options.put("chckbxExcludeGames", false);
									options.put("chckbxExcludeMachines", false);
									setPropertiesItemValue(options);
									updateDisabled();
								});
							}});
							addItem(new MenuItem() {{	// split
								setTitle(Client.session.getMsg("MainFrame.mntmPdMameSplit.text"));
								addClickHandler(e->{
									HashMap<String,Object> options = new HashMap<>();
									options.put("chckbxCreateMissingSets", true);
									options.put("chckbxCreateOnlyComplete", false);
									options.put("chckbxIgnoreUnneededContainers", false);
									options.put("chckbxIgnoreUnneededEntries", false);
									options.put("chckbxIgnoreUnknownContainers", true); // Don't remove _ReadMe_.txt
									options.put("chckbxUseImplicitMerge", true);
									options.put("chckbxIgnoreMergeNameDisks", true);
									options.put("chckbxIgnoreMergeNameRoms", false);
									options.put("cbCompression", "TZIP");
									options.put("cbbxMergeMode", "SPLIT");
									options.put("chckbxExcludeGames", false);
									options.put("chckbxExcludeMachines", false);
									setPropertiesItemValue(options);
									updateDisabled();
								});
							}});
						}});
					}});
				}});
			}});
		}});
		setItems(
			new CheckboxItem("chckbxNeedSHA1", Client.session.getMsg("MainFrame.chckbxNeedSHA1.text")) {{
				addChangedHandler(event->setPropertyItemValue(getName(), "need_sha1_or_md5", (boolean)getValue()));
			}},
			new CheckboxItem("chckbxCreateMissingSets", Client.session.getMsg("MainFrame.chckbxCreateMissingSets.text")) {{
				addChangedHandler(event->{
					setPropertyItemValue(getName(), "create_mode", (boolean)getValue());
					updateDisabled();
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
					put("ZIP", Client.session.getMsg("FormatOptions.Zip"));
					put("ZIPE", Client.session.getMsg("FormatOptions.ZipExternal"));
					put("SEVENZIP", Client.session.getMsg("FormatOptions.SevenZip"));
					put("TZIP", Client.session.getMsg("FormatOptions.TorrentZip"));
					put("DIR", Client.session.getMsg("FormatOptions.Directories"));
					put("FAKE", Client.session.getMsg("FormatOptions.SingleFile"));
				}});
				setDefaultValue("ZIP");
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
					updateDisabled();
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

}
