package jrm.webui.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.ResultSet;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.SortNormalizer;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.SplitPane;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;

import jrm.webui.client.Client;
import jrm.webui.client.datasources.DSRemoteFileChooser;
import jrm.webui.client.datasources.DSRemoteRootChooser;
import jrm.webui.client.protocol.Q_Global;
import jrm.webui.client.ui.RemoteFileChooser.Options.SelMode;
import jrm.webui.client.utils.CaseInsensitiveString;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;

/**
 * Remote file chooser dialog for navigating server-side file systems.
 * <p>
 * Presents a split pane with a root/drive list on the left and the file list
 * of the currently selected directory on the right. Depending on the
 * {@link Options context} it can be used to choose files and/or directories,
 * or to manage (create, rename, delete, upload, download, archive) remote
 * entries. Selections are reported back through a {@link CallBack}.
 *
 * @since 2.5
 */
public final class RemoteFileChooser extends Window /* NOSONAR */ {
    /** Attribute name used to flag directory records. */
    private static final String IS_DIR = "isDir";
    /** Label showing the relative path of the currently listed directory. */
    Label parentLab;
    /** Upload progress bar shown while files are being transferred. */
    Progressbar pb;
    /** Layout wrapping the upload progress bar and its cancel button. */
    private Layout pbLayout;
    /** Text label painted on top of {@link #pb} (e.g. {@code "5/10"}). */
    private Label pbText;
    /** Spacer shown in place of the progress bar when no upload is running. */
    LayoutSpacer pbSpacer;
    /** Close/Cancel button of the chooser. */
    private IButton close;

    /** Whether the current upload batch has been cancelled by the user. */
    private boolean cancelled = false;

    /** The current upload list instance (shared static for JS access). */
    static UploadList list;

    /** Absolute path of the currently listed directory. */
    private String parent;
    /** Relative path of the currently listed directory, used for selections. */
    private String relparent;
    /** Currently selected root/drive path. */
    private String root;

    /**
     * Configuration options for the remote file chooser.
     *
     * @since 2.5
     */
    public static class Options {
        /**
         * Selection mode for the file chooser.
         *
         * @since 2.5
         */
        public enum SelMode {
            /** No selection allowed. */
            NONE,
            /** Single file selection. */
            FILE,
            /** Single directory selection. */
            DIR,
            /** Multiple file/directory selection. */
            FILE_DIR;
        }

        /** Identifier of the calling context; drives the selection rules. */
        public final String context;
        /** Optional initial path to expand, or {@code null} for the root. */
        public final String  initialPath;
        /** Selection mode derived from the {@link #context}. */
        public final SelMode selMode;
        /** Whether multi-row selection is enabled for this context. */
        public final boolean isMultiple;
        /** Whether the chooser operates in "choose" (vs. "manage") mode. */
        public final boolean  isChoose;

        /**
         * Builds the options for the given context, deriving the selection
         * mode, multi-selection flag and choose/manage flag from a fixed rule
         * table keyed on {@code context}.
         *
         * @param context     the calling context identifier
         * @param initialPath the optional initial path to expand
         */
        public Options(String context, String initialPath) {
            this.context = context;
            this.initialPath = initialPath;
            switch (context) {
                case "tfRomsDest", "tfDisksDest", "tfSWDest", "tfSWDisksDest", "tfSamplesDest", "tfBackupDest" -> {
                    selMode = SelMode.DIR;
                    isMultiple = false;
                    isChoose = true;
                }
                case "listSrcDir", "addDatSrc" -> {
                    selMode = SelMode.DIR;
                    isMultiple = true;
                    isChoose = true;
                }
                case "manageUploads" -> {
                    selMode = SelMode.NONE;
                    isMultiple = true;
                    isChoose = false;
                }
                case "updDat", "updTrnt" -> {
                    selMode = SelMode.DIR;
                    isMultiple = true;
                    isChoose = true;
                }
                case "importDat" -> {
                    selMode = SelMode.FILE;
                    isMultiple = true;
                    isChoose = true;
                }
                case "addArc" -> {
                    selMode = SelMode.FILE;
                    isMultiple = true;
                    isChoose = true;
                }
                case "addDat" -> {
                    selMode = SelMode.FILE_DIR;
                    isMultiple = true;
                    isChoose = true;
                }
                case "addTrnt" -> {
                    selMode = SelMode.FILE;
                    isMultiple = true;
                    isChoose = true;
                }
                case "tfSrcDir" -> {
                    selMode = SelMode.DIR;
                    isMultiple = false;
                    isChoose = true;
                }
                case "tfDstDat" -> {
                    selMode = SelMode.FILE;
                    isMultiple = false;
                    isChoose = true;
                }
                default -> {
                    selMode = SelMode.FILE;
                    isMultiple = false;
                    isChoose = true;
                }
            }
        }
    }

    /**
     * Holds path information for selected files/directories.
     *
     * @since 2.5
     */
    public class PathInfo {
        String path;
        String parent;
        String name;

        /**
         * Constructs a new PathInfo with explicit path components.
         *
         * @param path the full path
         * @param parent the parent path
         * @param name the file/directory name
         */
        public PathInfo(String path, String parent, String name) {
            this.path = path;
            this.parent = parent;
            this.name = name;
        }

        /**
         * Constructs a new PathInfo from a ListGridRecord.
         *
         * @param recrd the record containing path information
         */
        public PathInfo(Record recrd) {
            this(recrd.getAttribute("Path"), RemoteFileChooser.this.parent, recrd.getAttribute("Name"));
        }
    }

    /**
     * Callback interface for file selection events.
     *
     * @since 2.5
     */
    public interface CallBack {
        /**
         * Invoked once the user has confirmed a selection.
         *
         * @param path the selected paths, never {@code null}
         */
        public void apply(PathInfo[] path);
    }

    /**
     * Left-hand navigation grid listing the available server-side roots/drives.
     *
     * @since 2.5
     */
    public final class RootList extends ListGrid /* NOSONAR */ {
        /** Whether the initial path has not yet been applied to a selection. */
        boolean initial = true;

        /**
         * Builds the root list grid for the given options, wiring up the
         * selection and data-arrived handlers that drive the detail list.
         *
         * @param options the chooser options
         */
        private RootList(Options options) {
            setID("RemoteFileChooser_RootList_" + options.context);
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
            addSelectionChangedHandler(event -> {
                if (event.getState()) {
                    Map<String, String> params = new HashMap<>();
                    params.put("context", options.context);
                    params.put("root", event.getRecord().getAttribute("Path"));
                    if (initial) {
                        if (options.initialPath != null)
                            params.put("initialPath", options.initialPath);
                        initial = false;
                    }
                    list.ds.setExtraData(params);
                    list.invalidateCache();
                }
            });
            addDataArrivedHandler(event -> {
                if (getSelectedRecord() == null)
                    selectRecord(0);
            });
            var typeField = new ListGridField("Type");
            typeField.setWidth(20);
            typeField.setMaxWidth(20);
            typeField.setCellFormatter((value, recrd, rowNum, colNum) -> "<img src='/images/icons/drive.png'/>");
            var nameField = new ListGridField("Name");
            nameField.setWidth("*");
            setDataSource(DSRemoteRootChooser.getInstance(options.context), typeField, nameField);
        }
    }

    /**
     * Right-hand detail grid listing the entries of the currently selected
     * directory, with support for editing, deleting, uploading, downloading
     * and archiving entries depending on the chooser options.
     *
     * @since 2.5
     */
    public class UploadList extends ListGrid /* NOSONAR */ {
        /** Total number of files in the current upload batch. */
        private int tot;
        /** Per-file upload progress (0..1) for the current batch. */
        private float[] val;
        /** The chooser options this list was built with. */
        private final Options options;
        /** The datasource backing this list. */
        private final DSRemoteFileChooser ds;
        /** The callback to invoke when a selection is confirmed. */
        private final CallBack cb;

        /**
         * Builds the upload list grid for the given options, configuring its
         * datasource, fields, sort order, editing handlers and context menu.
         *
         * @param options the chooser options
         * @param cb      the callback to invoke on confirmed selection
         */
        public UploadList(Options options, CallBack cb) {
            super();
            this.options = options;
            this.cb = cb;
            ds = DSRemoteFileChooser.getInstance(options.context);
            ds.setCB(data -> {
                root = XMLTools.selectString(data, "/response/root");
                parent = XMLTools.selectString(data, "/response/parent");
                relparent = XMLTools.selectString(data, "/response/relparent");
                parentLab.setContents(XMLTools.selectString(data, "/response/parentRelative"));
            });
            setID("RemoteFileChooser_UploadList_" + options.context);
            setBorder("2px solid lightgrey");
            setShowFilterEditor(false);
            setShowHover(true);
            setCanHover(true);
            setHoverWidth(200);
            setSelectionType(options.isMultiple ? SelectionStyle.MULTIPLE : SelectionStyle.SINGLE);
            addEditFailedHandler(event -> startEditing(event.getRowNum(), event.getColNum()));
            addEditCompleteHandler(event -> {
                if (event.getDsResponse().getStatus() == 0 && event.getOldValues() != null)
                        refreshData((dsResponse, data, dsRequest) -> selectSingleRecord(event.getNewValuesAsRecord()));
            });
            setContextMenu(createContextMenu());
            addRecordClickHandler(event -> {
            });
            addRecordDoubleClickHandler(this::onRecordDoubleClick);
            setInitialSort(new SortSpecifier(IS_DIR, SortDirection.DESCENDING), new SortSpecifier("Name", SortDirection.ASCENDING));
            setDatetimeFormatter(DateDisplayFormat.TOSERIALIZEABLEDATE);
            setDataSource(ds);
            SortNormalizer normalizer = (recrd, fieldName) -> recrd.getAttribute("Name").equals("..") ? "\0" : recrd.getAttribute(fieldName);
            var isDirField = new ListGridField(IS_DIR);
            isDirField.setWidth(20);
            isDirField.setMaxWidth(20);
            isDirField.setCellFormatter((value, recrd, rowNum, colNum) -> "<img src='/images/icons/" + ((boolean) value ? "folder.png" : "page.png") + "'/>");
            var nameField = new ListGridField("Name");
            nameField.setWidth("*");
            nameField.setSortNormalizer(normalizer);
            var sizeField = new ListGridField("Size");
            sizeField.setWidth(60);
            sizeField.setCellFormatter((value, recrd, rowNum, colNum) -> ((int) value) < 0 ? "" : readableFileSize((int) value));
            sizeField.setSortNormalizer(normalizer);
            var modifiedField = new ListGridField("Modified");
            modifiedField.setWidth(115);
            modifiedField.setSortNormalizer(normalizer);
            setFields(isDirField, nameField, sizeField, modifiedField);
            setSelectionProperty("isSelected");
            var rs = new ResultSet();
            rs.setUseClientFiltering(false);
            rs.setUseClientSorting(true);
            setDataProperties(rs);
        }

        /**
         * Navigates the list into the directory represented by the given record.
         *
         * @param recrd the directory record to enter
         */
        public void enterDir(ListGridRecord recrd) {
            String path = recrd.getAttribute("Path");
            Map<String, String> params = new HashMap<>();
            params.put("context", options.context);
            params.put("parent", path);
            params.put("root", root);
            ds.setExtraData(params);
            invalidateCache();
        }

        /**
         * Formats a byte size into a human-readable string (e.g. {@code "1.5 MB"}).
         *
         * @param size the size in bytes
         * @return a human-readable size string, or {@code "0"} for non-positive input
         */
        public String readableFileSize(long size) {
            if (size <= 0)
                return "0";
            final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
            return NumberFormat.getFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        }

        /**
         * Returns the relative path of the currently listed directory.
         *
         * @return the current parent-relative path
         */
        @JsMethod
        public String getParentPath() {
            return parentLab.getContents();
        }

        /**
         * Reports upload progress for a single file and refreshes the aggregate
         * progress bar; completes the batch when all files are done.
         *
         * @param idx the file index within the current batch
         * @param val the per-file progress (0..1)
         */
        @JsMethod
        public void setProgress(int idx, float val) {
            this.val[idx] = val;
            float valtot = 0;
            int cnt = 0;
            for (int i = 0; i < this.tot; i++) {
                valtot += this.val[i];
                if (this.val[i] == 1)
                    cnt++;
            }
            pb.setPercentDone((int) (valtot * 100 / this.tot));
            pbText.setContents(cnt + "/" + tot);
            if (cnt == tot)
                endProgress();
        }

        /**
         * Ends the current upload batch: hides the progress bar, restores the
         * close button and refreshes the listing.
         */
        @JsMethod
        public void endProgress() {
            pbLayout.hide();
            pbSpacer.show();
            close.setDisabled(false);
            invalidateCache();
        }

        /**
         * Starts a new upload batch of {@code tot} files, resetting state and
         * showing the progress bar.
         *
         * @param tot the number of files in the batch
         */
        @JsMethod
        public void initProgress(int tot) {
            cancelled = false;
            this.tot = tot;
            this.val = new float[tot];
            for (int i = 0; i < this.tot; i++)
                this.val[i] = 0;
            pb.setPercentDone(0);
            pbText.setContents(0 + "/" + tot);
            pbSpacer.hide();
            close.setDisabled(true);
            pbLayout.show();
        }

        /** Queue of in-flight upload requests. */
        private final List<JavaScriptObject> activeQueue = new ArrayList<>();
        /** Maximum number of concurrent uploads. */
        private static final int ACTIVE_MAX = 5;

        /**
         * Returns the maximum number of concurrent uploads allowed.
         *
         * @return the concurrent upload limit
         */
        @JsMethod
        public int getActiveMax() {
            return ACTIVE_MAX;
        }

        /**
         * Returns the number of currently in-flight uploads.
         *
         * @return the active upload count
         */
        @JsMethod
        public int getActiveLength() {
            return activeQueue.size();
        }

        /**
         * Adds an in-flight upload descriptor to the active queue.
         *
         * @param data the upload descriptor to enqueue
         */
        @JsMethod
        public void addActive(JavaScriptObject data) {
            activeQueue.add(data);
        }

        /**
         * Removes completed uploads from the active queue, optionally aborting
         * the ones still in progress.
         *
         * @param abort {@code true} to abort uploads still in progress
         */
        @JsMethod
        public void purgeActive(boolean abort) {
            Iterator<JavaScriptObject> it = activeQueue.iterator();
            while (it.hasNext()) {
                JavaScriptObject data = it.next();
                if (!stillActive(data, abort))
                    it.remove();
            }
        }

        /**
         * Native helper that optionally aborts the given upload request and
         * reports whether it is still considered active.
         *
         * @param data  the upload descriptor
         * @param abort {@code true} to abort the underlying XHR
         * @return {@code true} if the upload is still in progress
         */
        @JsMethod
        public native boolean stillActive(JavaScriptObject data, boolean abort) /*-{
			if(abort) data.xhr.abort();
			return data.status < 3;
		}-*/;

        /**
         * Tells whether the current upload batch has been cancelled by the user.
         *
         * @return {@code true} if the batch was cancelled
         */
        @JsMethod
        public boolean isCancelled() {
            return cancelled;
        }

        /**
         * Native entry point invoked when the user selects files for upload.
         * <p>
         * Schedules a timer that progressively starts uploads (up to
         * {@link #ACTIVE_MAX} concurrently) and updates the progress bar
         * until all files have been transferred or the batch is cancelled.
         *
         * @param files the selected files (File API objects)
         */
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
							var left = Math.min(afiles.length, self.getActiveMax() - self.getActiveLength());
							for(var i = 0; i < left; i++)
								self.addActive(self.uploadFile(idx++, afiles.shift()));
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

        /**
         * Native helper that starts the upload of a single file, wiring up the
         * progress, load, error and abort listeners on the underlying XHR.
         *
         * @param i    the file index within the current batch
         * @param file the File API object to upload
         * @return the upload descriptor tracking this transfer
         */
        @JsMethod
        public native JavaScriptObject uploadFile(int i, JavaScriptObject file) /*-{
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
					self.uploadResult(data,evt.loaded);
				}
				else
				{
					data.status = 1;
					self.uploadResult(data,0);
				}
			}, false);

			// File upload finished
			data.xhr.addEventListener("load", function(evt)
			{
				var infos = eval('('+evt.target.responseText+')');
				data.status = infos.status;
				data.extstatus = infos.extstatus;
				self.uploadResult(data,data.fsize);
				if(data.status==0)
				{
					data.xhr.open("put", "/upload/?", true);
					data.xhr.setRequestHeader("X-File-Name", encodeURIComponent(file.filepath?file.filepath:(file.name?file.name:file.fileName)));
					data.xhr.setRequestHeader("X-File-Parent", encodeURIComponent(self.getParentPath()));
					data.xhr.setRequestHeader("X-File-Size", file.size?file.size:file.fileSize);
					data.xhr.setRequestHeader("X-File-Type", file.type);
					self.uploadResult(data);
					data.xhr.send(file);
				}
			}, false);

			data.xhr.addEventListener("error", function(evt)
			{
				data.status = 4;
				data.extstatus = "An error occurred while transferring the file.";
				self.uploadResult(data);
			}, false);  
			
			data.xhr.addEventListener("abort", function(evt)
			{  
				data.status = 5;
				data.extstatus = "The transfer has been canceled by the user.";
				self.uploadResult(data);
			}, false);  
			
			data.xhr.open("post", "/upload/?init=1", true);
			data.xhr.setRequestHeader("X-File-Name", encodeURIComponent(file.filepath?file.filepath:(file.name?file.name:file.fileName)));
			data.xhr.setRequestHeader("X-File-Parent", encodeURIComponent(self.getParentPath()));
			data.xhr.setRequestHeader("X-File-Size", file.size);
			data.xhr.setRequestHeader("X-File-Type", file.type);
			self.uploadResult(data);
			data.xhr.send();
			return data;
		}-*/;

        /**
         * Native helper invoked to report the result/progress of an upload,
         * updating the per-file and aggregate progress accordingly.
         *
         * @param data   the upload descriptor
         * @param loaded the number of bytes transferred so far
         */
        @JsMethod
        public native void uploadResult(JavaScriptObject data, int loaded) /*-{
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

        /**
         * Builds the context menu for the upload list (create dir, edit,
         * delete, download, archive).
         *
         * @return the configured context menu
         */
        private Menu createContextMenu() {
            var ctxMenu = new Menu();
            var createDirItem = new MenuItem();
            createDirItem.setTitle("Create dir");
            createDirItem.addClickHandler(event -> UploadList.this.startEditingNew(Map.of("Name", "New Folder", IS_DIR, true, "Size", -1)));
            var editItem = new MenuItem();
            editItem.setTitle("Edit selection");
            editItem.addClickHandler(event -> UploadList.this.startEditing(UploadList.this.getRecordIndex(UploadList.this.getSelectedRecord())));
            editItem.setEnableIfCondition((target, menu, item) -> !options.isChoose && UploadList.this.getSelectedRecords().length == 1);
            var deleteItem = new MenuItem();
            deleteItem.setTitle("Delete selection");
            deleteItem.addClickHandler(event -> UploadList.this.removeSelectedData());
            deleteItem.setEnableIfCondition((target, menu, item) -> !options.isChoose && UploadList.this.getSelectedRecords().length > 0);
            var downloadItem = new MenuItem();
            downloadItem.setTitle("Download selection");
            downloadItem.addClickHandler(event -> {
                var recrd = UploadList.this.getSelectedRecord();
                var form = new DynamicForm();
                form.setAction("/download/");
                var hiddenItem = new HiddenItem("path");
                hiddenItem.setDefaultValue(recrd.getAttribute("Path"));
                form.setItems(hiddenItem);
                form.setTarget("_blank");
                form.setMethod(FormMethod.POST);
                form.setCanSubmit(true);
                form.draw();
                form.submitForm();
                form.destroy();
            });
            downloadItem.setEnableIfCondition((target, menu, item) -> !options.isChoose && UploadList.this.getSelectedRecords().length == 1);
            var archiveItem = new MenuItem();
            archiveItem.setTitle("Archive");
            archiveItem.setEnableIfCondition((target, menu, item) -> isArchiveFile());
            var archiveMenu = new Menu();
            var extractHereItem = new MenuItem();
            extractHereItem.setTitle("Extract here");
            extractHereItem.addClickHandler(e -> UploadList.this.getDataSource().performCustomOperation("extract_here",
                    UploadList.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> UploadList.this.invalidateCache()));
            var extractSubItem = new MenuItem();
            extractSubItem.setDynamicTitleFunction((target, menu, item) -> {
                var name = UploadList.this.getSelectedRecord().getAttribute("Name");
                return "Extract to " + name.substring(0, name.length() - 4) + "/";
            });
            extractSubItem.addClickHandler(e -> UploadList.this.getDataSource().performCustomOperation("extract_subfolder",
                    UploadList.this.getSelectedRecord(), (dsResponse, data, dsRequest) -> UploadList.this.invalidateCache()));
            archiveMenu.setItems(extractHereItem, extractSubItem);
            archiveItem.setSubmenu(archiveMenu);
            ctxMenu.setItems(createDirItem, editItem, deleteItem, downloadItem, archiveItem);
            return ctxMenu;
        }

        /**
         * Tells whether the single selected record is a {@code .zip} archive
         * that can be extracted.
         *
         * @return {@code true} if the selection is a single extractable zip
         */
        private boolean isArchiveFile() {
            if (!options.isChoose && UploadList.this.getSelectedRecords().length == 1)
                return new CaseInsensitiveString(UploadList.this.getSelectedRecord().getAttribute("Name")).endsWith(".zip");
            return false;
        }

        /**
         * Handles double-click on a record: enters a directory, or confirms
         * the selection when a file is double-clicked in choose mode.
         *
         * @param event the double-click event
         */
        private void onRecordDoubleClick(RecordDoubleClickEvent event) {
            ListGridRecord recrd = event.getRecord();
            String relpath = recrd.getAttribute("RelPath");
            String name = recrd.getAttribute("Name");
            if (Boolean.TRUE.equals(recrd.getAttributeAsBoolean(IS_DIR))) {
                enterDir(recrd);
            } else if (options.isChoose) {
                processPaths(options.context, cb, new PathInfo[] { new PathInfo(relpath, parent, name) });
                RemoteFileChooser.this.markForDestroy();
            }
        }
    }

    /**
     * Constructs and shows the remote file chooser for the given context.
     *
     * @param context     the calling context identifier, used to derive the
     *                    selection rules and window title
     * @param initialPath the optional initial path to expand, or {@code null}
     * @param cb          the callback invoked when a selection is confirmed
     */
    public RemoteFileChooser(String context, String initialPath, CallBack cb) {
        super();
        Client.getChildWindows().remove(this);
        final Options options = new Options(context, initialPath);
        setID("RemoteFileChooser_" + context);
        setWidth(700);
        setHeight(500);
        setAutoCenter(true);
        setIsModal(true);
        if (!options.isChoose)
            setTitle(options.selMode == SelMode.DIR ? "Manage directories" : "Manage files");
        else if (options.isMultiple)
            setTitle(options.selMode == SelMode.DIR ? "Choose directories" : "Choose files");
        else
            setTitle(options.selMode == SelMode.DIR ? "Choose a directory" : "Choose a file");
        setCanDragResize(true);
        setShowMaximizeButton(true);
        setShowModalMask(true);
        setCanDragReposition(true);
        setDismissOnEscape(false);
        addCloseClickHandler(event -> {
            if (Boolean.FALSE.equals(close.isDisabled()))
                RemoteFileChooser.this.markForDestroy();
        });

        parentLab = new Label("parent");
        parentLab.setWidth100();
        parentLab.setBorder("1px inset");

        final var splitPane = new SplitPane();
        splitPane.setHeight("*");
        splitPane.setNavigationPane(new RootList(options));
        list = new UploadList(options, cb); /* NOSONAR */
        splitPane.setDetailPane(list);
        splitPane.setDetailToolButtons(parentLab);
        splitPane.setNavigationPaneWidth(100);
        addItem(splitPane);

        var bottomBar = new HLayout();
        bottomBar.setHeight(20);
        bottomBar.setLayoutAlign(Alignment.RIGHT);
        bottomBar.setPaddingAsLayoutMargin(true);
        bottomBar.setPadding(3);
        bottomBar.setMembersMargin(3);
        bottomBar.setWidth100();
        close = new IButton(options.isChoose ? "Cancel" : "Close", e -> RemoteFileChooser.this.markForDestroy());
        close.setID("RemoteFileChooser_CloseBtn_" + context);
        close.setAutoFit(true);
        bottomBar.addMember(close);
        pbSpacer = new LayoutSpacer("*", "20");
        bottomBar.addMember(pbSpacer);
        pbLayout = new HLayout();
        pbLayout.setMembersMargin(3);
        pb = new Progressbar();
        pb.setLength("*");
        pb.setBreadth(20);
        pb.setLayoutAlign(VerticalAlignment.CENTER);
        pbText = new Label();
        pbText.setWidth100();
        pbText.setAlign(Alignment.CENTER);
        pb.addChild(pbText, "label", true);
        pbLayout.addMember(pb);
        var cancel = new IButton("Cancel", e -> cancelled = true);
        cancel.setID("RemoteFileChooser_CancelBtn_" + context);
        cancel.setAutoFit(true);
        pbLayout.addMember(cancel);
        pbLayout.hide();
        bottomBar.addMember(pbLayout);
        if (options.isChoose) {
            var choose = new IButton("Choose");
            choose.setID("RemoteFileChooser_ChooseBtn_" + context);
            choose.setAutoFit(true);
            choose.addClickHandler(clickChoose(context, cb, options));
            bottomBar.addMember(choose);
        }
        addItem(bottomBar);
        initUpload(!options.isChoose, list.getID());
        show();
    }

    /**
     * Builds the click handler for the "Choose" button: validates the current
     * selection, optionally enters a directory, then dispatches the selected
     * paths through {@link #processPaths}.
     *
     * @param context the calling context identifier
     * @param cb      the callback to invoke on confirmed selection
     * @param options the chooser options
     * @return the click handler
     */
    private ClickHandler clickChoose(String context, CallBack cb, final Options options) {
        return e -> {
            ListGridRecord[] records = list.getSelectedRecords();
            if (records.length > 0) {
                if (options.selMode == SelMode.FILE && enterDirIfRequired(records)) {
                    return;
                }
                PathInfo[] pathInfos = Stream.of(records).map(PathInfo::new).toList().toArray(new PathInfo[0]);
                processPaths(context, cb, pathInfos);
                RemoteFileChooser.this.markForDestroy();
            } else if (options.selMode == SelMode.DIR || options.selMode == SelMode.FILE_DIR) {
                processPaths(context, cb, new PathInfo[] { new PathInfo(relparent, null, null) });
                RemoteFileChooser.this.markForDestroy();
            }
        };
    }

    /**
     * If the selection consists of a single directory, enters it and signals
     * that the click should not confirm a selection.
     *
     * @param records the currently selected records
     * @return {@code true} if a directory was entered (or the selection is
     *         mixed) and the choose action should be aborted
     */
    private boolean enterDirIfRequired(ListGridRecord[] records) {
        if (records.length == 1) {
            if (Boolean.TRUE.equals(records[0].getAttributeAsBoolean(IS_DIR))) {
                list.enterDir(records[0]);
                return true;
            }
        } else if (Stream.of(records).anyMatch(r -> Boolean.TRUE.equals(r.getAttributeAsBoolean(IS_DIR)))) {
            return true;
        }
        return false;
    }

    /**
     * Native helper that recursively walks a {@code DataTransferItemList} of
     * dropped entries (files and directories) and resolves to a flat array of
     * File objects annotated with their full relative path.
     *
     * @param dataTransferItems the drag-and-drop items
     * @return a JavaScript promise resolving to the collected files
     */
    @JsMethod(namespace = JsPackage.GLOBAL)
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

    /**
     * Native helper that installs or removes the global drag-and-drop handlers
     * used to upload files into the chooser.
     *
     * @param init {@code true} to install the handlers, {@code false} to remove them
     * @param id   the event-proxy id of the target drop area
     */
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

    /**
     * Returns the currently active upload list (exposed to native JS code).
     *
     * @return the active upload list, or {@code null} if none was created
     */
    @JsMethod(namespace = JsPackage.GLOBAL)
    public static UploadList getUploadList() {
        return list;
    }

    /**
     * Removes this window from the client child-window registry when destroyed.
     */
    @Override
    protected void onDestroy() {
        Client.getChildWindows().remove(this);
        super.onDestroy();
    }

    /**
     * Persists the selected parent directory for the given context and
     * dispatches the paths to the appropriate handler (e.g. archive expansion
     * for {@code addArc}, otherwise the supplied callback).
     *
     * @param context the calling context identifier
     * @param cb      the callback to invoke, or {@code null}
     * @param paths   the selected paths
     */
    private void processPaths(String context, CallBack cb, PathInfo[] paths) {
        if (parent != null)
            Q_Global.SetProperty.instantiate().setProperty("dir." + context, parent).send();
        else if (paths != null && paths.length > 0 && paths[0].parent != null)
            Q_Global.SetProperty.instantiate().setProperty("dir." + context, paths[0].parent).send();
        switch (context) /* NOSONAR */ {
            case "addArc" -> addArc(cb, paths);
            default -> {
                if (cb != null)
                    cb.apply(paths);
            }
        }
    }

    /**
     * Expands the selected archives on the server and reports the resulting
     * entries back through the callback.
     *
     * @param cb    the callback to invoke with the expanded entries
     * @param paths the selected archive paths
     */
    private void addArc(CallBack cb, PathInfo[] paths) {
        final var request = new DSRequest();
        request.setData(Map.of("paths", Stream.of(paths).map(p -> p.path).toArray()));
        list.getDataSource().performCustomOperation("expand", null, (dsResponse, data, dsRequest) -> {
            if (cb != null)
                cb.apply(Stream.of(dsResponse.getData()).map(PathInfo::new).toList().toArray(new PathInfo[0]));
        }, request);
    }

}
