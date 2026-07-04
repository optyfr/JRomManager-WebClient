package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

/**
 * SmartGWT data source for individual ware entries (ROMs, disks, samples, etc.).
 * <p>
 * Exposes the per-ware details attached to a parent {@code AnywareList} entry,
 * including hash checksums (CRC, MD5, SHA-1) and size. Requests are issued as
 * POSTXML FETCH operations against the {@code /datasources/Anyware} endpoint,
 * with optional extra data merged into each outgoing request.
 *
 * @since 2.5
 */
public class DSAnyware extends RestDataSource {
    /** Base identifier and URL path segment for this data source. */
    private static final String BASENAME = "Anyware";

    /** Additional data merged into every outgoing request. */
    private Map<String, String> extradata = Collections.emptyMap();

    /**
     * Constructs a new {@code Anyware} data source.
     * <p>
     * Configures the XML data format, the FETCH operation binding, and the
     * declared fields: {@code list}, {@code ware}, {@code name}, {@code status},
     * {@code size}, {@code crc}, {@code md5}, and {@code sha1}.
     */
    private DSAnyware() {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);
        OperationBinding operationBinding = new OperationBinding();
        operationBinding.setOperationType(DSOperationType.FETCH);
        operationBinding.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(operationBinding);
        DataSourceTextField listField = new DataSourceTextField("list");
        listField.setPrimaryKey(true);
        listField.setHidden(true);
        listField.setForeignKey("AnywareList.list");
        DataSourceTextField wareField = new DataSourceTextField("ware");
        wareField.setPrimaryKey(true);
        wareField.setHidden(true);
        wareField.setForeignKey("AnywareList.name");
        DataSourceTextField nameField = new DataSourceTextField("name");
        nameField.setPrimaryKey(true);
        setFields(
                listField,
                wareField,
                nameField,
                new DataSourceTextField("status"),
                new DataSourceIntegerField("size"),
                new DataSourceTextField("crc"),
                new DataSourceTextField("md5"),
                new DataSourceTextField("sha1"));
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
     * {@inheritDoc}
     * <p>
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
     * Returns the singleton instance, creating it if necessary.
     *
     * @return the shared {@code DSAnyware} instance
     */
    public static DSAnyware getInstance() {
        if (null == get(BASENAME))
            return new DSAnyware();
        return (DSAnyware) get(BASENAME);
    }
}
