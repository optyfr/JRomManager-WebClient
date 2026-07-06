package jrm.webui.client.protocol;

/**
 * Query commands for batch compressor operations.
 *
 * @since 2.5
 */
// NOSONAR
public class Q_Compressor extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_Compressor() {
        super();
    }

    /**
     * Query to start the batch compressor operation.
     */
    public static class Start extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Start() {
            super();
        }

        /**
         * Creates a new compressor start query.
         *
         * @return a new Start query instance
         */
        public static final Start instantiate() {
            return Q_.instantiateCmd("Compressor.start").cast();
        }
    }
}
