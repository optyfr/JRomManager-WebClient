package jrm.webui.client.protocol;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

public class A_Session extends EnhJSO
{
	private static Map<String,String> msgs_cache = null;
	
	protected A_Session()
	{
		super();
	}
	
	public final String getSession()
	{
		return get("session");
	}
	

	public final Map<String,String> getMsgs()
	{
		if (msgs_cache == null)
		{
			msgs_cache = new HashMap<>();
			EnhJSO prop_jso = getJSO("msgs");
			JsArrayString props = getProperties(prop_jso);
			for (int i = 0; i < props.length(); i++)
				msgs_cache.put(props.get(i), prop_jso.get(props.get(i)));
		}
		return msgs_cache;
	}
	
	public final String getMsg(String code)
	{
		return getMsgs().get(code);
	}

}
