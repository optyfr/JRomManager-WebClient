package jrm.webui.client.ui;

import java.util.Map;

import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.HeaderItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Dir2Dat;

public class Dir2DatPanel extends VLayout //NOSONAR
{
	private static final String BORDER_STYLE = "1px solid WindowFrame";
	private static final String TXT_SRC_DIR = "txtSrcDir";
	private static final String TXT_DST_DAT = "txtDstDat";

	SettingsForm options;
	SettingsForm headers;
	SettingsForm io;

	public Dir2DatPanel() {
		setLayoutMargin(5);
		options = buildOptionsForm();
		headers = buildHeadersForm();
		io = buildIOForm();
		HLayout topLayout = new HLayout();
		topLayout.setMembers(options, headers);
		setMembers(topLayout, io);
	}

	private SettingsForm buildOptionsForm() {
		SettingsForm form = new SettingsForm();
		form.setMargin(5);
		form.setBorder(BORDER_STYLE);
		form.setWidth("50%");
		form.setHeight100();
		form.setColWidths("30%", "70%");
		form.setFields(
				buildHeaderItem(Client.getSession().getMsg("MainFrame.Options")),
				buildSpacerItem(),
				buildScanSubfoldersCheckbox(form),
				buildDeepScanCheckbox(form),
				buildAddMD5Checkbox(form),
				buildAddSHA1Checkbox(form),
				buildJunkFoldersCheckbox(form),
				buildDoNotScanArchivesCheckbox(form),
				buildMatchProfileCheckbox(form),
				buildIncludeEmptyDirsCheckbox(form),
				buildSpacerItem());
		return form;
	}

	private CheckboxItem buildScanSubfoldersCheckbox(SettingsForm form) {
		CheckboxItem item = new CheckboxItem("chckbxScanSubfolders");
		item.setTitle(Client.getSession().getMsg("MainFrame.chckbxScanSubfolders.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean(SettingsForm.fname2name.get(item.getName()), true));
		item.addChangedHandler(event -> form.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (boolean) item.getValue()));
		return item;
	}

	private CheckboxItem buildDeepScanCheckbox(SettingsForm form) {
		CheckboxItem item = new CheckboxItem("chckbxDeepScan");
		item.setTitle(Client.getSession().getMsg("MainFrame.chckbxDeepScanFor.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean(SettingsForm.fname2name.get(item.getName()), false));
		item.addChangedHandler(event -> form.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (boolean) item.getValue()));
		return item;
	}

	private CheckboxItem buildAddMD5Checkbox(SettingsForm form) {
		CheckboxItem item = new CheckboxItem("chckbxAddMD5");
		item.setTitle(Client.getSession().getMsg("MainFrame.chckbxAddMd.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean(SettingsForm.fname2name.get(item.getName()), false));
		item.addChangedHandler(event -> form.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (boolean) item.getValue()));
		return item;
	}

	private CheckboxItem buildAddSHA1Checkbox(SettingsForm form) {
		CheckboxItem item = new CheckboxItem("chckbxAddSHA1");
		item.setTitle(Client.getSession().getMsg("MainFrame.chckbxAddShamd.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean(SettingsForm.fname2name.get(item.getName()), false));
		item.addChangedHandler(event -> form.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (boolean) item.getValue()));
		return item;
	}

	private CheckboxItem buildJunkFoldersCheckbox(SettingsForm form) {
		CheckboxItem item = new CheckboxItem("chckbxJunkFolders");
		item.setTitle(Client.getSession().getMsg("MainFrame.chckbxJunkSubfolders.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean(SettingsForm.fname2name.get(item.getName()), false));
		item.addChangedHandler(event -> form.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (boolean) item.getValue()));
		return item;
	}

	private CheckboxItem buildDoNotScanArchivesCheckbox(SettingsForm form) {
		CheckboxItem item = new CheckboxItem("chckbxDoNotScanArchives");
		item.setTitle(Client.getSession().getMsg("MainFrame.chckbxDoNotScan.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean(SettingsForm.fname2name.get(item.getName()), false));
		item.addChangedHandler(event -> form.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (boolean) item.getValue()));
		return item;
	}

	private CheckboxItem buildMatchProfileCheckbox(SettingsForm form) {
		CheckboxItem item = new CheckboxItem("chckbxMatchProfile");
		item.setTitle(Client.getSession().getMsg("MainFrame.chckbxMatchCurrentProfile.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean(SettingsForm.fname2name.get(item.getName()), false));
		item.addChangedHandler(event -> form.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (boolean) item.getValue()));
		return item;
	}

	private CheckboxItem buildIncludeEmptyDirsCheckbox(SettingsForm form) {
		CheckboxItem item = new CheckboxItem("chckbxIncludeEmptyDirs");
		item.setTitle(Client.getSession().getMsg("MainFrame.chckbxIncludeEmptyDirs.text"));
		item.setDefaultValue(Client.getSession().getSettingAsBoolean(SettingsForm.fname2name.get(item.getName()), false));
		item.addChangedHandler(event -> form.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (boolean) item.getValue()));
		return item;
	}

	private SettingsForm buildHeadersForm() {
		SettingsForm form = new SettingsForm();
		form.setMargin(5);
		form.setBorder(BORDER_STYLE);
		form.setWidth("50%");
		form.setHeight100();
		form.setColWidths(100, "*");
		form.setFields(
				buildHeaderItem(Client.getSession().getMsg("MainFrame.Headers")),
				buildSpacerItem(),
				buildTextField("tfDir2DatName", "MainFrame.lblName.text"),
				buildTextField("tfDir2DatDescription", "MainFrame.lblDescription.text"),
				buildTextField("tfDir2DatVersion", "MainFrame.lblVersion.text"),
				buildTextField("tfDir2DatAuthor", "MainFrame.lblAuthor.text"),
				buildTextField("tfDir2DatComment", "MainFrame.lblComment.text"),
				buildTextField("tfDir2DatCategory", "MainFrame.lblCategory.text"),
				buildTextField("tfDir2DatDate", "MainFrame.lblDate.text"),
				buildTextField("tfDir2DatEMail", "MainFrame.lblEmail.text"),
				buildTextField("tfDir2DatHomepage", "MainFrame.lblHomepage.text"),
				buildTextField("tfDir2DatURL", "MainFrame.lblUrl.text"),
				buildSpacerItem());
		return form;
	}

	private TextItem buildTextField(String name, String msgKey) {
		TextItem item = new TextItem(name);
		item.setTitle(Client.getSession().getMsg(msgKey));
		item.setWidth("*");
		return item;
	}

	private SettingsForm buildIOForm() {
		SettingsForm form = new SettingsForm();
		form.setMargin(5);
		form.setBorder(BORDER_STYLE);
		form.setCellPadding(1);
		form.setNumCols(4);
		form.setColWidths(100, "*", 28, 100);
		form.setFields(
				buildHeaderItem(Client.getSession().getMsg("MainFrame.IO")),
				buildSrcDirField(),
				buildSrcDirBrowseButton(),
				buildGenerateButton(),
				buildDstDatField(),
				buildDstDatBrowseButton(),
				buildFormatRadioGroup());
		return form;
	}

	private TextItem buildSrcDirField() {
		TextItem item = new TextItem(TXT_SRC_DIR);
		item.setTitle(Client.getSession().getMsg("MainFrame.lblSrcDir_1.text"));
		item.setWidth("*");
		item.setCanEdit(false);
		item.setEndRow(false);
		item.setDefaultValue(Client.getSession().getSetting(SettingsForm.fname2name.get(item.getName()), null));
		return item;
	}

	private ButtonItem buildSrcDirBrowseButton() {
		ButtonItem item = new ButtonItem();
		item.setStartRow(false);
		item.setIcon("icons/disk.png");
		item.setTitle(null);
		item.setValueIconRightPadding(0);
		item.setEndRow(false);
		item.addClickHandler(event -> new RemoteFileChooser("tfSrcDir", null, infos -> {
			io.setGPropertyItemValue(TXT_SRC_DIR, SettingsForm.fname2name.get(TXT_SRC_DIR), infos[0].path);
			event.getForm().getItem(TXT_SRC_DIR).setValue(infos[0].path);
		}));
		return item;
	}

	private ButtonItem buildGenerateButton() {
		ButtonItem item = new ButtonItem();
		item.setStartRow(false);
		item.setTitle(Client.getSession().getMsg("MainFrame.btnGenerate.text"));
		item.setEndRow(false);
		item.setRowSpan(3);
		item.setHeight("*");
		item.setWidth("*");
		item.addClickHandler(event -> Q_Dir2Dat.Start.instantiate().setOptions(options.getFilteredValues())
				.setHeaders(headers.getFilteredValues()).setIO(io.getFilteredValues()).send());
		return item;
	}

	private TextItem buildDstDatField() {
		TextItem item = new TextItem(TXT_DST_DAT);
		item.setTitle(Client.getSession().getMsg("MainFrame.lblDstDat.text"));
		item.setWidth("*");
		item.setCanEdit(false);
		item.setEndRow(false);
		item.setDefaultValue(Client.getSession().getSetting(SettingsForm.fname2name.get(item.getName()), null));
		return item;
	}

	private ButtonItem buildDstDatBrowseButton() {
		ButtonItem item = new ButtonItem();
		item.setStartRow(false);
		item.setIcon("icons/disk.png");
		item.setTitle(null);
		item.setValueIconRightPadding(0);
		item.addClickHandler(event -> new RemoteFileChooser("tfDstDat", null, infos -> {
			io.setGPropertyItemValue(TXT_DST_DAT, SettingsForm.fname2name.get(TXT_DST_DAT), infos[0].path);
			event.getForm().getItem(TXT_DST_DAT).setValue(infos[0].path);
		}));
		return item;
	}

	private RadioGroupItem buildFormatRadioGroup() {
		RadioGroupItem item = new RadioGroupItem("rgFormat");
		item.setTitle(Client.getSession().getMsg("MainFrame.lblFormat.text"));
		item.setVertical(false);
		item.setFillHorizontalSpace(true);
		item.setWidth("*");
		item.setColSpan(2);
		item.addChangedHandler(event -> io.setGPropertyItemValue(item.getName(), SettingsForm.fname2name.get(item.getName()), (String) item.getValue()));
		item.setValueMap(Map.ofEntries(
				Map.entry("MAME", Client.getSession().getMsg("MainFrame.rdbtnMame.text")),
				Map.entry("DATAFILE", Client.getSession().getMsg("MainFrame.rdbtnLogiqxDat.text")),
				Map.entry("SOFTWARELIST", Client.getSession().getMsg("MainFrame.rdbtnSwList.text"))));
		item.setDefaultValue(Client.getSession().getSetting(SettingsForm.fname2name.get(item.getName()), "MAME"));
		return item;
	}

	private static HeaderItem buildHeaderItem(String title) {
		HeaderItem item = new HeaderItem();
		item.setDefaultValue(title);
		return item;
	}

	private static CanvasItem buildSpacerItem() {
		CanvasItem item = new CanvasItem();
		item.setHeight("*");
		item.setShowTitle(false);
		item.setColSpan(2);
		return item;
	}
}
