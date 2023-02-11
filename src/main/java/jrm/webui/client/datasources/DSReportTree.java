package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

public class DSReportTree extends RestDataSource
{
	private static final String TITLE = "title";
	private static final String PARENT_ID = "ParentID";
	private static final String STATUS = "status";
	private static final String IS_FIXABLE = "isFixable";

	private static final String BASENAME = "Report";

	private Map<String, String> extradata = Collections.emptyMap();

	@FunctionalInterface
	public interface ResponseCB
	{
		void apply(Object data);
	}

	private ResponseCB cb = null;

	private DSReportTree(String src)
	{
		setID(BASENAME);
		setDataURL("/datasources/" + BASENAME);
		setDataFormat(DSDataFormat.XML);
		setTitleField(TITLE);
		
		if (src != null)
			setExtraData(Collections.singletonMap("src", src));

		final var fetch = new OperationBinding();
		fetch.setOperationType(DSOperationType.FETCH);
		fetch.setDataProtocol(DSProtocol.POSTXML);
		final var custom = new OperationBinding();
		custom.setOperationType(DSOperationType.CUSTOM);
		custom.setDataProtocol(DSProtocol.POSTXML);
		setOperationBindings(fetch, custom);
		
		DataSourceTextField nameField = new DataSourceTextField(TITLE);
		DataSourceIntegerField idField = new DataSourceIntegerField("ID");
		idField.setPrimaryKey(true);
		idField.setRequired(true);
		DataSourceIntegerField parentIDField = new DataSourceIntegerField(PARENT_ID);
		parentIDField.setRequired(true);
		parentIDField.setForeignKey(id + ".ID");
		parentIDField.setRootValue(0);
		DataSourceTextField classField = new DataSourceTextField("class");
		DataSourceTextField statusField = new DataSourceTextField(STATUS);
		DataSourceBooleanField hasNotesField = new DataSourceBooleanField("hasNotes");
		DataSourceBooleanField isFixableField = new DataSourceBooleanField(IS_FIXABLE);
		setFields(nameField, idField, parentIDField, classField, statusField, hasNotesField, isFixableField);
	}
	
	public void setExtraData(Map<String, String> data)
	{
		this.extradata = data;
	}
	
	public DSReportTree setCB(ResponseCB cb)
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

	public static DSReportTree getInstance(String src)
	{
		if(null == get(BASENAME)) return new DSReportTree(src);
		return (DSReportTree)get(BASENAME);
	}
}
