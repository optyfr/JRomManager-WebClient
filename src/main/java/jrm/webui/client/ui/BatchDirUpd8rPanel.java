package jrm.webui.client.ui;

import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class BatchDirUpd8rPanel extends VLayout
{

	ListGrid src;
	ListGrid dst;
	
	public BatchDirUpd8rPanel()
	{
		setHeight100();
		addMember(src = new ListGrid() {{
			setHeight("40%");
			setShowResizeBar(true);
		}});
		addMember(dst = new ListGrid() {{
			setHeight("60%");
		}});
		addMember(new HLayout() {{
			setHeight(20);
			addMember(new LayoutSpacer("*",20));
			addMember(new DynamicForm() {{
				setColWidths(100,50);
				setWrapItemTitles(false);
				setItems(new CheckboxItem("dryrun", "dry run") {{
					setLabelAsTitle(true);
					setShowLabel(false);
				}});
			}});
			addMember(new IButton("Start", event-> {}));
		}});
	}

}
