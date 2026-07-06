package jrm.webui.client.protocol;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

/**
 * Action response for global operations.
 * <p>
 * Contains nested action types for global server notifications including
 * memory updates, property changes, warnings, and multi-command execution.
 *
 * @since 2.5
 */
public class A_Global extends A_ // NOSONAR
{
    /** JSON property key for the {@code params} object. */
    private static final String PARAMS_STR = "params";

    /**
     * Constructs a Global action from a base action.
     *
     * @param a the base action containing the response data
     */
    public A_Global(final A_ a) {
        this(a.response);
    }

    /**
     * Constructs a Global action from an enhanced JavaScript response.
     *
     * @param response the enhanced JavaScript response object
     */
    public A_Global(final EnhJSO response) {
        super(response);
    }

    /**
     * Action to set or display memory information.
     */
    public static class SetMemory extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a SetMemory action from a base action.
         *
         * @param a the base action
         */
        public SetMemory(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a SetMemory action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public SetMemory(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns the memory message.
         *
         * @return the memory message string
         */
        public final String getMsg() {
            return params.getString("msg");
        }
    }

    /**
     * Action to update one or more properties on the client.
     */
    public static class UpdateProperty extends A_ {
        /** The map of updated property names to their values. */
        private Map<String, Object> params = new HashMap<>();

        /**
         * Constructs an UpdateProperty action from a base action.
         *
         * @param a the base action
         */
        public UpdateProperty(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs an UpdateProperty action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public UpdateProperty(final EnhJSO response) {
            super(response);
            EnhJSO p = response.getJSO(PARAMS_STR);
            JsArrayString keys = EnhJSO.getProperties(p);
            for (int i = 0; i < keys.length(); i++) {
                String name = keys.get(i);
                params.put(name, p.get(name));
            }
        }

        /**
         * Returns the map of property names to values.
         *
         * @return the properties map
         */
        public final Map<String, Object> getProperties() {
            return params;
        }
    }

    /**
     * Action to display a warning message to the user.
     */
    public static class Warn extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a Warn action from a base action.
         *
         * @param a the base action
         */
        public Warn(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a Warn action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public Warn(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns the warning message.
         *
         * @return the warning message string
         */
        public final String getMsg() {
            return params.getString("msg");
        }
    }

    /**
     * Action containing multiple sub-commands to execute sequentially.
     */
    public static class MultiCMD extends A_ {
        /**
         * Constructs a MultiCMD action from a base action.
         *
         * @param a the base action
         */
        public MultiCMD(final A_ a) {
            super(a.response);
        }

        /**
         * Returns the array of sub-commands to execute.
         *
         * @return the array of sub-command actions
         */
        public A_[] getSubCMDs() {
            final var jsa = response.getJSAJSO(PARAMS_STR);
            final A_[] subcmds = new A_[jsa.length()];
            for (int i = 0; i < jsa.length(); i++)
                subcmds[i] = new A_(jsa.get(i).cast());
            return subcmds;
        }
    }
}
