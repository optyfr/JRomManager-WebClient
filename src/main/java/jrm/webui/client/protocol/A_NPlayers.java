package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

public class A_NPlayers extends A_	//NOSONAR
{
	public A_NPlayers(final A_ a)
	{
		this(a.response);
	}
	
	public A_NPlayers(final EnhJSO response)
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
		
		public final String getPath()
		{
			return params.getString("path");
		}
	}

	
}
