package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class DSBatchDat2DirSDR extends RestDataSource
{
	private static final String BASENAME = "BatchDat2DirSDR";

	private DSBatchDat2DirSDR()
	{
		setID(BASENAME);
		setDataURL("/datasources/"+BASENAME);
		setDataFormat(DSDataFormat.XML);
		OperationBinding fetchob = new OperationBinding();
		fetchob.setOperationType(DSOperationType.FETCH);
		fetchob.setDataProtocol(DSProtocol.POSTXML);
		OperationBinding addob = new OperationBinding();
		addob.setOperationType(DSOperationType.ADD);
		addob.setDataProtocol(DSProtocol.POSTXML);
		OperationBinding removeob = new OperationBinding();
		removeob.setOperationType(DSOperationType.REMOVE);
		removeob.setDataProtocol(DSProtocol.POSTXML);
		OperationBinding updateob = new OperationBinding();
		updateob.setOperationType(DSOperationType.UPDATE);
		updateob.setDataProtocol(DSProtocol.POSTXML);
		setOperationBindings(fetchob, addob, removeob, updateob);
		DataSourceTextField idField = new DataSourceTextField("id");
		idField.setPrimaryKey(true);
		setFields(
			idField,
			new DataSourceTextField("src"),
			new DataSourceTextField("dst"),
			new DataSourceTextField("result"),
			new DataSourceBooleanField("selected")
		);
	}

	public static DSBatchDat2DirSDR getInstance()
	{
		if(null == get(BASENAME)) return new DSBatchDat2DirSDR();
		return (DSBatchDat2DirSDR)get(BASENAME);
	}
}
