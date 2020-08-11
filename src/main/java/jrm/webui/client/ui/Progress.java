package jrm.webui.client.ui;

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
import com.smartgwt.client.widgets.layout.VLayout;

import jrm.webui.client.Client;
import jrm.webui.client.protocol.A_Progress;
import jrm.webui.client.protocol.Q_Progress;

public class Progress extends Window
{
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

	public Progress()
	{
		super();
		Client.childWindows.add(this);
		setIsModal(true);
		setShowModalMask(true);
		setWidth(500);
		setHeight(250);
		setMinHeight(150);
		// setBackgroundColor("#EEEEEE");
		setCanDragResize(true);
		// setParentCanvas(parent);
		setID("Progress");
		setTitle("Progression");
		Map<String, Object> headerIconDefaults = new HashMap<>();
		headerIconDefaults.put("width", 16);
		headerIconDefaults.put("height", 16);
		headerIconDefaults.put("src", "rom.png");
		setHeaderIconDefaults(headerIconDefaults);
		setShowHeaderIcon(true);
		setShowCloseButton(false);

		panel = new VLayout()
		{
			{
				setMembersMargin(2);
				setWidth100();
				setHeight100();
				// setBackgroundColor("#EEEEEE");
				setOverflow(Overflow.AUTO);
			}
		};
		setInfos(1, false);

		progressBar = new Progressbar();
		progressBar.setLength("100%");

		lblTimeleft = new Label("<code>--:--:--/--:--:--</code>");
		lblTimeleft.setWidth("*");
		lblTimeleft.setWrap(false);
		lblTimeleft.setHeight(20);
		lblTimeleft.setValign(VerticalAlignment.CENTER);
		lblTimeleft.setAlign(Alignment.CENTER);

		progressBarLabel = new Label();
		progressBarLabel.setWidth100();
		progressBarLabel.setAlign(Alignment.CENTER);
		progressBar.addChild(progressBarLabel, "label", true);

		progressBar2 = new Progressbar();
		progressBar2.setLength("100%");
		
		lblTimeLeft2 = new Label("<code>--:--:--/--:--:--</code>");
		lblTimeLeft2.setWidth("*");
		lblTimeLeft2.setWrap(false);
		lblTimeLeft2.setHeight(20);
		lblTimeLeft2.setValign(VerticalAlignment.CENTER);
		lblTimeLeft2.setAlign(Alignment.CENTER);

		progressBarLabel2 = new Label();
		progressBarLabel2.setWidth100();
		progressBarLabel2.setAlign(Alignment.CENTER);
		progressBar2.addChild(progressBarLabel2, "label", true);

		btnCancel = new IButton("Cancel");
		btnCancel.setPadding(2);
		btnCancel.setLayoutAlign(Alignment.CENTER);
		btnCancel.addClickHandler(e->cancel());
		btnCancel.setIcon("icons/stop.png");

		addItem(new VLayout()
		{
			{
				setWidth100();
				setHeight100();
				setLayoutMargin(2);
				setMembersMargin(2);
				addMembers(panel, new HLayout()
				{
					{
						setWidth100();
						setHeight(10);
						setMembersMargin(2);
						addMembers(progressBar, lblTimeleft);
					}
				}, new HLayout()
				{
					{
						setWidth100();
						setHeight(10);
						setMembersMargin(2);
						addMembers(progressBar2, lblTimeLeft2);
					}
				}, btnCancel);
			}
		});

		centerInPage();
		show();
		packHeight();
	}

	public void setInfos(int threadCnt, boolean multipleSubInfos)
	{
		panel.removeMembers(panel.getMembers());

		lblInfo = new Label[threadCnt];
		lblSubInfo = new Label[multipleSubInfos ? threadCnt : 1];

		for (int i = 0; i < threadCnt; i++)
		{
			lblInfo[i] = new Label();
			lblInfo[i].setWidth100();
			lblInfo[i].setHeight(20);
			lblInfo[i].setBorder("2px inset");
			lblInfo[i].setBackgroundColor("#DDDDDD");
			lblInfo[i].setWrap(false);
			lblInfo[i].setOverflow(Overflow.CLIP_H);
			lblInfo[i].setShowClippedTitleOnHover(true);
			lblInfo[i].setShowHover(true);
			panel.addMember(lblInfo[i]);

			if (multipleSubInfos)
			{
				lblSubInfo[i] = new Label();
				lblSubInfo[i].setWidth100();
				lblSubInfo[i].setHeight(20);
				lblSubInfo[i].setBorder("2px inset");
				lblSubInfo[i].setBackgroundColor("#DDDDDD");
				lblSubInfo[i].setWrap(false);
				lblSubInfo[i].setOverflow(Overflow.CLIP_H);
				lblSubInfo[i].setShowClippedTitleOnHover(true);
				lblSubInfo[i].setShowHover(true);
				panel.addMember(lblSubInfo[i]);
			}
		}
		if (!multipleSubInfos)
		{
			lblSubInfo[0] = new Label();
			lblSubInfo[0].setWidth100();
			lblSubInfo[0].setHeight(20);
			lblSubInfo[0].setBorder("2px inset");
			lblSubInfo[0].setBackgroundColor("#DDDDDD");
			lblSubInfo[0].setWrap(false);
			lblSubInfo[0].setOverflow(Overflow.CLIP_H);
			lblSubInfo[0].setShowClippedTitleOnHover(true);
			lblSubInfo[0].setShowHover(true);
			panel.addMember(lblSubInfo[0]);
		}
		if (isVisible() && isDrawn())
			packHeight();
	}

	public void clearInfos()
	{
		for (Label label : lblInfo)
			label.setContents("&nbsp;");
		for (Label label : lblSubInfo)
			label.setContents("&nbsp;");
	}

	public void setFullProgress(A_Progress.SetFullProgress.ProgressData pd)
	{
		for (int i = 0; i < lblInfo.length; i++)
			lblInfo[i].setContents(Optional.ofNullable(pd.getInfos().get(i)).orElse(""));
		for (int i = 0; i < lblSubInfo.length; i++)
			lblSubInfo[i].setContents(Optional.ofNullable(pd.getSubInfos().get(i)).orElse(""));
		if (progressBar.isVisible() != pd.getPB1().isVisible())
		{
			progressBar.setVisibility(pd.getPB1().isVisible() ? Visibility.INHERIT : Visibility.HIDDEN);
			lblTimeleft.setVisibility(progressBar.getVisibility());
			packHeight();
		}
		if (pd.getPB1().isVisible())
		{
			if(pd.getPB1().isIndeterminate())
			{
				progressBar.setPercentDone(0);
				progressBarLabel.setContents("<center><img height='16' width='16' src='/images/loading.gif'></center>");
			}
			else if (pd.getPB1().getVal() > 0)
			{
				if (progressBar.getPercentDone() != (int) pd.getPB1().getPerc())
					progressBar.setPercentDone((int) pd.getPB1().getPerc());
				if (pd.getPB1().hasStringPainted())
					progressBarLabel.setContents(Optional.ofNullable(pd.getPB1().getMsg()).orElse(""));
				else
					progressBarLabel.setContents("");
				lblTimeleft.setContents("<code>" + pd.getPB1().getTimeleft() + "</code>");
			}
			else
				lblTimeleft.setContents("<code>--:--:--/--:--:--</code>");
		}
		if (progressBar2.isVisible() != pd.getPB2().isVisible())
		{
			progressBar2.setVisibility(pd.getPB2().isVisible() ? Visibility.INHERIT : Visibility.HIDDEN);
			lblTimeLeft2.setVisibility(progressBar2.getVisibility());
			packHeight();
		}
		if (pd.getPB2().isVisible())
		{
			if(pd.getPB2().isIndeterminate())
			{
				progressBar2.setPercentDone(0);
				progressBarLabel2.setContents("<center><img height='16' width='16' src='/images/loading.gif'></center>");
			}
			else if (pd.getPB2().getVal() > 0)
			{
				if (progressBar2.getPercentDone() != (int) pd.getPB2().getPerc())
					progressBar2.setPercentDone((int) pd.getPB2().getPerc());
				if (pd.getPB2().hasStringPainted())
					progressBarLabel2.setContents(Optional.ofNullable(pd.getPB2().getMsg()).orElse(""));
				else
					progressBarLabel2.setContents("");
				lblTimeLeft2.setContents("<code>" + pd.getPB2().getTimeleft() + "</code>");
			}
			else
				lblTimeLeft2.setContents("<code>--:--:--/--:--:--</code>");
		}
	}

	public void cancel()
	{
		btnCancel.setDisabled(true);
		btnCancel.setTitle("Canceling"); //$NON-NLS-1$
		Client.sendMsg(JsonUtils.stringify(Q_Progress.Cancel.instantiate()));
	}

	private void packHeight()
	{
		// markForRedraw();
	}

	@Override
	protected void onDestroy()
	{
		Client.childWindows.remove(this);
		super.onDestroy();
	}

	@Override
	public void close()
	{
		markForDestroy();
	}

	public void canCancel(boolean canCancel)
	{
		btnCancel.setDisabled(!canCancel);
	}
}
