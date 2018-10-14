package jrm.webui.client.ui;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;

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
			new SectionStack() {{
				addSection(new SectionStackSection(Client.session.getMsg("MainFrame.systemsFilter.viewportBorderTitle")) {{
					setCanCollapse(false);
					setExpanded(true);
					setItems(systems = new ListGrid() {{
						setShowAllRecords(true);
						setSelectionProperty("selected");
						setFields(new ListGridField("name"));
						setSelectionAppearance(SelectionAppearance.CHECKBOX);
						addSelectionChangedHandler(event->{
							Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("property"), event.getState())));
						});
					}});
				}});
			}}
		);
	}
}
