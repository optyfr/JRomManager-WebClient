package jrm.webui.client;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.PreserveOpenState;
import com.smartgwt.client.types.TabBarControls;
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
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class MainWindow extends Window
{
	public MainWindow()
	{
		super();
		setTitle("JRomManager");
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
				setTitle("Profiles");
				setPane(new VLayout() {{
					addMembers(
						new SplitPane() {{
							ListGrid listgrid = new ListGrid() {{
								setShowFilterEditor(false);
								setShowHover(true);
								setCanHover(true);
								setHoverWidth(200);
								setDataSource(new RestDataSource() {{
									setID("profilesList");
									setDataFormat(DSDataFormat.XML);
									setDataURL("/datasources/"+getID());
									setOperationBindings(
										new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
									);
							        DataSourceTextField nameField = new DataSourceTextField("Name");
							        DataSourceTextField pathField = new DataSourceTextField("Path");
							        pathField.setHidden(true);
							        DataSourceTextField verField = new DataSourceTextField("version");
							        DataSourceTextField haveSetsField = new DataSourceTextField("haveSets");
							        DataSourceTextField haveRomsField = new DataSourceTextField("haveRoms");
							        DataSourceTextField haveDisksField = new DataSourceTextField("haveDisks");
							        DataSourceTextField createdField = new DataSourceTextField("created");
							        DataSourceTextField scannedField = new DataSourceTextField("scanned");
							        DataSourceTextField fixedField = new DataSourceTextField("fixed");
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
								new IButton("Import Dat"),
								new IButton("Import Software List")
							);
						}}
					);
				}});
			}});
			addTab(new Tab() {{
				setTitle("Scanner");
				setDisabled(true);
			}});
			addTab(new Tab() {{
				setTitle("Dir2Dat");
			}});
			addTab(new Tab() {{
				setTitle("Batch Tools");
			}});
			addTab(new Tab() {{
				setTitle("Settings");
			}});
		}});
		centerInPage();
		show();
	}

}
