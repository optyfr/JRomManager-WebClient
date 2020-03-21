package jrm.webui.client.protocol;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;

import jrm.webui.client.Client;
import jrm.webui.client.utils.EnhJSO;

public class Q_ extends EnhJSO
{
	protected Q_()
	{
		super();
	}

	final static private Q_ _instantiate()
	{
		return JavaScriptObject.createObject().cast();
	}
	
	final static protected Q_ instantiateCmd(String cmd)
	{
		Q_ q = _instantiate();
		q.setCmd(cmd);
		return q;
	}
	
	final protected void setCmd(String cmd)
	{
		set("cmd", cmd);
	}

	final protected EnhJSO getParams()
	{
		if(!exists("params"))
			set("params", JavaScriptObject.createObject());
		return getJSO("params");
	}
	
	final public void send()
	{
		Client.sendMsg(JsonUtils.stringify(this));
	}
	
}
