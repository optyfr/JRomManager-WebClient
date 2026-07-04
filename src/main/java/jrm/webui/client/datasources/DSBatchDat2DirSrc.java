package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

/**
 * SmartGWT data source for the Dat-to-Dir batch source list.
 * <p>
 * Manages the list of Dat/XML source entries that feed a Dat-to-Dir batch
 * operation. Supports FETCH, ADD, and REMOVE operations as POSTXML against the
 * {@code /datasources/BatchDat2DirSrc} endpoint.
 *
 * @since 2.5
 */
public class DSBatchDat2DirSrc extends RestDataSource {
    /** Base identifier and URL path segment for this data source. */
    private static final String BASENAME = "BatchDat2DirSrc";

    /**
     * Constructs a new Dat-to-Dir batch source data source.
     * <p>
     * Configures the XML data format, the FETCH, ADD, and REMOVE operation
     * bindings, and the declared field: {@code name} (primary key).
     */
    private DSBatchDat2DirSrc() {
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
        setOperationBindings(fetchob, addob, removeob);
        DataSourceTextField nameField = new DataSourceTextField("name");
        nameField.setPrimaryKey(true);
        setFields(
                nameField);
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     *
     * @return the shared {@code DSBatchDat2DirSrc} instance
     */
    public static DSBatchDat2DirSrc getInstance() {
        if (null == get(BASENAME))
            return new DSBatchDat2DirSrc();
        return (DSBatchDat2DirSrc) get(BASENAME);
    }
}
