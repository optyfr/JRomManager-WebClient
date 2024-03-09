package jrm.webui.client.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Profile;
import jrm.webui.client.utils.EnhJSO;

public final class ScannerFiltersPanel extends HLayout	//NOSONAR
{
	Systems systems;
	Sources sources;
	FilterForm filterForm;
	
	class Systems extends ListGrid	//NOSONAR
	{
		private boolean setproperty = true;
		
		public Systems()
		{
			setShowAllRecords(true);
			setAlternateRecordStyles(false);
			setSelectionProperty("selected");
			setFields(new ListGridField("name",Client.getSession().getMsg("MainFrame.systemsFilter.viewportBorderTitle")));
			setSelectionAppearance(SelectionAppearance.CHECKBOX);
			setShowSelectedStyle(false);
			setCanEdit(false);
			setCanRemoveRecords(false);
			addSelectionChangedHandler(event->{
				if(setproperty)
				{
					Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("property"), event.getState())));
					if(ProfileViewer.canResetPV)
						ProfileViewer.reset();
				}
			});
			final var menu = new Menu();
			final var item1 = new MenuItem();
			item1.setTitle(Client.getSession().getMsg("MainFrame.mnSelect.text"));
			Menu submenu1 = new Menu();
			submenu1.addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmSelectAll.text")) {{
				addClickHandler(event->ProfileViewer.reset(()->systems.selectAllRecords()));
			}});
			submenu1.addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmAllBios.text")) {{
				addClickHandler(event->ProfileViewer.reset(()->systems.selectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("BIOS")).collect(Collectors.toList()).toArray(new ListGridRecord[0]))));
			}});
			submenu1.addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmAllSoftwares.text")) {{
				addClickHandler(event->ProfileViewer.reset(()->systems.selectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("SOFTWARELIST")).collect(Collectors.toList()).toArray(new ListGridRecord[0]))));
			}});
			item1.setSubmenu(submenu1);
			menu.addItem(item1);
			final var item2 = new MenuItem();
			item2.setTitle(Client.getSession().getMsg("MainFrame.mnUnselect.text"));
			Menu submenu2 = new Menu();
			submenu2.addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmSelectNone.text")) {{
				addClickHandler(event->ProfileViewer.reset(()->systems.deselectAllRecords()));
			}});
			submenu2.addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmAllBios.text")) {{
				addClickHandler(event->ProfileViewer.reset(()->systems.deselectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("BIOS")).collect(Collectors.toList()).toArray(new ListGridRecord[0]))));
			}});
			submenu2.addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmAllSoftwares.text")) {{
				addClickHandler(event->ProfileViewer.reset(()->systems.deselectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("SOFTWARELIST")).collect(Collectors.toList()).toArray(new ListGridRecord[0]))));
			}});
			item2.setSubmenu(submenu2);
			menu.addItem(item2);
			final var item3 = new MenuItem(Client.getSession().getMsg("MainFrame.mntmInvertSelection.text"));
			item3.addClickHandler(event->ProfileViewer.reset(()->{
				ListGridRecord[] toUnselect = systems.getSelectedRecords();
				List<ListGridRecord> toUnselectList = Arrays.asList(toUnselect);
				ListGridRecord[] toSelect = Stream.of(systems.getRecords()).filter(r->!toUnselectList.contains(r)).collect(Collectors.toList()).toArray(new ListGridRecord[0]);
				systems.deselectRecords(toUnselect);
				systems.selectRecords(toSelect);
			}));
			menu.addItem(item3);
			setContextMenu(menu);
		}
		
		@Override
		public ListGrid setData(Record[] data)
		{
			ProfileViewer.reset(()->{
				setproperty=false;
				super.setData(data);
				setproperty=true;
			});
			return this;
		}
	}

	class Sources extends ListGrid	//NOSONAR
	{
		private boolean setproperty = true;
		
		public Sources()
		{
			setShowAllRecords(true);
			setAlternateRecordStyles(false);
			setSelectionProperty("selected");
			setFields(new ListGridField("name",Client.getSession().getMsg("MainFrame.sourcesFilter.viewportBorderTitle")));
			setSelectionAppearance(SelectionAppearance.CHECKBOX);
			setShowSelectedStyle(false);
			setCanEdit(false);
			setCanRemoveRecords(false);
			addSelectionChangedHandler(event->{
				if(setproperty)
				{
					Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("property"), event.getState())));
					if(ProfileViewer.canResetPV)
						ProfileViewer.reset();
				}
			});
			setContextMenu(new Menu() {{
				addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmSelectAll.text")) {{
					addClickHandler(event->ProfileViewer.reset(()->sources.selectAllRecords()));
				}});
				addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmSelectNone.text")) {{
					addClickHandler(event->ProfileViewer.reset(()->sources.deselectAllRecords()));
				}});
				addItem(new MenuItem(Client.getSession().getMsg("MainFrame.mntmInvertSelection.text")) {{
					addClickHandler(event->ProfileViewer.reset(()->{
						ListGridRecord[] to_unselect = sources.getSelectedRecords();
						List<ListGridRecord> to_unselect_list = Arrays.asList(to_unselect);
						ListGridRecord[] to_select = Stream.of(sources.getRecords()).filter(r->!to_unselect_list.contains(r)).collect(Collectors.toList()).toArray(new ListGridRecord[0]);
						sources.deselectRecords(to_unselect);
						sources.selectRecords(to_select);
					}));
				}});
			}});
		}
		
		@Override
		public ListGrid setData(Record[] data)
		{
			ProfileViewer.reset(()->{
				setproperty=false;
				super.setData(data);
				setproperty=true;
			});
			return this;
		}
	}

	class FilterForm extends SettingsForm	//NOSONAR
	{
		public FilterForm()
		{
			this(null);
		}
		
		public FilterForm(EnhJSO settings)
		{
			setWidth("80%");
			setLayoutAlign(Alignment.CENTER);
			setNumCols(3);
			setColWidths("*",80,"*");
			setItems(
				new CheckboxItem("chckbxIncludeClones", Client.getSession().getMsg("MainFrame.chckbxIncludeClones.text")) {{
					setTitleColSpan(2);
					setLabelAsTitle(true);
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue())));
					setDefaultValue(true);
				}},
				new CheckboxItem("chckbxIncludeDisks", Client.getSession().getMsg("MainFrame.chckbxIncludeDisks.text")) {{
					setTitleColSpan(2);
					setLabelAsTitle(true);
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue())));
					setDefaultValue(true);
				}},
				new CheckboxItem("chckbxIncludeSamples", Client.getSession().getMsg("MainFrame.chckbxIncludeSamples.text")) {{
					setTitleColSpan(2);
					setLabelAsTitle(true);
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue())));
					setDefaultValue(true);
				}},
				new SelectItem("cbMachineType", Client.getSession().getMsg("MainFrame.lblMachineType.text")) {{
					setTitleColSpan(2);
					setWidth("*");
					setValueMap("any","upright","cocktail");
					setDefaultValue("any");
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new SelectItem("cbOrientation", Client.getSession().getMsg("MainFrame.lblOrientation.text")) {{
					setTitleColSpan(2);
					setWidth("*");
					setValueMap("any","horizontal","vertical");
					setDefaultValue("any");
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new SelectItem("cbDriverStatus", Client.getSession().getMsg("MainFrame.lblDriverStatus.text")) {{
					setTitleColSpan(2);
					setWidth("*");
					setValueMap("good","imperfect","preliminary");
					setDefaultValue("preliminary");
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new SelectItem("cbSwMinSupport", Client.getSession().getMsg("MainFrame.lblSwMinSupport.text")) {{
					setTitleColSpan(2);
					setWidth("*");
					setValueMap("no","partial","yes");
					setDefaultValue("no");
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new SelectItem("cbYearMin") {{
					setShowTitle(false);
					setEndRow(false);
					setWidth("*");
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new StaticTextItem() {{
					setShowTitle(false);
					setDefaultValue(Client.getSession().getMsg("MainFrame.lblYear.text"));
					setTextAlign(Alignment.CENTER);
					setWidth("*");
				}},
				new SelectItem("cbYearMax") {{
					setShowTitle(false);
					setStartRow(false);
					setWidth("*");
					addChangedHandler(event->ProfileViewer.reset(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}}
			);
			if(hasSettings)
				initPropertyItemValues(settings);
		}
	}
	
	public ScannerFiltersPanel()
	{
		super();
		setWidth100();
		setHeight100();
		final var left = new VLayout();
		left.setShowResizeBar(true);
		left.addMember(new LayoutSpacer("*", "*"));
		filterForm = new FilterForm();
		left.addMember(filterForm);
		left.addMember(new LayoutSpacer("*", "*"));
		final var right = new VLayout();
		systems = new Systems();
		systems.setShowResizeBar(true);
		right.addMember(systems);
		sources = new Sources();
		right.addMember(sources);
		setMembers(left, right);
	}
	
}
