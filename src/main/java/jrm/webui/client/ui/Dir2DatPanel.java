package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.form.fields.*;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Dir2Dat;

public class Dir2DatPanel extends VLayout
{
	SettingsForm options,headers,io;
	
	public Dir2DatPanel()
	{
		setLayoutMargin(5);
		setMembers(
			new HLayout() {{
				setMembers(
					options = new SettingsForm() {{
						setMargin(5);
						setBorder("1px solid WindowFrame");
						setWidth("50%");
						setHeight100();
						setColWidths("30%","70%");
						setFields(
							new HeaderItem() {{
								setDefaultValue(Client.getSession().getMsg("MainFrame.Options"));
							}},
							new CanvasItem() {{
								setHeight("*");
								setShowTitle(false);
								setColSpan(2);
							}},
							new CheckboxItem("chckbxScanSubfolders") {{
								setTitle(Client.getSession().getMsg("MainFrame.chckbxScanSubfolders.text"));
								setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), true));
								addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue()));
							}},
							new CheckboxItem("chckbxDeepScan") {{
								setTitle(Client.getSession().getMsg("MainFrame.chckbxDeepScanFor.text"));
								setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), false));
								addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue()));
							}},
							new CheckboxItem("chckbxAddMD5") {{
								setTitle(Client.getSession().getMsg("MainFrame.chckbxAddMd.text"));
								setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), false));
								addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue()));
							}},
							new CheckboxItem("chckbxAddSHA1") {{
								setTitle(Client.getSession().getMsg("MainFrame.chckbxAddShamd.text"));
								setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), false));
								addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue()));
							}},
							new CheckboxItem("chckbxJunkFolders") {{
								setTitle(Client.getSession().getMsg("MainFrame.chckbxJunkSubfolders.text"));
								setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), false));
								addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue()));
							}},
							new CheckboxItem("chckbxDoNotScanArchives") {{
								setTitle(Client.getSession().getMsg("MainFrame.chckbxDoNotScan.text"));
								setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), false));
								addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue()));
							}},
							new CheckboxItem("chckbxMatchProfile") {{
								setTitle(Client.getSession().getMsg("MainFrame.chckbxMatchCurrentProfile.text"));
								setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), false));
								addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue()));
							}},
							new CheckboxItem("chckbxIncludeEmptyDirs") {{
								setTitle(Client.getSession().getMsg("MainFrame.chckbxIncludeEmptyDirs.text"));
								setDefaultValue(Client.getSession().getSettingAsBoolean(fname2name.get(getName()), false));
								addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue()));
							}},
							new CanvasItem() {{
								setHeight("*");
								setShowTitle(false);
								setColSpan(2);
							}}
						);
					}},
					headers = new SettingsForm() {{
						setMargin(5);
						setBorder("1px solid WindowFrame");
						setWidth("50%");
						setHeight100();
						setColWidths(100,"*");
						setFields(
							new HeaderItem() {{
								setDefaultValue(Client.getSession().getMsg("MainFrame.Headers"));
							}},
							new CanvasItem() {{
								setHeight("*");
								setShowTitle(false);
								setColSpan(2);
							}},
							new TextItem("tfDir2DatName") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblName.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatDescription") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblDescription.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatVersion") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblVersion.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatAuthor") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblAuthor.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatComment") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblComment.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatCategory") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblCategory.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatDate") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblDate.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatEMail") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblEmail.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatHomepage") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblHomepage.text"));
								setWidth("*");
							}},
							new TextItem("tfDir2DatURL") {{
								setTitle(Client.getSession().getMsg("MainFrame.lblUrl.text"));
								setWidth("*");
							}},
							new CanvasItem() {{
								setHeight("*");
								setShowTitle(false);
								setColSpan(2);
							}}
						);
					}}
				);
			}},
			io = new SettingsForm() {{
				setMargin(5);
				setBorder("1px solid WindowFrame");
				setCellPadding(1);
				setNumCols(4);
				setColWidths(100,"*",28,100);
				setFields(
					new HeaderItem() {{
						setDefaultValue(Client.getSession().getMsg("MainFrame.IO"));
					}},
					new TextItem("txtSrcDir") {{
						setTitle(Client.getSession().getMsg("MainFrame.lblSrcDir_1.text"));
						setWidth("*");
						setCanEdit(false);
						setEndRow(false);
						setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), null));
					}},
					new ButtonItem() {{
						setStartRow(false);
						setIcon("icons/disk.png");
						setTitle(null);
						setValueIconRightPadding(0);
						setEndRow(false);
						addClickHandler(event->new RemoteFileChooser("tfSrcDir",  null, infos->{
							setGPropertyItemValue("txtSrcDir", fname2name.get("txtSrcDir"), infos[0].path);
							event.getForm().getItem("txtSrcDir").setValue(infos[0].path);
						}));
					}},
					new ButtonItem() {{
						setStartRow(false);
						setTitle(Client.getSession().getMsg("MainFrame.btnGenerate.text"));
						setEndRow(false);
						setRowSpan(3);
						setHeight("*");
						setWidth("*");
						addClickHandler(event->{
							Q_Dir2Dat.Start.instantiate().
								setOptions(options.getFilteredValues()).
								setHeaders(headers.getFilteredValues()).
								setIO(io.getFilteredValues()).
								send();
							;
						});
					}},
					new TextItem("txtDstDat") {{
						setTitle(Client.getSession().getMsg("MainFrame.lblDstDat.text"));
						setWidth("*");
						setCanEdit(false);
						setEndRow(false);
						setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), null));
					}},
					new ButtonItem() {{
						setStartRow(false);
						setIcon("icons/disk.png");
						setTitle(null);
						setValueIconRightPadding(0);
						addClickHandler(event->new RemoteFileChooser("tfDstDat", null, infos->{
							setGPropertyItemValue("txtDstDat", fname2name.get("txtDstDat"), infos[0].path);
							event.getForm().getItem("txtDstDat").setValue(infos[0].path);
						}));
					}},
					new RadioGroupItem("rgFormat") {{
						setTitle(Client.getSession().getMsg("MainFrame.lblFormat.text"));
						setVertical(false);
						setFillHorizontalSpace(true);
						setWidth("*");
						setColSpan(2);
						addChangedHandler(event->setGPropertyItemValue(getName(), fname2name.get(getName()), (String)getValue()));
						setValueMap(new HashMap<String,String>() {
							private static final long serialVersionUID = 1L;
							{
								put("MAME",Client.getSession().getMsg("MainFrame.rdbtnMame.text"));
								put("DATAFILE",Client.getSession().getMsg("MainFrame.rdbtnLogiqxDat.text"));
								put("SOFTWARELIST",Client.getSession().getMsg("MainFrame.rdbtnSwList.text"));
							}
						});
						setDefaultValue(Client.getSession().getSetting(fname2name.get(getName()), "MAME"));
					}}
				);
			}}
		);
	}

}
