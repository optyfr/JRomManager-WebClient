package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

import jrm.webui.client.Client;

final class SettingsCompressorPanel extends SectionStack
{
	class SettingsCompressorZipPanel extends SettingsForm
	{
		public SettingsCompressorZipPanel()
		{
			setWidth("75%");
			setLayoutAlign(Alignment.CENTER);
			setNumCols(2);
			setColWidths(300,"*");
			final var tempThreshold = new SelectItem("cbZipTempThreshold");
			tempThreshold.setTitle(Client.getSession().getMsg("MainFrame.lblTemporaryFilesThreshold.text"));
			final var thresholdValues = new HashMap<String,String>();
			thresholdValues.put("_NEVER",Client.getSession().getMsg("ZipTempThreshold.Never")); //$NON-NLS-1$
			thresholdValues.put("_1MB",Client.getSession().getMsg("ZipTempThreshold.1MB")); //$NON-NLS-1$
			thresholdValues.put("_2MB",Client.getSession().getMsg("ZipTempThreshold.2MB")); //$NON-NLS-1$
			thresholdValues.put("_5MB",Client.getSession().getMsg("ZipTempThreshold.5MB")); //$NON-NLS-1$
			thresholdValues.put("_10MB",Client.getSession().getMsg("ZipTempThreshold.10MB")); //$NON-NLS-1$
			thresholdValues.put("_25MB",Client.getSession().getMsg("ZipTempThreshold.25MB")); //$NON-NLS-1$
			thresholdValues.put("_50MB",Client.getSession().getMsg("ZipTempThreshold.50MB")); //$NON-NLS-1$
			thresholdValues.put("_100MB",Client.getSession().getMsg("ZipTempThreshold.100MB")); //$NON-NLS-1$
			thresholdValues.put("_250MB",Client.getSession().getMsg("ZipTempThreshold.250MB")); //$NON-NLS-1$
			thresholdValues.put("_500MB",Client.getSession().getMsg("ZipTempThreshold.500MB")); //$NON-NLS-1$
			tempThreshold.setValueMap(thresholdValues);
			tempThreshold.setWidth("*");
			tempThreshold.addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)tempThreshold.getValue()));
			tempThreshold.setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "_10MB"));
			final var zipLevel = new SelectItem("cbZipLevel");
			zipLevel.setTitle(Client.getSession().getMsg("MainFrame.lblCompressionLevel.text"));
			final var zipLevels = new HashMap<String,String>();
			zipLevels.put("DEFAULT",Client.getSession().getMsg("ZipOptions.DEFAULT")); //$NON-NLS-1$
			zipLevels.put("STORE",Client.getSession().getMsg("ZipOptions.STORE")); //$NON-NLS-1$
			zipLevels.put("FASTEST",Client.getSession().getMsg("ZipOptions.FASTEST")); //$NON-NLS-1$
			zipLevels.put("FAST",Client.getSession().getMsg("ZipOptions.FAST")); //$NON-NLS-1$
			zipLevels.put("NORMAL",Client.getSession().getMsg("ZipOptions.NORMAL")); //$NON-NLS-1$
			zipLevels.put("MAXIMUM",Client.getSession().getMsg("ZipOptions.MAXIMUM")); //$NON-NLS-1$
			zipLevels.put("ULTRA",Client.getSession().getMsg("ZipOptions.ULTRA")); //$NON-NLS-1$
			zipLevel.setValueMap(zipLevels);
			zipLevel.setWidth("*");
			zipLevel.addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)zipLevel.getValue()));
			zipLevel.setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "DEFAULT"));
			setItems(tempThreshold, zipLevel);
		}

	}

	class SettingsCompressor7zEPanel extends SettingsForm
	{
		public SettingsCompressor7zEPanel()
		{
			setWidth("75%");
			setLayoutAlign(Alignment.CENTER);
			setNumCols(4);
			setColWidths(200,"*",200,"*");
			final var level = new SelectItem("cb7ZLevel");
			level.setColSpan(3);
			level.setTitle(Client.getSession().getMsg("MainFrame.lbl7zArgs.text"));
			final var levels = new HashMap<String,String>();
			levels.put("STORE",Client.getSession().getMsg("SevenZipOptions.STORE")); //$NON-NLS-1$
			levels.put("FASTEST",Client.getSession().getMsg("SevenZipOptions.FASTEST")); //$NON-NLS-1$
			levels.put("FAST",Client.getSession().getMsg("SevenZipOptions.FAST")); //$NON-NLS-1$
			levels.put("NORMAL",Client.getSession().getMsg("SevenZipOptions.NORMAL")); //$NON-NLS-1$
			levels.put("MAXIMUM",Client.getSession().getMsg("SevenZipOptions.MAXIMUM")); //$NON-NLS-1$
			levels.put("ULTRA",Client.getSession().getMsg("SevenZipOptions.ULTRA")); //$NON-NLS-1$
			level.setValueMap(levels);
			level.setWidth("*");
			level.addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)level.getValue()));
			level.setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "NORMAL"));
			final var threads = new IntegerItem("txt7ZThreads", Client.getSession().getMsg("MainFrame.lbl7zThreads.text"));
			threads.addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), threads.getValueAsInteger()));
			threads.setDefaultValue(Client.getSession().getSettingAsInteger(fname2name.get(getName()), -1));
			final var solid = new CheckboxItem("chkbx7ZSolid", Client.getSession().getMsg("MainFrame.ckbx7zSolid.text"));
			solid.setLabelAsTitle(true);
			solid.addChangedHandler(event->{
				setGPropertyItemValue(getName(), fname2name.get(getName()), solid.getValueAsBoolean());
				event.getForm().getItem("cb7ZLevel").setDisabled(!solid.getValueAsBoolean());
			});
			solid.setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), true));
			setItems(level, threads, solid);
		}
	}

	public SettingsCompressorPanel()
	{
		setVisibilityMode(VisibilityMode.MULTIPLE);
		setMargin(5);
		setOverflow(Overflow.AUTO);
		final var zip = new SectionStackSection();
		zip.setCanCollapse(false);
		zip.setExpanded(true);
		zip.setCanClose(false);
		zip.setTitle("zip");
		zip.addItem(new SettingsCompressorZipPanel());
		addSection(zip);
		final var sevenzip = new SectionStackSection();
		sevenzip.setTitle("7zip");
		sevenzip.setCanCollapse(false);
		sevenzip.setExpanded(true);
		sevenzip.setCanClose(false);
		sevenzip.addItem(new SettingsCompressor7zEPanel());
		addSection(sevenzip);
	}

}
