package jrm.webui.client;

import java.util.HashSet;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
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

public class Client implements EntryPoint
{
	public static String session;
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
						session = rawData.toString();
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
										JSONObject jso = JSONParser.parseStrict(msg).isObject();
										if(jso!= null && jso.containsKey("cmd"))
										{
											JSONObject params = jso.containsKey("params")?jso.get("params").isObject():null;
											SC.logWarn(jso.get("cmd").isString().stringValue());
											switch(jso.get("cmd").isString().stringValue())
											{
												case "Progress":
													progress = new Progress();
													break;
												case "Progress.setInfos":
													progress.setInfos(
														(int)params.get("threadCnt").isNumber().doubleValue(),
														params.get("multipleSubInfos").isBoolean().booleanValue()
													);
													break;
												case "Progress.clearInfos":
													progress.clearInfos();
													break;
												case "Progress.setProgress":
													progress.setProgress(
														(int)params.get("offset").isNumber().doubleValue(),
														params.get("msg").isString().stringValue(),
														params.get("val").isNull()!=null?null:((int)params.get("val").isNumber().doubleValue()),
														params.get("max").isNull()!=null?null:((int)params.get("max").isNumber().doubleValue()),
														params.get("submsg").isString().stringValue()
													);
													break;
												case "Progress.setProgress2":
													progress.setProgress2(
														params.get("msg").isString().stringValue(),
														params.get("val").isNull()!=null?null:((int)params.get("val").isNumber().doubleValue()),
														params.get("max").isNull()!=null?null:((int)params.get("max").isNumber().doubleValue())
													);
													break;
											}
										}
									}
									catch(Exception e)
									{
										e.printStackTrace();
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
