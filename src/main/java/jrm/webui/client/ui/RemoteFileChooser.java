package jrm.webui.client.ui;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.layout.VLayout;

import jrm.webui.client.Client;

public class RemoteFileChooser extends Window
{

	public RemoteFileChooser(String context)
	{
		super();
		setWidth(500);
		setHeight(400);
		setAutoCenter(true);
		setIsModal(true);
		setCanDragResize(true);
		setCanDragReposition(true);
		addCloseClickHandler(new CloseClickHandler()
		{
			@Override
			public void onCloseClick(CloseClickEvent event)
			{
				RemoteFileChooser.this.markForDestroy();
			}
		});
		addItem(new SplitPane() {{
			setHeight("*");
			setNavigationPane(new ListGrid() {{
				
			}});
			setDetailPane(new ListGrid() {{
				setShowFilterEditor(false);
				setShowHover(true);
				setCanHover(true);
				setHoverWidth(200);
				setAutoFetchData(true);
				addRecordClickHandler(new RecordClickHandler()
				{
					@Override
					public void onRecordClick(RecordClickEvent event)
					{
					}
				});
				addRecordDoubleClickHandler(new RecordDoubleClickHandler()
				{
					@Override
					public void onRecordDoubleClick(RecordDoubleClickEvent event)
					{
						ListGridRecord record = event.getRecord();
						if(record.getAttributeAsBoolean("isDir"))
						{
							Criteria criteria = new Criteria("Parent",record.getAttribute("Parent")+"/"+record.getAttribute("Name"));
							invalidateCache();
							fetchData(criteria);
						}
					}
				});
				setDataSource(new RestDataSource() {{
					setID("remoteFileChooser");
					setDataURL("/datasources/"+getID());
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
							new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
					);
					DataSourceTextField nameField = new DataSourceTextField("Name", Client.session.getMsg("FileTableModel.Profile"));
					DataSourceTextField pathField = new DataSourceTextField("Parent");
					pathField.setHidden(true);
					pathField.setPrimaryKey(true);
					DataSourceBooleanField isDir = new DataSourceBooleanField("isDir");
					setFields(isDir, nameField,pathField);
				}});
				setFields(
					new ListGridField("isDir") {{
						setWidth(20);
						setTitle("");
						setMaxWidth(20);
						setCellFormatter(new CellFormatter()
						{
							@Override
							public String format(Object value, ListGridRecord record, int rowNum, int colNum)
							{
								return ((boolean)value)?"<img src='/images/icons/folder.png'/>":"<img src='/images/icons/file.png'/>";
							}
						});
					}},
					new ListGridField("Name")
				);
				setDataProperties(new ResultSet() {{setFilterLocalData(false);}});
			}});
			setNavigationPaneWidth(100);
		}});
		addItem(new VLayout() {{
			addMember(new Label("pipo") {{
				setWidth100();
				setBorder("1px inset gray");
			}});
		}});
		show();
	}
}
