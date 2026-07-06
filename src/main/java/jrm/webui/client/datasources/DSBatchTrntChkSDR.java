package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

/**
 * SmartGWT data source for the batch Torrent Check source-destination-result list.
 * <p>
 * Manages the list of source/destination pairs to be checked by the Torrent Check
 * batch operation, including selection state and result status.
 *
 * @since 2.5
 */
public final class DSBatchTrntChkSDR extends RestDataSource {
    private static final String BASENAME = "BatchTrntChkSDR";

    /**
     * Constructs a new batch Torrent Check SDR data source.
     */
    private DSBatchTrntChkSDR() {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);
        OperationBinding fetchob = new OperationBinding();
        fetchob.setOperationType(DSOperationType.FETCH);
        fetchob.setDataProtocol(DSProtocol.POSTXML);
        OperationBinding addob = new OperationBinding();
        addob.setOperationType(DSOperationType.ADD);
        addob.setDataProtocol(DSProtocol.POSTXML);
        OperationBinding removeob = new OperationBinding();
        removeob.setOperationType(DSOperationType.REMOVE);
        removeob.setDataProtocol(DSProtocol.POSTXML);
        OperationBinding updateob = new OperationBinding();
        updateob.setOperationType(DSOperationType.UPDATE);
        updateob.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(fetchob, addob, removeob, updateob);
        DataSourceTextField idField = new DataSourceTextField("id");
        idField.setPrimaryKey(true);
        setFields(
                idField,
                new DataSourceTextField("src"),
                new DataSourceTextField("dst"),
                new DataSourceTextField("result"),
                new DataSourceBooleanField("selected"));
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     *
     * @return the shared {@code DSBatchTrntChkSDR} instance
     */
    public static DSBatchTrntChkSDR getInstance() {
        if (null == get(BASENAME))
            return new DSBatchTrntChkSDR();
        return (DSBatchTrntChkSDR) get(BASENAME);
    }
}