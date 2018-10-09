package jrm.webui.client.ui;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.*;

import jrm.webui.client.Client;

public class ScannerDirPanel extends DynamicForm
{
	public ScannerDirPanel()
	{
		super();
		setWidth100();
		setHeight100();
		setNumCols(4);
		setColWidths("200","*","20","20");
		setWrapItemTitles(false);
		setItems(
			new TextItem("tfRomsDest",Client.session.getMsg("MainFrame.lblRomsDest.text")) {{
				setWidth("*");
				setEndRow(false);
			}},
			new ButtonItem() {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle("");
				setEndRow(false);
			}},
			new SpacerItem(),
			new TextItem("tfDisksDest",Client.session.getMsg("MainFrame.lblDisksDest.text")) {{
				setWidth("*");
				setDisabled(true);
				setEndRow(false);
			}},
			new ButtonItem() {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle("");
				setDisabled(true);
				setEndRow(false);
			}},
			new CheckboxItem() {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
			}},
			new TextItem("tfSWDest",Client.session.getMsg("MainFrame.chckbxSoftwareDest.text")) {{
				setWidth("*");
				setDisabled(true);
				setEndRow(false);
			}},
			new ButtonItem() {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle("");
				setDisabled(true);
				setEndRow(false);
			}},
			new CheckboxItem() {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
			}},
			new TextItem("tfSWDisksDest",Client.session.getMsg("MainFrame.chckbxSwdisksdest.text")) {{
				setWidth("*");
				setDisabled(true);
				setEndRow(false);
			}},
			new ButtonItem() {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle("");
				setDisabled(true);
				setEndRow(false);
			}},
			new CheckboxItem() {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
			}},
			new TextItem("tfSamplesDest",Client.session.getMsg("MainFrame.lblSamplesDest.text")) {{
				setWidth("*");
				setDisabled(true);
				setEndRow(false);
			}},
			new ButtonItem() {{
				setStartRow(false);
				setIcon("icons/disk.png");
				setTitle("");
				setDisabled(true);
				setEndRow(false);
			}},
			new CheckboxItem() {{
				setStartRow(false);
				setShowLabel(false);
				setShowTitle(false);
			}},
			new TextAreaItem("listSrcDir",Client.session.getMsg("MainFrame.lblSrcDir.text")) {{
				setWidth("*");
				setEndRow(true);
			}}
		);
	}
}
