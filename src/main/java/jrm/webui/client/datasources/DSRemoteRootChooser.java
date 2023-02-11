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

public class DSRemoteRootChooser extends RestDataSource
{
	private static final String BASENAME = "remoteRootChooser";
	
	private Map<String, String> extradata;

	protected DSRemoteRootChooser(String context)
	{
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

	public void setExtraData(Map<String, String> data)
	{
		this.extradata = data;
	}
	
	@Override
	protected Object transformRequest(DSRequest dsRequest)
	{
		final var data = dsRequest.getData();
		if(data != null)
			JSOHelper.addProperties(data, JSOHelper.convertMapToJavascriptObject(extradata));
		else
			dsRequest.setData(extradata);
		return super.transformRequest(dsRequest);
	}
	
	public static DataSource getInstance(String context)
	{
		if(null == get(BASENAME + "_" + context)) return new DSRemoteRootChooser(context);
		return get(BASENAME + "_" + context);
	}

}
