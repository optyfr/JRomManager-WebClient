package jrm.webui.client.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

public class A_Session extends EnhJSO
{
	private static Map<String,String> msgs_cache = new HashMap<>();
	
	protected A_Session()
	{
		super();
	}
	
	public final String getSession()
	{
		return get("session");
	}
	
	public final boolean isAuthenticated()
	{
		return Optional.ofNullable(getBoolean("authenticated")).orElse(false);
	}
	
	public final boolean isAdmin()
	{
		return Optional.ofNullable(getBoolean("admin")).orElse(false);
	}
	
	public final Map<String,String> getMsgs()
	{
		if (msgs_cache.size()==0)
		{
			EnhJSO prop_jso = getJSO("msgs");
			JsArrayString props = getProperties(prop_jso);
			for (int i = 0; i < props.length(); i++)
				msgs_cache.put(props.get(i), prop_jso.get(props.get(i)));
		}
		return msgs_cache;
	}
	
	public final String getSetting(String key, String def)
	{
		EnhJSO prop_jso = getJSO("settings");
		return prop_jso.exists(key)?prop_jso.get(key):def;
	}
	
	public final Boolean getSettingAsBoolean(String key, boolean def)
	{
		EnhJSO prop_jso = getJSO("settings");
		return prop_jso.exists(key)?prop_jso.getBoolean(key):def;
	}
	
	public final Integer getSettingAsInteger(String key, int def)
	{
		EnhJSO prop_jso = getJSO("settings");
		return prop_jso.exists(key)?prop_jso.getInteger(key):def;
	}
	
	public final void setSetting(String key, EnhJSO val)
	{
		EnhJSO prop_jso = getJSO("settings");
		prop_jso.set(key, val);
	}
	
	public final void setSetting(String key, String val)
	{
		EnhJSO prop_jso = getJSO("settings");
		prop_jso.set(key, val);
	}
	
	public final void setSetting(String key, boolean val)
	{
		EnhJSO prop_jso = getJSO("settings");
		prop_jso.set(key, val);
	}
	
	public final void setSetting(String key, int val)
	{
		EnhJSO prop_jso = getJSO("settings");
		prop_jso.set(key, val);
	}
	
	public final String getMsg(String code)
	{
		return getMsgs().get(code);
	}

}
