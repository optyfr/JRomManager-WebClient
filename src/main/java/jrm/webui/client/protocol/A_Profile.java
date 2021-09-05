package jrm.webui.client.protocol;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

public class A_Profile extends A_	//NOSONAR
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
		private static final String SUCCESS_STR = "success";
		private static final String PARAMS_STR = "params";
		private EnhJSO params;
		
		public Loaded(final A_ a)
		{
			this(a.response);
		}
		
		public Loaded(final EnhJSO response)
		{
			super(response);
			params = response.getJSO(PARAMS_STR);
		}
		
		public final boolean getSuccess()
		{
			return params.getBool(SUCCESS_STR);
		}
		
		public final String getName()
		{
			return params.getString("name");
		}
		
		public final EnhJSO getSettings()
		{
			return params.getJSO("settings");
		}
		
		public final JsArrayString getYears()
		{
			return params.getJSO("years");
		}

		public final JsArray<JavaScriptObject> getSystems()
		{
			return params.getJSAJSO("systems");
		}
	}

	
	public static class Scanned extends A_
	{
		private static final String SUCCESS_STR = "success";
		private static final String PARAMS_STR = "params";
		private EnhJSO params;
		
		public Scanned(final A_ a)
		{
			this(a.response);
		}
		
		public Scanned(final EnhJSO response)
		{
			super(response);
			params = response.getJSO(PARAMS_STR);
		}
		
		public final boolean getSuccess()
		{
			return params.getBool(SUCCESS_STR);
		}
		
		public final Integer getActions()
		{
			return params.getInteger("actions");
		}
		public final boolean hasReport()
		{
			return params.getBool("report");
		}
	}

	public static class Fixed extends A_
	{
		private static final String SUCCESS_STR = "success";
		private static final String PARAMS_STR = "params";
		private EnhJSO params;
		
		public Fixed(final A_ a)
		{
			this(a.response);
		}
		
		public Fixed(final EnhJSO response)
		{
			super(response);
			params = response.getJSO(PARAMS_STR);
		}
		
		public final boolean getSuccess()
		{
			return params.getBool(SUCCESS_STR);
		}
		
		public final Integer getActions()
		{
			return params.getInteger("actions");
		}
	}

	public static class Imported extends A_
	{
		private static final String PARAMS_STR = "params";
		private EnhJSO params;
		
		public Imported(final A_ a)
		{
			this(a.response);
		}
		
		public Imported(final EnhJSO response)
		{
			super(response);
			params = response.getJSO(PARAMS_STR);
		}
		
		public final String getPath()
		{
			return params.getString("path");
		}
		
		public final String getParent()
		{
			return params.getString("parent");
		}
		
		public final String getName()
		{
			return params.getString("name");
		}
	}

	
}
