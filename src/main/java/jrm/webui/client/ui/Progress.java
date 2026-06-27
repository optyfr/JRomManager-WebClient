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

public class Progress extends Window /* NOSONAR */ {
    private static final String LOADING_IMG = "<center><img height='16' width='16' src='/images/loading.gif'></center>";

    private static final String LABEL = "label";

    private static final String TIME_TIME = "<code>--:--:--/--:--:--</code>";

    private VLayout panel;

    /** The lbl info. */
    private Label[] lblInfo;

    /** The lbl sub info. */
    private Label[] lblSubInfo;

    /** The progress bar. */
    private final Progressbar progressBar;
    private final Label progressBarLabel;

    /** The lbl timeleft. */
    private final Label lblTimeleft;

    /** The btn cancel. */
    private final IButton btnCancel;

    /** The progress bar 2. */
    private final Progressbar progressBar2;
    private final Label progressBarLabel2;

    /** The lbl time left 2. */
    private final Label lblTimeLeft2;

    /** The progress bar 3. */
    private final Progressbar progressBar3;
    private final Label progressBarLabel3;

    /** The lbl time left 3. */
    private final Label lblTimeLeft3;

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

    private static final String COLOR_NORMAL = "rgb(70% 70% 70%)";
    private static final String COLOR_LIGHT = "rgb(80% 80% 80%)";
    private static final String COLOR_LIGHTER = "rgb(90% 90% 90%)";
    private static final String CODE_OPEN = "<code>";
    private static final String CODE_CLOSE = "</code>";

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

    private VLayout buildPanel() {
        VLayout p = new VLayout();
        p.setMembersMargin(2);
        p.setWidth100();
        p.setHeight100();
        p.setOverflow(Overflow.AUTO);
        return p;
    }

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

    private Layout buildProgressBarRow(Progressbar pb, Label timeLabel) {
        HLayout row = new HLayout();
        row.setWidth100();
        row.setMembersMargin(2);
        row.addMembers(pb, timeLabel);
        return row;
    }

    private boolean isOdd(int i) {
        return (i % 2) != 0;
    }

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

    public void clearInfos() {
        for (Label label : lblInfo)
            label.setContents("&nbsp;");
        for (Label label : lblSubInfo)
            label.setContents("&nbsp;");
    }

    public void setFullProgress(A_Progress.SetFullProgress.ProgressData pd) {
        for (int i = 0; i < lblInfo.length; i++)
            lblInfo[i].setContents(Optional.ofNullable(pd.getInfos().get(i)).orElse(""));
        for (int i = 0; i < lblSubInfo.length; i++)
            lblSubInfo[i].setContents(Optional.ofNullable(pd.getSubInfos().get(i)).orElse(""));
        updateProgressBar(progressBar, progressBarLabel, lblTimeleft, pd.getPB1(), false);
        updateProgressBar(progressBar2, progressBarLabel2, lblTimeLeft2, pd.getPB2(), true);
        updateProgressBar(progressBar3, progressBarLabel3, lblTimeLeft3, pd.getPB3(), true);
    }

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

    public void cancel() {
        btnCancel.setDisabled(true);
        btnCancel.setTitle("Canceling"); //$NON-NLS-1$
        Client.sendMsg(JsonUtils.stringify(Q_Progress.Cancel.instantiate()));
    }

    private void packHeight() {
        // Reserved for future use - currently not needed as SmartGWT handles layout automatically
    }

    @Override
    protected void onDestroy() {
        Client.getChildWindows().remove(this);
        super.onDestroy();
    }

    @Override
    public void close() {
        markForDestroy();
    }

    public void canCancel(boolean canCancel) {
        btnCancel.setDisabled(!canCancel);
    }
}
