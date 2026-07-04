package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;
import jrm.webui.client.utils.EnhJSO.ForEachConsumer;

/**
 * Action response for report operations.
 * <p>
 * Contains nested action types for report-related server notifications
 * including filter application.
 *
 * @since 2.5
 */
public class A_Report extends A_ // NOSONAR
{
    /**
     * Constructs a Report action from a base action.
     *
     * @param a the base action containing the response data
     */
    public A_Report(final A_ a) {
        this(a.response);
    }

    /**
     * Constructs a Report action from an enhanced JavaScript response.
     *
     * @param response the enhanced JavaScript response object
     */
    public A_Report(final EnhJSO response) {
        super(response);
    }

    /**
     * Action to apply filters to the report display.
     */
    public static class ApplyFilter extends A_ {
        /**
         * Constructs an ApplyFilter action from a base action.
         *
         * @param a the base action
         */
        public ApplyFilter(final A_ a) {
            this(a.response);
        }

        /**
         * Constructs an ApplyFilter action from an enhanced JavaScript response.
         *
         * @param response the enhanced JavaScript response object
         */
        public ApplyFilter(final EnhJSO response) {
            super(response);
        }

        /**
         * Iterates over the filter parameters, invoking the consumer for each entry.
         *
         * @param bc the consumer to invoke for each filter name-value pair
         */
        public final void forEachParams(ForEachConsumer<String, Boolean> bc) {
            response.forEachBoolean("params", bc);
        }
    }

}
