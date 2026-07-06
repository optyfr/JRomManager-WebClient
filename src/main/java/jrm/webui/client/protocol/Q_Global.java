package jrm.webui.client.protocol;

/**
 * Query commands for global operations.
 *
 * @since 2.5
 */
public class Q_Global extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_Global() {
        super();
    }

    /**
     * Query to set a global property on the server.
     */
    public static class SetProperty extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected SetProperty() {
            super();
        }

        /**
         * Creates a new set-property query.
         *
         * @return a new SetProperty query instance
         */
        public static final SetProperty instantiate() {
            return Q_.instantiateCmd("Global.setProperty").cast();
        }

        /**
         * Sets a string property value.
         *
         * @param name  the property name
         * @param value the property value
         * @return this query for chaining
         */
        public final SetProperty setProperty(String name, String value) {
            getParams().set(name, value);
            return this;
        }

        /**
         * Sets an integer property value.
         *
         * @param name  the property name
         * @param value the property value
         * @return this query for chaining
         */
        public final SetProperty setProperty(String name, int value) {
            getParams().set(name, value);
            return this;
        }

        /**
         * Sets a boolean property value.
         *
         * @param name  the property name
         * @param value the property value
         * @return this query for chaining
         */
        public final SetProperty setProperty(String name, boolean value) {
            getParams().set(name, value);
            return this;
        }
    }

    /**
     * Query to trigger garbage collection on the server.
     */
    public static class GC extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected GC() {
            super();
        }

        /**
         * Creates a new garbage collection query.
         *
         * @return a new GC query instance
         */
        public static final GC instantiate() {
            return Q_.instantiateCmd("Global.GC").cast();
        }

    }

    /**
     * Query to retrieve server memory information.
     */
    public static class GetMemory extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected GetMemory() {
            super();
        }

        /**
         * Creates a new get-memory query.
         *
         * @return a new GetMemory query instance
         */
        public static final GetMemory instantiate() {
            return Q_.instantiateCmd("Global.getMemory").cast();
        }

    }
}
