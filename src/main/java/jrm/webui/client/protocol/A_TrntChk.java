package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

public class A_TrntChk extends A_	//NOSONAR
{
	public A_TrntChk(final A_ a)
	{
		this(a.response);
	}
	
	public A_TrntChk(final EnhJSO response)
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
}
