package jrm.webui.client.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.A_Progress;
import jrm.webui.client.protocol.Q_Progress;

/**
 * Modal SmartGWT window that reports progress of a long-running server operation.
 * <p>
 * Displays a variable number of thread info labels (with optional sub-info lines)
 * plus up to three progress bars, each with its own time-left label, and a cancel
 * button. The window content is driven by {@link A_Progress.SetFullProgress}
 * payloads received from the server.
 *
 * @since 2.5
 */
public class Progress extends Window /* NOSONAR */ {
    /** HTML markup for the indeterminate "loading" spinner shown inside a progress bar. */
    private static final String LOADING_IMG = "<center><img height='16' width='16' src='/images/loading.gif'></center>";

    /** SmartGWT child-name used to overlay a label onto a {@link Progressbar}. */
    private static final String LABEL = "label";

    /** Placeholder text shown in a time-left label when no timing information is available. */
    private static final String TIME_TIME = "<code>--:--:--/--:--:--</code>";

    /** Vertical layout holding the per-thread info and sub-info labels. */
    private VLayout panel;

    /** Per-thread info labels, one row per server-side worker thread. */
    private Label[] lblInfo;

    /** Per-thread (or shared) sub-info labels displayed below the info labels. */
    private Label[] lblSubInfo;

    /** Primary progress bar (overall scan/audit operation). */
    private final Progressbar progressBar;

    /** Label painted on top of {@link #progressBar} showing its current message. */
    private final Label progressBarLabel;

    /** Time-left label associated with {@link #progressBar}. */
    private final Label lblTimeleft;

    /** Cancel button allowing the user to abort the running server operation. */
    private final IButton btnCancel;

    /** Secondary progress bar (sub-operation, e.g. a fix or compression pass). */
    private final Progressbar progressBar2;

    /** Label painted on top of {@link #progressBar2} showing its current message. */
    private final Label progressBarLabel2;

    /** Time-left label associated with {@link #progressBar2}. */
    private final Label lblTimeLeft2;

    /** Tertiary progress bar (additional sub-operation). */
    private final Progressbar progressBar3;

    /** Label painted on top of {@link #progressBar3} showing its current message. */
    private final Label progressBarLabel3;

    /** Time-left label associated with {@link #progressBar3}. */
    private final Label lblTimeLeft3;

    /**
     * Creates the progress window, registers it with the client child windows,
     * builds the layout, centers it on the page, and shows it.
     */
    public Progress() {
        super();
        Client.getChildWindows().add(this);
        setIsModal(true);
        setShowModalMask(true);
        setWidth(500);
        setHeight(250);
        setMinHeight(150);
        setCanDragResize(true);
        setID("Progress");
        setTitle("Progression");
        Map<String, Object> headerIconDefaults = new HashMap<>();
        headerIconDefaults.put("width", 16);
        headerIconDefaults.put("height", 16);
        headerIconDefaults.put("src", "rom.png");
        setHeaderIconDefaults(headerIconDefaults);
        setShowHeaderIcon(true);
        setShowCloseButton(false);

        panel = buildPanel();
        setInfos(1, false);

        progressBar = new Progressbar();
        progressBar.setLength("100%");

        lblTimeleft = new Label(TIME_TIME);
        lblTimeleft.setWidth("*");
        lblTimeleft.setWrap(false);
        lblTimeleft.setHeight(20);
        lblTimeleft.setValign(VerticalAlignment.CENTER);
        lblTimeleft.setAlign(Alignment.CENTER);

        progressBarLabel = new Label();
        progressBarLabel.setWidth100();
        progressBarLabel.setAlign(Alignment.CENTER);
        progressBarLabel.setWrap(false);
        progressBar.addChild(progressBarLabel, LABEL, true);

        progressBar2 = new Progressbar();
        progressBar2.setLength("100%");

        lblTimeLeft2 = new Label(TIME_TIME);
        lblTimeLeft2.setWidth("*");
        lblTimeLeft2.setWrap(false);
        lblTimeLeft2.setHeight(20);
        lblTimeLeft2.setValign(VerticalAlignment.CENTER);
        lblTimeLeft2.setAlign(Alignment.CENTER);

        progressBarLabel2 = new Label();
        progressBarLabel2.setWidth100();
        progressBarLabel2.setAlign(Alignment.CENTER);
        progressBarLabel2.setWrap(false);
        progressBar2.addChild(progressBarLabel2, LABEL, true);

        progressBar3 = new Progressbar();
        progressBar3.setLength("100%");

        lblTimeLeft3 = new Label(TIME_TIME);
        lblTimeLeft3.setWidth("*");
        lblTimeLeft3.setWrap(false);
        lblTimeLeft3.setHeight(20);
        lblTimeLeft3.setValign(VerticalAlignment.CENTER);
        lblTimeLeft3.setAlign(Alignment.CENTER);

        progressBarLabel3 = new Label();
        progressBarLabel3.setWidth100();
        progressBarLabel3.setAlign(Alignment.CENTER);
        progressBarLabel3.setWrap(false);
        progressBar3.addChild(progressBarLabel3, LABEL, true);

        btnCancel = new IButton("Cancel");
        btnCancel.setPadding(2);
        btnCancel.setLayoutAlign(Alignment.CENTER);
        btnCancel.addClickHandler(e -> cancel());
        btnCancel.setIcon("icons/stop.png");

        addItem(buildMainLayout());

        centerInPage();
        show();
        packHeight();
    }

    /** Background color for odd-numbered info rows (darker shade). */
    private static final String COLOR_NORMAL = "rgb(70% 70% 70%)";
    /** Background color for even-numbered info rows (lighter shade). */
    private static final String COLOR_LIGHT = "rgb(80% 80% 80%)";
    /** Background color for the shared sub-info row (lightest shade). */
    private static final String COLOR_LIGHTER = "rgb(90% 90% 90%)";
    /** Opening {@code <code>} HTML tag, used to wrap time-left values. */
    private static final String CODE_OPEN = "<code>";
    /** Closing {@code </code>} HTML tag, used to wrap time-left values. */
    private static final String CODE_CLOSE = "</code>";

    /**
     * (Re)builds the info label area for the given number of threads.
     *
     * @param threadCnt
     *            the number of thread info lines to display
     * @param multipleSubInfos
     *            {@code true} to show one sub-info line per thread, {@code false}
     *            to show a single shared sub-info line, or {@code null} to show none
     */
    public void setInfos(int threadCnt, Boolean multipleSubInfos) {
        panel.removeMembers(panel.getMembers());

        lblInfo = new Label[threadCnt];
        int subInfoSize = 0;
        if (multipleSubInfos != null) {
            subInfoSize = multipleSubInfos ? threadCnt : 1;
        }
        lblSubInfo = new Label[subInfoSize];

        for (int i = 0; i < threadCnt; i++) {
            lblInfo[i] = buildLabel(isOdd(i) ? COLOR_NORMAL : COLOR_LIGHT);
            panel.addMember(lblInfo[i]);

            if (Boolean.TRUE.equals(multipleSubInfos)) {
                lblSubInfo[i] = buildLabel(isOdd(i) ? COLOR_NORMAL : COLOR_LIGHT);
                panel.addMember(lblSubInfo[i]);
            }
        }
        if (multipleSubInfos != null && !multipleSubInfos) {
            lblSubInfo[0] = buildLabel(COLOR_LIGHTER);
            panel.addMember(lblSubInfo[0]);
        }
        if (isVisible() && Boolean.TRUE.equals(isDrawn()))
            packHeight();
    }

    /**
     * Grows the info label area to the given thread count, preserving existing
     * labels and appending new ones as needed.
     *
     * @param threadCnt
     *            the new total number of thread info lines
     * @param multipleSubInfos
     *            {@code true} to also extend the per-thread sub-info lines
     */
    public void extendInfos(int threadCnt, Boolean multipleSubInfos) {
        if (lblInfo == null || lblInfo.length == threadCnt)
            return;

        if (Boolean.TRUE.equals(multipleSubInfos) && lblSubInfo == null)
            return;

        final var oldThreadCnt = lblInfo.length;

        lblInfo = Arrays.copyOf(lblInfo, threadCnt);
        if (Boolean.TRUE.equals(multipleSubInfos))
            lblSubInfo = Arrays.copyOf(lblSubInfo, threadCnt);

        for (int i = oldThreadCnt; i < threadCnt; i++) {
            lblInfo[i] = buildLabel(isOdd(i) ? COLOR_NORMAL : COLOR_LIGHT);
            panel.addMember(lblInfo[i]);

            if (Boolean.TRUE.equals(multipleSubInfos)) {
                lblSubInfo[i] = buildLabel(isOdd(i) ? COLOR_NORMAL : COLOR_LIGHT);
                panel.addMember(lblSubInfo[i]);
            }
        }

        if (isVisible() && Boolean.TRUE.equals(isDrawn()))
            packHeight();
    }

    /**
     * Builds the scrollable vertical panel that hosts the info and sub-info labels.
     *
     * @return a new configured {@link VLayout}
     */
    private VLayout buildPanel() {
        VLayout p = new VLayout();
        p.setMembersMargin(2);
        p.setWidth100();
        p.setHeight100();
        p.setOverflow(Overflow.AUTO);
        return p;
    }

    /**
     * Builds the main vertical layout assembling the info panel, the three
     * progress-bar rows and the cancel button.
     *
     * @return the assembled main layout
     */
    private VLayout buildMainLayout() {
        VLayout main = new VLayout();
        main.setWidth100();
        main.setHeight100();
        main.setLayoutMargin(2);
        main.setMembersMargin(2);
        main.addMembers(
            panel,
            buildProgressBarRow(progressBar, lblTimeleft),
            buildProgressBarRow(progressBar2, lblTimeLeft2),
            buildProgressBarRow(progressBar3, lblTimeLeft3),
            btnCancel
        );
        return main;
    }

    /**
     * Builds a single horizontal row containing a progress bar and its
     * associated time-left label.
     *
     * @param pb        the progress bar widget for this row
     * @param timeLabel the time-left label for this row
     * @return the assembled row layout
     */
    private Layout buildProgressBarRow(Progressbar pb, Label timeLabel) {
        HLayout row = new HLayout();
        row.setWidth100();
        row.setMembersMargin(2);
        row.addMembers(pb, timeLabel);
        return row;
    }

    /**
     * Tells whether an index is odd.
     *
     * @param i the index to test
     * @return {@code true} if {@code i} is odd, {@code false} otherwise
     */
    private boolean isOdd(int i) {
        return (i % 2) != 0;
    }

    /**
     * Builds a styled info/sub-info label with the given background color.
     *
     * @param color the CSS background color to apply
     * @return a new configured {@link Label}
     */
    private Label buildLabel(String color) {
        final var label = new Label();
        label.setWidth100();
        label.setHeight(20);
        label.setBorder("2px inset");
        label.setBackgroundColor(color);
        label.setWrap(false);
        label.setOverflow(Overflow.CLIP_H);
        label.setShowClippedTitleOnHover(true);
        label.setShowHover(true);
        return label;
    }

    /**
     * Clears the contents of all info and sub-info labels.
     */
    public void clearInfos() {
        for (Label label : lblInfo)
            label.setContents("&nbsp;");
        for (Label label : lblSubInfo)
            label.setContents("&nbsp;");
    }

    /**
     * Updates the whole window from a full-progress payload received from the server.
     *
     * @param pd
     *            the progress data to apply
     */
    public void setFullProgress(A_Progress.SetFullProgress.ProgressData pd) {
        for (int i = 0; i < lblInfo.length; i++)
            lblInfo[i].setContents(Optional.ofNullable(pd.getInfos().get(i)).orElse(""));
        for (int i = 0; i < lblSubInfo.length; i++)
            lblSubInfo[i].setContents(Optional.ofNullable(pd.getSubInfos().get(i)).orElse(""));
        updateProgressBar(progressBar, progressBarLabel, lblTimeleft, pd.getPB1(), false);
        updateProgressBar(progressBar2, progressBarLabel2, lblTimeLeft2, pd.getPB2(), true);
        updateProgressBar(progressBar3, progressBarLabel3, lblTimeLeft3, pd.getPB3(), true);
    }

    /**
     * Applies a single progress-bar update from its payload, toggling visibility,
     * indeterminate state, percent-done, label and time-left text as needed.
     *
     * @param pb
     *            the progress bar widget to update
     * @param pbLabel
     *            the label painted on top of the progress bar
     * @param timeLabel
     *            the time-left label associated with the bar
     * @param pbData
     *            the progress payload for this bar
     * @param usePercCheck
     *            {@code true} to gate the update on {@code perc >= 0},
     *            {@code false} to gate it on {@code val > 0}
     */
    private void updateProgressBar(Progressbar pb, Label pbLabel, Label timeLabel,
            A_Progress.SetFullProgress.ProgressData.Progress pbData, boolean usePercCheck) {
        if (pb.isVisible() != pbData.isVisible()) {
            pb.setVisibility(pbData.isVisible() ? Visibility.INHERIT : Visibility.HIDDEN);
            timeLabel.setVisibility(pb.getVisibility());
            packHeight();
        }
        if (!pbData.isVisible())
            return;
        if (pbData.isIndeterminate()) {
            pb.setPercentDone(0);
            pbLabel.setContents(LOADING_IMG);
        } else if (usePercCheck ? pbData.getPerc() >= 0 : pbData.getVal() > 0) {
            if (pb.getPercentDone() != (int) pbData.getPerc())
                pb.setPercentDone((int) pbData.getPerc());
            if (pbData.hasStringPainted())
                pbLabel.setContents(Optional.ofNullable(pbData.getMsg()).orElse(""));
            else
                pbLabel.setContents("");
            timeLabel.setContents(CODE_OPEN + pbData.getTimeleft() + CODE_CLOSE);
        } else
            timeLabel.setContents(TIME_TIME);
    }

    /**
     * Sends a cancel request to the server and disables the cancel button.
     */
    public void cancel() {
        btnCancel.setDisabled(true);
        btnCancel.setTitle("Canceling"); //$NON-NLS-1$
        Client.sendMsg(JsonUtils.stringify(Q_Progress.Cancel.instantiate()));
    }

    /**
     * Reserved hook to recompute the window height after content changes.
     * <p>
     * Currently a no-op because SmartGWT handles the layout automatically.
     */
    private void packHeight() {
        // Reserved for future use - currently not needed as SmartGWT handles layout automatically
    }

    /**
     * Removes this window from the client child-window registry when destroyed.
     */
    @Override
    protected void onDestroy() {
        Client.getChildWindows().remove(this);
        super.onDestroy();
    }

    /**
     * Closes the progress window by marking it for destruction.
     */
    @Override
    public void close() {
        markForDestroy();
    }

    /**
     * Toggles whether the cancel button is enabled.
     *
     * @param canCancel
     *            {@code true} to enable the cancel button, {@code false} to disable it
     */
    public void canCancel(boolean canCancel) {
        btnCancel.setDisabled(!canCancel);
    }
}
