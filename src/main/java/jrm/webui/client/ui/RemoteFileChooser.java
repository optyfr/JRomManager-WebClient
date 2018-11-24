package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.*;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.*;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;

public final class RemoteFileChooser extends Window
{
	Label parentLab;
	Progressbar pb;
	Label pb_text;
	LayoutSpacer pb_spacer;

	static UploadList list;
	
	String parent;

	public class PathInfo
	{
		String path;
		String parent;
		String name;
		
		public PathInfo(String path, String parent, String name)
		{
			this.path = path;
			this.parent = parent;
			this.name = name;
		}
		
		public PathInfo(Record record)
		{
			this(record.getAttribute("Path"), RemoteFileChooser.this.parent, record.getAttribute("Name"));
		}
	}
	
	public interface CallBack
	{
		public void apply(PathInfo[] path);
	}
	
	public class UploadList extends ListGrid
	{
		private int tot;
		private float val[];
		
		@SuppressWarnings("serial")
		public UploadList(String context, CallBack cb)
		{
			super();
			final boolean isMultiple, isChoose;
			switch(context)
			{
				case "tfRomsDest":
				case "tfDisksDest":
				case "tfSWDest":
				case "tfSWDisksDest":
				case "tfSamplesDest":
					isMultiple = false;
					isChoose = true;
					break;
				case "listSrcDir":
				case "addDatSrc":
					isMultiple = true;
					isChoose = true;
					break;
				case "manageUploads":
					isMultiple = true;
					isChoose = false;
					break;
				case "updDat":
				case "updTrnt":
					isMultiple = false;
					isChoose = true;
					break;
				case "importDat":
					isMultiple = true;
					isChoose = true;
					break;
				case "addDat":
				case "addTrnt":
				default:
					isMultiple = false;
					isChoose = true;
					break;
			}
			setBorder("2px solid lightgrey");
			setID("UploadList");
			setShowFilterEditor(false);
			setShowHover(true);
			setCanHover(true);
			setHoverWidth(200);
			setAutoFetchData(true);
			setSelectionType(isMultiple?SelectionStyle.MULTIPLE:SelectionStyle.SINGLE);
			addEditFailedHandler(event->startEditing(event.getRowNum(),event.getColNum()));
			addEditCompleteHandler(event->{
				if(event.getDsResponse().getStatus()==0)
					if(event.getOldValues()!=null)
						refreshData((dsResponse, data, dsRequest)->selectSingleRecord(event.getNewValuesAsRecord()));
			});
			setContextMenu(new Menu() {{
				setItems(
					new MenuItem() {{
						setTitle("Create dir");
						addClickHandler(event->{
							UploadList.this.startEditingNew(new HashMap<String,Object>() {{
								put("Name","New Folder");
								put("isDir",true);
								put("Size",-1);
							}});
						});
					}},
					new MenuItem() {{
						setTitle("Edit selection");
						addClickHandler(event -> UploadList.this.startEditing(UploadList.this.getRecordIndex(UploadList.this.getSelectedRecord())));
						setEnableIfCondition((target, menu, item) -> !isChoose && UploadList.this.getSelectedRecords().length == 1);
					}},
					new MenuItem() {{
						setTitle("Delete selection");
						addClickHandler(event -> UploadList.this.removeSelectedData());
						setEnableIfCondition((target, menu, item) -> !isChoose && UploadList.this.getSelectedRecords().length > 0);
					}}
				);
			}});
			addRecordClickHandler(event->{
			});
			addRecordDoubleClickHandler(event->{
				ListGridRecord record = event.getRecord();
				String path = record.getAttribute("Path");
				String name = record.getAttribute("Name");
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
				else if(isChoose)
				{
					if(cb!=null)
						cb.apply(new PathInfo[] {new PathInfo(path, parent, name)});
					RemoteFileChooser.this.markForDestroy();
				}
			});
			setInitialSort(new SortSpecifier("isDir", SortDirection.DESCENDING),new SortSpecifier("Name", SortDirection.ASCENDING));
			setDataSource(new RestDataSource() {
				{
					setID("remoteFileChooser");
					setDataURL("/datasources/"+getID());
					setRequestProperties(new DSRequest() {{setData(Collections.singletonMap("context", context));}});
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}}
					);
					DataSourceTextField nameField = new DataSourceTextField("Name");
					nameField.setPrimaryKey(true);
					DataSourceTextField pathField = new DataSourceTextField("Path");
					pathField.setHidden(true);
					DataSourceBooleanField isDir = new DataSourceBooleanField("isDir");
					isDir.setCanEdit(false);
					DataSourceIntegerField sizefield = new DataSourceIntegerField("Size");
					sizefield.setCanEdit(false);
					DataSourceDateTimeField modifiedfield = new DataSourceDateTimeField("Modified") {{
						setDatetimeFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
						setCanEdit(false);
					}};
					setFields(isDir, nameField, pathField, sizefield, modifiedfield);
				}
				@Override
				protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data) {
					if (dsResponse.getStatus() == 0)
						parentLab.setContents(parent = XMLTools.selectString(data, "/response/parent"));
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
			setDataProperties(new ResultSet() {{
				setUseClientFiltering(false);
				setUseClientSorting(true);
			}});
		}

		public String readableFileSize(long size)
		{
		    if(size <= 0) return "0";
		    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		    return NumberFormat.getFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
		}
		
		@JsMethod
		public String getParentPath()
		{
			return parentLab.getContents();
		}
		
		@JsMethod
		public void setProgress(int idx, float val)
		{
			this.val[idx] = val;
			float valtot = 0;
			int cnt = 0;
			for(int i = 0; i < this.tot; i++)
			{
				valtot += this.val[i];
				if(this.val[i]==1)
					cnt++;
			}
			pb.setPercentDone((int)(valtot*100/this.tot));
			pb_text.setContents(cnt+"/"+tot);
			if(cnt==tot)
			{
				pb.hide();
				pb_text.hide();
				pb_spacer.show();
				invalidateCache();
			}
		}
		
		@JsMethod
		public void initProgress(int tot)
		{
			this.tot = tot;
			this.val = new float[tot];
			for(int i = 0; i < this.tot; i++)
				this.val[i]=0;
			pb.setPercentDone(0);
			pb.show();
			pb_text.setContents(0+"/"+tot);
			pb_text.show();
			pb_spacer.hide();
		}
		
		@JsMethod
		public native void handleFileSelect(JavaScriptObject files) /*-{
			if(typeof files !== "undefined")
			{
				if(files.length > 0)
				{
					this.initProgress(files.length);
					for (var i=0, l=files.length; i<l; i++)
						this.upload_file(i, files[i]);
				}
			}
			else	// Should never happen since we redirect non compatible browser to the "classic" interface
				$wnd.isc.warn("No support for the File API in this web browser");
		}-*/;

		@JsMethod
		public native void upload_file(int i, JavaScriptObject file) /*-{
			var self = this;
			var data = {
				xhr:new XMLHttpRequest(),
				fname:file.filepath?file.filepath:(file.name?file.name:file.fileName),
				fsize:file.size?file.size:file.fileSize,
				status:-1,
				extstatus:'',
				start_time:new Date(),
				rownum:i
			};
//			self.getData().add(data);
//			data.rownum = self.getData().getLength()-1;
			
			var upload = data.xhr.upload || data.xhr;
//			var session = $wnd.session || $wnd.defs.session;

			// Upload in progress...
			upload.addEventListener("progress", function(evt)
			{
				if(evt.lengthComputable)
				{
					data.status = evt.loaded==evt.total?2:1;
					self.upload_result(data,evt.loaded);
				}
				else
				{
					data.status = 1;
					self.upload_result(data,0);
				}
			}, false);

			// File upload finished
			data.xhr.addEventListener("load", function(evt)
			{
				var infos = eval('('+evt.target.responseText+')');
				data.status = infos.status;
				data.extstatus = infos.extstatus;
				self.upload_result(data,data.fsize);
				if(data.status==0)
				{
					data.xhr.open("put", "/upload/?", true);
					data.xhr.setRequestHeader("X-File-Name", encodeURIComponent(file.filepath?file.filepath:(file.name?file.name:file.fileName)));
					data.xhr.setRequestHeader("X-File-Parent", encodeURIComponent(self.getParentPath()));
					data.xhr.setRequestHeader("X-File-Size", file.size?file.size:file.fileSize);
					data.xhr.setRequestHeader("X-File-Type", file.type);
					self.upload_result(data);
					data.xhr.send(file);
				}
			}, false);

			data.xhr.addEventListener("error", function(evt)
			{
				data.status = 4;
				data.extstatus = "An error occurred while transferring the file.";
				self.upload_result(data);
			}, false);  
			
			data.xhr.addEventListener("abort", function(evt)
			{  
				data.status = 5;
				data.extstatus = "The transfer has been canceled by the user.";
				self.upload_result(data);
			}, false);  
			
			data.xhr.open("post", "/upload/?init=1", true);
			data.xhr.setRequestHeader("X-File-Name", encodeURIComponent(file.filepath?file.filepath:(file.name?file.name:file.fileName)));
			data.xhr.setRequestHeader("X-File-Parent", encodeURIComponent(self.getParentPath()));
			data.xhr.setRequestHeader("X-File-Size", file.size);
			data.xhr.setRequestHeader("X-File-Type", file.type);
			self.upload_result(data);
			data.xhr.send();

		}-*/;

		@JsMethod
		public native void upload_result(JavaScriptObject data, int loaded) /*-{
			if(loaded==undefined) loaded=0;
			data.percent = loaded / data.fsize;
			data.current_time = new Date();
			data.loaded = loaded;
			data.elapsed = (data.current_time.getTime()-data.start_time.getTime())/1000.0;
			data.speed = data.loaded>0?(data.elapsed>0?(data.loaded / data.elapsed):0):-1;
			data.remain = data.speed>0?((data.fsize-data.loaded)/data.speed):-1;
			switch(data.status)
			{
				case -1:
					this.setProgress(data.rownum, 0);
					break;
				case 0:
					this.setProgress(data.rownum, 0);
					break;
				case 1:
				case 2:
					this.setProgress(data.rownum, data.percent);
					break;
				case 3:
					this.setProgress(data.rownum, 1);
					break;
				default:
					console.log(data.extstatus);
					break;
			}
		}-*/;
	}
	
	public RemoteFileChooser(String context, CallBack cb)
	{
		super();
		Client.childWindows.remove(this);
		setWidth(600);
		setHeight(500);
		final boolean isDir, isMultiple, isChoose;
		switch(context)
		{
			case "tfRomsDest":
			case "tfDisksDest":
			case "tfSWDest":
			case "tfSWDisksDest":
			case "tfSamplesDest":
				isDir = true;
				isMultiple = false;
				isChoose = true;
				break;
			case "listSrcDir":
			case "addDatSrc":
				isDir = true;
				isMultiple = true;
				isChoose = true;
				break;
			case "manageUploads":
				isDir = false;
				isMultiple = true;
				isChoose = false;
				break;
			case "updDat":
			case "updTrnt":
				isDir = true;
				isMultiple = false;
				isChoose = true;
				break;
			case "importDat":
				isDir = false;
				isMultiple = true;
				isChoose = true;
				break;
			case "addDat":
			case "addTrnt":
			default:
				isDir = false;
				isMultiple = false;
				isChoose = true;
				break;
		}
		setAutoCenter(true);
		setIsModal(true);
		if(!isChoose)
			setTitle(isDir?"Manage directories":"Manage files");
		else if(isMultiple)
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
			setDetailPane(list=new UploadList(context,cb));
			setDetailToolButtons(parentLab=new Label("parent") {{
				setWidth100();
				setBorder("1px inset");
			}});
			setNavigationPaneWidth(100);
		}});
		addItem(new HLayout() {{
			setHeight(20);
			setLayoutAlign(Alignment.RIGHT);
			setPaddingAsLayoutMargin(true);
			setPadding(3);
			setMembersMargin(3);
			setWidth100();
			addMember(new IButton(isChoose?"Cancel":"Close") {{
				setAutoFit(true);
				addClickHandler(e->RemoteFileChooser.this.markForDestroy());
			}});
			addMember(pb_spacer = new LayoutSpacer("*","20"));
			addMember(pb = new Progressbar() {{
				setLength("*");
				setBreadth(10);
				setLayoutAlign(VerticalAlignment.CENTER);
				hide();
			}});
			addMember(pb_text = new Label() {{
				setWidth(25);
				setAlign(Alignment.CENTER);
				setLayoutAlign(VerticalAlignment.CENTER);
				hide();
			}});
			if(isChoose)
			{
				addMember(new IButton("Choose") {{
					setAutoFit(true);
					addClickHandler(e->{
						ListGridRecord[] records =  list.getSelectedRecords();
						if(records.length>0)
						{
							if(cb != null)
								cb.apply(Stream.of(records).map(PathInfo::new).collect(Collectors.toList()).toArray(new PathInfo[0]));
							RemoteFileChooser.this.markForDestroy();
						}
						else if(isDir)
						{
							String path = parent;
							if(cb != null)
								cb.apply(new PathInfo[] {new PathInfo(path, null, null)});
							RemoteFileChooser.this.markForDestroy();
						}
					});
				}});
			}
		}});
		initUpload(!isChoose);
		show();
	}
	
	@JsMethod(namespace=JsPackage.GLOBAL)
	public static native JavaScriptObject getFilesWebkitDataTransferItems(JavaScriptObject dataTransferItems) /*-{
		function traverseFileTreePromise(item, path)
		{
			if(path===undefined)
				path='';
			return new Promise(function(resolve) {
				if (item.isFile)
				{
		        	item.file(function(file) {
		        		file.filepath = path + file.name; //save full path
		        		files.push(file);
		        		resolve(file);
		        	});
				}
				else if (item.isDirectory)
				{
					var dirReader = item.createReader();
					var entriesPromises = [];
					var readEntries = function() {
						dirReader.readEntries(function(entries) {
							if(entries.length)
							{
								for(var idx = 0; idx < entries.length; idx++)
									entriesPromises.push(traverseFileTreePromise(entries[idx], path + item.name + "/"));
								readEntries();
							}
							else
								resolve(Promise.all(entriesPromises));
						});
					};
					readEntries();
				}
			});
		}

		var files = [];
		return new Promise(function(resolve, reject) {
			var entriesPromises = [];
			for(var idx = 0; idx < dataTransferItems.length; idx++)
				entriesPromises.push(traverseFileTreePromise(dataTransferItems[idx].webkitGetAsEntry()));
			Promise.all(entriesPromises).then(function(entries) {
				//console.log(entries)
				resolve(files);
			});
		});
	}-*/;
	
	private static native void initUpload(boolean init) /*-{
		if($wnd.File && $wnd.FileReader)
		{
			if(init)
			{
				if(!$wnd.c_drop) $wnd.c_drop = {
					isInDropArea : function (target)
					{
						if(target)
						{
							if(target.getAttribute)
							{
								var eventproxy = target.getAttribute('eventproxy');
								if(eventproxy)
									if(eventproxy=='UploadList')
										return eventproxy;
							}
							return this.isInDropArea(target.parentNode);
						}
						return false;
					},
					dragEnter:function (evt)
					{
						var eventproxy = $wnd.c_drop.isInDropArea(evt.target);
						if(eventproxy)
						{
							if($wnd.c_drop.elt && $wnd.c_drop.elt!=$wnd[eventproxy])
								$wnd.c_drop.elt.setBorder("2px solid lightgrey");
							$wnd.c_drop.elt = $wnd[eventproxy];
							$wnd.c_drop.elt.setBorder("2px solid #55FF55");
							evt.preventDefault();
							evt.stopPropagation();
						}
						else if($wnd.c_drop.elt)
						{
							$wnd.c_drop.elt.setBorder("2px solid lightgrey");
							$wnd.c_drop.elt=null;
						}
					},
					dragLeave:function (evt)
					{
						if($wnd.c_drop.isInDropArea(evt.target))
						{
							evt.preventDefault();
							evt.stopPropagation();
						}
					},
					dragOver:function (evt)
					{
						if($wnd.c_drop.isInDropArea(evt.target))
						{
							evt.preventDefault();
							evt.stopPropagation();
						}
					},
					drop:function (evt)
					{
						if($wnd.c_drop.isInDropArea(evt.target))
						{
							if($wnd.c_drop.elt)
							{
								$wnd.c_drop.elt.setBorder("2px solid lightgrey");
								$wnd.c_drop.elt=null;
							}
							
							var items = evt.dataTransfer.items;
							if(items)
							{
								$wnd.getFilesWebkitDataTransferItems(items).then(function(files) {
									$wnd.getUploadList().handleFileSelect(files);
								});
							}
							else
								$wnd.getUploadList().handleFileSelect(evt.dataTransfer.files);
								
							evt.preventDefault();
							evt.stopPropagation();
						}
					},
					elt:null
				};
				
				$doc.addEventListener("dragenter", $wnd.c_drop.dragEnter, false);
				$doc.addEventListener("dragleave", $wnd.c_drop.dragLeave, false);
				$doc.addEventListener("dragover", $wnd.c_drop.dragOver, false);
				$doc.addEventListener("drop", $wnd.c_drop.drop, false);
			}
			else if($wnd.c_drop)
			{
				$doc.removeEventListener("dragenter", $wnd.c_drop.dragEnter, false);
				$doc.removeEventListener("dragleave", $wnd.c_drop.dragLeave, false);
				$doc.removeEventListener("dragover", $wnd.c_drop.dragOver, false);
				$doc.removeEventListener("drop", $wnd.c_drop.drop, false);
			}
		}
	}-*/;

	@JsMethod(namespace=JsPackage.GLOBAL)
	public static UploadList getUploadList()
	{
		return list;
	}

	@Override
	protected void onDestroy()
	{
		Client.childWindows.remove(this);
		super.onDestroy();
	}
	
}
