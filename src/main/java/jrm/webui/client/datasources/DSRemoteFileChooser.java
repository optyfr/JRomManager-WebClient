package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

public class DSRemoteFileChooser extends RestDataSource
{
	private static final String BASENAME = "remoteFileChooser";

	private Map<String, String> extradata;

	@FunctionalInterface
	public interface ResponseCB
	{
		void apply(Object data);
	}

	private ResponseCB cb = null;

	protected DSRemoteFileChooser(String context)
	{
		setID(BASENAME + "_" + context);
		setDataURL("/datasources/" + BASENAME);

		setExtraData(Collections.singletonMap("context", context));

		setDataFormat(DSDataFormat.XML);

		final var fetchOB = new OperationBinding();
		fetchOB.setOperationType(DSOperationType.FETCH);
		fetchOB.setDataProtocol(DSProtocol.POSTXML);

		final var removeOB = new OperationBinding();
		removeOB.setOperationType(DSOperationType.REMOVE);
		removeOB.setDataProtocol(DSProtocol.POSTXML);

		final var addOB = new OperationBinding();
		addOB.setOperationType(DSOperationType.ADD);
		addOB.setDataProtocol(DSProtocol.POSTXML);

		final var updateOB = new OperationBinding();
		updateOB.setOperationType(DSOperationType.UPDATE);
		updateOB.setDataProtocol(DSProtocol.POSTXML);

		final var customOB = new OperationBinding();
		customOB.setOperationType(DSOperationType.CUSTOM);
		customOB.setDataProtocol(DSProtocol.POSTXML);

		setOperationBindings(fetchOB, removeOB, addOB, updateOB, customOB);

		final var nameField = new DataSourceTextField("Name");
		nameField.setPrimaryKey(true);
		final var pathField = new DataSourceTextField("Path");
		pathField.setHidden(true);
		final var isDir = new DataSourceBooleanField("isDir");
		isDir.setCanEdit(false);
		final var sizefield = new DataSourceTextField("Size");
		sizefield.setCanEdit(false);
		final var modifiedfield = new DataSourceDateTimeField("Modified");
		modifiedfield.setCanEdit(false);
		setFields(isDir, nameField, pathField, sizefield, modifiedfield);
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
		if(dsResponse.getStatus() == 0 && cb != null) cb.apply(data);
		super.transformResponse(dsResponse, dsRequest, data);
	}

	public DSRemoteFileChooser setCB(ResponseCB cb)
	{
		this.cb = cb;
		return this;
	}

	public static DSRemoteFileChooser getInstance(String context)
	{
		if(null == get(BASENAME + "_" + context)) return new DSRemoteFileChooser(context);
		return (DSRemoteFileChooser)get(BASENAME + "_" + context);
	}
}
