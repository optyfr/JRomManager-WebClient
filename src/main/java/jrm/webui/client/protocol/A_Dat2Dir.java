package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

public class A_Dat2Dir extends A_
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
//		private EnhJSO params;
		
		public ClearResults(final A_ a)
		{
			this(a.response);
		}
		
		public ClearResults(final EnhJSO response)
		{
			super(response);
//			params = response.getJSO("params");
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

	
}
