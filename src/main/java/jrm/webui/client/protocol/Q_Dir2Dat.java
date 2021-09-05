package jrm.webui.client.protocol;

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

import jrm.webui.client.utils.EnhJSO;

public class Q_Dir2Dat extends Q_	//NOSONAR
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
		
		public static final Start instantiate()
		{
			return Q_.instantiateCmd("Dir2Dat.start").cast();
		}

		public final Start setOptions(Map<String,Object> values)
		{
			EnhJSO jso = JavaScriptObject.createObject().cast();
			values.forEach(jso::set);
			getParams().set("options", jso);
			return this;
		}

		public final Start setHeaders(Map<String,Object> values)
		{
			EnhJSO jso = JavaScriptObject.createObject().cast();
			values.forEach(jso::set);
			getParams().set("headers", jso);
			return this;
		}

		public final Start setIO(Map<String,Object> values)
		{
			EnhJSO jso = JavaScriptObject.createObject().cast();
			values.forEach(jso::set);
			getParams().set("io", jso);
			return this;
		}
	}
}
