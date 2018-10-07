package jrm.webui.client;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.PreserveOpenState;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import jrm.webui.client.protocol.Q_Profile;

public class ProfilePanel extends VLayout
{
	public ProfilePanel()
	{
		super();
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
					addDataArrivedHandler(new DataArrivedHandler()
					{
						@Override
						public void onDataArrived(DataArrivedEvent event)
						{
							selectRecord(0);
							listgrid.invalidateCache();
							listgrid.fetchData(new Criteria() {{addCriteria("Path", "");}});
						}
					});
					addRecordClickHandler(new RecordClickHandler()
					{
						@Override
						public void onRecordClick(RecordClickEvent event)
						{
							navigateDetailPane();
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
				setShowNavigationBar(true);
				setShowDetailToolStrip(true);
				setDetailToolButtons(
					new IButton(Client.session.getMsg("MainFrame.btnImportDat.text")) {{
						setAutoFit(true);
						setIcon("icons/script_go.png");
					}},
					new IButton(Client.session.getMsg("MainFrame.btnImportSL.text")) {{
						setAutoFit(true);
						setIcon("icons/application_go.png");
					}}
				);
				setDetailPane(listgrid);
			}}
		);
	}
}
