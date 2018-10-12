package jrm.webui.client.ui;

import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;

public final class ScannerFiltersPanel extends HLayout
{
	ListGrid systems;
	
	public ScannerFiltersPanel()
	{
		super();
		setWidth100();
		setHeight100();
		setMembers(
			new DynamicForm() {{
				setWidth("50%");
				setShowResizeBar(true);
			}},
			systems = new ListGrid() {{
				setShowAllRecords(true);
				setFields(new ListGridField("type"),new ListGridField("name"));
				setSelectionAppearance(SelectionAppearance.CHECKBOX);
			}}
		);
	}
}
