package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

/**
 * Action response for Category/Version (CatVer) operations.
 * <p>
 * Contains nested action types for CatVer-related server notifications.
 *
 * @since 2.5
 */
public class A_CatVer extends A_ // NOSONAR
{
    /**
     * Constructs a CatVer action from a base action.
     *
     * @param a the base action containing the response data
     */
    public A_CatVer(final A_ a) {
        this(a.response);
    }

    /**
     * Constructs a CatVer action from an enhanced JavaScript response.
     *
     * @param response the enhanced JavaScript response object
     */
    public A_CatVer(final EnhJSO response) {
        super(response);
    }

    /**
     * Action indicating that a CatVer file has been loaded.
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
            params = response.getJSO("params");
        }

        /**
         * Returns the path of the loaded CatVer file.
         *
         * @return the file path
         */
        public final String getPath() {
            return params.getString("path");
        }
    }

}
