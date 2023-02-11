package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class DSBatchDat2DirResult extends RestDataSource
{
	private static final String BASENAME = "BatchDat2DirResult";

	public DSBatchDat2DirResult(String foreignKey)
	{
		setID(BASENAME);
		setDataURL("/datasources/"+BASENAME);
		setDataFormat(DSDataFormat.XML);
		setOperationBindings(
			new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
		);
		setFields(
			new DataSourceTextField("src","Dat/XML") {{
				setPrimaryKey(true);
				setForeignKey(foreignKey);
			}},
			new DataSourceIntegerField("have"),
			new DataSourceIntegerField("create"),
			new DataSourceIntegerField("fix"),
			new DataSourceIntegerField("miss"),
			new DataSourceIntegerField("total")
		);
	}

	public static DSBatchDat2DirResult getInstance(String foreignKey)
	{
		if(null == get(BASENAME)) return new DSBatchDat2DirResult(foreignKey);
		return (DSBatchDat2DirResult)get(BASENAME);
	}
}
