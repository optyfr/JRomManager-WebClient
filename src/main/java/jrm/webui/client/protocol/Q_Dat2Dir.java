package jrm.webui.client.protocol;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Query commands for DAT to directory conversion operations.
 *
 * @since 2.5
 */
public class Q_Dat2Dir extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_Dat2Dir() {
        super();
    }

    /**
     * Query to start the DAT to directory conversion.
     */
    public static class Start extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Start() {
            super();
        }

        /**
         * Creates a new Dat2Dir start query.
         *
         * @return a new Start query instance
         */
        public static final Start instantiate() {
            return Q_.instantiateCmd("Dat2Dir.start").cast();
        }
    }

    /**
     * Query to configure DAT to directory conversion settings.
     */
    public static class Settings extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Settings() {
            super();
        }

        /**
         * Creates a new Dat2Dir settings query.
         *
         * @return a new Settings query instance
         */
        public static final Settings instantiate() {
            return Q_.instantiateCmd("Dat2Dir.settings").cast();
        }

        /**
         * Sets the list of source paths for the conversion.
         *
         * @param srcs the source paths
         * @return this query for chaining
         */
        public final Settings setSrcs(List<String> srcs) {
            JsArrayString jsarrstr = JavaScriptObject.createArray().cast();
            srcs.forEach(jsarrstr::push);
            getParams().set("srcs", jsarrstr);
            return this;
        }
    }
}
