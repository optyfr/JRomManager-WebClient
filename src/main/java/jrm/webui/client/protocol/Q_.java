package jrm.webui.client.protocol;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;

import jrm.webui.client.Client;
import jrm.webui.client.utils.EnhJSO;

/**
 * Base class for all client-to-server query/command request objects.
 * <p>
 * Provides the foundation for building JSON command messages to send to the server.
 * Each command has a command identifier and an optional parameters object.
 *
 * @since 2.5
 */
public class Q_ extends EnhJSO // NOSONAR
{
    /** JSON property key for the {@code params} object. */
    private static final String PARAMS_STR = "params";

    /** Protected constructor for JavaScript object overlay types. */
    protected Q_() {
        super();
    }

    /**
     * Creates a new empty query object.
     *
     * @return a new query instance
     */
    static final private Q_ _instantiate() // NOSONAR
    {
        return JavaScriptObject.createObject().cast();
    }

    /**
     * Creates a new query object with the specified command identifier.
     *
     * @param cmd the command identifier
     * @return a new query instance with the command set
     */
    protected static final Q_ instantiateCmd(String cmd) {
        Q_ q = _instantiate();
        q.setCmd(cmd);
        return q;
    }

    /**
     * Sets the command identifier for this query.
     *
     * @param cmd the command identifier
     */
    protected final void setCmd(String cmd) {
        set("cmd", cmd);
    }

    /**
     * Returns the parameters object for this query, creating it if it doesn't exist.
     *
     * @return the parameters object
     */
    protected final EnhJSO getParams() {
        if (!exists(PARAMS_STR))
            set(PARAMS_STR, JavaScriptObject.createObject());
        return getJSO(PARAMS_STR);
    }

    /**
     * Serializes this query to JSON and sends it to the server.
     */
    public final void send() {
        Client.sendMsg(JsonUtils.stringify(this));
    }

}
