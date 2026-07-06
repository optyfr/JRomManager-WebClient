package jrm.webui.client.protocol;

import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;

import jrm.webui.client.utils.EnhJSO;

/**
 * Query commands for directory to DAT conversion operations.
 *
 * @since 2.5
 */
public class Q_Dir2Dat extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_Dir2Dat() {
        super();
    }

    /**
     * Query to start the directory to DAT conversion.
     */
    public static class Start extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Start() {
            super();
        }

        /**
         * Creates a new Dir2Dat start query.
         *
         * @return a new Start query instance
         */
        public static final Start instantiate() {
            return Q_.instantiateCmd("Dir2Dat.start").cast();
        }

        /**
         * Sets the conversion options.
         *
         * @param values the options map
         * @return this query for chaining
         */
        public final Start setOptions(Map<String, Object> values) {
            EnhJSO jso = JavaScriptObject.createObject().cast();
            values.forEach(jso::set);
            getParams().set("options", jso);
            return this;
        }

        /**
         * Sets the DAT header values.
         *
         * @param values the header map
         * @return this query for chaining
         */
        public final Start setHeaders(Map<String, Object> values) {
            EnhJSO jso = JavaScriptObject.createObject().cast();
            values.forEach(jso::set);
            getParams().set("headers", jso);
            return this;
        }

        /**
         * Sets the input/output configuration.
         *
         * @param values the I/O configuration map
         * @return this query for chaining
         */
        public final Start setIO(Map<String, Object> values) {
            EnhJSO jso = JavaScriptObject.createObject().cast();
            values.forEach(jso::set);
            getParams().set("io", jso);
            return this;
        }
    }
}
