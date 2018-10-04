package jrm.webui.client;

import java.util.HashSet;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsonUtils;
import com.sksamuel.gwt.websockets.Websocket;
import com.sksamuel.gwt.websockets.WebsocketListener;
import com.smartgwt.client.rpc.RPCCallback;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;

import jrm.webui.client.protocol.A_;
import jrm.webui.client.protocol.A_Progress;
import jrm.webui.client.protocol.A_Session;

public class Client implements EntryPoint
{
	public static A_Session session;
	public static Websocket socket;
	public static MainWindow mainwindow;
	public static HashSet<Window> childWindows;

	public Client()
	{
		super();
		childWindows = new HashSet<>();
	}

	@Override
	public void onModuleLoad()
	{
		new Label()
		{
			{
				setWidth100();
				setAlign(Alignment.CENTER);
				setContents("<span style='font:bold 24px arial'>JRomManager</span>");
			}
		}.draw();
		Page.setTitle("JRomManager");
		RPCManager.sendRequest(
			new RPCRequest() {{
				setActionURL("/session");
			}},
			new RPCCallback()
			{
				@Override
				public void execute(RPCResponse response, Object rawData, RPCRequest request)
				{
					if(response.getHttpResponseCode() == 200)
					{
						session = JsonUtils.safeEval(rawData.toString());
						if(Websocket.isSupported())
						{
							socket = new Websocket("ws://"+com.google.gwt.user.client.Window.Location.getHost());
							socket.addListener(new WebsocketListener()
							{
								Progress progress;
								
								@Override
								public void onMessage(String msg)
								{
									try
									{
										A_ a = new A_(JsonUtils.safeEval(msg));
										switch(a.getCmd())
										{
											case "Progress":
												if(progress==null)
													progress = new Progress();
												else
													progress.show();
												break;
											case "Progress.close":
												progress.close();;
												break;
											case "Progress.setInfos":
											{
												A_Progress.SetInfos params = new A_Progress.SetInfos(a);
												progress.setInfos(params.getThreadCnt(), params.getMultipleSubInfos());
												break;
											}
											case "Progress.clearInfos":
												progress.clearInfos();
												break;
											case "Progress.setProgress":
											{
												A_Progress.SetProgress params = new A_Progress.SetProgress(a);
												progress.setProgress(params.getOffset(), params.getMsg(), params.getVal(), params.getMax(), params.getSubMsg());
												break;
											}
											case "Progress.setProgress2":
											{
												A_Progress.SetProgress2 params = new A_Progress.SetProgress2(a);
												progress.setProgress2(params.getMsg(), params.getVal(), params.getMax());
												break;
											}
										}
									}
									catch(Exception e)
									{
										SC.logWarn(e.getMessage());
									}
								}
	
								@Override
								public void onOpen()
								{
									mainwindow  = new MainWindow();
								}
	
								@Override
								public void onClose()
								{
									childWindows.forEach(Window::markForDestroy);
									mainwindow.markForDestroy();
									SC.say("Error", "Server closed connection");
								}
							});
							socket.open();
						}
						else
							SC.say("Error", "Your browser does not support websockets!");
					}
				}
			}
		);
	}

}
