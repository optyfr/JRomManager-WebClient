package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

public class A_Global extends A_
{
	public A_Global(final A_ a)
	{
		this(a.response);
	}
	
	public A_Global(final EnhJSO response)
	{
		super(response);
	}
	
	public static class SetMemory extends A_
	{
		private EnhJSO params;
		
		public SetMemory(final A_ a)
		{
			this(a.response);
		}
		
		public SetMemory(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		final public String getMsg()
		{
			return params.getString("msg");
		}
	}

	public static class Warn extends A_
	{
		private EnhJSO params;
		
		public Warn(final A_ a)
		{
			this(a.response);
		}
		
		public Warn(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		final public String getMsg()
		{
			return params.getString("msg");
		}
	}

	
}
