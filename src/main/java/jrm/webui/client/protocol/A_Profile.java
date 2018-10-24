package jrm.webui.client.protocol;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import jrm.webui.client.utils.EnhJSO;

public class A_Profile extends A_
{
	public A_Profile(final A_ a)
	{
		this(a.response);
	}
	
	public A_Profile(final EnhJSO response)
	{
		super(response);
	}
	
	public static class Loaded extends A_
	{
		private EnhJSO params;
		
		public Loaded(final A_ a)
		{
			this(a.response);
		}
		
		public Loaded(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		final public boolean getSuccess()
		{
			return params.getBool("success");
		}
		
		final public String getName()
		{
			return params.getString("name");
		}
		
		final public EnhJSO getSettings()
		{
			return params.getJSO("settings");
		}

		final public JsArray<JavaScriptObject> getSystems()
		{
			return params.getJSAJSO("systems");
		}
	}

	
	public static class Scanned extends A_
	{
		private EnhJSO params;
		
		public Scanned(final A_ a)
		{
			this(a.response);
		}
		
		public Scanned(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		final public boolean getSuccess()
		{
			return params.getBool("success");
		}
		
		final public Integer getActions()
		{
			return params.getInteger("actions");
		}
	}

	
}
