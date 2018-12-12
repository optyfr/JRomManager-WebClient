package jrm.webui.client.protocol;

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

import jrm.webui.client.utils.EnhJSO;

public class Q_Dir2Dat extends Q_
{
	protected Q_Dir2Dat()
	{
		super();
	}
	
	public static class Start extends Q_
	{
		protected Start()
		{
			super();
		}
		
		final public static Start instantiate()
		{
			return Q_.instantiateCmd("Dir2Dat.start").cast();
		}

		final public Start setOptions(Map<String,Object> values)
		{
			EnhJSO jso = JavaScriptObject.createObject().cast();
			values.forEach((k,v)->jso.set(k, v));
			getParams().set("options", jso);
			return this;
		}

		final public Start setHeaders(Map<String,Object> values)
		{
			EnhJSO jso = JavaScriptObject.createObject().cast();
			values.forEach((k,v)->jso.set(k, v));
			getParams().set("headers", jso);
			return this;
		}

		final public Start setIO(Map<String,Object> values)
		{
			EnhJSO jso = JavaScriptObject.createObject().cast();
			values.forEach((k,v)->jso.set(k, v));
			getParams().set("io", jso);
			return this;
		}
	}
}
