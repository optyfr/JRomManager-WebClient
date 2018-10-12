package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.*;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.SplitPane;

public final class RemoteFileChooser extends Window
{
	Label parent;
	ListGrid list;
	
	public interface CallBack
	{
		public void apply(String[] path);
	}
	
	public RemoteFileChooser(String context, CallBack cb)
	{
		super();
		setWidth(600);
		setHeight(500);
		final boolean isDir, isMultiple;
		switch(context)
		{
			case "tfRomsDest":
			case "tfDisksDest":
			case "tfSWDest":
			case "tfSWDisksDest":
			case "tfSamplesDest":
				isDir = true;
				isMultiple = false;
				break;
			case "listSrcDir":
				isDir = true;
				isMultiple = true;
				break;
			default:
				isDir = false;
				isMultiple = false;
				break;
		}
		setAutoCenter(true);
		setIsModal(true);
		if(isMultiple)
			setTitle(isDir?"Choose directories":"Choose files");
		else
			setTitle(isDir?"Choose a directory":"Choose a file");
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
					DataSourceTextField nameField = new DataSourceTextField("Name");
					DataSourceTextField pathField = new DataSourceTextField("Path");
					pathField.setHidden(true);
					pathField.setPrimaryKey(true);
					setFields(nameField, pathField);
				}});
				setFields(
					new ListGridField("Type") {{
						setWidth(20);
						setMaxWidth(20);
						setCellFormatter((value,record,rowNum,colNum)->"<img src='/images/icons/drive.png'/>");
					}},
					new ListGridField("Name") {{
						setWidth("*");
					}}
				);
			}});
			setDetailPane(list=new ListGrid() {{
				setShowFilterEditor(false);
				setShowHover(true);
				setCanHover(true);
				setHoverWidth(200);
				setAutoFetchData(true);
				setSelectionType(isMultiple?SelectionStyle.MULTIPLE:SelectionStyle.SINGLE);
				addRecordClickHandler(event->{
				});
				setInitialSort(new SortSpecifier("isDir", SortDirection.DESCENDING),new SortSpecifier("Name", SortDirection.ASCENDING));
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
						cb.apply(new String[] {path});
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
						DataSourceTextField nameField = new DataSourceTextField("Name");
						DataSourceTextField pathField = new DataSourceTextField("Path");
						pathField.setHidden(true);
						pathField.setPrimaryKey(true);
						DataSourceBooleanField isDir = new DataSourceBooleanField("isDir");
						DataSourceIntegerField sizefield = new DataSourceIntegerField("Size");
						DataSourceDateTimeField modifiedfield = new DataSourceDateTimeField("Modified") {{
							setDatetimeFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
						}};
						setFields(isDir, nameField, pathField, sizefield, modifiedfield);
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
					new ListGridField("Name") {{
						setWidth("*");
					}},
					new ListGridField("Size") {{
						setAutoFitWidth(true);
						setCellFormatter((value,record,rowNum,colNum)->{
							return ((int)value)<0?"":readableFileSize((int)value);
						});
					}},
					new ListGridField("Modified") {{
						setAutoFitWidth(true);
					}}
				);
				setDataProperties(new ResultSet() {{setUseClientFiltering(false);this.setUseClientSorting(true);}});
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
			setPadding(2);
			setMembersMargin(2);
			setMembers(
				new IButton("Cancel") {{
					setAutoFit(true);
					addClickHandler(e->RemoteFileChooser.this.markForDestroy());
				}},
				new LayoutSpacer() {{setWidth("*");}},
				new IButton("Choose") {{
					setAutoFit(true);
					addClickHandler(e->{
						ListGridRecord[] records =  list.getSelectedRecords();
						if(records.length>0)
						{
							cb.apply(Stream.of(records).map(record->record.getAttribute("Path")).collect(Collectors.toList()).toArray(new String[0]));
							RemoteFileChooser.this.markForDestroy();
						}
						else if(isDir)
						{
							String path = parent.getContents();
							cb.apply(new String[] {path});
							RemoteFileChooser.this.markForDestroy();
						}
					});
				}}
			);
		}});
		show();
	}
	
	public static String readableFileSize(long size)
	{
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return NumberFormat.getFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}
