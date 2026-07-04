package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

/**
 * SmartGWT data source for the batch compressor file-result list.
 * <p>
 * Exposes the per-file outcome of a batch compression operation, where each
 * entry pairs a file path with its compression result. Supports FETCH, ADD,
 * REMOVE, UPDATE, and CUSTOM operations as POSTXML against the
 * {@code /datasources/BatchCompressorFR} endpoint.
 *
 * @since 2.5
 */
public class DSBatchCompressorFR extends RestDataSource {
    /** Base identifier and URL path segment for this data source. */
    private static final String BASENAME = "BatchCompressorFR";

    /**
     * Constructs a new batch compressor file-result data source.
     * <p>
     * Configures the XML data format, the FETCH, ADD, REMOVE, UPDATE, and
     * CUSTOM operation bindings, and the declared fields: {@code id} (primary
     * key), {@code file}, and {@code result}.
     */
    private DSBatchCompressorFR() {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);

        final var fetchop = new OperationBinding();
        fetchop.setOperationType(DSOperationType.FETCH);
        fetchop.setDataProtocol(DSProtocol.POSTXML);
        final var addop = new OperationBinding();
        addop.setOperationType(DSOperationType.ADD);
        addop.setDataProtocol(DSProtocol.POSTXML);
        final var removeop = new OperationBinding();
        removeop.setOperationType(DSOperationType.REMOVE);
        removeop.setDataProtocol(DSProtocol.POSTXML);
        final var updateop = new OperationBinding();
        updateop.setOperationType(DSOperationType.UPDATE);
        updateop.setDataProtocol(DSProtocol.POSTXML);
        final var customop = new OperationBinding();
        customop.setOperationType(DSOperationType.CUSTOM);
        customop.setDataProtocol(DSProtocol.POSTXML);

        setOperationBindings(fetchop, addop, removeop, updateop, customop);

        final var id = new DataSourceTextField("id");
        id.setPrimaryKey(true);

        setFields(id, new DataSourceTextField("file"), new DataSourceTextField("result"));
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     *
     * @return the shared {@code DSBatchCompressorFR} instance
     */
    public static DSBatchCompressorFR getInstance() {
        if (null == get(BASENAME))
            return new DSBatchCompressorFR();
        return (DSBatchCompressorFR) get(BASENAME);
    }
}
