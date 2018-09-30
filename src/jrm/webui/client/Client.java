package jrm.webui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
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

public class Client implements EntryPoint
{
	private static String session;

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
		RPCManager.sendRequest(new RPCRequest()
		{
			{
				setActionURL("/session");
			}
		}, new RPCCallback()
		{
			@Override
			public void execute(RPCResponse response, Object rawData, RPCRequest request)
			{
				if (response.getHttpResponseCode() == 200)
				{
					session = rawData.toString();
					SC.logWarn("rawData: " + rawData.toString());
					Websocket socket = new Websocket("ws://localhost:8080");
					socket.addListener(new WebsocketListener() {
					    @Override
					    public void onMessage(String msg) {
					    	SC.logWarn(msg);
					    }

					    @Override
					    public void onOpen() {
					       socket.send("coucou");
					    }

						@Override
						public void onClose()
						{
						}
					});
					socket.open();
					new MainWindow();
					new Timer()
					{
						@Override
						public void run()
						{
							socket.send(session);
						}
					}.scheduleRepeating(10000);
				}
			}
		});
	}

}
