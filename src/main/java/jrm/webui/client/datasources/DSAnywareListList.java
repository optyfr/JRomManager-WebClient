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

public class DSAnywareListList extends RestDataSource
{
	private static final String BASENAME = "AnywareListList";

	private Map<String, String> extradata = Collections.emptyMap();

	public DSAnywareListList()
	{
		setID(BASENAME);
		setDataURL("/datasources/"+BASENAME);
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
			new DataSourceTextField("have", Client.getSession().getMsg("SoftwareListListRenderer.Have"))
		);
	}

	public void setExtraData(Map<String, String> data)
	{
		this.extradata = data;
	}

	@Override
	protected Object transformRequest(DSRequest dsRequest)
	{
		final var data = dsRequest.getData();
		dsRequest.setBypassCache(true);
		if(data != null)
			JSOHelper.addProperties(data, JSOHelper.convertMapToJavascriptObject(extradata));
		else
			dsRequest.setData(extradata);
		return super.transformRequest(dsRequest);
	}
	
	public static DSAnywareListList getInstance()
	{
		if(null == get(BASENAME)) return new DSAnywareListList();
		return (DSAnywareListList)get(BASENAME);
	}
}
