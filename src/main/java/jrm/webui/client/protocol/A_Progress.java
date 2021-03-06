package jrm.webui.client.protocol;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArrayString;

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
		private EnhJSO params;

		public Close(final A_ a)
		{
			this(a.response);
			params = response.getJSO("params");
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
			params = response.getJSO("params");
		}
		
		final public int getThreadCnt()
		{
			return params.getInt("threadCnt");
		}
		
		final public Boolean getMultipleSubInfos()
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
			params = response.getJSO("params");
		}
		
		final public boolean canCancel()
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
			params = response.getJSO("params");
		}
		
		final public ProgressData getParams()
		{
			return params;
		}

		public static class ProgressData extends EnhJSO
		{
			protected ProgressData()
			{
			}
			
			final public JsArrayString getInfos()
			{
				return getJSAStrJSO("infos");
			}
			
			final public JsArrayString getSubInfos()
			{
				return getJSAStrJSO("subinfos");
			}
			
			final public Boolean isMultipleSubInfos()
			{
				return getBoolean("multipleSubInfos");
			}
			
			final public int getThreadCnt()
			{
				return getInt("threadCnt");
			}
			
			final public Progress getPB1()
			{
				return getJSO("pb1");
			}
			
			final public Progress getPB2()
			{
				return getJSO("pb2");
			}
			
			final public Progress getPB3()
			{
				return getJSO("pb3");
			}
			
			public static class Progress extends EnhJSO
			{
				protected Progress()
				{
					
				}
				
				final public boolean isVisible()
				{
					return getBool("visibility");
				}
				
				final public boolean hasStringPainted()
				{
					return getBool("stringPainted");
				}

				final public boolean isIndeterminate()
				{
					return getBool("indeterminate");
				}
				
				final public int getVal()
				{
					return getInt("val");
				}
				
				final public int getMax()
				{
					return getInt("max");
				}
				
				final public float getPerc()
				{
					return getInt("perc");
				}
				
				final public String getMsg()
				{
					return get("msg");
				}
				
				final public String getTimeleft()
				{
					return get("timeleft");
				}
			}
		}
	}
	


}
