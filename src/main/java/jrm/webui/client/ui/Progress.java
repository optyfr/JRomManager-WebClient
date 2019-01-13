package jrm.webui.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsonUtils;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.util.NumberUtil;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import jrm.webui.client.Client;
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

	/** The start time. */
	private long startTime = 0;
	
	/** The start time 2. */
	private long startTime2 = 0;

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
//		setBackgroundColor("#EEEEEE");
		setCanDragResize(true);
		//setParentCanvas(parent);
		setID("Progress");
		setTitle("Progression");
		Map<String,Object> headerIconDefaults = new HashMap<>();
		headerIconDefaults.put("width", 16);
		headerIconDefaults.put("height", 16);
		headerIconDefaults.put("src", "rom.png");
		setHeaderIconDefaults(headerIconDefaults);
		setShowCloseButton(false);
		
		panel = new VLayout() {{
			setMembersMargin(2);
			setWidth100();
			setHeight100();
//			setBackgroundColor("#EEEEEE");
			setOverflow(Overflow.AUTO);
		}};
		setInfos(1, false);


		progressBar = new Progressbar() {{setLength("100%");}};
		lblTimeleft = new Label("<code>--:--:--/--:--:--</code>") {{
			setWidth("*");
			setWrap(false);
			setHeight(20);
			setValign(VerticalAlignment.CENTER);
			setAlign(Alignment.CENTER);
		}};
		progressBar.addChild(progressBarLabel=new Label() {{setWidth100();setAlign(Alignment.CENTER);}}, "label", true);

		progressBar2 = new Progressbar() {{setLength("100%");}};
		lblTimeLeft2 = new Label("<code>--:--:--/--:--:--</code>") {{
			setWidth("*");
			setWrap(false);
			setHeight(20);
			setValign(VerticalAlignment.CENTER);
			setAlign(Alignment.CENTER);
		}};
		progressBar2.addChild(progressBarLabel2=new Label() {{setWidth100();setAlign(Alignment.CENTER);}}, "label", true);

		btnCancel = new IButton("Cancel") {{
			setPadding(2);
			setLayoutAlign(Alignment.CENTER);
			addClickHandler(new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					cancel();
				}
			});
		}};

		addItem(new VLayout() {{
			setWidth100();
			setHeight100();
			setLayoutMargin(2);
			setMembersMargin(2);
			addMembers(
				panel,
				new HLayout() {{
					setWidth100();
					setHeight(10);
					setMembersMargin(2);
					addMembers(
						progressBar,
						lblTimeleft
					);
				}},
				new HLayout() {{
					setWidth100();
					setHeight(10);
					setMembersMargin(2);
					addMembers(
						progressBar2,
						lblTimeLeft2
					);
				}},
				btnCancel
			);
		}});
		
		centerInPage();
		show();
		packHeight();
	}

	public void setInfos(int threadCnt, boolean multipleSubInfos)
	{
		panel.removeMembers(panel.getMembers());
		
		lblInfo = new Label[threadCnt];
		lblSubInfo = new Label[multipleSubInfos?threadCnt:1];
		startTime = System.currentTimeMillis();
		startTime2 = System.currentTimeMillis();
		
		for(int i = 0; i < threadCnt; i++)
		{
			panel.addMember(lblInfo[i] = new Label() {{
				setWidth100();
				setHeight(20);
				setBorder("2px inset");
				setBackgroundColor("#DDDDDD");
				setWrap(false);
				setOverflow(Overflow.CLIP_H);
				setShowClippedTitleOnHover(true);
				setShowHover(true);
			}});
	
			if(multipleSubInfos)
			{
				panel.addMember(lblSubInfo[i] = new Label() {{
					setWidth100();
					setHeight(20);
					setBorder("2px inset");
					setBackgroundColor("#DDDDDD");
					setWrap(false);
					setOverflow(Overflow.CLIP_H);
					setShowClippedTitleOnHover(true);
					setShowHover(true);
				}});
			}
		}
		if(!multipleSubInfos)
		{
			panel.addMember(lblSubInfo[0] = new Label() {{
				setWidth100();
				setHeight(20);
				setBorder("2px inset");
				setBackgroundColor("#DDDDDD");
				setWrap(false);
				setOverflow(Overflow.CLIP_H);
				setShowClippedTitleOnHover(true);
				setShowHover(true);
			}});
		}
		if(isVisible()&&isDrawn())
			packHeight();
	}
	
	public void clearInfos()
	{
		for(Label label : lblInfo)
			label.setContents("&nbsp;");
		for(Label label : lblSubInfo)
			label.setContents("&nbsp;");
	}
	
	private int pb_val, pb_max;
	
	public void setProgress(final int offset, final String msg, final Integer val, final Integer max, final String submsg)
	{
		if (msg != null)
			lblInfo[offset].setContents(msg);
		if (val != null)
		{
			if (val < 0 && progressBar.isVisible())
			{
				progressBar.setVisibility(Visibility.HIDDEN);
				lblTimeleft.setVisibility(Visibility.HIDDEN);
				packHeight();
			}
			else if (val > 0 && !progressBar.isVisible())
			{
				progressBar.setVisibility(Visibility.INHERIT);
				lblTimeleft.setVisibility(Visibility.INHERIT);
				packHeight();
			}
			if (max != null)
				pb_max = max;
			if (val > 0)
			{
				int percent = (pb_val = val) * 100 / pb_max;
				if (progressBar.getPercentDone() != percent)
				{
					progressBar.setPercentDone(percent);
					progressBarLabel.setContents(progressBar.getPercentDone()+"%");
				}
			}
			if (val == 0)
				startTime = System.currentTimeMillis();
			if (val > 0)
			{
				pb_val = val;
				final String left = toHMS(((System.currentTimeMillis() - startTime) * (pb_max - pb_val) / pb_val) / 1000); //$NON-NLS-1$
				final String total = toHMS(((System.currentTimeMillis() - startTime) * pb_max / pb_val) / 1000); //$NON-NLS-1$
				lblTimeleft.setContents("<code>" + left + "/" + total + "</code>"); //$NON-NLS-1$
			}
			else
				lblTimeleft.setContents("<code>--:--:--/--:--:--</code>"); //$NON-NLS-1$
		}
		if(lblSubInfo.length==1)
			lblSubInfo[0].setContents(submsg!=null?submsg:"&nbsp;");
		else
			lblSubInfo[offset].setContents(submsg!=null?submsg:"&nbsp;");
	}

	public void cancel()
	{
		btnCancel.setDisabled(true);
		btnCancel.setTitle("Canceling"); //$NON-NLS-1$
		Client.socket.send(JsonUtils.stringify(Q_Progress.Cancel.instantiate()));
	}

	private int pb2_val, pb2_max;

	public void setProgress2(final String msg, final Integer val, final Integer max)
	{
		if (msg != null && val != null)
		{
			if (!progressBar2.isVisible())
			{
				progressBar2.setVisibility(Visibility.INHERIT);
				lblTimeLeft2.setVisibility(Visibility.INHERIT);
				packHeight();
			}
			final String oldmsg = progressBarLabel2.getContents();
			if(oldmsg==null || !msg.equals(oldmsg))
				progressBarLabel2.setContents(msg);
			if (max != null)
				pb2_max = max;;
			if (val > 0)
			{
				int percent = (pb2_val = val) * 100 / pb2_max;
				if (progressBar2.getPercentDone() != percent)
					progressBar2.setPercentDone(percent);
			}
			if (val == 0)
				startTime2 = System.currentTimeMillis();
			if (val > 0)
			{
				pb2_val = val;
				final String left = toHMS(((System.currentTimeMillis() - startTime2) * (pb2_max - pb2_val) / pb2_val) / 1000);
				final String total = toHMS(((System.currentTimeMillis() - startTime2) * pb2_max / pb2_val) / 1000);
				lblTimeLeft2.setContents("<code>" + left + "/" + total + "</code>"); //$NON-NLS-1$
			}
			else
				lblTimeLeft2.setContents("<code>--:--:--/--:--:--</code>"); //$NON-NLS-1$
		}
		else if (progressBar2.isVisible())
		{
			progressBar2.setVisibility(Visibility.HIDDEN);
			lblTimeLeft2.setVisibility(Visibility.HIDDEN);
			packHeight();
		}
	}

	private void packHeight()
	{
//		markForRedraw();
	}
	
	public int getValue()
	{
		return pb_val;
	}

	public int getValue2()
	{
		return pb2_val;
	}
	
	private String toHMS(long sec_num)
	{
		long hours   = (long)Math.floor(sec_num / 3600.0);
		long minutes = (long)Math.floor((sec_num - (hours * 3600.0)) / 60.0);
		long seconds = sec_num - (hours * 3600) - (minutes * 60);
		return NumberUtil.format(hours,"00")+':'+NumberUtil.format(minutes,"00")+':'+NumberUtil.format(seconds,"00");
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
