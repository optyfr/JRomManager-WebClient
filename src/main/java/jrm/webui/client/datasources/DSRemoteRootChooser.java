package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

/**
 * SmartGWT data source for the remote root chooser.
 * <p>
 * Provides the list of available root directories on the server for a given context.
 * Each instance is scoped to a specific context identifier and supports extra data
 * injection into outgoing requests.
 *
 * @since 2.5
 */
public class DSRemoteRootChooser extends RestDataSource {
    private static final String BASENAME = "remoteRootChooser";

    /** Additional data merged into every outgoing request. */
    private Map<String, String> extradata;

    /**
     * Constructs a new remote root chooser data source for the given context.
     *
     * @param context the context identifier scoping this data source instance
     */
    protected DSRemoteRootChooser(String context) {
        setID(BASENAME + "_" + context);
        setDataURL("/datasources/" + BASENAME);

        setExtraData(Collections.singletonMap("context", context));

        setDataFormat(DSDataFormat.XML);
        OperationBinding operationBinding = new OperationBinding();
        operationBinding.setOperationType(DSOperationType.FETCH);
        operationBinding.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(operationBinding);
        DataSourceTextField nameField = new DataSourceTextField("Name");
        DataSourceTextField pathField = new DataSourceTextField("Path");
        pathField.setHidden(true);
        pathField.setPrimaryKey(true);
        setFields(nameField, pathField);
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
     * Returns the instance for the given context, creating it if necessary.
     *
     * @param context the context identifier
     * @return the shared {@code DataSource} instance for the context
     */
    public static DataSource getInstance(String context) {
        if (null == get(BASENAME + "_" + context))
            return new DSRemoteRootChooser(context);
        return get(BASENAME + "_" + context);
    }

}
