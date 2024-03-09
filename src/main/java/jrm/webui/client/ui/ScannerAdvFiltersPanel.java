package jrm.webui.client.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.DataArrivedEvent;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_CatVer;
import jrm.webui.client.protocol.Q_NPlayers;
import jrm.webui.client.protocol.Q_Profile;

public final class ScannerAdvFiltersPanel extends HLayout
{
	TextItem catver_path;
	CatVerTree catver_tree;
	TextItem nplayers_path;
	NPlayersList nplayers_list;
	
	class NPlayersList extends ListGrid
	{
		boolean enableEvents = false;
		
		class DataSource extends RestDataSource
		{
			DataSource()
			{
				setID("NPlayers");
				setDataURL("/datasources/"+getID());
				setDataFormat(DSDataFormat.XML);
				OperationBinding ob = new OperationBinding();
				ob.setOperationType(DSOperationType.FETCH);
				ob.setDataProtocol(DSProtocol.POSTXML);
				setOperationBindings(ob);
				addField(new DataSourceTextField("Name",Client.getSession().getMsg("MainFrame.NPlayers")));
				DataSourceField dsf = new DataSourceTextField("ID");
				dsf.setPrimaryKey(true);
				dsf.setHidden(true);
				addField(dsf);
				dsf = new DataSourceBooleanField("isSelected");
				dsf.setHidden(true);
				addField(dsf);
				dsf = new DataSourceIntegerField("Cnt");
				dsf.setHidden(true);
				addField(dsf);
			}
		}
		
		NPlayersList()
		{
			setSelectionAppearance(SelectionAppearance.CHECKBOX);
			setShowAllRecords(true);
			setShowSelectedStyle(false);
			setAutoFetchData(true);
			setSelectionProperty("isSelected");
			setAlternateRecordStyles(false);
			setCanEdit(false);
			setCanRemoveRecords(false);
			addSelectionChangedHandler(event -> {
				if(enableEvents)
				{
					Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("ID"), event.getState())));
					if(ProfileViewer.canResetPV)
						ProfileViewer.reset();
				}
			});
			addDataArrivedHandler(event->enableEvents = true);
			setDataSource(new DataSource());
			ListGridField field = new ListGridField("Name");
			field.setCellFormatter((value, record, rowNum, colNum) -> value + " (" + record.getAttribute("Cnt") + ")");
			setFields(field);
			Menu menu = new Menu();
			MenuItem item = new MenuItem(Client.getSession().getMsg("MainFrame.SelectAll"));
			item.addClickHandler(event->nplayers_list.selectAllRecords());
			menu.addItem(item);
			item = new MenuItem(Client.getSession().getMsg("MainFrame.SelectNone"));
			item.addClickHandler(event->nplayers_list.deselectAllRecords());
			menu.addItem(item);
			item = new MenuItem(Client.getSession().getMsg("MainFrame.InvertSelection"));
			item.addClickHandler(event->{
				ListGridRecord[] to_unselect = nplayers_list.getSelectedRecords();
				List<ListGridRecord> to_unselect_list = Arrays.asList(to_unselect);
				ListGridRecord[] to_select = Stream.of(nplayers_list.getRecords()).filter(r->!to_unselect_list.contains(r)).collect(Collectors.toList()).toArray(new ListGridRecord[0]);
				nplayers_list.deselectRecords(to_unselect);
				nplayers_list.selectRecords(to_select);
			});
			menu.addItem(item);
			item = new MenuItem();
			item.setIsSeparator(true);
			menu.addItem(item);
			item = new MenuItem(Client.getSession().getMsg("ScannerAdvFilterPanel.mntmClear_1.text"));
			item.addClickHandler(event->Client.sendMsg(JsonUtils.stringify(Q_NPlayers.Load.instantiate().setPath(null))));
			menu.addItem(item);
			setContextMenu(menu);
		}
	}
	
	class CatVerTree extends TreeGrid
	{
		boolean enableEvents = false;
		
		class DataSource extends RestDataSource
		{
			DataSource()
			{
				setID("CatVer");
				setDataURL("/datasources/"+getID());
				setDataFormat(DSDataFormat.XML);
				OperationBinding ob = new OperationBinding();
				ob.setOperationType(DSOperationType.FETCH);
				ob.setDataProtocol(DSProtocol.POSTXML);
				setOperationBindings(ob);
				DataSourceField dsf = new DataSourceTextField("Name",Client.getSession().getMsg("MainFrame.Categories"));
				addField(dsf);
				dsf = new DataSourceTextField("ID");
				dsf.setPrimaryKey(true);
				addField(dsf);
				dsf = new DataSourceTextField("ParentID");
				dsf.setForeignKey("ID");
				dsf.setRootValue(1);
				addField(dsf);
				dsf = new DataSourceBooleanField("isOpen");
				dsf.setHidden(true);
				addField(dsf);
				dsf = new DataSourceBooleanField("isSelected");
				dsf.setHidden(true);
				addField(dsf);
				dsf = new DataSourceBooleanField("isFolder");
				dsf.setHidden(true);
				addField(dsf);
				dsf = new DataSourceIntegerField("Cnt");
				dsf.setHidden(true);
				addField(dsf);
			}
		}
		
		CatVerTree()
		{
			setShowAllRecords(true);
			setSelectionAppearance(SelectionAppearance.CHECKBOX);
			setShowSelectedStyle(false);
			setShowPartialSelection(true);
			setShowConnectors(true);
			setCascadeSelection(true);
			setCellPadding(0);
			setCanEdit(false);
			setCanRemoveRecords(false);
			setAutoFetchData(true);
//			setCellHeight(16);
			Tree tree = new Tree();
			tree.setModelType(TreeModelType.PARENT);
			tree.setRootValue(1);
			tree.setNameProperty("Name");
			tree.setIdField("ID");
			tree.setParentIdField("ParentID");
			tree.setOpenProperty("isOpen");
			tree.setIsFolderProperty("isFolder");
			setDataProperties(tree);
			setSelectionProperty("isSelected");
			setTreeFieldTitle(Client.getSession().getMsg("MainFrame.Categories"));
			setNodeIcon(null);
			setFolderIcon(null);
			addSelectionChangedHandler(event -> {
				if(enableEvents)
				{
					Client.sendMsg(JsonUtils.stringify(Q_Profile.SetProperty.instantiate().setProperty(event.getRecord().getAttribute("ID"), !isPartiallySelected(event.getRecord()) && event.getState())));
					if(ProfileViewer.canResetPV)
						ProfileViewer.reset();
				}
			});
			addDataArrivedHandler((DataArrivedEvent event) -> {
				enableEvents = true;
				markForRedraw();
			});
			setDataSource(new DataSource());
			TreeGridField field = new TreeGridField("Name");
			field.setCellFormatter((value, record, rowNum, colNum)->{
				TreeNode node = Tree.nodeForRecord(record);
				if(node.getAttributeAsBoolean("isFolder"))
				{
					TreeNode[] children = getData().getDescendantLeaves(node);
					if(children!=null)
					{
						int count = 0;
						for(TreeNode child : children)
						{
							if(isSelected(child))
								count += Integer.parseInt(child.getAttribute("Cnt"));
						}
						return value + " ("+count+")";
					}
				}
				return value + " ("+record.getAttribute("Cnt")+")";
			});
			setFields(field);
			Menu menu = new Menu();
			MenuItem mnitem = new MenuItem();
			mnitem.setTitle(Client.getSession().getMsg("MainFrame.Select"));
			Menu smenu = new Menu();
			MenuItem smnitem = new MenuItem();
			smnitem.setTitle(Client.getSession().getMsg("MainFrame.All"));
			smnitem.addClickHandler(event->catver_tree.selectAllRecords());
			smenu.addItem(smnitem);
			smnitem = new MenuItem();
			smnitem.setTitle(Client.getSession().getMsg("MainFrame.Mature"));
			smnitem.addClickHandler(event->catver_tree.selectRecords(catver_tree.getData().findAll(new AdvancedCriteria("Name", OperatorId.ENDS_WITH, "* Mature *"))));
			smenu.addItem(smnitem);
			mnitem.setSubmenu(smenu);
			menu.addItem(mnitem);
			mnitem = new MenuItem();
			mnitem.setTitle(Client.getSession().getMsg("MainFrame.Unselect"));
			smenu = new Menu();
			smnitem = new MenuItem();
			smnitem.setTitle(Client.getSession().getMsg("MainFrame.All"));
			smnitem.addClickHandler(event->catver_tree.deselectAllRecords());
			smenu.addItem(smnitem);
			smnitem = new MenuItem();
			smnitem.setTitle(Client.getSession().getMsg("MainFrame.Mature"));
			smnitem.addClickHandler(event->catver_tree.deselectRecords(catver_tree.getData().findAll(new AdvancedCriteria("Name", OperatorId.ENDS_WITH, "* Mature *"))));
			smenu.addItem(smnitem);
			mnitem.setSubmenu(smenu);
			menu.addItem(mnitem);
			mnitem = new MenuItem();
			mnitem.setIsSeparator(true);
			menu.addItem(mnitem);
			mnitem = new MenuItem(Client.getSession().getMsg("ScannerAdvFilterPanel.mntmClear.text"));
			mnitem.addClickHandler(event->Client.sendMsg(JsonUtils.stringify(Q_CatVer.Load.instantiate().setPath(null))));
			menu.addItem(mnitem);
			setContextMenu(menu);
		}
	}
	
	public ScannerAdvFiltersPanel()
	{
		super();
		VLayout nplayers_layout = new VLayout(); 
		nplayers_layout.setShowResizeBar(true);
		DynamicForm nplayers_form = new DynamicForm();
		nplayers_form.setCellPadding(0);
		nplayers_form.setNumCols(2);
		nplayers_form.setColWidths("*",26);
		nplayers_path = new TextItem();
		nplayers_path.setShowTitle(false);
		nplayers_path.setWidth("*");
		nplayers_path.setCanEdit(false);
		ButtonItem nplayers_frbt = new ButtonItem();
		nplayers_frbt.setStartRow(false);
		nplayers_frbt.setIcon("icons/disk.png");
		nplayers_frbt.setTitle(null);
		nplayers_frbt.setValueIconRightPadding(0);
		nplayers_frbt.setEndRow(false);
		nplayers_frbt.addClickHandler(event->new RemoteFileChooser("NPlayers", null, path -> Client.sendMsg(JsonUtils.stringify(Q_NPlayers.Load.instantiate().setPath(path[0].path)))));
		nplayers_form.setItems(nplayers_path, nplayers_frbt);
		nplayers_layout.setMembers(nplayers_form, nplayers_list = new NPlayersList());
		VLayout catver_layout = new VLayout();
		DynamicForm catver_form = new DynamicForm();
		catver_form.setWidth100();
		catver_form.setCellPadding(0);
		catver_form.setNumCols(2);
		catver_form.setColWidths("*",26);
		catver_path = new TextItem();
		catver_path.setShowTitle(false);
		catver_path.setWidth("*");
		catver_path.setCanEdit(false);
		ButtonItem catver_frbt = new ButtonItem();
		catver_frbt.setStartRow(false);
		catver_frbt.setIcon("icons/disk.png");
		catver_frbt.setTitle(null);
		catver_frbt.setValueIconRightPadding(0);
		catver_frbt.setEndRow(false);
		catver_frbt.addClickHandler(event -> new RemoteFileChooser("CatVer", null, path -> Client.sendMsg(JsonUtils.stringify(Q_CatVer.Load.instantiate().setPath(path[0].path)))));
		catver_form.setItems(catver_path,catver_frbt);
		catver_layout.setMembers(catver_form, catver_tree = new CatVerTree());
		setMembers(nplayers_layout, catver_layout);
	}

}
