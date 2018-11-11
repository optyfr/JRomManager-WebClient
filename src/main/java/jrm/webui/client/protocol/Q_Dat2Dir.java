package jrm.webui.client.protocol;

import java.util.List;

import com.google.gwt.core.client.JsArrayString;

public class Q_Dat2Dir extends Q_
{
	protected Q_Dat2Dir()
	{
		super();
	}
	
	public static class Start extends Q_
	{
		protected Start()
		{
			super();
		}
		
		final public static Start instantiate()
		{
			return Q_.instantiateCmd("Dat2Dir.start").cast();
		}
	}
	
	public static class Settings extends Q_
	{
		protected Settings()
		{
			super();
		}
		
		final public static Settings instantiate()
		{
			return Q_.instantiateCmd("Dat2Dir.settings").cast();
		}
		
		final public Settings setSrcs(List<String> srcs)
		{
			JsArrayString jsarrstr = JsArrayString.createArray().cast();
			srcs.forEach(s->jsarrstr.push(s));
			getParams().set("srcs", jsarrstr);
			return this;
		}
	}
}
