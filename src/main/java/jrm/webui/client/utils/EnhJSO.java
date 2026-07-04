package jrm.webui.client.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

/**
 * Enhanced {@link JavaScriptObject} that provides typed getter, setter, type-checking,
 * and iteration methods for working with JavaScript objects via JSNI.
 *
 * <p>This class serves as the base type for all GWT overlay types in the protocol
 * package. It delegates to static native methods that operate on raw
 * {@link JavaScriptObject} references and uses SmartGWT's {@code isc.isA}
 * utilities for type detection.</p>
 *
 * @since 2.5
 */
public class EnhJSO extends JavaScriptObject {
    /** Protected constructor for JavaScript object overlay types. */
    protected EnhJSO() {
        super();
    }

    /**
     * Functional interface for iterating over key-value pairs of a nested
     * JavaScript object property.
     *
     * @param <T> the key type
     * @param <U> the value type
     */
    public interface ForEachConsumer<T, U> {
        /**
         * Accepts a key-value pair.
         *
         * @param key   the property name
         * @param value the property value
         */
        public void accept(T key, U value);
    }

    /**
     * Deletes a property from a JavaScript object.
     *
     * @param jso  the JavaScript object
     * @param name the property name to delete
     */
    protected static native void delete(JavaScriptObject jso, String name) /*-{
        delete jso[name];
	}-*/;

    /**
     * Checks whether a property exists on a JavaScript object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return {@code true} if the property is defined
     */
    protected static native boolean exists(JavaScriptObject jso, String name) /*-{
        return jso[name] !== undefined;
	}-*/;

    /**
     * Gets a boolean property value from a JavaScript object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return the boolean value
     */
    protected static native boolean getBool(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

    /**
     * Gets an integer property value from a JavaScript object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return the integer value
     */
    protected static native int getInt(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

    /**
     * Gets a double property value from a JavaScript object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return the double value
     */
    protected static native double getDouble(JavaScriptObject jso, String name) /*-{
	    return jso[name];
	}-*/;

    /**
     * Gets a JavaScript object array property from a JavaScript object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return the array of JavaScript objects
     */
    protected static native JsArray<JavaScriptObject> getJSAJSO(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

    /**
     * Gets a string array property from a JavaScript object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return the string array
     */
    protected static native JsArrayString getJSAStrJSO(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

    /**
     * Gets a typed JavaScript object property.
     *
     * @param <T>  the overlay type
     * @param jso  the JavaScript object
     * @param name the property name
     * @return the typed JavaScript object
     */
    protected static native <T extends JavaScriptObject> T getJSO(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

    /**
     * Returns the property names of a JavaScript object.
     *
     * @param jso the JavaScript object
     * @return a string array of property names
     */
    public static native JsArrayString getProperties(JavaScriptObject jso) /*-{
		return Object.keys(jso);
	}-*/;

    /**
     * Gets a string property value from a JavaScript object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return the string value
     */
    protected static native String getString(JavaScriptObject jso, String name) /*-{
        return jso[name];
	}-*/;

    /**
     * Checks whether a property is a SmartGWT array.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return {@code true} if the property is an array
     */
    protected static native boolean isArray(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.Array(jso[name]);
	}-*/;

    /**
     * Checks whether a property is a SmartGWT object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return {@code true} if the property is an object
     */
    protected static native boolean isObject(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.Object(jso[name]);
	}-*/;

    /**
     * Checks whether a property is an empty SmartGWT object.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return {@code true} if the property is an empty object
     */
    protected static native boolean isEmptyObject(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.emptyObject(jso[name]);
	}-*/;

    /**
     * Checks whether a property is void (undefined, null, or empty).
     *
     * <p>A property is considered void if it is undefined, null, an empty array,
     * an empty string, an empty object, zero, or {@code false}.</p>
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return {@code true} if the property is void
     */
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

    /**
     * Checks whether a property is a SmartGWT boolean.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return {@code true} if the property is a boolean
     */
    protected static native boolean isBoolean(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.Boolean(jso[name]);
	}-*/;

    /**
     * Checks whether a property is a SmartGWT string.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return {@code true} if the property is a string
     */
    protected static native boolean isString(JavaScriptObject jso, String name) /*-{
		return $wnd.isc.isA.String(jso[name]);
	}-*/;

    /**
     * Checks whether a property is null or undefined.
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return {@code true} if the property is null or undefined
     */
    protected static native boolean isNull(JavaScriptObject jso, String name) /*-{
		if(typeof (jso[name]) == 'undefined')
			return true;
		else if(jso[name] == null)
			return true;
		return false;
	}-*/;

    /**
     * Sets a boolean property on a JavaScript object.
     *
     * @param jso   the JavaScript object
     * @param name  the property name
     * @param value the boolean value
     * @return the value that was set
     */
    protected static native boolean set(JavaScriptObject jso, String name, boolean value) /*-{
        return jso[name] = value;
	}-*/;

    /**
     * Sets an integer property on a JavaScript object.
     *
     * @param jso   the JavaScript object
     * @param name  the property name
     * @param value the integer value
     * @return the value that was set
     */
    protected static native int set(JavaScriptObject jso, String name, int value) /*-{
        return jso[name] = value;
	}-*/;

    /**
     * Sets a JavaScript object property on a JavaScript object.
     *
     * @param jso   the JavaScript object
     * @param name  the property name
     * @param value the JavaScript object value
     * @return the value that was set
     */
    protected static native JavaScriptObject set(JavaScriptObject jso, String name, JavaScriptObject value) /*-{
        return jso[name] = value;
	}-*/;

    /**
     * Sets a string property on a JavaScript object.
     *
     * @param jso   the JavaScript object
     * @param name  the property name
     * @param value the string value
     * @return the value that was set
     */
    protected static native String set(JavaScriptObject jso, String name, String value) /*-{
        return jso[name] = value;
	}-*/;

    /**
     * Converts a property to a JavaScript object array.
     *
     * <p>If the property is already an array, it is returned directly.
     * If it is an empty string, an empty array is returned.
     * Otherwise, the single value is wrapped in an array.</p>
     *
     * @param jso  the JavaScript object
     * @param name the property name
     * @return the property value as an array
     */
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

    /**
     * Deletes a property from this JavaScript object.
     *
     * @param name the property name to delete
     */
    public final void delete(final String name) {
        EnhJSO.delete(this, name);

    }

    /**
     * Checks whether a property exists on this JavaScript object.
     *
     * @param name the property name
     * @return {@code true} if the property is defined
     */
    public final boolean exists(final String name) {
        return EnhJSO.exists(this, name);
    }

    /**
     * Gets a nullable string property value.
     *
     * @param name the property name
     * @return the string value, or {@code null} if the property is null or undefined
     */
    public final String get(final String name) {
        return EnhJSO.getString(this, name);
    }

    /**
     * Gets a boolean property value.
     *
     * @param name the property name
     * @return the boolean value
     */
    public final boolean getBool(final String name) {
        return EnhJSO.getBool(this, name);
    }

    /**
     * Gets a nullable Boolean property value.
     *
     * @param name the property name
     * @return the Boolean value, or {@code null} if the property is null or undefined
     */
    public final Boolean getBoolean(final String name) {
        if (EnhJSO.isNull(this, name))
            return null; // NOSONAR
        return EnhJSO.getBool(this, name);
    }

    /**
     * Gets an integer property value.
     *
     * @param name the property name
     * @return the integer value
     */
    public final int getInt(final String name) {
        return EnhJSO.getInt(this, name);
    }

    /**
     * Gets a nullable Integer property value.
     *
     * @param name the property name
     * @return the Integer value, or {@code null} if the property is null or undefined
     */
    public final Integer getInteger(final String name) {
        if (EnhJSO.isNull(this, name))
            return null;
        return EnhJSO.getInt(this, name);
    }

    /**
     * Gets a double property value.
     *
     * @param name the property name
     * @return the double value
     */
    public final double getDouble(final String name) {
        return EnhJSO.getDouble(this, name);
    }

    /**
     * Gets a JavaScript object array property.
     *
     * @param name the property name
     * @return the array of JavaScript objects
     */
    public final JsArray<JavaScriptObject> getJSAJSO(final String name) {
        return EnhJSO.getJSAJSO(this, name);
    }

    /**
     * Gets a string array property.
     *
     * @param name the property name
     * @return the string array
     */
    protected final JsArrayString getJSAStrJSO(final String name) {
        return EnhJSO.getJSAStrJSO(this, name);
    }

    /**
     * Gets a typed JavaScript object property.
     *
     * @param <T>  the overlay type
     * @param name the property name
     * @return the typed JavaScript object
     */
    public final <T extends JavaScriptObject> T getJSO(final String name) {
        return EnhJSO.getJSO(this, name);
    }

    /**
     * Gets a string property value, optionally returning an empty string for null/undefined.
     *
     * @param name   the property name
     * @param nonull if {@code true}, return empty string instead of null
     * @return the string value
     */
    public final String getString(final String name, final boolean nonull) {
        return nonull && EnhJSO.isVoid(this, name) ? "" : EnhJSO.getString(this, name);
    }

    /**
     * Gets a string property value, returning an empty string for null/undefined.
     *
     * @param name the property name
     * @return the string value, or empty string if null/undefined
     */
    public final String getString(final String name) {
        return getString(name, true);
    }

    /**
     * Checks whether a property is a SmartGWT array.
     *
     * @param name the property name
     * @return {@code true} if the property is an array
     */
    public final boolean isArray(final String name) {
        return EnhJSO.isArray(this, name);
    }

    /**
     * Checks whether a property is an empty SmartGWT object.
     *
     * @param name the property name
     * @return {@code true} if the property is an empty object
     */
    public final boolean isEmptyObject(final String name) {
        return EnhJSO.isEmptyObject(this, name);
    }

    /**
     * Checks whether a property is a SmartGWT object.
     *
     * @param name the property name
     * @return {@code true} if the property is an object
     */
    public final boolean isObject(final String name) {
        return EnhJSO.isObject(this, name);
    }

    /**
     * Checks whether a property is void (undefined, null, or empty).
     *
     * @param name the property name
     * @return {@code true} if the property is void
     */
    public final boolean isVoid(final String name) {
        return EnhJSO.isVoid(this, name);
    }

    /**
     * Checks whether a property is a SmartGWT boolean.
     *
     * @param name the property name
     * @return {@code true} if the property is a boolean
     */
    public final boolean isBoolean(final String name) {
        return EnhJSO.isBoolean(this, name);
    }

    /**
     * Checks whether a property is a SmartGWT string.
     *
     * @param name the property name
     * @return {@code true} if the property is a string
     */
    public final boolean isString(final String name) {
        return EnhJSO.isString(this, name);
    }

    /**
     * Sets a boolean property on this JavaScript object.
     *
     * @param name  the property name
     * @param value the boolean value
     * @return the value that was set
     */
    public final boolean set(final String name, final boolean value) {
        return EnhJSO.set(this, name, value);
    }

    /**
     * Sets an integer property on this JavaScript object.
     *
     * @param name  the property name
     * @param value the integer value
     * @return the value that was set
     */
    public final int set(final String name, final int value) {
        return EnhJSO.set(this, name, value);
    }

    /**
     * Sets a JavaScript object property on this JavaScript object.
     *
     * @param name  the property name
     * @param value the JavaScript object value
     * @return the value that was set
     */
    public final JavaScriptObject set(final String name, final JavaScriptObject value) {
        return EnhJSO.set(this, name, value);
    }

    /**
     * Sets a string property on this JavaScript object.
     *
     * @param name  the property name
     * @param value the string value
     * @return the value that was set
     */
    public final String set(final String name, final String value) {
        return EnhJSO.set(this, name, value);
    }

    /**
     * Sets a property on this JavaScript object, auto-detecting the type.
     *
     * <p>Supports {@link Boolean}, {@link Integer}, and {@link String} values.
     * Other types are converted to string via {@code toString()}.</p>
     *
     * @param name  the property name
     * @param value the property value
     * @return the value that was set
     */
    public final Object set(final String name, final Object value) {
        if (value instanceof Boolean booleanVal)
            return EnhJSO.set(this, name, booleanVal);
        if (value instanceof Integer integerVal)
            return EnhJSO.set(this, name, integerVal);
        return EnhJSO.set(this, name, (String) value);
    }

    /**
     * Converts a property to a JavaScript object array.
     *
     * @param name the property name
     * @return the property value as an array
     */
    public final JsArray<JavaScriptObject> toArray(final String name) {
        return EnhJSO.toArray(this, name);
    }

    /**
     * Iterates over the key-value pairs of a nested JavaScript object property,
     * invoking the consumer for each pair.
     *
     * @param prop the name of the nested object property to iterate
     * @param bc   the consumer to invoke for each key-value pair
     */
    public final void forEach(String prop, ForEachConsumer<String, EnhJSO> bc) {
        EnhJSO propJso = getJSO(prop);
        JsArrayString props = getProperties(propJso);
        for (int i = 0; i < props.length(); i++) {
            prop = props.get(i);
            bc.accept(prop, propJso.getJSO(prop));
        }
    }

    /**
     * Iterates over the key-value pairs of a nested JavaScript object property,
     * invoking the consumer for each pair where the value is a boolean.
     *
     * @param prop the name of the nested object property to iterate
     * @param bc   the consumer to invoke for each key-value pair
     */
    public final void forEachBoolean(String prop, ForEachConsumer<String, Boolean> bc) {
        EnhJSO propJso = getJSO(prop);
        JsArrayString props = getProperties(propJso);
        for (int i = 0; i < props.length(); i++) {
            prop = props.get(i);
            bc.accept(prop, propJso.getBoolean(prop));
        }
    }
}
