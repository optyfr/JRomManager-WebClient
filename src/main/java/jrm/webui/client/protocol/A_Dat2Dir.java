package jrm.webui.client.protocol;

import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

/**
 * Action response for DAT to directory conversion operations.
 * <p>
 * Contains nested action types for Dat2Dir-related server notifications
 * including result updates, operation completion, and settings display.
 *
 * @since 2.5
 */
public class A_Dat2Dir extends A_ // NOSONAR
{
    /**
     * Constructs a Dat2Dir action from a base action.
     *
     * @param a the base action containing the response data
     */
    public A_Dat2Dir(final A_ a) {
        this(a.response);
    }

    /**
     * Constructs a Dat2Dir action from an enhanced JavaScript response.
     *
     * @param response the enhanced JavaScript response object
     */
    public A_Dat2Dir(final EnhJSO response) {
        super(response);
    }

    /**
     * Action to clear all Dat2Dir results from the display.
     */
    public static class ClearResults extends A_ {
        /**
         * Constructs a ClearResults action from a base action.
         *
         * @param a the base action
         */
        public ClearResults(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a ClearResults action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public ClearResults(final EnhJSO response) {
            super(response);
        }
    }

    /**
     * Action indicating that the Dat2Dir operation has ended.
     */
    public static class End extends A_ {
        /**
         * Constructs an End action from a base action.
         *
         * @param a the base action
         */
        public End(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs an End action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public End(final EnhJSO response) {
            super(response);
        }
    }

    /**
     * Action to update a single result row in the Dat2Dir display.
     */
    public static class UpdateResult extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs an UpdateResult action from a base action.
         *
         * @param a the base action
         */
        public UpdateResult(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs an UpdateResult action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public UpdateResult(final EnhJSO response) {
            super(response);
            params = response.getJSO("params");
        }

        /**
         * Returns the row index to update.
         *
         * @return the row index
         */
        public int getRow() {
            return params.getInt("row");
        }

        /**
         * Returns the result text for the row.
         *
         * @return the result text
         */
        public String getResult() {
            return params.get("result");
        }
    }

    /**
     * Action to display the Dat2Dir settings dialog.
     */
    public static class ShowSettings extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a ShowSettings action from a base action.
         *
         * @param a the base action
         */
        public ShowSettings(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a ShowSettings action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public ShowSettings(final EnhJSO response) {
            super(response);
            params = response.getJSO("params");
        }

        /**
         * Returns the settings object.
         *
         * @return the settings as an enhanced JavaScript object
         */
        public EnhJSO getSettings() {
            return params.getJSO("settings");
        }

        /**
         * Returns the array of source paths.
         *
         * @return the source paths as a JavaScript string array
         */
        public JsArrayString getSrcs() {
            return params.getJSO("srcs").cast();
        }
    }
}
