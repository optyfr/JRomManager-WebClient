package jrm.webui.client.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

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
				setAlternateRecordStyles(false);
				setSelectionProperty("selected");
				setFields(new ListGridField("name",Client.session.getMsg("MainFrame.systemsFilter.viewportBorderTitle")));
				setSelectionAppearance(SelectionAppearance.CHECKBOX);
				setShowSelectedStyle(false);
				setCanEdit(false);
				setCanRemoveRecords(false);
				addSelectionChangedHandler(event->{
					Client.socket.send(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("property"), event.getState())));
				});
				setContextMenu(new Menu() {{
					addItem(new MenuItem() {{
						setTitle(Client.session.getMsg("MainFrame.mnSelect.text"));
						this.setSubmenu(new Menu() {{
							addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmSelectAll.text")) {{
								addClickHandler(event->systems.selectAllRecords());
							}});
							addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAllBios.text")) {{
								addClickHandler(event->systems.selectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("BIOS")).collect(Collectors.toList()).toArray(new ListGridRecord[0])));
							}});
							addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAllSoftwares.text")) {{
								addClickHandler(event->systems.selectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("SOFTWARELIST")).collect(Collectors.toList()).toArray(new ListGridRecord[0])));
							}});
						}});
					}});
					addItem(new MenuItem() {{
						setTitle(Client.session.getMsg("MainFrame.mnUnselect.text"));
						this.setSubmenu(new Menu() {{
							addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmSelectNone.text")) {{
								addClickHandler(event->systems.deselectAllRecords());
							}});
							addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAllBios.text")) {{
								addClickHandler(event->systems.deselectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("BIOS")).collect(Collectors.toList()).toArray(new ListGridRecord[0])));
							}});
							addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAllSoftwares.text")) {{
								addClickHandler(event->systems.deselectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("SOFTWARELIST")).collect(Collectors.toList()).toArray(new ListGridRecord[0])));
							}});
						}});
					}});
					addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmInvertSelection.text")) {{
						addClickHandler(event->{
							ListGridRecord[] to_unselect = systems.getSelectedRecords();
							List<ListGridRecord> to_unselect_list = Arrays.asList(to_unselect);
							ListGridRecord[] to_select = Stream.of(systems.getRecords()).filter(r->!to_unselect_list.contains(r)).collect(Collectors.toList()).toArray(new ListGridRecord[0]);
							systems.deselectRecords(to_unselect);
							systems.selectRecords(to_select);
						});
					}});
				}});
			}}
		);
	}
}
