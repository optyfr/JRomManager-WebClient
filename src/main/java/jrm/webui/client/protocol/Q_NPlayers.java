package jrm.webui.client.protocol;

/**
 * Query commands for NPlayers (number of players) data operations.
 *
 * @since 2.5
 */
public class Q_NPlayers extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_NPlayers() {
        super();
    }

    /**
     * Query to load an NPlayers file.
     */
    public static class Load extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Load() {
            super();
        }

        /**
         * Creates a new NPlayers load query.
         *
         * @return a new Load query instance
         */
        public static final Load instantiate() {
            return Q_.instantiateCmd("NPlayers.load").cast();
        }

        /**
         * Sets the path of the NPlayers file to load.
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
