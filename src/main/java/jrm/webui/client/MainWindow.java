package jrm.webui.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.tree.TreeGrid;

import jrm.webui.client.protocol.Q_Profile;

public class MainWindow extends Window
{
	public MainWindow()
	{
		super();
		setTitle("JRomManager Web Client");
		setWidth(800);
		setHeight(600);
		setAnimateMinimize(true);
		setCanDragReposition(true);
		setCanDragResize(true);
		setShowFooter(true);
		setShowHeaderIcon(true);
		setShowMaximizeButton(true);
		Map<String,Object> headerIconDefaults = new HashMap<>();
		headerIconDefaults.put("width", 16);
		headerIconDefaults.put("height", 16);
		headerIconDefaults.put("src", "rom.png");
		setHeaderIconDefaults(headerIconDefaults);
		setShowHeaderIcon(true);
		addCloseClickHandler(new CloseClickHandler()
		{
			@Override
			public void onCloseClick(CloseClickEvent event)
			{
				close();
			}
		});
		addItem(new TabSet() {{
			setPaneMargin(0);
			setTabBarControls(
				TabBarControls.TAB_SCROLLER,
				TabBarControls.TAB_PICKER
			);
			addTab(new Tab() {{
				setTitle(Client.session.getMsg("MainFrame.Profiles"));
				setPane(new VLayout() {{
					addMembers(
						new SplitPane() {{
							ListGrid listgrid = new ListGrid() {{
								setShowFilterEditor(false);
								setShowHover(true);
								setCanHover(true);
								setHoverWidth(200);
								addRecordDoubleClickHandler(new RecordDoubleClickHandler()
								{
									@Override
									public void onRecordDoubleClick(RecordDoubleClickEvent event)
									{
										Client.socket.send(JsonUtils.stringify(Q_Profile.Load.instantiate().setPath(event.getRecord().getAttribute("Path"))));
									}
								});
								setDataSource(new RestDataSource() {{
									setID("profilesList");
									setDataFormat(DSDataFormat.XML);
									setDataURL("/datasources/"+getID());
									setOperationBindings(
										new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
									);
							        DataSourceTextField nameField = new DataSourceTextField("Name",Client.session.getMsg("FileTableModel.Profile"));
							        DataSourceTextField pathField = new DataSourceTextField("Path");
							        pathField.setHidden(true);
							        DataSourceTextField verField = new DataSourceTextField("version",Client.session.getMsg("FileTableModel.Version"));
							        DataSourceTextField haveSetsField = new DataSourceTextField("haveSets",Client.session.getMsg("FileTableModel.HaveSets"));
							        DataSourceTextField haveRomsField = new DataSourceTextField("haveRoms",Client.session.getMsg("FileTableModel.HaveRoms"));
							        DataSourceTextField haveDisksField = new DataSourceTextField("haveDisks",Client.session.getMsg("FileTableModel.HaveDisks"));
							        DataSourceTextField createdField = new DataSourceTextField("created",Client.session.getMsg("FileTableModel.Created"));
							        DataSourceTextField scannedField = new DataSourceTextField("scanned",Client.session.getMsg("FileTableModel.Scanned"));
							        DataSourceTextField fixedField = new DataSourceTextField("fixed",Client.session.getMsg("FileTableModel.Fixed"));
							        setFields(nameField, pathField, verField, haveSetsField, haveRomsField, haveDisksField, createdField, scannedField, fixedField);
								}});
							}};
							setNavigationPane(new TreeGrid() {{
								setShowRoot(true);
								setAutoFetchData(true);
								setLoadDataOnDemand(false);
								setAutoFitFieldWidths(true);
								setAutoPreserveOpenState(PreserveOpenState.ALWAYS);
								setIndentSize(10);
								setExtraIconGap(0);
								setShowHover(true);
								setHoverWidth(200);
								setCanHover(true);
								addRecordClickHandler(new RecordClickHandler()
								{
									@Override
									public void onRecordClick(RecordClickEvent event)
									{
										String path = event.getRecord().getAttribute("Path");
										listgrid.invalidateCache();
										listgrid.fetchData(new Criteria() {{addCriteria("Path", path==null?"":path);}});
									}
								});
								//setShowConnectors(true);
								setDataSource(new RestDataSource() {{
									setID("profilesTree");
							        //setRequestProperties(new DSRequest() {{setData(Collections.singletonMap("pipo", "papa"));}});
									setOperationBindings(
										new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
									);
									setDataFormat(DSDataFormat.XML);
									setDataURL("/datasources/"+getID());
							        DataSourceTextField nameField = new DataSourceTextField("title");
							        DataSourceTextField pathField = new DataSourceTextField("Path");
							        pathField.setHidden(true);
							        DataSourceIntegerField IDField = new DataSourceIntegerField("ID");  
							        IDField.setPrimaryKey(true);  
							        IDField.setRequired(true);  
							        DataSourceIntegerField parentIDField = new DataSourceIntegerField("ParentID");  
							        parentIDField.setRequired(true);  
							        parentIDField.setForeignKey(id + ".ID");  
							        parentIDField.setRootValue("0");
							        setFields(nameField, pathField, IDField, parentIDField);  
								}});
								setFields(new ListGridField("title") {{
									setHoverCustomizer(new HoverCustomizer()
									{
										
										@Override
										public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum)
										{
											SC.logWarn(record.getAttribute("title"));
											return record.getAttribute("title");
										}
									});
								}});
							}});
							setNavigationPaneWidth(200);
							setListPane(listgrid);
						}},
						new ToolStrip() {{
							setAlign(Alignment.CENTER);
							addMembers(
								new IButton(Client.session.getMsg("MainFrame.btnImportDat.text")) {{setAutoFit(true);}},
								new IButton(Client.session.getMsg("MainFrame.btnImportSL.text")) {{setAutoFit(true);}}
							);
						}}
					);
				}});
			}});
			addTab(new Tab() {{
				setTitle(Client.session.getMsg("MainFrame.Scanner"));
				setDisabled(true);
			}});
			addTab(new Tab() {{
				setTitle(Client.session.getMsg("MainFrame.Dir2Dat"));
			}});
			addTab(new Tab() {{
				setTitle(Client.session.getMsg("MainFrame.BatchTools"));
			}});
			addTab(new Tab() {{
				setTitle(Client.session.getMsg("MainFrame.Settings"));
			}});
		}});
		centerInPage();
		show();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
