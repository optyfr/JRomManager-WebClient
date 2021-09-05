package jrm.webui.client.protocol;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

public class A_Progress extends A_	//NOSONAR
{
	private static final String PARAMS_STR = "params";

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
		private EnhJSO params;

		public Close(final A_ a)
		{
			this(a.response);
			params = response.getJSO(PARAMS_STR);
		}
		
		public Close(final EnhJSO response)
		{
			super(response);
		}
		
		public boolean hasErrors()
		{
			return ((JsArrayString) params.getJSO("errors")).length() > 0;
		}
		
		public List<String> getErrors()
		{
			final JsArrayString errors = params.getJSO("errors");
			final List<String> result = new ArrayList<>();
			for (int i = 0; i < errors.length(); i++)
				result.add(errors.get(i));
			return result;
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
			params = response.getJSO(PARAMS_STR);
		}
		
		public final int getThreadCnt()
		{
			return params.getInt("threadCnt");
		}
		
		public final Boolean getMultipleSubInfos()
		{
			return params.getBoolean("multipleSubInfos");
		}
	}

	public static class CanCancel extends A_
	{
		private EnhJSO params;
		
		public CanCancel(final A_ a)
		{
			this(a.response);
		}
		
		public CanCancel(final EnhJSO response)
		{
			super(response);
			params = response.getJSO(PARAMS_STR);
		}
		
		public final boolean canCancel()
		{
			return params.getBool("canCancel");
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


	public static class SetFullProgress extends A_
	{
		private ProgressData params;
		
		public SetFullProgress(final A_ a)
		{
			this(a.response);
		}
		
		public SetFullProgress(final EnhJSO response)
		{
			super(response);
			params = response.getJSO(PARAMS_STR);
		}
		
		public final ProgressData getParams()
		{
			return params;
		}

		public static class ProgressData extends EnhJSO
		{
			protected ProgressData()
			{
			}
			
			public final JsArrayString getInfos()
			{
				return getJSAStrJSO("infos");
			}
			
			public final JsArrayString getSubInfos()
			{
				return getJSAStrJSO("subinfos");
			}
			
			public final Boolean isMultipleSubInfos()
			{
				return getBoolean("multipleSubInfos");
			}
			
			public final int getThreadCnt()
			{
				return getInt("threadCnt");
			}
			
			public final Progress getPB1()
			{
				return getJSO("pb1");
			}
			
			public final Progress getPB2()
			{
				return getJSO("pb2");
			}
			
			public final Progress getPB3()
			{
				return getJSO("pb3");
			}
			
			public static class Progress extends EnhJSO
			{
				protected Progress()
				{
					
				}
				
				public final boolean isVisible()
				{
					return getBool("visibility");
				}
				
				public final boolean hasStringPainted()
				{
					return getBool("stringPainted");
				}

				public final boolean isIndeterminate()
				{
					return getBool("indeterminate");
				}
				
				public final int getVal()
				{
					return getInt("val");
				}
				
				public final int getMax()
				{
					return getInt("max");
				}
				
				public final float getPerc()
				{
					return getInt("perc");
				}
				
				public final String getMsg()
				{
					return get("msg");
				}
				
				public final String getTimeleft()
				{
					return get("timeleft");
				}
			}
		}
	}
	


}
