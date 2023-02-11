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
		setOperationBindings(
			new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
			new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
			new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}},
			new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}}
		);
		setFields(
			new DataSourceTextField("id") {{
				setPrimaryKey(true);
			}},
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
