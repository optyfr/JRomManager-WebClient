package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

/**
 * SmartGWT data source for the Dat-to-Dir batch result summary.
 * <p>
 * Exposes per-source aggregate counts for a Dat-to-Dir batch operation, namely
 * how many entries were already present ({@code have}), created, fixed, missing,
 * and the total. The {@code src} field acts as the primary key and references a
 * foreign key from the parent data source so the result rows can be linked back
 * to their originating Dat/XML source entry.
 *
 * @since 2.5
 */
public class DSBatchDat2DirResult extends RestDataSource {
    /** Base identifier and URL path segment for this data source. */
    private static final String BASENAME = "BatchDat2DirResult";

    /**
     * Constructs a new Dat-to-Dir batch result data source bound to the given
     * foreign key.
     * <p>
     * Configures the XML data format, the FETCH operation binding, and the
     * declared fields: {@code src} (primary key with a foreign key to the
     * parent data source), {@code have}, {@code create}, {@code fix},
     * {@code miss}, and {@code total}.
     *
     * @param foreignKey the foreign key linking {@code src} to its parent
     *                   data source field, in the form {@code DataSourceID.field}
     */
    public DSBatchDat2DirResult(String foreignKey) {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);
        OperationBinding fetchob = new OperationBinding();
        fetchob.setOperationType(DSOperationType.FETCH);
        fetchob.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(fetchob);
        DataSourceTextField srcField = new DataSourceTextField("src", "Dat/XML");
        srcField.setPrimaryKey(true);
        srcField.setForeignKey(foreignKey);
        setFields(
                srcField,
                new DataSourceIntegerField("have"),
                new DataSourceIntegerField("create"),
                new DataSourceIntegerField("fix"),
                new DataSourceIntegerField("miss"),
                new DataSourceIntegerField("total"));
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     *
     * @param foreignKey the foreign key to pass to the constructor when a new
     *                   instance is created; ignored when an instance already exists
     * @return the shared {@code DSBatchDat2DirResult} instance
     */
    public static DSBatchDat2DirResult getInstance(String foreignKey) {
        if (null == get(BASENAME))
            return new DSBatchDat2DirResult(foreignKey);
        return (DSBatchDat2DirResult) get(BASENAME);
    }
}
