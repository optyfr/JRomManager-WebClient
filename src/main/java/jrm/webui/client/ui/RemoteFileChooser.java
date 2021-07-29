package jrm.webui.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.Record;
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
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.SortNormalizer;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.ui.RemoteFileChooser.Options.SelMode;
import jrm.webui.client.utils.CaseInsensitiveString;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;

public final class RemoteFileChooser extends Window
{
	Label parentLab;
	Progressbar pb;
	private Layout pb_layout;
	private Label pb_text;
	LayoutSpacer pb_spacer;
	private IButton close;
	
	private boolean cancelled = false;

	static UploadList list;
	
	private String parent, relparent;
	private String root;

	static class Options
	{
		enum SelMode
		{
			NONE, FILE, DIR, FILE_DIR;
		}
		
		public final String context, initialPath;
		public final SelMode selMode;
		public final boolean isMultiple, isChoose;
		
		public Options(String context, String initialPath)
		{
			this.context = context;
			this.initialPath = initialPath;
			switch(context)
			{
				case "tfRomsDest":
				case "tfDisksDest":
				case "tfSWDest":
				case "tfSWDisksDest":
				case "tfSamplesDest":
					selMode = SelMode.DIR;
					isMultiple = false;
					isChoose = true;
					break;
				case "listSrcDir":
				case "addDatSrc":
					selMode = SelMode.DIR;
					isMultiple = true;
					isChoose = true;
					break;
				case "manageUploads":
					selMode = SelMode.NONE;
					isMultiple = true;
					isChoose = false;
					break;
				case "updDat":
				case "updTrnt":
					selMode = SelMode.DIR;
					isMultiple = true;
					isChoose = true;
					break;
				case "importDat":
					selMode = SelMode.FILE;
					isMultiple = true;
					isChoose = true;
					break;
				case "addArc":
					selMode = SelMode.FILE;
					isMultiple = true;
					isChoose = true;
					break;
				case "addDat":
					selMode = SelMode.FILE_DIR;
					isMultiple = true;
					isChoose = true;
					break;
				case "addTrnt":
					selMode = SelMode.FILE;
					isMultiple = true;
					isChoose = true;
					break;
				default:
					selMode = SelMode.FILE;
					isMultiple = false;
					isChoose = true;
					break;
			}
		}
	}
	
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
	
	public class RootList extends ListGrid
	{
		boolean initial = true;
		
		private RootList(Options options)
		{
			setID("RemoteFileChooser_RootList_"+options.context);
			setCanSort(false);
			setCanGroupBy(false);
			setCanFreezeFields(false);
			setCanReorderFields(false);
			setCanReorderRecords(false);
			setCanAutoFitFields(false);
			setShowFilterEditor(false);
			setShowHeaderMenuButton(false);
			setShowHiddenFields(false);
			setShowHeaderContextMenu(false);
			setShowHover(true);
			setCanHover(true);
			setHoverWidth(200);
			setAutoFetchData(true);
			setSelectionType(SelectionStyle.SINGLE);
			addSelectionChangedHandler(event->{
				if(event.getState())
				{
					Map<String,String> params = new HashMap<>();
					params.put("context", options.context);
					params.put("root", event.getRecord().getAttribute("Path"));
					if(initial)
					{
						if(options.initialPath!=null)
							params.put("initialPath", options.initialPath);
						initial = false;
					}
					list.getDataSource().setRequestProperties(new DSRequest() {{setData(params);}});
				//	if(list.getTotalRows()>0)
						list.invalidateCache();
				//	else
				//		list.fetchData();
				}
			});
			addDataArrivedHandler(event->{
				if(getSelectedRecord()==null)
					selectRecord(0);
			});
			setDataSource(new RestDataSource() {{
					setID("remoteRootChooser");
					setDataURL("/datasources/"+getID());
					setRequestProperties(new DSRequest() {{setData(Collections.singletonMap("context", options.context));}});
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
					);
					DataSourceTextField nameField = new DataSourceTextField("Name");
					DataSourceTextField pathField = new DataSourceTextField("Path");
					pathField.setHidden(true);
					pathField.setPrimaryKey(true);
					setFields(nameField, pathField);
				}},	new ListGridField("Type") {{
					setWidth(20);
					setMaxWidth(20);
					setCellFormatter((value,record,rowNum,colNum)->"<img src='/images/icons/drive.png'/>");
				}},
				new ListGridField("Name") {{
					setWidth("*");
				}}
			);
		}		
	}
	
	public class UploadList extends ListGrid
	{
		private int tot;
		private float val[];
		private Options options;
		
		@SuppressWarnings("serial")
		public UploadList(Options options, CallBack cb)
		{
			super();
			this.options = options;
			setID("RemoteFileChooser_UploadList_"+options.context);
			setBorder("2px solid lightgrey");
			setShowFilterEditor(false);
			setShowHover(true);
			setCanHover(true);
			setHoverWidth(200);
			setAutoFetchData(true);
			setSelectionType(options.isMultiple?SelectionStyle.MULTIPLE:SelectionStyle.SINGLE);
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
						setEnableIfCondition((target, menu, item) -> !options.isChoose && UploadList.this.getSelectedRecords().length == 1);
					}},
					new MenuItem() {{
						setTitle("Delete selection");
						addClickHandler(event -> UploadList.this.removeSelectedData());
						setEnableIfCondition((target, menu, item) -> !options.isChoose && UploadList.this.getSelectedRecords().length > 0);
					}},
					new MenuItem() {{
						setTitle("Download selection");
						addClickHandler(event -> {
							Record record = UploadList.this.getSelectedRecord();
							DynamicForm form = new DynamicForm();
							form.setAction("/download/");
							HiddenItem item = new HiddenItem("path");
							item.setDefaultValue(record.getAttribute("Path"));
							form.setItems(item);
							form.setTarget("_blank");
							form.setMethod(FormMethod.POST);
							form.setCanSubmit(true);
							form.draw();
							form.submitForm();
							form.destroy();
						});
						setEnableIfCondition((target, menu, item) -> !options.isChoose && UploadList.this.getSelectedRecords().length == 1);
					}},
					new MenuItem() {{
						setTitle("Archive");
						setEnableIfCondition((target, menu, item) -> {
							if(!options.isChoose && UploadList.this.getSelectedRecords().length == 1)
								return new CaseInsensitiveString(UploadList.this.getSelectedRecord().getAttribute("Name")).endsWith(".zip");
							return false;
						});
						this.setSubmenu(new Menu() {{
							setItems(new MenuItem() {{
								setTitle("Extract here");
								addClickHandler(e->{
									UploadList.this.getDataSource().performCustomOperation("extract_here", UploadList.this.getSelectedRecord(), new DSCallback()
									{
										@Override
										public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
										{
											UploadList.this.invalidateCache();
										}
									});
								});
							}}, new MenuItem() {{
								setDynamicTitleFunction((target, menu, item)->{
									String name = UploadList.this.getSelectedRecord().getAttribute("Name");
									return "Extract to " + name.substring(0, name.length() - 4) + "/";
								});
								addClickHandler(e->{
									UploadList.this.getDataSource().performCustomOperation("extract_subfolder", UploadList.this.getSelectedRecord(), new DSCallback()
									{
										@Override
										public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest)
										{
											UploadList.this.invalidateCache();
										}
									});
								});
							}});
						}});
					}}
				);
			}});
			addRecordClickHandler(event->{
			});
			addRecordDoubleClickHandler(event->{
				ListGridRecord record = event.getRecord();
				@SuppressWarnings("unused")
				String path = record.getAttribute("Path");
				String relpath = record.getAttribute("RelPath");
				String name = record.getAttribute("Name");
				if(record.getAttributeAsBoolean("isDir"))
				{
					enterDir(record);
				}
				else if(options.isChoose)
				{
					processPaths(options.context, cb, new PathInfo[] {new PathInfo(relpath, parent, name)});
					RemoteFileChooser.this.markForDestroy();
				}
			});
			setInitialSort(new SortSpecifier("isDir", SortDirection.DESCENDING),new SortSpecifier("Name", SortDirection.ASCENDING));
			setDataSource(new RestDataSource() {
				{
					setID("remoteFileChooser");
					setDataURL("/datasources/"+getID());
					setRequestProperties(new DSRequest() {{setData(Collections.singletonMap("context", options.context));}});
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.CUSTOM);setDataProtocol(DSProtocol.POSTXML);}}
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
					{
						root = XMLTools.selectString(data, "/response/root");
						parent = XMLTools.selectString(data, "/response/parent");
						relparent = XMLTools.selectString(data, "/response/relparent");
						parentLab.setContents(XMLTools.selectString(data, "/response/parentRelative"));
						
					}
					super.transformResponse(dsResponse, dsRequest, data);
				};
			});
			SortNormalizer normalizer = (record, fieldName) -> record.getAttribute("Name").equals("..") ? "\0" : record.getAttribute(fieldName);
			setFields(
					new ListGridField("isDir") {{
						setWidth(20);
						setMaxWidth(20);
						setCellFormatter((value,record,rowNum,colNum)->"<img src='/images/icons/"+((boolean)value?"folder.png":"page.png")+"'/>");
					}},
					new ListGridField("Name") {{
						setWidth("*");
						setSortNormalizer(normalizer);
					}},
					new ListGridField("Size") {{
						setWidth(60);
						setCellFormatter((value,record,rowNum,colNum)->{
							return ((int)value)<0?"":readableFileSize((int)value);
						});
						setSortNormalizer(normalizer);
					}},
					new ListGridField("Modified") {{
						setWidth(115);
						setSortNormalizer(normalizer);
					}}
			);
			setSelectionProperty("isSelected");
			setDataProperties(new ResultSet() {{
				setUseClientFiltering(false);
				setUseClientSorting(true);
			}});
		}

		public void enterDir(ListGridRecord record)
		{
			String path = record.getAttribute("Path");
			getDataSource().setRequestProperties(new DSRequest() {{
				Map<String,String> params = new HashMap<>();
				params.put("context", options.context);
				params.put("parent", path);
				params.put("root", root);
				//setParams(params);
				setData(params);
			}});
			invalidateCache();
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
				endProgress();
		}
		
		@JsMethod
		public void endProgress()
		{
			pb_layout.hide();
			pb_spacer.show();
			close.setDisabled(false);
			invalidateCache();
		}
		
		@JsMethod
		public void initProgress(int tot)
		{
			cancelled = false;
			this.tot = tot;
			this.val = new float[tot];
			for(int i = 0; i < this.tot; i++)
				this.val[i]=0;
			pb.setPercentDone(0);
			pb_text.setContents(0+"/"+tot);
			pb_spacer.hide();
			close.setDisabled(true);
			pb_layout.show();
		}
		
		private List<JavaScriptObject> activeQueue = new ArrayList<>();
		private final int activeMax = 5;
		
		@JsMethod
		public int getActiveMax()
		{
			return activeMax;
		}
		
		@JsMethod
		public int getActiveLength()
		{
			return activeQueue.size();
		}
		
		@JsMethod
		public void addActive(JavaScriptObject data)
		{
			activeQueue.add(data);
		}
		
		@JsMethod
		public void purgeActive(boolean abort)
		{
			Iterator<JavaScriptObject> it = activeQueue.iterator();
			while(it.hasNext())
			{
				JavaScriptObject data = it.next();
				if(!stillActive(data, abort))
					it.remove();
			}
		}
		
		@JsMethod
		public native boolean stillActive(JavaScriptObject data, boolean abort) /*-{
			if(abort) data.xhr.abort();
			return data.status < 3;
		}-*/;
		
		@JsMethod
		public boolean isCancelled()
		{
			return cancelled;
		}
		
		@JsMethod
		public native void handleFileSelect(JavaScriptObject files) /*-{
			if(typeof files !== "undefined")
			{
				if(files.length > 0)
				{
					var afiles = !Array.isArray(files)?Array.from(files):files; 
					var self = this;
					var idx = 0;
					this.initProgress(afiles.length);
					var timer = null
					var execute = function() {
						self.purgeActive(self.isCancelled());
						if(!self.isCancelled())
						{
							var todo = Math.min(afiles.length, self.getActiveMax() - self.getActiveLength());
							for(var i = 0; i < todo; i++)
								self.addActive(self.upload_file(idx++, afiles.shift()));
						}
						else
							afiles = [];
						if(afiles.length == 0 && self.getActiveLength() == 0)
						{
							$wnd.clearInterval(timer);
							self.endProgress();
						}
					};
					timer = $wnd.setInterval(execute, 10);
				}
			}
			else	// Should never happen since we redirect non compatible browser to the "classic" interface
				$wnd.isc.warn("No support for the File API in this web browser");
		}-*/;

		

		@JsMethod
		public native JavaScriptObject upload_file(int i, JavaScriptObject file) /*-{
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
			return data;
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
	
	public RemoteFileChooser(String context, String initialPath, CallBack cb)
	{
		super();
		Client.getChildWindows().remove(this);
		final Options options = new Options(context, initialPath);
		setID("RemoteFileChooser_"+context);
		setWidth(700);
		setHeight(500);
		setAutoCenter(true);
		setIsModal(true);
		if(!options.isChoose)
			setTitle(options.selMode==SelMode.DIR?"Manage directories":"Manage files");
		else if(options.isMultiple)
			setTitle(options.selMode==SelMode.DIR?"Choose directories":"Choose files");
		else
			setTitle(options.selMode==SelMode.DIR?"Choose a directory":"Choose a file");
		setCanDragResize(true);
		setShowMaximizeButton(true);
		setShowModalMask(true);
		setCanDragReposition(true);
		setDismissOnEscape(false);
		addCloseClickHandler(event -> {
			if(!close.isDisabled()) RemoteFileChooser.this.markForDestroy();
		});
		addItem(new SplitPane() {{
			setHeight("*");
			setNavigationPane(new RootList(options));
			setDetailPane(list=new UploadList(options,cb));
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
			close = new IButton(options.isChoose?"Cancel":"Close",e->RemoteFileChooser.this.markForDestroy());
			close.setID("RemoteFileChooser_CloseBtn_"+context);
			close.setAutoFit(true);
			addMember(close);
			pb_spacer = new LayoutSpacer("*","20");
			addMember(pb_spacer);
			pb_layout = new HLayout();
			pb_layout.setMembersMargin(3);
			pb = new Progressbar();
			pb.setLength("*");
			pb.setBreadth(20);
			pb.setLayoutAlign(VerticalAlignment.CENTER);
			pb_text = new Label();
			pb_text.setWidth100();
			pb_text.setAlign(Alignment.CENTER);
			pb.addChild(pb_text,"label",true);
			pb_layout.addMember(pb);
			IButton cancel = new IButton("Cancel", e->cancelled=true);
			cancel.setID("RemoteFileChooser_CancelBtn_"+context);
			cancel.setAutoFit(true);
			pb_layout.addMember(cancel);
			pb_layout.hide();
			addMember(pb_layout);
			if(options.isChoose)
			{
				IButton choose = new IButton("Choose");
				choose.setID("RemoteFileChooser_ChooseBtn_"+context);
				choose.setAutoFit(true);
				choose.addClickHandler(e->{
					ListGridRecord[] records =  list.getSelectedRecords();
					if(records.length>0)
					{
						if(options.selMode==SelMode.FILE)
						{
							if(records.length==1)
							{
								if(records[0].getAttributeAsBoolean("isDir"))
								{
									list.enterDir(records[0]);
									return;
								}
							}	
							else if(Stream.of(records).filter(r->r.getAttributeAsBoolean("isDir")).count()>0)
								return;
						}
						processPaths(context, cb, Stream.of(records).map(PathInfo::new).toArray(PathInfo[]::new));
						RemoteFileChooser.this.markForDestroy();
					}
					else if(options.selMode==SelMode.DIR || options.selMode==SelMode.FILE_DIR)
					{
						processPaths(context, cb, new PathInfo[] {new PathInfo(relparent, null, null)});
						RemoteFileChooser.this.markForDestroy();
					}
				});
				addMember(choose);
			}
		}});
		initUpload(!options.isChoose, list.getID());
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
	
	private static native void initUpload(boolean init, String id) /*-{
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
									if(eventproxy==id)
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
		Client.getChildWindows().remove(this);
		super.onDestroy();
	}
	
	@SuppressWarnings("serial")
	private void processPaths(String context, CallBack cb, PathInfo[] paths)
	{
		if (parent != null)
			Q_Global.SetProperty.instantiate().setProperty("dir." + context, parent).send();
		else if (paths != null && paths.length > 0 && paths[0].parent != null)
			Q_Global.SetProperty.instantiate().setProperty("dir." + context, paths[0].parent).send();
		switch(context)
		{
			case "addArc":
			{
				list.getDataSource().performCustomOperation("expand", null, (dsResponse, data, dsRequest)->{
					if (cb != null)
						cb.apply(Stream.of(dsResponse.getData()).map(PathInfo::new).toArray(PathInfo[]::new));
				}, new DSRequest() {{
					setData(new HashMap<String, Object>() {{
						put("paths", Stream.of(paths).map(p->p.path).toArray());
					}});
				}});
				break;
			}
			default:
				if (cb != null)
					cb.apply(paths);
				break;
		}
	}
	
}
