package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class DSBatchCompressorFR extends RestDataSource
{
	private static final String BASENAME = "BatchCompressorFR";

	private DSBatchCompressorFR()
	{
		setID(BASENAME);
		setDataURL("/datasources/" + BASENAME);
		setDataFormat(DSDataFormat.XML);

		final var fetchop = new OperationBinding();
		fetchop.setOperationType(DSOperationType.FETCH);
		fetchop.setDataProtocol(DSProtocol.POSTXML);
		final var addop = new OperationBinding();
		addop.setOperationType(DSOperationType.ADD);
		addop.setDataProtocol(DSProtocol.POSTXML);
		final var removeop = new OperationBinding();
		removeop.setOperationType(DSOperationType.REMOVE);
		removeop.setDataProtocol(DSProtocol.POSTXML);
		final var updateop = new OperationBinding();
		updateop.setOperationType(DSOperationType.UPDATE);
		updateop.setDataProtocol(DSProtocol.POSTXML);
		final var customop = new OperationBinding();
		customop.setOperationType(DSOperationType.CUSTOM);
		customop.setDataProtocol(DSProtocol.POSTXML);

		setOperationBindings(fetchop, addop, removeop, updateop, customop);

		final var id = new DataSourceTextField("id");
		id.setPrimaryKey(true);

		setFields(id, new DataSourceTextField("file"), new DataSourceTextField("result"));
	}

	
	public static DSBatchCompressorFR getInstance()
	{
		if(null == get(BASENAME)) return new DSBatchCompressorFR();
		return (DSBatchCompressorFR)get(BASENAME);
	}
}
