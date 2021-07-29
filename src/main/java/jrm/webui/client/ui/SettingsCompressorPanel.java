package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

import jrm.webui.client.Client;

final class SettingsCompressorPanel extends TabSet
{

	SettingsCompressorZipPanel settingsCompressorZipPanel;
	SettingsCompressorZipEPanel settingsCompressorZipEPanel;
	SettingsCompressor7zEPanel settingsCompressor7zEPanel;

	class SettingsCompressorZipPanel extends SettingsForm
	{
		@SuppressWarnings("serial")
		public SettingsCompressorZipPanel()
		{
			setWidth("75%");
			setLayoutAlign(Alignment.CENTER);
			setNumCols(2);
			setColWidths(300,"*");
			setItems(
				new SelectItem("cbZipTempThreshold") {{
					setTitle(Client.getSession().getMsg("MainFrame.lblTemporaryFilesThreshold.text"));
					setValueMap(new HashMap<String,String>() {{
						put("_NEVER",Client.getSession().getMsg("ZipTempThreshold.Never")); //$NON-NLS-1$
						put("_1MB",Client.getSession().getMsg("ZipTempThreshold.1MB")); //$NON-NLS-1$
						put("_2MB",Client.getSession().getMsg("ZipTempThreshold.2MB")); //$NON-NLS-1$
						put("_5MB",Client.getSession().getMsg("ZipTempThreshold.5MB")); //$NON-NLS-1$
						put("_10MB",Client.getSession().getMsg("ZipTempThreshold.10MB")); //$NON-NLS-1$
						put("_25MB",Client.getSession().getMsg("ZipTempThreshold.25MB")); //$NON-NLS-1$
						put("_50MB",Client.getSession().getMsg("ZipTempThreshold.50MB")); //$NON-NLS-1$
						put("_100MB",Client.getSession().getMsg("ZipTempThreshold.100MB")); //$NON-NLS-1$
						put("_250MB",Client.getSession().getMsg("ZipTempThreshold.250MB")); //$NON-NLS-1$
						put("_500MB",Client.getSession().getMsg("ZipTempThreshold.500MB")); //$NON-NLS-1$
					}});
					setWidth("*");
					addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)getValue()));
					setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "_10MB"));
				}},
				new SelectItem("cbZipLevel") {{
					setTitle(Client.getSession().getMsg("MainFrame.lblCompressionLevel.text"));
					setValueMap(new HashMap<String,String>() {{
						put("DEFAULT",Client.getSession().getMsg("ZipOptions.DEFAULT")); //$NON-NLS-1$
						put("STORE",Client.getSession().getMsg("ZipOptions.STORE")); //$NON-NLS-1$
						put("FASTEST",Client.getSession().getMsg("ZipOptions.FASTEST")); //$NON-NLS-1$
						put("FAST",Client.getSession().getMsg("ZipOptions.FAST")); //$NON-NLS-1$
						put("NORMAL",Client.getSession().getMsg("ZipOptions.NORMAL")); //$NON-NLS-1$
						put("MAXIMUM",Client.getSession().getMsg("ZipOptions.MAXIMUM")); //$NON-NLS-1$
						put("ULTRA",Client.getSession().getMsg("ZipOptions.ULTRA")); //$NON-NLS-1$
					}});
					setWidth("*");
					addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)getValue()));
					setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "DEFAULT"));
				}}
			);
		}

	}

	class SettingsCompressorZipEPanel extends SettingsForm
	{
		@SuppressWarnings("serial")
		public SettingsCompressorZipEPanel()
		{
			setWidth("75%");
			setLayoutAlign(Alignment.CENTER);
			setNumCols(2);
			setColWidths(300,"*");
			setItems(
				new SelectItem("cbZipELevel") {{
					setTitle(Client.getSession().getMsg("MainFrame.lblZipEArgs.text"));
					setValueMap(new HashMap<String,String>() {{
						put("STORE",Client.getSession().getMsg("ZipOptions.STORE")); //$NON-NLS-1$
						put("FASTEST",Client.getSession().getMsg("ZipOptions.FASTEST")); //$NON-NLS-1$
						put("FAST",Client.getSession().getMsg("ZipOptions.FAST")); //$NON-NLS-1$
						put("NORMAL",Client.getSession().getMsg("ZipOptions.NORMAL")); //$NON-NLS-1$
						put("MAXIMUM",Client.getSession().getMsg("ZipOptions.MAXIMUM")); //$NON-NLS-1$
						put("ULTRA",Client.getSession().getMsg("ZipOptions.ULTRA")); //$NON-NLS-1$
					}});
					setWidth("*");
					addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), getValueAsString()));
					setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "NORMAL"));
				}}
			);
		}
	}

	class SettingsCompressor7zEPanel extends SettingsForm
	{
		@SuppressWarnings("serial")
		public SettingsCompressor7zEPanel()
		{
			setWidth("75%");
			setLayoutAlign(Alignment.CENTER);
			setNumCols(4);
			setColWidths(200,"*",200,"*");
			setItems(
				new SelectItem("cb7ZLevel") {{
					setColSpan(3);
					setTitle(Client.getSession().getMsg("MainFrame.lbl7zArgs.text"));
					setValueMap(new HashMap<String,String>() {{
						put("STORE",Client.getSession().getMsg("SevenZipOptions.STORE")); //$NON-NLS-1$
						put("FASTEST",Client.getSession().getMsg("SevenZipOptions.FASTEST")); //$NON-NLS-1$
						put("FAST",Client.getSession().getMsg("SevenZipOptions.FAST")); //$NON-NLS-1$
						put("NORMAL",Client.getSession().getMsg("SevenZipOptions.NORMAL")); //$NON-NLS-1$
						put("MAXIMUM",Client.getSession().getMsg("SevenZipOptions.MAXIMUM")); //$NON-NLS-1$
						put("ULTRA",Client.getSession().getMsg("SevenZipOptions.ULTRA")); //$NON-NLS-1$
					}});
					setWidth("*");
					addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)getValue()));
					setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "NORMAL"));
				}},
				new IntegerItem("txt7ZThreads", Client.getSession().getMsg("MainFrame.lbl7zThreads.text")) {{
					addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), getValueAsInteger()));
					setDefaultValue(Client.getSession().getSettingAsInteger(fname2name.get(getName()), -1));
				}},
				new CheckboxItem("chkbx7ZSolid", Client.getSession().getMsg("MainFrame.ckbx7zSolid.text")) {{
					setLabelAsTitle(true);
					addChangedHandler(event->{
						setGPropertyItemValue(getName(), fname2name.get(getName()), getValueAsBoolean());
						event.getForm().getItem("cb7ZLevel").setDisabled(!getValueAsBoolean());
					});
					setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), true));
				}}
			);
		}
	}

	public SettingsCompressorPanel()
	{
		setPaneMargin(0);
		setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER);
		addTab(new Tab()
		{
			{
				setTitle(Client.getSession().getMsg("MainFrame.Zip"));
				setPane(new VLayout() {{
					addMember(new LayoutSpacer("*","*"));
					addMember(settingsCompressorZipPanel = new SettingsCompressorZipPanel());
					addMember(new LayoutSpacer("*","*"));
				}});
			}
		});
		addTab(new Tab()
		{
			{
				setTitle(Client.getSession().getMsg("MainFrame.ZipExternal"));
				setPane(new VLayout() {{
					addMember(new LayoutSpacer("*","*"));
					addMember(settingsCompressorZipEPanel = new SettingsCompressorZipEPanel());
					addMember(new LayoutSpacer("*","*"));
				}});
			}
		});
		addTab(new Tab()
		{
			{
				setTitle(Client.getSession().getMsg("MainFrame.7zExternal"));
				setPane(new VLayout() {{
					addMember(new LayoutSpacer("*","*"));
					addMember(settingsCompressor7zEPanel = new SettingsCompressor7zEPanel());
					addMember(new LayoutSpacer("*","*"));
				}});
			}
		});
	}

}
