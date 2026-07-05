package jrm.webui.client.protocol;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArrayString;

import jrm.webui.client.utils.EnhJSO;

/**
 * Action response for progress tracking operations.
 * <p>
 * Contains nested action types for progress-related server notifications
 * including progress updates, cancellation control, and completion status.
 *
 * @since 2.5
 */
public class A_Progress extends A_ // NOSONAR
{
    /** JSON property key for the {@code params} object. */
    private static final String PARAMS_STR = "params";
    /** JSON property key for the {@code threadCnt} value. */
    private static final String THREAD_CNT_STR = "threadCnt";
    /** JSON property key for the {@code multipleSubInfos} flag. */
    private static final String MULTIPLE_SUB_INFOS_STR = "multipleSubInfos";

    /**
     * Constructs a Progress action from a base action.
     *
     * @param a the base action containing the response data
     */
    public A_Progress(final A_ a) {
        this(a.response);
    }

    /**
     * Constructs a Progress action from an enhanced JavaScript response.
     *
     * @param response the enhanced JavaScript response object
     */
    public A_Progress(final EnhJSO response) {
        super(response);
    }

    /**
     * Action indicating that the progress dialog should be closed.
     */
    public static class Close extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a Close action from a base action.
         *
         * @param a the base action
         */
        public Close(final A_ a) {
            this(a.response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Constructs a Close action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public Close(final EnhJSO response) {
            super(response);
        }

        /**
         * Returns whether there were errors during the operation.
         *
         * @return {@code true} if errors occurred
         */
        public boolean hasErrors() {
            return ((JsArrayString) params.getJSO("errors")).length() > 0;
        }

        /**
         * Returns the list of error messages.
         *
         * @return the error messages
         */
        public List<String> getErrors() {
            final JsArrayString errors = params.getJSO("errors");
            final List<String> result = new ArrayList<>();
            for (int i = 0; i < errors.length(); i++)
                result.add(errors.get(i));
            return result;
        }
    }

    /**
     * Action to set initial progress information.
     */
    public static class SetInfos extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a SetInfos action from a base action.
         *
         * @param a the base action
         */
        public SetInfos(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a SetInfos action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public SetInfos(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns the number of threads being used.
         *
         * @return the thread count
         */
        public final int getThreadCnt() {
            return params.getInt(THREAD_CNT_STR);
        }

        /**
         * Returns whether multiple sub-progress indicators are used.
         *
         * @return {@code true} if multiple sub-infos are present
         */
        public final Boolean getMultipleSubInfos() {
            return params.getBoolean(MULTIPLE_SUB_INFOS_STR);
        }
    }

    /**
     * Action to extend progress information with additional details.
     */
    public static class ExtendInfos extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs an ExtendInfos action from a base action.
         *
         * @param a the base action
         */
        public ExtendInfos(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs an ExtendInfos action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public ExtendInfos(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns the number of threads being used.
         *
         * @return the thread count
         */
        public final int getThreadCnt() {
            return params.getInt(THREAD_CNT_STR);
        }

        /**
         * Returns whether multiple sub-progress indicators are used.
         *
         * @return {@code true} if multiple sub-infos are present
         */
        public final Boolean getMultipleSubInfos() {
            return params.getBoolean(MULTIPLE_SUB_INFOS_STR);
        }
    }

    /**
     * Action indicating whether the operation can be cancelled.
     */
    public static class CanCancel extends A_ {
        /** The {@code params} object extracted from the server response. */
        private EnhJSO params;

        /**
         * Constructs a CanCancel action from a base action.
         *
         * @param a the base action
         */
        public CanCancel(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a CanCancel action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public CanCancel(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns whether the operation can be cancelled.
         *
         * @return {@code true} if cancellation is allowed
         */
        public final boolean canCancel() {
            return params.getBool("canCancel");
        }
    }

    /**
     * Action to clear all progress information.
     */
    public static class ClearInfos extends A_ {
        /**
         * Constructs a ClearInfos action from a base action.
         *
         * @param a the base action
         */
        public ClearInfos(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a ClearInfos action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public ClearInfos(final EnhJSO response) {
            super(response);
        }
    }

    /**
     * Action to set complete progress data including all sub-progress information.
     */
    public static class SetFullProgress extends A_ {
        /** The progress data extracted from the server response. */
        private ProgressData params;

        /**
         * Constructs a SetFullProgress action from a base action.
         *
         * @param a the base action
         */
        public SetFullProgress(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs a SetFullProgress action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public SetFullProgress(final EnhJSO response) {
            super(response);
            params = response.getJSO(PARAMS_STR);
        }

        /**
         * Returns the progress data parameters.
         *
         * @return the progress data
         */
        public final ProgressData getParams() {
            return params;
        }

        /**
         * Container for complete progress data.
         */
        public static class ProgressData extends EnhJSO {
            /** Protected constructor for JavaScript object overlay types. */
            protected ProgressData() {
            }

            /**
             * Returns the main progress information lines.
             *
             * @return the info lines as a JavaScript string array
             */
            public final JsArrayString getInfos() {
                return getJSAStrJSO("infos");
            }

            /**
             * Returns the sub-progress information lines.
             *
             * @return the sub-info lines as a JavaScript string array
             */
            public final JsArrayString getSubInfos() {
                return getJSAStrJSO("subinfos");
            }

            /**
             * Returns whether multiple sub-progress indicators are used.
             *
             * @return {@code true} if multiple sub-infos are present
             */
            public final Boolean isMultipleSubInfos() {
                return getBoolean(MULTIPLE_SUB_INFOS_STR);
            }

            /**
             * Returns the number of threads being used.
             *
             * @return the thread count
             */
            public final int getThreadCnt() {
                return getInt(THREAD_CNT_STR);
            }

            /**
             * Returns the first progress bar data.
             *
             * @return the progress bar data
             */
            public final Progress getPB1() {
                return getJSO("pb1");
            }

            /**
             * Returns the second progress bar data.
             *
             * @return the progress bar data
             */
            public final Progress getPB2() {
                return getJSO("pb2");
            }

            /**
             * Returns the third progress bar data.
             *
             * @return the progress bar data
             */
            public final Progress getPB3() {
                return getJSO("pb3");
            }

            /**
             * Container for a single progress bar's state.
             *
             * @since 2.5
             */
            public static class Progress extends EnhJSO {
                /** Protected constructor for JavaScript object overlay types. */
                protected Progress() {

                }

                /**
                 * Returns whether the progress bar is visible.
                 *
                 * @return {@code true} if visible
                 */
                public final boolean isVisible() {
                    return getBool("visibility");
                }

                /**
                 * Returns whether the progress bar displays a string overlay.
                 *
                 * @return {@code true} if string painting is enabled
                 */
                public final boolean hasStringPainted() {
                    return getBool("stringPainted");
                }

                /**
                 * Returns whether the progress bar is in indeterminate mode.
                 *
                 * @return {@code true} if indeterminate
                 */
                public final boolean isIndeterminate() {
                    return getBool("indeterminate");
                }

                /**
                 * Returns the current progress value.
                 *
                 * @return the current value
                 */
                public final int getVal() {
                    return getInt("val");
                }

                /**
                 * Returns the maximum progress value.
                 *
                 * @return the maximum value
                 */
                public final int getMax() {
                    return getInt("max");
                }

                /**
                 * Returns the progress percentage.
                 *
                 * @return the percentage
                 */
                public final float getPerc() {
                    return getInt("perc");
                }

                /**
                 * Returns the progress message text.
                 *
                 * @return the message string
                 */
                public final String getMsg() {
                    return get("msg");
                }

                /**
                 * Returns the estimated time remaining.
                 *
                 * @return the time-left string
                 */
                public final String getTimeleft() {
                    return get("timeleft");
                }
            }
        }
    }

}
