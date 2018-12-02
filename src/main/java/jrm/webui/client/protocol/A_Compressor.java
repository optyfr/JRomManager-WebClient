package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

public class A_Compressor extends A_
{
	public A_Compressor(final A_ a)
	{
		this(a.response);
	}
	
	public A_Compressor(final EnhJSO response)
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

	public static class UpdateFile extends A_
	{
		private EnhJSO params;
		
		public UpdateFile(final A_ a)
		{
			this(a.response);
		}
		
		public UpdateFile(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		public int getRow()
		{
			return params.getInt("row");
		}
		
		public String getFile()
		{
			return params.get("file");
		}
	}
}
