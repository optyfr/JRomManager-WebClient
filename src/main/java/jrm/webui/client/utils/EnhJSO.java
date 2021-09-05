package jrm.webui.client.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class EnhJSO extends JavaScriptObject
{
	protected EnhJSO()
	{
		super();
	}

	public interface ForEachConsumer<T,U>
	{
		public void accept(T key, U value);
	}

	protected static native void delete(JavaScriptObject jso, String name) /*-{
        delete jso[name];
	}-*/;

	protected static native boolean exists(JavaScriptObject jso, String name) /*-{
        return jso[name] !== undefined;
	}-*/;

	protected static native boolean getBool(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

	protected static native int getInt(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

	protected static native double getDouble(JavaScriptObject jso, String name) /*-{
	    return jso[name];
	}-*/;

	protected static native JsArray<JavaScriptObject> getJSAJSO(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

	protected static native JsArrayString getJSAStrJSO(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

	protected static native <T extends JavaScriptObject> T getJSO(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

	public static native JsArrayString getProperties(JavaScriptObject jso) /*-{
		return Object.keys(jso);
	}-*/;

	protected static native String getString(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

	protected static native boolean isArray(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.Array(jso[name]);
	}-*/;

	protected static native boolean isObject(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.Object(jso[name]);
	}-*/;

	protected static native boolean isEmptyObject(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.emptyObject(jso[name]);
	}-*/;

	protected static native boolean isVoid(JavaScriptObject jso, String name) /*-{
		if(typeof (jso[name]) == 'undefined')
			return true;
		else if(jso[name] == null)
			return true;
		else if($wnd.isc.isA.Array(jso[name]))
			return jso[name].length == 0;
		else if($wnd.isc.isA.String(jso[name]))
			return $wnd.isc.isA.emptyString(jso[name]);
		else if($wnd.isc.isA.Object(jso[name]))
			return $wnd.isc.isA.emptyObject(jso[name]);
		else if($wnd.isc.isA.Number(jso[name]))
			return jso[name] == 0;
		else if($wnd.isc.isA.Boolean(jso[name]))
			return !jso[name];
		return false;
	}-*/;

	protected static native boolean isBoolean(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.Boolean(jso[name]);
	}-*/;

	protected static native boolean isString(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.String(jso[name]);
	}-*/;

	protected static native boolean isNull(JavaScriptObject jso, String name) /*-{
		if(typeof (jso[name]) == 'undefined')
			return true;
		else if(jso[name] == null)
			return true;
		return false;
	}-*/;

	protected static native boolean set(JavaScriptObject jso, String name, boolean value) /*-{
        return jso[name] = value;
	}-*/;

	protected static native int set(JavaScriptObject jso, String name, int value) /*-{
        return jso[name] = value;
	}-*/;

	protected static native JavaScriptObject set(JavaScriptObject jso, String name, JavaScriptObject value) /*-{
        return jso[name] = value;
	}-*/;
	
	protected static native String set(JavaScriptObject jso, String name, String value) /*-{
        return jso[name] = value;
	}-*/;
	
	protected static native JsArray<JavaScriptObject> toArray(JavaScriptObject jso, String name) /*-{
		if(jso[name]==null)
			return [];
		else if($wnd.isc.isA.Array(jso[name]))
			return jso[name];
		else if($wnd.isc.isA.emptyString(jso[name]))
			return [];
		else
			return [jso[name]];
	}-*/;

	public final void delete(final String name)
	{
		EnhJSO.delete(this, name);
		
	}

	public final boolean exists(final String name)
	{
		return EnhJSO.exists(this, name);
	}

	/**
	 * will return the value of a property as a nullable string
	 * @param name
	 * @return a string for value of property {@code name} or null in other cases
	 */
	public final String get(final String name)
	{
		return EnhJSO.getString(this, name);
	}

	public final boolean getBool(final String name)
	{
		return EnhJSO.getBool(this, name);
	}

	public final Boolean getBoolean(final String name)
	{
		if(EnhJSO.isNull(this, name))
			return null;	//NOSONAR
		return EnhJSO.getBool(this, name);
	}

	public final int getInt(final String name)
	{
		return EnhJSO.getInt(this, name);
	}

	public final Integer getInteger(final String name)
	{
		if(EnhJSO.isNull(this, name))
			return null;
		return EnhJSO.getInt(this, name);
	}

	public final double getDouble(final String name)
	{
		return EnhJSO.getDouble(this, name);
	}

	public final JsArray<JavaScriptObject> getJSAJSO(final String name)
	{
		return EnhJSO.getJSAJSO(this, name);
	}

	protected final JsArrayString getJSAStrJSO(final String name)
	{
		return EnhJSO.getJSAStrJSO(this, name);
	}

	public final <T extends JavaScriptObject> T getJSO(final String name)
	{
		return EnhJSO.getJSO(this, name);
	}

	/**
	 * will return the value of a property as a non null string
	 * @param name the name of the property to return
	 * @return the value of property {@code name} as a string otherwise an empty string
	 */
	public final String getString(final String name)
	{
		return getString(name, true);
	}

	/**
	 * will return the value of a property as a non null string
	 * @param name the name of the property to return
	 * @param nonull set it to true to return non null strings
	 * @return the value of property {@code name} as a string otherwise an empty string (if {@code nonull} is true) or a {@code null} value
	 */
	public final String getString(final String name, final boolean nonull)
	{
		return nonull&&EnhJSO.isVoid(this, name)?"":EnhJSO.getString(this, name);
	}

	public final boolean isArray(final String name)
	{
		return EnhJSO.isArray(this, name);
	}

	public final boolean isEmptyObject(final String name)
	{
		return EnhJSO.isEmptyObject(this, name);
	}

	public final boolean isObject(final String name)
	{
		return EnhJSO.isObject(this, name);
	}

	public final boolean isVoid(final String name)
	{
		return EnhJSO.isVoid(this, name);
	}

	public final boolean isBoolean(final String name)
	{
		return EnhJSO.isBoolean(this, name);
	}

	public final boolean isString(final String name)
	{
		return EnhJSO.isString(this, name);
	}

	public final boolean set(final String name, final boolean value)
	{
		return EnhJSO.set(this, name, value);
	}

	public final int set(final String name, final int value)
	{
		return EnhJSO.set(this, name, value);
	}

	public final JavaScriptObject set(final String name, final JavaScriptObject value)
	{
		return EnhJSO.set(this, name, value);
	}

	public final String set(final String name, final String value)
	{
		return EnhJSO.set(this, name, value);
	}

	public final Object set(final String name, final Object value)
	{
		if(value instanceof Boolean)
			return EnhJSO.set(this, name, (Boolean)value);
		if(value instanceof Integer)
			return EnhJSO.set(this, name, (Integer)value);
		return EnhJSO.set(this, name, (String)value);
	}

	public final JsArray<JavaScriptObject> toArray(final String name)
	{
		return EnhJSO.toArray(this, name);
	}
	
	public final void forEach(String prop, ForEachConsumer<String, EnhJSO> bc)
	{
		EnhJSO propJso = getJSO(prop);
		JsArrayString props = getProperties(propJso);
		for(int i = 0; i < props.length(); i++)
		{
			prop = props.get(i);
			bc.accept(prop, propJso.getJSO(prop));
		}
	}
	
	public final void forEachBoolean(String prop, ForEachConsumer<String, Boolean> bc)
	{
		EnhJSO propJso = getJSO(prop);
		JsArrayString props = getProperties(propJso);
		for(int i = 0; i < props.length(); i++)
		{
			prop = props.get(i);
			bc.accept(prop, propJso.getBoolean(prop));
		}
	}
}
