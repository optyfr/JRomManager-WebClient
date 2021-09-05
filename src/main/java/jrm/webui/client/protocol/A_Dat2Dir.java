package jrm.webui.client.protocol;

import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

public class A_Dat2Dir extends A_	//NOSONAR
{
	public A_Dat2Dir(final A_ a)
	{
		this(a.response);
	}
	
	public A_Dat2Dir(final EnhJSO response)
	{
		super(response);
	}
	
	public static class ClearResults extends A_
	{
		public ClearResults(final A_ a)
		{
			this(a.response);
		}
		
		public ClearResults(final EnhJSO response)
		{
			super(response);
		}
	}

	public static class End extends A_
	{
		public End(final A_ a)
		{
			this(a.response);
		}
		
		public End(final EnhJSO response)
		{
			super(response);
		}
	}

	public static class UpdateResult extends A_
	{
		private EnhJSO params;
		
		public UpdateResult(final A_ a)
		{
			this(a.response);
		}
		
		public UpdateResult(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		public int getRow()
		{
			return params.getInt("row");
		}
		
		public String getResult()
		{
			return params.get("result");
		}
	}

	public static class ShowSettings extends A_
	{
		private EnhJSO params;
		
		public ShowSettings(final A_ a)
		{
			this(a.response);
		}

		public ShowSettings(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		public EnhJSO getSettings()
		{
			return params.getJSO("settings");
		}
		
		public JsArrayString getSrcs()
		{
			return params.getJSO("srcs").cast();
		}
	}
}
