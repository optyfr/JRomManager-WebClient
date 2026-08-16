package jrm.webui.client.protocol;

/**
 * Query commands for report operations.
 *
 * @since 2.5
 */
public class Q_Report extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_Report() {
        super();
    }

    /**
     * Query to set a report filter.
     */
    public static class SetFilter extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected SetFilter() {
            super();
        }

        /**
         * Creates a new set-filter query.
         *
         * @param lite {@code true} to use the lite report endpoint
         * @return a new SetFilter query instance
         */
        public static final SetFilter instantiate(boolean lite) {
            return Q_.instantiateCmd(lite ? "ReportLite.setFilter" : "Report.setFilter").cast();
        }

        /**
         * Sets a filter value.
         *
         * @param name  the filter name
         * @param value the filter value
         * @return this query for chaining
         */
        public final SetFilter setFilter(String name, boolean value) {
            getParams().set(name, value);
            return this;
        }
    }

    /**
     * Query to export a fixDAT of missing and partial titles.
     */
    public static class CreateFixDat extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected CreateFixDat() {
            super();
        }

        /**
         * Creates a new create-fixDAT query.
         *
         * @return a new CreateFixDat query instance
         */
        public static final CreateFixDat instantiate() {
            return Q_.instantiateCmd("Report.createFixDat").cast();
        }

        /**
         * Sets the destination path for the fixDAT file.
         *
         * @param path the abstract destination path
         * @return this query for chaining
         */
        public final CreateFixDat setPath(String path) {
            getParams().set("path", path);
            return this;
        }
    }
}
