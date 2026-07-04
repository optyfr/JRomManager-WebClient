package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

/**
 * SmartGWT data source for the batch Torrent Check report tree.
 * <p>
 * Provides a hierarchical tree structure with parent-child relationships
 * for displaying Torrent Check batch results. Supports extra data injection
 * and an optional response callback for post-processing.
 *
 * @since 2.5
 */
public class DSBatchTrntChkReportTree extends RestDataSource {
    private static final String BASENAME = "BatchTrntChkReportTree";

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
     * Constructs a new batch Torrent Check report tree data source.
     */
    private DSBatchTrntChkReportTree() {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);
        OperationBinding operationBinding = new OperationBinding();
        operationBinding.setOperationType(DSOperationType.FETCH);
        operationBinding.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(
                operationBinding);
        DataSourceIntegerField idField = new DataSourceIntegerField("ID");
        idField.setPrimaryKey(true);
        idField.setRequired(true);
        DataSourceIntegerField parentIdField = new DataSourceIntegerField("ParentID");
        parentIdField.setRequired(true);
        parentIdField.setForeignKey(id + ".ID");
        parentIdField.setRootValue(0);
        setFields(
                idField,
                parentIdField,
                new DataSourceTextField("title"),
                new DataSourceIntegerField("length"),
                new DataSourceTextField("status"));
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
    public DSBatchTrntChkReportTree setCB(ResponseCB cb) {
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
     * @return the shared {@code DSBatchTrntChkReportTree} instance
     */
    public static DSBatchTrntChkReportTree getInstance() {
        if (null == get(BASENAME))
            return new DSBatchTrntChkReportTree();
        return (DSBatchTrntChkReportTree) get(BASENAME);
    }
}
