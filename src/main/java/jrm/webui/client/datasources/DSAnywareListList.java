package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

import jrm.webui.client.Client;

/**
 * SmartGWT data source for the list of software lists.
 * <p>
 * Exposes the top-level collection of software lists (each an
 * {@code AnywareList}) available to the client, with localized title and
 * description labels resolved from the server session. Requests are issued as
 * POSTXML FETCH operations against the {@code /datasources/AnywareListList}
 * endpoint, with cache bypass and optional extra data on each request.
 *
 * @since 2.5
 */
public class DSAnywareListList extends RestDataSource {
    /** Base identifier and URL path segment for this data source. */
    private static final String BASENAME = "AnywareListList";

    /** Additional data merged into every outgoing request. */
    private Map<String, String> extradata = Collections.emptyMap();

    /**
     * Constructs a new {@code AnywareListList} data source.
     * <p>
     * Configures the XML data format, the FETCH operation binding, and the
     * declared fields: {@code status}, {@code name}, {@code description}, and
     * {@code have}. The {@code name}, {@code description}, and {@code have}
     * field titles are localized through the server session message bundle.
     */
    public DSAnywareListList() {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);
        OperationBinding fetchob = new OperationBinding();
        fetchob.setOperationType(DSOperationType.FETCH);
        fetchob.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(fetchob);
        DataSourceTextField nameField = new DataSourceTextField("name", Client.getSession().getMsg("SoftwareListListRenderer.Name"));
        nameField.setPrimaryKey(true);
        setFields(
                new DataSourceTextField("status"),
                nameField,
                new DataSourceTextField("description", Client.getSession().getMsg("SoftwareListListRenderer.Description")),
                new DataSourceTextField("have", Client.getSession().getMsg("SoftwareListListRenderer.Have")));
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
     * Forces a cache bypass and merges extra data into the request before sending.
     */
    @Override
    protected Object transformRequest(DSRequest dsRequest) {
        final var data = dsRequest.getData();
        dsRequest.setBypassCache(true);
        if (data != null)
            JSOHelper.addProperties(data, JSOHelper.convertMapToJavascriptObject(extradata));
        else
            dsRequest.setData(extradata);
        return super.transformRequest(dsRequest);
    }

    /**
     * Returns the singleton instance, creating it if necessary.
     *
     * @return the shared {@code DSAnywareListList} instance
     */
    public static DSAnywareListList getInstance() {
        if (null == get(BASENAME))
            return new DSAnywareListList();
        return (DSAnywareListList) get(BASENAME);
    }
}
