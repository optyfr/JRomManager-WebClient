package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

/**
 * SmartGWT data source for the list of wares within a software list.
 * <p>
 * Exposes the entries of a single {@code AnywareListList} (a software list),
 * each describing a ware with its status, type, description, availability, and
 * clone/sample relationships. Supports FETCH, UPDATE, and CUSTOM operations as
 * POSTXML against the {@code /datasources/AnywareList} endpoint, and reports the
 * total number of found entries back through the response.
 *
 * @since 2.5
 */
public class DSAnywareList extends RestDataSource {
    /** Base identifier and URL path segment for this data source. */
    private static final String BASENAME = "AnywareList";

    /** Additional data merged into every outgoing request. */
    private Map<String, String> extradata = Collections.emptyMap();

    /**
     * Constructs a new {@code AnywareList} data source.
     * <p>
     * Configures the XML data format, the FETCH, UPDATE, and CUSTOM operation
     * bindings, and the declared fields: {@code status}, {@code list},
     * {@code name}, {@code type}, {@code description}, {@code have},
     * {@code cloneof}, {@code cloneof_status}, {@code romof}, {@code romof_status},
     * {@code sampleof}, {@code sampleof_status}, and {@code selected}.
     */
    private DSAnywareList() {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);

        OperationBinding fetchob = new OperationBinding();
        fetchob.setOperationType(DSOperationType.FETCH);
        fetchob.setDataProtocol(DSProtocol.POSTXML);
        OperationBinding updateob = new OperationBinding();
        updateob.setOperationType(DSOperationType.UPDATE);
        updateob.setDataProtocol(DSProtocol.POSTXML);
        OperationBinding customob = new OperationBinding();
        customob.setOperationType(DSOperationType.CUSTOM);
        customob.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(fetchob, updateob, customob);

        DataSourceTextField listField = new DataSourceTextField("list");
        listField.setPrimaryKey(true);
        listField.setHidden(true);
        listField.setForeignKey("AnywareListList.name");
        DataSourceTextField nameField = new DataSourceTextField("name");
        nameField.setPrimaryKey(true);
        setFields(
                new DataSourceTextField("status"),
                listField,
                nameField,
                new DataSourceTextField("type"),
                new DataSourceTextField("description"),
                new DataSourceTextField("have"),
                new DataSourceTextField("cloneof"),
                new DataSourceTextField("cloneof_status"),
                new DataSourceTextField("romof"),
                new DataSourceTextField("romof_status"),
                new DataSourceTextField("sampleof"),
                new DataSourceTextField("sampleof_status"),
                new DataSourceBooleanField("selected"));
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
     * {@inheritDoc}
     * <p>
     * On a successful response, extracts the {@code found} count from the
     * XML payload and stores it as a response attribute before delegating
     * to the superclass implementation.
     */
    @Override
    protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data) {
        if (dsResponse.getStatus() == 0)
            dsResponse.setAttribute("found", XMLTools.selectString(data, "/response/found"));
        super.transformResponse(dsResponse, dsRequest, data);
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     *
     * @return the shared {@code DSAnywareList} instance
     */
    public static DSAnywareList getInstance() {
        if (null == get(BASENAME))
            return new DSAnywareList();
        return (DSAnywareList) get(BASENAME);
    }
}
