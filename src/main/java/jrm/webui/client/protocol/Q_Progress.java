package jrm.webui.client.protocol;

/**
 * Query commands for progress tracking operations.
 *
 * @since 2.5
 */
public class Q_Progress extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_Progress() {
        super();
    }

    /**
     * Query to cancel the current operation.
     */
    public static class Cancel extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Cancel() {
            super();
        }

        /**
         * Creates a new cancel query.
         *
         * @return a new Cancel query instance
         */
        public static final Cancel instantiate() {
            return Q_.instantiateCmd("Progress.cancel").cast();
        }
    }
}
