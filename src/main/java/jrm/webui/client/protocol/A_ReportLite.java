package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;
import jrm.webui.client.utils.EnhJSO.ForEachConsumer;

/**
 * Action response for lite report operations.
 * <p>
 * Contains nested action types for lite report-related server notifications
 * including filter application.
 *
 * @since 2.5
 */
public class A_ReportLite extends A_ // NOSONAR
{
    /**
     * Constructs a ReportLite action from a base action.
     *
     * @param a the base action containing the response data
     */
    public A_ReportLite(final A_ a) {
        this(a.response);
    }

    /**
     * Constructs a ReportLite action from an enhanced JavaScript response.
     *
     * @param response the enhanced JavaScript response object
     */
    public A_ReportLite(final EnhJSO response) {
        super(response);
    }

    /**
     * Action to apply filters to the lite report display.
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
