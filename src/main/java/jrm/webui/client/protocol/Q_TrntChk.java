package jrm.webui.client.protocol;

/**
 * Query commands for Torrent Check operations.
 *
 * @since 2.5
 */
public class Q_TrntChk extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_TrntChk() {
        super();
    }

    /**
     * Query to start the Torrent Check operation.
     */
    public static class Start extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Start() {
            super();
        }

        /**
         * Creates a new Torrent Check start query.
         *
         * @return a new Start query instance
         */
        public static final Start instantiate() {
            return Q_.instantiateCmd("TrntChk.start").cast();
        }
    }
}
