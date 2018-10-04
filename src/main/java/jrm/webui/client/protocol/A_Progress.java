package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

public class A_Progress extends A_
{
	public A_Progress(final A_ a)
	{
		this(a.response);
	}
	
	public A_Progress(final EnhJSO response)
	{
		super(response);
	}
	
	public static class Close extends A_
	{
		public Close(final A_ a)
		{
			this(a.response);
		}
		
		public Close(final EnhJSO response)
		{
			super(response);
		}
	}
	
	public static class SetInfos extends A_
	{
		private EnhJSO params;
		
		public SetInfos(final A_ a)
		{
			this(a.response);
		}
		
		public SetInfos(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		final public int getThreadCnt()
		{
			return params.getInt("threadCnt");
		}
		
		final public boolean getMultipleSubInfos()
		{
			return params.getBool("multipleSubInfos");
		}
	}

	public static class ClearInfos extends A_
	{
		public ClearInfos(final A_ a)
		{
			this(a.response);
		}
		
		public ClearInfos(final EnhJSO response)
		{
			super(response);
		}
	}

	public static class SetProgress extends A_
	{
		private EnhJSO params;
		
		public SetProgress(final A_ a)
		{
			this(a.response);
		}
		
		public SetProgress(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		final public int getOffset()
		{
			return params.getInt("offset");
		}
		
		final public String getMsg()
		{
			return params.getString("msg",false);
		}
		
		final public String getSubMsg()
		{
			return params.getString("submsg",false);
		}
		
		final public Integer getVal()
		{
			return params.getInteger("val");
		}
		
		final public Integer getMax()
		{
			return params.getInteger("max");
		}
	}
	

	public static class SetProgress2 extends A_
	{
		private EnhJSO params;
		
		public SetProgress2(final A_ a)
		{
			this(a.response);
		}
		
		public SetProgress2(final EnhJSO response)
		{
			super(response);
			params = response.getJSO("params");
		}
		
		final public String getMsg()
		{
			return params.getString("msg",false);
		}
		
		final public Integer getVal()
		{
			return params.getInteger("val");
		}
		
		final public Integer getMax()
		{
			return params.getInteger("max");
		}
	}
	
}
