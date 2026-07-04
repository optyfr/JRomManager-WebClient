package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

/**
 * SmartGWT data source for the Dat-to-Dir batch source-destination-result list.
 * <p>
 * Manages the list of source/destination directory pairs to be processed by the
 * Dat-to-Dir batch operation, tracking each entry's result status and selection
 * state. Supports FETCH, ADD, REMOVE, and UPDATE operations as POSTXML against
 * the {@code /datasources/BatchDat2DirSDR} endpoint.
 *
 * @since 2.5
 */
public class DSBatchDat2DirSDR extends RestDataSource {
    /** Base identifier and URL path segment for this data source. */
    private static final String BASENAME = "BatchDat2DirSDR";

    /**
     * Constructs a new Dat-to-Dir batch SDR data source.
     * <p>
     * Configures the XML data format, the FETCH, ADD, REMOVE, and UPDATE
     * operation bindings, and the declared fields: {@code id} (primary key),
     * {@code src}, {@code dst}, {@code result}, and {@code selected}.
     */
    private DSBatchDat2DirSDR() {
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
     * @return the shared {@code DSBatchDat2DirSDR} instance
     */
    public static DSBatchDat2DirSDR getInstance() {
        if (null == get(BASENAME))
            return new DSBatchDat2DirSDR();
        return (DSBatchDat2DirSDR) get(BASENAME);
    }
}
