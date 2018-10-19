package jrm.webui.client.ui;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;

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
				setSelectionProperty("selected");
				setFields(new ListGridField("name",Client.session.getMsg("MainFrame.systemsFilter.viewportBorderTitle")));
				setSelectionAppearance(SelectionAppearance.CHECKBOX);
				setShowSelectedStyle(false);
				addSelectionChangedHandler(event->{
					Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("property"), event.getState())));
				});
			}}
		);
	}
}
