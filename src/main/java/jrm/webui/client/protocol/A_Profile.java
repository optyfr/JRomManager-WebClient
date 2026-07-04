package jrm.webui.client.protocol;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

/**
 * Action response for profile operations.
 * <p>
 * Contains nested action types for profile-related server notifications
 * including loading, scanning, fixing, and importing profiles.
 *
 * @since 2.5
 */
public class A_Profile extends A_ // NOSONAR
{
    /** JSON property key for the {@code success} flag. */
    private static final String SUCCESS_STR = "success";
    /** JSON property key for the {@code params} object. */
    private static final String PARAMS_STR = "params";

    /**
     * Constructs a Profile action from a base action.
     *
     * @param a the base action containing the response data
     */
    public A_Profile(final A_ a) {
        this(a.response);
    }

    /**
     * Constructs a Profile action from an enhanced JavaScript response.
     *
     * @param response the enhanced JavaScript response object
     */
    public A_Profile(final EnhJSO response) {
        super(response);
    }

    /**
     * Action indicating that a profile has been loaded.
     */
    public static class Loaded extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a Loaded action from a base action.
         *
         * @param a the base action
         */
        public Loaded(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a Loaded action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public Loaded(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns whether the profile was loaded successfully.
         *
         * @return {@code true} if successful
         */
        public final boolean getSuccess() {
            return params.getBool(SUCCESS_STR);
        }

        /**
         * Returns the name of the loaded profile.
         *
         * @return the profile name
         */
        public final String getName() {
            return params.getString("name");
        }

        /**
         * Returns the profile settings.
         *
         * @return the settings as an enhanced JavaScript object
         */
        public final EnhJSO getSettings() {
            return params.getJSO("settings");
        }

        /**
         * Returns the array of years associated with the profile.
         *
         * @return the years as a JavaScript string array
         */
        public final JsArrayString getYears() {
            return params.getJSO("years");
        }

        /**
         * Returns the array of systems in the profile.
         *
         * @return the systems as a JavaScript object array
         */
        public final JsArray<JavaScriptObject> getSystems() {
            return params.getJSAJSO("systems");
        }

        /**
         * Returns the array of sources in the profile.
         *
         * @return the sources as a JavaScript object array
         */
        public final JsArray<JavaScriptObject> getSources() {
            return params.getJSAJSO("sources");
        }
    }

    /**
     * Action indicating that a profile scan has completed.
     */
    public static class Scanned extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a Scanned action from a base action.
         *
         * @param a the base action
         */
        public Scanned(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a Scanned action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public Scanned(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns whether the scan was successful.
         *
         * @return {@code true} if successful
         */
        public final boolean getSuccess() {
            return params.getBool(SUCCESS_STR);
        }

        /**
         * Returns the number of actions performed during the scan.
         *
         * @return the action count, or {@code null} if not available
         */
        public final Integer getActions() {
            return params.getInteger("actions");
        }

        /**
         * Returns whether a report was generated.
         *
         * @return {@code true} if a report was generated
         */
        public final boolean hasReport() {
            return params.getBool("report");
        }
    }

    /**
     * Action indicating that a profile fix operation has completed.
     */
    public static class Fixed extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a Fixed action from a base action.
         *
         * @param a the base action
         */
        public Fixed(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a Fixed action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public Fixed(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns whether the fix operation was successful.
         *
         * @return {@code true} if successful
         */
        public final boolean getSuccess() {
            return params.getBool(SUCCESS_STR);
        }

        /**
         * Returns the number of actions performed during the fix.
         *
         * @return the action count, or {@code null} if not available
         */
        public final Integer getActions() {
            return params.getInteger("actions");
        }
    }

    /**
     * Action indicating that a profile has been imported.
     */
    public static class Imported extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs an Imported action from a base action.
         *
         * @param a the base action
         */
        public Imported(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs an Imported action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public Imported(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns the path of the imported profile.
         *
         * @return the file path
         */
        public final String getPath() {
            return params.getString("path");
        }

        /**
         * Returns the parent directory of the imported profile.
         *
         * @return the parent path
         */
        public final String getParent() {
            return params.getString("parent");
        }

        /**
         * Returns the name of the imported profile.
         *
         * @return the profile name
         */
        public final String getName() {
            return params.getString("name");
        }
    }

}
