package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

/**
 * SmartGWT data source for the scan report tree.
 * <p>
 * Provides a hierarchical tree structure for displaying ROM scan results,
 * including status information and fixability indicators. Supports extra data
 * injection and an optional response callback for post-processing.
 *
 * @since 2.5
 */
public class DSReportTree extends RestDataSource {
    private static final String TITLE = "title";
    private static final String PARENT_ID = "ParentID";
    private static final String STATUS = "status";
    private static final String IS_FIXABLE = "isFixable";

    private static final String BASENAME = "Report";

    /** Additional data merged into every outgoing request. */
    private Map<String, String> extradata = Collections.emptyMap();

    /**
     * Callback invoked when a response is successfully received.
     */
    @FunctionalInterface
    public interface ResponseCB {
        /**
         * Processes the raw response data.
         *
         * @param data the raw response data
         */
        void apply(Object data);
    }

    /** Optional callback for response post-processing. */
    private ResponseCB cb = null;

    /**
     * Constructs a new report tree data source.
     *
     * @param src optional source filter; may be {@code null}
     */
    private DSReportTree(String src) {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);
        setTitleField(TITLE);

        if (src != null)
            setExtraData(Collections.singletonMap("src", src));

        final var fetch = new OperationBinding();
        fetch.setOperationType(DSOperationType.FETCH);
        fetch.setDataProtocol(DSProtocol.POSTXML);
        final var custom = new OperationBinding();
        custom.setOperationType(DSOperationType.CUSTOM);
        custom.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(fetch, custom);

        DataSourceTextField nameField = new DataSourceTextField(TITLE);
        DataSourceIntegerField idField = new DataSourceIntegerField("ID");
        idField.setPrimaryKey(true);
        idField.setRequired(true);
        DataSourceIntegerField parentIDField = new DataSourceIntegerField(PARENT_ID);
        parentIDField.setRequired(true);
        parentIDField.setForeignKey(id + ".ID");
        parentIDField.setRootValue(0);
        DataSourceTextField classField = new DataSourceTextField("class");
        DataSourceTextField statusField = new DataSourceTextField(STATUS);
        DataSourceBooleanField hasNotesField = new DataSourceBooleanField("hasNotes");
        DataSourceBooleanField isFixableField = new DataSourceBooleanField(IS_FIXABLE);
        setFields(nameField, idField, parentIDField, classField, statusField, hasNotesField, isFixableField);
    }

    /**
     * Sets additional data to be merged into every outgoing request.
     *
     * @param data the extra data map
     */
    public void setExtraData(Map<String, String> data) {
        this.extradata = data;
    }

    /**
     * Sets the optional response callback.
     *
     * @param cb the callback, or {@code null} to clear
     * @return this instance for chaining
     */
    public DSReportTree setCB(ResponseCB cb) {
        this.cb = cb;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * Merges extra data into the request before sending.
     */
    @Override
    protected Object transformRequest(DSRequest dsRequest) {
        final var data = dsRequest.getData();
        if (data != null)
            JSOHelper.addProperties(data, JSOHelper.convertMapToJavascriptObject(extradata));
        else
            dsRequest.setData(extradata);
        return super.transformRequest(dsRequest);
    }

    /**
     * {@inheritDoc}
     *
     * Invokes the response callback if present and the status is successful.
     */
    @Override
    protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data) {
        if (dsResponse.getStatus() == 0 && cb != null)
            cb.apply(data);
        super.transformResponse(dsResponse, dsRequest, data);
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     *
     * @param src optional source filter; may be {@code null}
     * @return the shared {@code DSReportTree} instance
     */
    public static DSReportTree getInstance(String src) {
        if (null == get(BASENAME))
            return new DSReportTree(src);
        return (DSReportTree) get(BASENAME);
    }
}
