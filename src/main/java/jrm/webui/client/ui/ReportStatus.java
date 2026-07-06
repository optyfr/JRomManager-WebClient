package jrm.webui.client.ui;

import com.smartgwt.client.widgets.Window;

/**
 * Contract for UI components that can display a textual report status.
 * <p>
 * Implementors are typically report viewers or trees that expose a status bar
 * whose content can be updated from the outside.
 *
 * @since 2.5
 */
public interface ReportStatus {
    /**
     * Updates the displayed status text.
     *
     * @param status
     *            the new status text to display
     * @return the window hosting the status, for chaining
     */
    Window setStatus(String status);
}
