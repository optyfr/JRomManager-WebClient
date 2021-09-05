package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;
import jrm.webui.client.utils.EnhJSO.ForEachConsumer;

public class A_Report extends A_	//NOSONAR
{
	public A_Report(final A_ a)
	{
		this(a.response);
	}
	
	public A_Report(final EnhJSO response)
	{
		super(response);
	}
	
	public static class ApplyFilter extends A_
	{
		public ApplyFilter(final A_ a)
		{
			this(a.response);
		}
		
		public ApplyFilter(final EnhJSO response)
		{
			super(response);
		}
		
		public final void forEachParams(ForEachConsumer<String, Boolean> bc)
		{
			response.forEachBoolean("params", bc);
		}
	}

	
}
