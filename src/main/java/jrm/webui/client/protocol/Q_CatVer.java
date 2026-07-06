package jrm.webui.client.protocol;

/**
 * Query commands for Category/Version (CatVer) operations.
 *
 * @since 2.5
 */
public class Q_CatVer extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_CatVer() {
        super();
    }

    /**
     * Query to load a CatVer file.
     */
    public static class Load extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Load() {
            super();
        }

        /**
         * Creates a new CatVer load query.
         *
         * @return a new Load query instance
         */
        public static final Load instantiate() {
            return Q_.instantiateCmd("CatVer.load").cast();
        }

        /**
         * Sets the path of the CatVer file to load.
         *
         * @param path the file path
         * @return this query for chaining
         */
        public final Load setPath(String path) {
            getParams().set("path", path);
            return this;
        }
    }
}
