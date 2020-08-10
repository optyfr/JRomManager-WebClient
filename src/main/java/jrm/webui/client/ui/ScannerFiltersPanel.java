package jrm.webui.client.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.user.client.Timer;
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

public final class ScannerFiltersPanel extends HLayout
{
	Systems systems;
	private boolean canResetPV = true;
	FilterForm filterForm;
	
	class Systems extends ListGrid
	{
		private boolean setproperty = true;
		
		public Systems()
		{
			setShowAllRecords(true);
			setAlternateRecordStyles(false);
			setSelectionProperty("selected");
			setFields(new ListGridField("name",Client.session.getMsg("MainFrame.systemsFilter.viewportBorderTitle")));
			setSelectionAppearance(SelectionAppearance.CHECKBOX);
			setShowSelectedStyle(false);
			setCanEdit(false);
			setCanRemoveRecords(false);
			addSelectionChangedHandler(event->{
				if(setproperty)
				{
					Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("property"), event.getState())));
					if(canResetPV)
						resetProfileViewer();
				}
			});
			setContextMenu(new Menu() {{
				addItem(new MenuItem() {{
					setTitle(Client.session.getMsg("MainFrame.mnSelect.text"));
					this.setSubmenu(new Menu() {{
						addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmSelectAll.text")) {{
							addClickHandler(event->resetProfileViewer(()->systems.selectAllRecords()));
						}});
						addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAllBios.text")) {{
							addClickHandler(event->resetProfileViewer(()->systems.selectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("BIOS")).collect(Collectors.toList()).toArray(new ListGridRecord[0]))));
						}});
						addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAllSoftwares.text")) {{
							addClickHandler(event->resetProfileViewer(()->systems.selectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("SOFTWARELIST")).collect(Collectors.toList()).toArray(new ListGridRecord[0]))));
						}});
					}});
				}});
				addItem(new MenuItem() {{
					setTitle(Client.session.getMsg("MainFrame.mnUnselect.text"));
					this.setSubmenu(new Menu() {{
						addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmSelectNone.text")) {{
							addClickHandler(event->resetProfileViewer(()->systems.deselectAllRecords()));
						}});
						addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAllBios.text")) {{
							addClickHandler(event->resetProfileViewer(()->systems.deselectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("BIOS")).collect(Collectors.toList()).toArray(new ListGridRecord[0]))));
						}});
						addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmAllSoftwares.text")) {{
							addClickHandler(event->resetProfileViewer(()->systems.deselectRecords(Stream.of(systems.getRecords()).filter(r->r.getAttribute("type").equals("SOFTWARELIST")).collect(Collectors.toList()).toArray(new ListGridRecord[0]))));
						}});
					}});
				}});
				addItem(new MenuItem(Client.session.getMsg("MainFrame.mntmInvertSelection.text")) {{
					addClickHandler(event->resetProfileViewer(()->{
						ListGridRecord[] to_unselect = systems.getSelectedRecords();
						List<ListGridRecord> to_unselect_list = Arrays.asList(to_unselect);
						ListGridRecord[] to_select = Stream.of(systems.getRecords()).filter(r->!to_unselect_list.contains(r)).collect(Collectors.toList()).toArray(new ListGridRecord[0]);
						systems.deselectRecords(to_unselect);
						systems.selectRecords(to_select);
					}));
				}});
			}});
		}
		
		@Override
		public void setData(Record[] data)
		{
			resetProfileViewer(()->{
				setproperty=false;
				super.setData(data);
				setproperty=true;
			});
		}
	}

	class FilterForm extends SettingsForm
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
				new CheckboxItem("chckbxIncludeClones", Client.session.getMsg("MainFrame.chckbxIncludeClones.text")) {{
					setTitleColSpan(2);
					setLabelAsTitle(true);
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue())));
					setDefaultValue(true);
				}},
				new CheckboxItem("chckbxIncludeDisks", Client.session.getMsg("MainFrame.chckbxIncludeDisks.text")) {{
					setTitleColSpan(2);
					setLabelAsTitle(true);
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue())));
					setDefaultValue(true);
				}},
				new CheckboxItem("chckbxIncludeSamples", Client.session.getMsg("MainFrame.chckbxIncludeSamples.text")) {{
					setTitleColSpan(2);
					setLabelAsTitle(true);
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), (boolean)getValue())));
					setDefaultValue(true);
				}},
				new SelectItem("cbMachineType", Client.session.getMsg("MainFrame.lblMachineType.text")) {{
					setTitleColSpan(2);
					setWidth("*");
					setValueMap("any","upright","cocktail");
					setDefaultValue("any");
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new SelectItem("cbOrientation", Client.session.getMsg("MainFrame.lblOrientation.text")) {{
					setTitleColSpan(2);
					setWidth("*");
					setValueMap("any","horizontal","vertical");
					setDefaultValue("any");
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new SelectItem("cbDriverStatus", Client.session.getMsg("MainFrame.lblDriverStatus.text")) {{
					setTitleColSpan(2);
					setWidth("*");
					setValueMap("good","imperfect","preliminary");
					setDefaultValue("preliminary");
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new SelectItem("cbSwMinSupport", Client.session.getMsg("MainFrame.lblSwMinSupport.text")) {{
					setTitleColSpan(2);
					setWidth("*");
					setValueMap("no","partial","yes");
					setDefaultValue("no");
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new SelectItem("cbYearMin") {{
					setShowTitle(false);
					setEndRow(false);
					setWidth("*");
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
				}},
				new StaticTextItem() {{
					setShowTitle(false);
					setDefaultValue(Client.session.getMsg("MainFrame.lblYear.text"));
					setTextAlign(Alignment.CENTER);
					setWidth("*");
				}},
				new SelectItem("cbYearMax") {{
					setShowTitle(false);
					setStartRow(false);
					setWidth("*");
					addChangedHandler(event->resetProfileViewer(()->setPropertyItemValue(getName(), fname2name.get(getName()), getValue().toString())));
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
		setMembers(
			new VLayout() {{
				setShowResizeBar(true);
				addMember(new LayoutSpacer("*", "*"));
				addMember(filterForm = new FilterForm());
				addMember(new LayoutSpacer("*", "*"));
			}},
			systems = new Systems()
		);
	}
	
	interface resetProfileViewerCB
	{
		void apply();
	}
	
	public void resetProfileViewer(resetProfileViewerCB cb)
	{
		canResetPV=false;
		cb.apply();
		canResetPV=true;
		resetProfileViewer();
	}
	
	private Timer resetTimer = null;
	
	public void resetProfileViewer()
	{
		if(resetTimer == null)
		{
			resetTimer = new Timer()
			{
				@Override
				public void run()
				{
					if (canResetPV)
						if (Client.mainwindow.scannerPanel.profileViewer != null && Client.childWindows.contains(Client.mainwindow.scannerPanel.profileViewer))
							Client.mainwindow.scannerPanel.profileViewer.anywareListList.reset();
				}
			};
		}
		resetTimer.cancel();
		resetTimer.schedule(1000);
	}
}
