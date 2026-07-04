package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

/**
 * Base class for all server-to-client action response objects.
 * <p>
 * Wraps the JSON response from the server and provides access to the command
 * identifier that determines how the action should be processed.
 *
 * @since 2.5
 */
public class A_ // NOSONAR
{
    /** The enhanced JavaScript object containing the server response data. */
    protected EnhJSO response;

    /**
     * Constructs a new action response wrapper.
     *
     * @param response the enhanced JavaScript object containing response data
     */
    public A_(final EnhJSO response) {
        this.response = response;
    }

    /**
     * Returns the command identifier from the server response.
     * <p>
     * The command determines which handler should process this action
     * (e.g., "Progress", "Profile.loaded", "Global.setMemory").
     *
     * @return the command identifier string
     */
    public String getCmd() {
        return response.getString("cmd");
    }
}
