package jrm.webui.client.protocol;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class Q_Dat2Dir extends Q_	//NOSONAR
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
		
		public static final Start instantiate()
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
		
		public static final Settings instantiate()
		{
			return Q_.instantiateCmd("Dat2Dir.settings").cast();
		}
		
		public final Settings setSrcs(List<String> srcs)
		{
			JsArrayString jsarrstr = JavaScriptObject.createArray().cast();
			srcs.forEach(jsarrstr::push);
			getParams().set("srcs", jsarrstr);
			return this;
		}
	}
}
