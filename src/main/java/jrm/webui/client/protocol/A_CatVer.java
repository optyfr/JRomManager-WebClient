package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

public class A_CatVer extends A_
{
	public A_CatVer(final A_ a)
	{
		this(a.response);
	}
	
	public A_CatVer(final EnhJSO response)
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
		
		final public String getPath()
		{
			return params.getString("path");
		}
	}

	
}
