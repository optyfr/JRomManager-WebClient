package jrm.webui.client.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
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

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;

public final class RemoteFileChooser extends Window
{
	Label parent;
	Progressbar pb;
	Label pb_text;
	LayoutSpacer pb_spacer;

	static UploadList list;

	public interface CallBack
	{
		public void apply(String[] path);
	}
	
	public class UploadList extends ListGrid
	{
		private int tot;
		private float val[];
		
		public UploadList()
		{
			super();
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
			return parent.getContents();
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
				this.initProgress(files.length);
				for (var i=0, l=files.length; i<l; i++)
					this.upload_file(i, files[i]);
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
			
			data.xhr.open("post", "/upload/?init=1", false);
			data.xhr.setRequestHeader("X-File-Name", file.filepath?file.filepath:encodeURIComponent(file.name?file.name:file.fileName));
			data.xhr.setRequestHeader("X-File-Parent", self.getParentPath());
			data.xhr.setRequestHeader("X-File-Size", file.size);
			data.xhr.setRequestHeader("X-File-Type", file.type);
			self.upload_result(data);
			data.xhr.send();

			if(data.status==0)
			{
				data.xhr.open("put", "/upload/?", true);
				data.xhr.setRequestHeader("X-File-Name", file.filepath?file.filepath:encodeURIComponent(file.name?file.name:file.fileName));
				data.xhr.setRequestHeader("X-File-Parent", self.getParentPath());
				data.xhr.setRequestHeader("X-File-Size", file.size?file.size:file.fileSize);
				data.xhr.setRequestHeader("X-File-Type", file.type);
				self.upload_result(data);
				data.xhr.send(file);
			}
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
				isDir = true;
				isMultiple = true;
				isChoose = true;
				break;
			default:
				isDir = false;
				isMultiple = false;
				isChoose = true;
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
			setDetailPane(list=new UploadList() {{
				setID("UploadList");
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
			setPadding(3);
			setMembersMargin(3);
			setWidth100();
			setMembers(
				new IButton("Cancel") {{
					setAutoFit(true);
					addClickHandler(e->RemoteFileChooser.this.markForDestroy());
				}},
				pb_spacer = new LayoutSpacer("*","20"),
				pb = new Progressbar() {{
					setLength("*");
					setBreadth(10);
					setLayoutAlign(VerticalAlignment.CENTER);
					hide();
				}},
				pb_text = new Label() {{
					setWidth(25);
					setAlign(Alignment.CENTER);
					setLayoutAlign(VerticalAlignment.CENTER);
					hide();
				}},
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
		initUpload();
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
						});
					};
					readEntries();
					console.log(entriesPromises.length);
					resolve(Promise.all(entriesPromises));
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
	
	private static native void initUpload() /*-{
		if($wnd.File && $wnd.FileReader)
		{
			$wnd.c_drop = {
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
				elt:null
			};
			
			$doc.addEventListener("dragenter", function (evt)
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
			}, false);
			
			$doc.addEventListener("dragleave", function (evt)
			{
				if($wnd.c_drop.isInDropArea(evt.target))
				{
					evt.preventDefault();
					evt.stopPropagation();
				}
			}, false);
		
			$doc.addEventListener("dragover", function (evt)
			{
				if($wnd.c_drop.isInDropArea(evt.target))
				{
					evt.preventDefault();
					evt.stopPropagation();
				}
			}, false);
			
			$doc.addEventListener("drop", function (evt)
			{
				if($wnd.c_drop.isInDropArea(evt.target))
				{
					if($wnd.c_drop.elt)
					{
						$wnd.c_drop.elt.setBorder("2px solid lightgrey");
						$wnd.c_drop.elt=null;
					}
					//$wnd.getUploadList().handleFileSelect(evt.dataTransfer.files);
					
					var items = evt.dataTransfer.items;
					$wnd.getFilesWebkitDataTransferItems(items).then(function(files) {
						$wnd.getUploadList().handleFileSelect(files);
					});
					
					evt.preventDefault();
					evt.stopPropagation();
				}
			}, false);
		}
	}-*/;

	@JsMethod(namespace=JsPackage.GLOBAL)
	public static UploadList getUploadList()
	{
		return list;
	}

}
