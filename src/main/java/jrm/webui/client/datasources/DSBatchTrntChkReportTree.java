package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

public class DSBatchTrntChkReportTree extends RestDataSource
{
	private static final String BASENAME = "BatchTrntChkReportTree";
	
	private Map<String, String> extradata = Collections.emptyMap();

	@FunctionalInterface
	public interface ResponseCB
	{
		void apply(Object data);
	}

	private ResponseCB cb = null;

	public DSBatchTrntChkReportTree()
	{
		setID(BASENAME);
		setDataURL("/datasources/"+BASENAME);
		setDataFormat(DSDataFormat.XML);
		setOperationBindings(
			new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
		);
		setFields(
			new DataSourceIntegerField("ID") {{
				setPrimaryKey(true);
				setRequired(true);
			}},
			new DataSourceIntegerField("ParentID") {{
		        setRequired(true);  
		        setForeignKey(id + ".ID");  
		        setRootValue(0);
			}},
			new DataSourceTextField("title"),
			new DataSourceIntegerField("length"),
			new DataSourceTextField("status")
		);
	}

	public void setExtraData(Map<String, String> data)
	{
		this.extradata = data;
	}
	
	public DSBatchTrntChkReportTree setCB(ResponseCB cb)
	{
		this.cb = cb;
		return this;
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
		if(dsResponse.getStatus() == 0 && cb != null) cb.apply(data);
		super.transformResponse(dsResponse, dsRequest, data);
	}

	public static DSBatchTrntChkReportTree getInstance(String context)
	{
		if(null == get(BASENAME)) return new DSBatchTrntChkReportTree();
		return (DSBatchTrntChkReportTree)get(BASENAME);
	}
}
