package jrm.webui.client.protocol;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;

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

	public static class UpdateProperty extends A_
	{
		private Map<String,String> params = new HashMap<>();
		
		public UpdateProperty(final A_ a)
		{
			this(a.response);
		}
		
		public UpdateProperty(final EnhJSO response)
		{
			super(response);
			EnhJSO p = response.getJSO("params");
			JsArrayString keys = EnhJSO.getProperties(p);
			for(int i = 0; i < keys.length(); i++)
			{
				String name = keys.get(i);
				params.put(name, p.get(name));
			}
		}
		
		final public Map<String,String> getProperties()
		{
			return params;
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

	public static class MultiCMD extends A_
	{
		public MultiCMD(final A_ a)
		{
			super(a.response);
		}
		
		public A_[] getSubCMDs()
		{
			final var jsa = response.getJSAJSO("params");
			final A_[] subcmds = new A_[jsa.length()];
			for(int i = 0; i < jsa.length(); i++)
				subcmds[i] =  new A_(jsa.get(i).cast());
			return subcmds;
		}
	}
}
