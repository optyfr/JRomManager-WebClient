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

import jrm.webui.client.protocol.*;
import jrm.webui.client.ui.MainWindow;

public class Client implements EntryPoint
{
	public static A_Session session;
	public static Websocket socket;
	public static MainWindow mainwindow;
	public static HashSet<Window> childWindows = new HashSet<>();

	public Client()
	{
		super();
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
								@Override
								public void onMessage(String msg)
								{
									try
									{
										A_ a = new A_(JsonUtils.safeEval(msg));
										switch(a.getCmd())
										{
											case "Progress":
												mainwindow.update(new A_Progress(a));
												break;
											case "Progress.close":
												mainwindow.update(new A_Progress.Close(a));
												break;
											case "Progress.setInfos":
												mainwindow.update(new A_Progress.SetInfos(a));
												break;
											case "Progress.clearInfos":
												mainwindow.update(new A_Progress.ClearInfos(a));
												break;
											case "Progress.setProgress":
												mainwindow.update(new A_Progress.SetProgress(a));
												break;
											case "Progress.setProgress2":
												mainwindow.update(new A_Progress.SetProgress2(a));
												break;
											case "Profile.loaded":
												mainwindow.update(new A_Profile.Loaded(a));
												break;
											case "Profile.scanned":
												mainwindow.update(new A_Profile.Scanned(a));
												break;
											case "CatVer.loaded":
												mainwindow.update(new A_CatVer.Loaded(a));
												break;
											case "NPlayers.loaded":
												mainwindow.update(new A_NPlayers.Loaded(a));
												break;
										}
									}
									catch(Exception e)
									{
										//SC.logWarn(e.getMessage());
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
