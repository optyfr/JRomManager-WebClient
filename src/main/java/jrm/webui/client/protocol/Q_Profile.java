package jrm.webui.client.protocol;

/**
 * Query commands for profile operations.
 *
 * @since 2.5
 */
public class Q_Profile extends Q_ // NOSONAR
{
    /** Protected constructor for JavaScript object overlay types. */
    protected Q_Profile() {
        super();
    }

    /**
     * Query to import a profile.
     */
    public static class Import extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Import() {
            super();
        }

        /**
         * Creates a new profile import query.
         *
         * @return a new Import query instance
         */
        public static final Import instantiate() {
            return Q_.instantiateCmd("Profile.import").cast();
        }

        /**
         * Sets whether to import software lists.
         *
         * @param sl {@code true} to import software lists
         * @return this query for chaining
         */
        public final Import setSL(boolean sl) {
            getParams().set("sl", sl);
            return this;
        }

        /**
         * Sets the parent directory for the import.
         *
         * @param parent the parent directory path
         * @return this query for chaining
         */
        public final Import setParent(String parent) {
            getParams().set("parent", parent);
            return this;
        }
    }

    /**
     * Query to load a profile.
     */
    public static class Load extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Load() {
            super();
        }

        /**
         * Creates a new profile load query.
         *
         * @return a new Load query instance
         */
        public static final Load instantiate() {
            return Q_.instantiateCmd("Profile.load").cast();
        }

        /**
         * Sets the profile path to load.
         *
         * @param parent the parent directory
         * @param file   the file name
         * @return this query for chaining
         */
        public final Load setPath(String parent, String file) {
            getParams().set("parent", parent);
            getParams().set("file", file);
            return this;
        }
    }

    /**
     * Query to scan a profile.
     */
    public static class Scan extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Scan() {
            super();
        }

        /**
         * Creates a new profile scan query.
         *
         * @return a new Scan query instance
         */
        public static final Scan instantiate() {
            return Q_.instantiateCmd("Profile.scan").cast();
        }
    }

    /**
     * Query to fix a profile.
     */
    public static class Fix extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected Fix() {
            super();
        }

        /**
         * Creates a new profile fix query.
         *
         * @return a new Fix query instance
         */
        public static final Fix instantiate() {
            return Q_.instantiateCmd("Profile.fix").cast();
        }
    }

    /**
     * Query to import profile settings.
     */
    public static class ImportSettings extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected ImportSettings() {
            super();
        }

        /**
         * Creates a new import settings query.
         *
         * @return a new ImportSettings query instance
         */
        public static final ImportSettings instantiate() {
            return Q_.instantiateCmd("Profile.importSettings").cast();
        }

        /**
         * Sets the path of the settings file to import.
         *
         * @param path the file path
         * @return this query for chaining
         */
        public final ImportSettings setPath(String path) {
            getParams().set("path", path);
            return this;
        }
    }

    /**
     * Query to export profile settings.
     */
    public static class ExportSettings extends Q_ {
        /** Protected constructor for JavaScript object overlay types. */
        protected ExportSettings() {
            super();
        }

        /**
         * Creates a new export settings query.
         *
         * @return a new ExportSettings query instance
         */
        public static final ExportSettings instantiate() {
            return Q_.instantiateCmd("Profile.exportSettings").cast();
        }

        /**
         * Sets the path where settings should be exported.
         *
         * @param path the file path
         * @return this query for chaining
         */
        public final ExportSettings setPath(String path) {
            getParams().set("path", path);
            return this;
        }
    }

    /**
     * Query to set a profile property.
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
            return Q_.instantiateCmd("Profile.setProperty").cast();
        }

        /**
         * Sets the profile name.
         *
         * @param name the profile name
         * @return this query for chaining
         */
        public final SetProperty setProfile(String name) {
            set("profile", name);
            return this;
        }

        /**
         * Sets a property value, auto-detecting the type.
         *
         * @param name  the property name
         * @param value the property value
         * @return this query for chaining
         */
        public final SetProperty setProperty(String name, Object value) {
            if (value instanceof Boolean)
                return setProperty(name, (boolean) value);
            else if (value instanceof Integer)
                return setProperty(name, (int) value);
            else
                return setProperty(name, value.toString());
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
}
