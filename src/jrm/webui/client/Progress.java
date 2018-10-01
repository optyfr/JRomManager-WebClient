package jrm.webui.client;

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.NumberUtil;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Progressbar;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class Progress extends Window
{
	private VLayout panel = new VLayout();

	/** The lbl info. */
	private Label[] lblInfo;
	
	/** The lbl sub info. */
	private Label[] lblSubInfo;
	
	/** The thread id offset. */
	private Map<Long,Integer> threadId_Offset = new HashMap<>();

	/** The progress bar. */
	private final Progressbar progressBar;
	
	/** The cancel. */
	private boolean cancel = false;

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
	
	/** The lbl time left 2. */
	private final Label lblTimeLeft2;
	
	public Progress(Window parent)
	{
		super();
		setIsModal(true);
		setShowModalMask(true);
		setAutoHeight();
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
		
		addItem(panel);
		setInfos(1, false);

		progressBar = new Progressbar() {{setWidth100();}};
		lblTimeleft = new Label("--:--:--");

		addItem(new HLayout() {{
			addMembers(
				progressBar,
				lblTimeleft
			);
		}});

		progressBar2 = new Progressbar() {{setWidth100();}};
		lblTimeLeft2 = new Label("--:--:--");

		addItem(new HLayout() {{
			addMembers(
				progressBar2,
				lblTimeLeft2
			);
		}});
		
		addItem(btnCancel = new IButton("Cancel") {{setLayoutAlign(Alignment.CENTER);}});
		centerInPage();
		show();
	}

	public void setInfos(int threadCnt, boolean multipleSubInfos)
	{
		panel.removeMembers(panel.getMembers());
		
		lblInfo = new Label[threadCnt];
		lblSubInfo = new Label[multipleSubInfos?threadCnt:1];
		threadId_Offset.clear();
		
		for(int i = 0; i < threadCnt; i++)
		{
			panel.addMember(lblInfo[i] = new Label() {{setWidth100();}});
	
			if(multipleSubInfos)
			{
				panel.addMember(lblSubInfo[i] = new Label() {{setWidth100();}});
			}
		}
		if(!multipleSubInfos)
		{
			panel.addMember(lblSubInfo[0] = new Label() {{setWidth100();}});
		}
	}
	
	public void clearInfos()
	{
		for(Label label : lblInfo)
			label.setTitle(null);
		for(Label label : lblSubInfo)
			label.setTitle(null);
	}
	
	private int pb_val, pb_max;
	
	public void setProgress(final int offset, final String msg, final Integer val, final Integer max, final String submsg)
	{
		
		if (msg != null)
			lblInfo[offset].setTitle(msg);
		if (val != null)
		{
			if (val < 0 && progressBar.isVisible())
			{
				progressBar.setVisible(false);
				lblTimeleft.setVisible(false);
//				packHeight();
			}
			else if (val > 0 && !progressBar.isVisible())
			{
				progressBar.setVisible(true);
				lblTimeleft.setVisible(true);
//				packHeight();
			}
//			progressBar.setStringPainted(true);
			if (max != null)
				pb_max = max;
			if (val > 0)
				progressBar.setPercentDone((pb_val=val)*100/pb_max);
			if (val == 0)
				startTime = System.currentTimeMillis();
			if (val > 0)
			{
				pb_val = val;
				final String left = toHMS((System.currentTimeMillis() - startTime) * (pb_max - pb_val) / pb_val); //$NON-NLS-1$
				final String total = toHMS((System.currentTimeMillis() - startTime) * pb_max / pb_val); //$NON-NLS-1$
				lblTimeleft.setTitle(left +" / "+ total); //$NON-NLS-1$
			}
			else
				lblTimeleft.setTitle("--:--:-- / --:--:--"); //$NON-NLS-1$
		}
		if(lblSubInfo.length==1)
			lblSubInfo[0].setTitle(submsg);
		else
			lblSubInfo[offset].setTitle(submsg);
	}

	public boolean isCancel()
	{
		return cancel;
	}

	public void cancel()
	{
		cancel = true;
		btnCancel.setDisabled(true);
		btnCancel.setTitle("Canceling"); //$NON-NLS-1$
	}

	private int pb2_val, pb2_max;

	public void setProgress2(final String msg, final Integer val, final Integer max)
	{
		if (msg != null && val != null)
		{
			if (!progressBar2.isVisible())
			{
				progressBar2.setVisible(true);
				lblTimeLeft2.setVisible(true);
//				packHeight();
			}
//			progressBar2.setStringPainted(true);
			progressBar2.setTitle(msg);
			if (max != null)
				pb2_max = max;;
			if (val > 0)
				progressBar2.setPercentDone((pb2_val=val)/pb2_max);
			if (val == 0)
				startTime2 = System.currentTimeMillis();
			if (val > 0)
			{
				pb2_val = val;
				final String left = toHMS((System.currentTimeMillis() - startTime2) * (pb2_max - pb2_val) / pb2_val); //$NON-NLS-1$
				final String total = toHMS((System.currentTimeMillis() - startTime2) * pb2_max / pb2_val); //$NON-NLS-1$
				lblTimeLeft2.setTitle(left + " / " + total); //$NON-NLS-1$
			}
			else
				lblTimeLeft2.setTitle("--:--:-- / --:--:--"); //$NON-NLS-1$
		}
		else if (progressBar2.isVisible())
		{
			progressBar2.setVisible(false);
			lblTimeLeft2.setVisible(false);
//			packHeight();
		}
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
		long hours   = (long)Math.floor(sec_num / 3600);
		long minutes = (long)Math.floor((sec_num - (hours * 3600)) / 60);
		long seconds = sec_num - (hours * 3600) - (minutes * 60);
		return NumberUtil.format(hours,"00")+':'+NumberUtil.format(minutes,"00")+':'+NumberUtil.format(seconds,"00");
	}
}
