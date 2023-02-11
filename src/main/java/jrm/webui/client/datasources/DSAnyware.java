package jrm.webui.client.datasources;

import java.util.Collections;
import java.util.Map;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.util.JSOHelper;

public class DSAnyware extends RestDataSource
{
	private static final String BASENAME = "Anyware";

	private Map<String, String> extradata = Collections.emptyMap();

	private DSAnyware()
	{
		setID("Anyware");
		setDataURL("/datasources/" + BASENAME);
		setDataFormat(DSDataFormat.XML);
		setOperationBindings(new OperationBinding()
		{
			{
				setOperationType(DSOperationType.FETCH);
				setDataProtocol(DSProtocol.POSTXML);
			}
		});
		setFields(
			new DataSourceTextField("list") {{
				setPrimaryKey(true);
				setHidden(true);
				setForeignKey("AnywareList.list");
			}},
			new DataSourceTextField("ware") {{
				setPrimaryKey(true);
				setHidden(true);
				setForeignKey("AnywareList.name");
			}},
			new DataSourceTextField("name") {{
				setPrimaryKey(true);
			}},
			new DataSourceTextField("status"),
			new DataSourceIntegerField("size"),
			new DataSourceTextField("crc"),
			new DataSourceTextField("md5"),
			new DataSourceTextField("sha1")
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
	
	public static DSAnyware getInstance()
	{
		if(null == get(BASENAME)) return new DSAnyware();
		return (DSAnyware)get(BASENAME);
	}
}
