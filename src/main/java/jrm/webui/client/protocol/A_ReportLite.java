package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;
import jrm.webui.client.utils.EnhJSO.ForEachConsumer;

public class A_ReportLite extends A_
{
	public A_ReportLite(final A_ a)
	{
		this(a.response);
	}
	
	public A_ReportLite(final EnhJSO response)
	{
		super(response);
	}
	
	public static class ApplyFilter extends A_
	{
//		private EnhJSO params;
		
		public ApplyFilter(final A_ a)
		{
			this(a.response);
		}
		
		public ApplyFilter(final EnhJSO response)
		{
			super(response);
//			params = response.getJSO("params");
		}
		
		public final void forEachParams(ForEachConsumer<String, Boolean> bc)
		{
			response.forEachBoolean("params", bc);
		}
	}

	
}
