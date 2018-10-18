package jrm.webui.client.ui;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

public final class ScannerAdvFiltersPanel extends HLayout
{

	public ScannerAdvFiltersPanel()
	{
		super();
		setMembers(new VLayout() {{
			setShowResizeBar(true);
			setMembers(new DynamicForm() {{
				setCellPadding(0);
				setNumCols(2);
				setColWidths("*",24);
				setItems(new TextItem() {{
					setShowTitle(false);
					setWidth("*");
					setCanEdit(false);
				}}, new ButtonItem() {{
					setStartRow(false);
					setIcon("icons/disk.png");
					setTitle(null);
					setValueIconRightPadding(0);
					setEndRow(false);
				}});
			}}, new ListGrid() {{
				
			}});
		}}, new VLayout() {{
			setMembers(new DynamicForm() {{
				setCellPadding(0);
				setNumCols(2);
				setColWidths("*",24);
				setItems(new TextItem() {{
					setShowTitle(false);
					setWidth("*");
					setCanEdit(false);
				}}, new ButtonItem() {{
					setStartRow(false);
					setIcon("icons/disk.png");
					setTitle(null);
					setValueIconRightPadding(0);
					setEndRow(false);
				}});
			}}, new TreeGrid() {{
				
			}});
		}});
	}

}
