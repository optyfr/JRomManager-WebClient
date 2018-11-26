package jrm.webui.client.ui;

import java.util.HashMap;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.*;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import jrm.webui.client.Client;

public class Dir2DatPanel extends VLayout
{
	public Dir2DatPanel()
	{
		setLayoutMargin(5);
		setMembers(
			new HLayout() {{
				setMembers(
					new DynamicForm() {{
						setMargin(5);
						setBorder("1px solid WindowFrame");
						setWidth("50%");
						setHeight100();
						setColWidths("30%","70%");
						setFields(
							new HeaderItem() {{
								setDefaultValue(Client.session.getMsg("MainFrame.Options"));
							}},
							new CanvasItem() {{
								setHeight("*");
								setShowTitle(false);
								setColSpan(2);
							}},
							new CheckboxItem() {{
								setTitle(Client.session.getMsg("MainFrame.chckbxScanSubfolders.text"));
								setDefaultValue(true);
							}},
							new CheckboxItem() {{
								setTitle(Client.session.getMsg("MainFrame.chckbxDeepScanFor.text"));
							}},
							new CheckboxItem() {{
								setTitle(Client.session.getMsg("MainFrame.chckbxAddMd.text"));
							}},
							new CheckboxItem() {{
								setTitle(Client.session.getMsg("MainFrame.chckbxAddShamd.text"));
							}},
							new CheckboxItem() {{
								setTitle(Client.session.getMsg("MainFrame.chckbxJunkSubfolders.text"));
							}},
							new CheckboxItem() {{
								setTitle(Client.session.getMsg("MainFrame.chckbxDoNotScan.text"));
							}},
							new CheckboxItem() {{
								setTitle(Client.session.getMsg("MainFrame.chckbxMatchCurrentProfile.text"));
							}},
							new CheckboxItem() {{
								setTitle(Client.session.getMsg("MainFrame.chckbxIncludeEmptyDirs.text"));
							}},
							new CanvasItem() {{
								setHeight("*");
								setShowTitle(false);
								setColSpan(2);
							}}
						);
					}},
					new DynamicForm() {{
						setMargin(5);
						setBorder("1px solid WindowFrame");
						setWidth("50%");
						setHeight100();
						setColWidths(100,"*");
						setFields(
							new HeaderItem() {{
								setDefaultValue(Client.session.getMsg("MainFrame.Headers"));
							}},
							new CanvasItem() {{
								setHeight("*");
								setShowTitle(false);
								setColSpan(2);
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblName.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblDescription.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblVersion.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblAuthor.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblComment.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblCategory.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblDate.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblEmail.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblHomepage.text"));
								setWidth("*");
							}},
							new TextItem() {{
								setTitle(Client.session.getMsg("MainFrame.lblUrl.text"));
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
			new DynamicForm() {{
				setMargin(5);
				setBorder("1px solid WindowFrame");
				setCellPadding(1);
				setNumCols(4);
				setColWidths(100,"*",28,100);
				setFields(
					new HeaderItem() {{
						setDefaultValue(Client.session.getMsg("MainFrame.IO"));
					}},
					new TextItem() {{
						setTitle(Client.session.getMsg("MainFrame.lblSrcDir_1.text"));
						setWidth("*");
						setCanEdit(false);
						setEndRow(false);
					}},
					new ButtonItem() {{
						setStartRow(false);
						setIcon("icons/disk.png");
						setTitle(null);
						setValueIconRightPadding(0);
						setEndRow(false);
						addClickHandler(event->new RemoteFileChooser("tfDisksDest", records->{}));
					}},
					new ButtonItem() {{
						setStartRow(false);
						setTitle(Client.session.getMsg("MainFrame.btnGenerate.text"));
						setEndRow(false);
						setRowSpan(3);
						setHeight("*");
						setWidth("*");
						addClickHandler(event->{});
					}},
					new TextItem() {{
						setTitle(Client.session.getMsg("MainFrame.lblDstDat.text"));
						setWidth("*");
						setCanEdit(false);
						setEndRow(false);
					}},
					new ButtonItem() {{
						setStartRow(false);
						setIcon("icons/disk.png");
						setTitle(null);
						setValueIconRightPadding(0);
						addClickHandler(event->new RemoteFileChooser("tfDisksDest", records->{}));
					}},
					new RadioGroupItem() {{
						setTitle(Client.session.getMsg("MainFrame.lblFormat.text"));
						setVertical(false);
						setFillHorizontalSpace(true);
						setDefaultValue("MAME");
						setWidth("*");
						setColSpan(2);
						setValueMap(new HashMap<String,String>() {
							private static final long serialVersionUID = 1L;
							{
								put("MAME",Client.session.getMsg("MainFrame.rdbtnMame.text"));
								put("DATAFILE",Client.session.getMsg("MainFrame.rdbtnLogiqxDat.text"));
								put("SOFTWARELIST",Client.session.getMsg("MainFrame.rdbtnSwList.text"));
							}
						});
					}}
				);
			}}
		);
	}

}
