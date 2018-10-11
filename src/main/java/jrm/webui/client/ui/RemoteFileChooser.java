package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.SplitPane;

import jrm.webui.client.Client;

public final class RemoteFileChooser extends Window
{
	Label parent;
	ListGrid list;
	
	public interface CallBack
	{
		public void apply(String path);
	}
	
	public RemoteFileChooser(String context, CallBack cb)
	{
		super();
		setWidth(500);
		setHeight(400);
		switch(context)
		{
			case "tfRomsDest":
				setTitle("Choose a directory");
			default:
				setTitle("Choose a file or directory");
				break;
		}
		setAutoCenter(true);
		setIsModal(true);
		setCanDragResize(true);
		setCanDragReposition(true);
		addCloseClickHandler(event->RemoteFileChooser.this.markForDestroy());
		addItem(new SplitPane() {{
			setHeight("*");
			setNavigationPane(new ListGrid() {{
				setShowFilterEditor(false);
				setShowHover(true);
				setCanHover(true);
				setHoverWidth(200);
				setAutoFetchData(true);
				addRecordClickHandler(event->{
					list.getDataSource().setRequestProperties(new DSRequest() {{
						Map<String,String> params = new HashMap<>();
						params.put("context", context);
						params.put("parent", event.getRecord().getAttribute("Path"));
						setData(params);
					}});
					list.invalidateCache();
				});
				setDataSource(new RestDataSource() {{
					setID("remoteRootChooser");
					setDataURL("/datasources/"+getID());
					setRequestProperties(new DSRequest() {{setData(Collections.singletonMap("context", context));}});
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
					);
					DataSourceTextField nameField = new DataSourceTextField("Name", Client.session.getMsg("FileTableModel.Profile"));
					DataSourceTextField pathField = new DataSourceTextField("Path");
					pathField.setHidden(true);
					pathField.setPrimaryKey(true);
					setFields(nameField, pathField);
				}});
			}});
			setDetailPane(list=new ListGrid() {{
				setShowFilterEditor(false);
				setShowHover(true);
				setCanHover(true);
				setHoverWidth(200);
				setAutoFetchData(true);
				addRecordClickHandler(event->{
				});
				addRecordDoubleClickHandler(event->{
					ListGridRecord record = event.getRecord();
					String path = record.getAttribute("Path");
					if(record.getAttributeAsBoolean("isDir"))
					{
						getDataSource().setRequestProperties(new DSRequest() {{
							Map<String,String> params = new HashMap<>();
							params.put("context", context);
							params.put("parent", path);
							//setParams(params);
							setData(params);
						}});
						invalidateCache();
					}
					else
					{
						cb.apply(path);
						RemoteFileChooser.this.markForDestroy();
					}
				});
				setDataSource(new RestDataSource() {
					{
						setID("remoteFileChooser");
						setDataURL("/datasources/"+getID());
						setRequestProperties(new DSRequest() {{setData(Collections.singletonMap("context", context));}});
						setDataFormat(DSDataFormat.XML);
						setOperationBindings(
							new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
						);
						DataSourceTextField nameField = new DataSourceTextField("Name", Client.session.getMsg("FileTableModel.Profile"));
						DataSourceTextField pathField = new DataSourceTextField("Path");
						pathField.setHidden(true);
						pathField.setPrimaryKey(true);
						DataSourceBooleanField isDir = new DataSourceBooleanField("isDir");
						setFields(isDir, nameField, pathField);
					}
					@Override
					protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data) {
						parent.setContents(XMLTools.selectString(data, "/response/parent"));
						super.transformResponse(dsResponse, dsRequest, data);
					};
				});
				setFields(
					new ListGridField("isDir") {{
						setWidth(20);
						setMaxWidth(20);
						setCellFormatter((value,record,rowNum,colNum)->"<img src='/images/icons/"+((boolean)value?"folder.png":"page.png")+"'/>");
					}},
					new ListGridField("Name")
				);
				setDataProperties(new ResultSet() {{setFilterLocalData(false);}});
			}});
			setDetailToolButtons(parent=new Label("parent") {{
				setWidth100();
				setBorder("1px inset");
			}});
			setNavigationPaneWidth(100);
		}});
		addItem(new HLayout() {{
			setHeight(20);
			setLayoutAlign(Alignment.RIGHT);
			setPaddingAsLayoutMargin(true);
			setPadding(5);
			setMembersMargin(5);
			setMembers(
				new IButton("Cancel") {{
					setAutoFit(true);
					addClickHandler(e->RemoteFileChooser.this.markForDestroy());
				}},
				new LayoutSpacer() {{setWidth("*");}},
				new IButton("Choose") {{
					setAutoFit(true);
					addClickHandler(e->{
						Record record =  list.getSelectedRecord();
						if(record!=null)
						{
							String path = record.getAttribute("Path");
							cb.apply(path);
							RemoteFileChooser.this.markForDestroy();
						}
					});
				}}
			);
		}});
		show();
	}
}
