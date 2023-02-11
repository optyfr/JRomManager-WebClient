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

public class DSAnywareList extends RestDataSource
{
	private static final String BASENAME = "AnywareList";

	private Map<String, String> extradata = Collections.emptyMap();

	private DSAnywareList()
	{
		setID("AnywareList");
		setDataURL("/datasources/"+BASENAME);
		setDataFormat(DSDataFormat.XML);
		setOperationBindings(
			new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
			new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}},
			new OperationBinding(){{setOperationType(DSOperationType.CUSTOM);setDataProtocol(DSProtocol.POSTXML);}}
		);
		setFields(
			new DataSourceTextField("status"),
			new DataSourceTextField("list") {{
				setPrimaryKey(true);
				setHidden(true);
				setForeignKey("AnywareListList.name");
			}},
			new DataSourceTextField("name") {{
				setPrimaryKey(true);
			}},
			new DataSourceTextField("type"),
			new DataSourceTextField("description"),
			new DataSourceTextField("have"),
			new DataSourceTextField("cloneof"),
			new DataSourceTextField("cloneof_status"),
			new DataSourceTextField("romof"),
			new DataSourceTextField("romof_status"),
			new DataSourceTextField("sampleof"),
			new DataSourceTextField("sampleof_status"),
			new DataSourceBooleanField("selected")
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
		if(data != null)
			JSOHelper.addProperties(data, JSOHelper.convertMapToJavascriptObject(extradata));
		else
			dsRequest.setData(extradata);
		return super.transformRequest(dsRequest);
	}
	
	@Override
	protected void transformResponse(DSResponse dsResponse, DSRequest dsRequest, Object data)
	{
		if(dsResponse.getStatus()==0)
			dsResponse.setAttribute("found", XMLTools.selectString(data, "/response/found"));
		super.transformResponse(dsResponse, dsRequest, data);
	}

	public static DSAnywareList getInstance()
	{
		if(null == get(BASENAME)) return new DSAnywareList();
		return (DSAnywareList)get(BASENAME);
	}
}
