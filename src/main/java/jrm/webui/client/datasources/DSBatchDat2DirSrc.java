package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class DSBatchDat2DirSrc extends RestDataSource
{
	private static final String BASENAME = "BatchDat2DirSrc";

	private DSBatchDat2DirSrc()
	{
		setID(BASENAME);
		setDataURL("/datasources/"+BASENAME);
		setDataFormat(DSDataFormat.XML);
		setOperationBindings(
			new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
			new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
			new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}}
		);
		setFields(
			new DataSourceTextField("name") {{
				setPrimaryKey(true);
			}}
		);
	}
	
	public static DSBatchDat2DirSrc getInstance()
	{
		if(null == get(BASENAME)) return new DSBatchDat2DirSrc();
		return (DSBatchDat2DirSrc)get(BASENAME);
	}
}
