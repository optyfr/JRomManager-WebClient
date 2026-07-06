package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

/**
 * Action response for batch compressor operations.
 * <p>
 * Contains nested action types for compressor-related server notifications
 * including result updates, file updates, and operation completion.
 *
 * @since 2.5
 */
public class A_Compressor extends A_ // NOSONAR
{
    /**
     * Constructs a Compressor action from a base action.
     *
     * @param a the base action containing the response data
     */
    public A_Compressor(final A_ a) {
        this(a.response);
    }

    /**
     * Constructs a Compressor action from an enhanced JavaScript response.
     *
     * @param response the enhanced JavaScript response object
     */
    public A_Compressor(final EnhJSO response) {
        super(response);
    }

    /**
     * Action to clear all compressor results from the display.
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
     * Action indicating that the compressor operation has ended.
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
     * Action to update a single result row in the compressor display.
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
     * Action to update the file being processed in the compressor display.
     */
    public static class UpdateFile extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs an UpdateFile action from a base action.
         *
         * @param a the base action
         */
        public UpdateFile(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs an UpdateFile action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public UpdateFile(final EnhJSO response) {
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
         * Returns the file name being processed.
         *
         * @return the file name
         */
        public String getFile() {
            return params.get("file");
        }
    }
}
