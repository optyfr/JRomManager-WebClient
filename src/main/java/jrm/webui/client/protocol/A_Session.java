package jrm.webui.client.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

public class A_Session extends EnhJSO	//NOSONAR
{
	private static final String SETTINGS_STR = "settings";
	private static Map<String,String> msgsCache = new HashMap<>();
	
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
		if (msgsCache.size()==0)
		{
			EnhJSO propJso = getJSO("msgs");
			JsArrayString props = getProperties(propJso);
			for (int i = 0; i < props.length(); i++)
				msgsCache.put(props.get(i), propJso.get(props.get(i)));
		}
		return msgsCache;
	}
	
	public final String getSetting(String key, String def)
	{
		EnhJSO propJso = getJSO(SETTINGS_STR);
		return propJso.exists(key)?propJso.get(key):def;
	}
	
	public final Boolean getSettingAsBoolean(String key, boolean def)
	{
		EnhJSO propJso = getJSO(SETTINGS_STR);
		return propJso.exists(key)?propJso.getBoolean(key):def;
	}
	
	public final Integer getSettingAsInteger(String key, int def)
	{
		EnhJSO propJso = getJSO(SETTINGS_STR);
		return propJso.exists(key)?propJso.getInteger(key):def;
	}
	
	public final void setSetting(String key, EnhJSO val)
	{
		EnhJSO propJso = getJSO(SETTINGS_STR);
		propJso.set(key, val);
	}
	
	public final void setSetting(String key, String val)
	{
		EnhJSO propJso = getJSO(SETTINGS_STR);
		propJso.set(key, val);
	}
	
	public final void setSetting(String key, boolean val)
	{
		EnhJSO propJso = getJSO(SETTINGS_STR);
		propJso.set(key, val);
	}
	
	public final void setSetting(String key, int val)
	{
		EnhJSO propJso = getJSO(SETTINGS_STR);
		propJso.set(key, val);
	}
	
	public final String getMsg(String code)
	{
		return getMsgs().get(code);
	}

}
