package jrm.webui.client.protocol;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;

import jrm.webui.client.Client;
import jrm.webui.client.utils.EnhJSO;

public class Q_ extends EnhJSO	//NOSONAR
{
	private static final String PARAMS_STR = "params";

	protected Q_()
	{
		super();
	}

	static final private Q_ _instantiate()	//NOSONAR
	{
		return JavaScriptObject.createObject().cast();
	}
	
	protected static final Q_ instantiateCmd(String cmd)
	{
		Q_ q = _instantiate();
		q.setCmd(cmd);
		return q;
	}
	
	protected final void setCmd(String cmd)
	{
		set("cmd", cmd);
	}

	protected final EnhJSO getParams()
	{
		if(!exists(PARAMS_STR))
			set(PARAMS_STR, JavaScriptObject.createObject());
		return getJSO(PARAMS_STR);
	}
	
	public final void send()
	{
		Client.sendMsg(JsonUtils.stringify(this));
	}
	
}
