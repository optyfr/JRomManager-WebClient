package jrm.webui.client;

import java.util.HashSet;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
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
import com.smartgwt.client.widgets.Window;

import jrm.webui.client.protocol.A_;
import jrm.webui.client.protocol.A_CatVer;
import jrm.webui.client.protocol.A_Compressor;
import jrm.webui.client.protocol.A_Dat2Dir;
import jrm.webui.client.protocol.A_Global;
import jrm.webui.client.protocol.A_NPlayers;
import jrm.webui.client.protocol.A_Profile;
import jrm.webui.client.protocol.A_Progress;
import jrm.webui.client.protocol.A_Report;
import jrm.webui.client.protocol.A_ReportLite;
import jrm.webui.client.protocol.A_Session;
import jrm.webui.client.protocol.A_TrntChk;
import jrm.webui.client.ui.MainWindow;

public class Client implements EntryPoint
{
	public static A_Session session = null;
	public static MainWindow mainwindow = null;
	public static HashSet<Window> childWindows = new HashSet<>();
	
	private static Websocket socket = null;
	private static Timer lprTimer;

	public Client()
	{
		super();
	}

	private void processCmd(String msg)
	{
		try
		{
			Scheduler.get().scheduleIncremental(new RepeatingCommand()
			{
				@Override
				public boolean execute()
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
						case "Progress.canCancel":
							mainwindow.update(new A_Progress.CanCancel(a));
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
						case "Profile.imported":
							mainwindow.update(new A_Profile.Imported(a));
							break;
						case "CatVer.loaded":
							mainwindow.update(new A_CatVer.Loaded(a));
							break;
						case "NPlayers.loaded":
							mainwindow.update(new A_NPlayers.Loaded(a));
							break;
						case "Report.applyFilters":
							mainwindow.update(new A_Report.ApplyFilter(a));
							break;
						case "ReportLite.applyFilters":
							mainwindow.update(new A_ReportLite.ApplyFilter(a));
							break;
						case "Dat2Dir.clearResults":
							mainwindow.update(new A_Dat2Dir.ClearResults(a));
							break;
						case "Dat2Dir.updateResult":
							mainwindow.update(new A_Dat2Dir.UpdateResult(a));
							break;
						case "Dat2Dir.end":
							mainwindow.update(new A_Dat2Dir.End(a));
							break;
						case "Dat2Dir.showSettings":
							mainwindow.update(new A_Dat2Dir.ShowSettings(a));
							break;
						case "TrntChk.clearResults":
							mainwindow.update(new A_TrntChk.ClearResults(a));
							break;
						case "TrntChk.updateResult":
							mainwindow.update(new A_TrntChk.UpdateResult(a));
							break;
						case "TrntChk.end":
							mainwindow.update(new A_TrntChk.End(a));
							break;
						case "Compressor.clearResults":
							mainwindow.update(new A_Compressor.ClearResults(a));
							break;
						case "Compressor.updateResult":
							mainwindow.update(new A_Compressor.UpdateResult(a));
							break;
						case "Compressor.updateFile":
							mainwindow.update(new A_Compressor.UpdateFile(a));
							break;
						case "Compressor.end":
							mainwindow.update(new A_Compressor.End(a));
							break;
						case "Global.setMemory":
							mainwindow.update(new A_Global.SetMemory(a));
							break;
						case "Global.warn":
							SC.warn(new A_Global.Warn(a).getMsg());
							break;
					}
					return false;
				}
			});
		}
		catch(Exception e)
		{
			//SC.logWarn(e.getMessage());
		}
	}
	
	private void lpr()
	{
		if(mainwindow == null) mainwindow = new MainWindow();
		RPCRequest request = new RPCRequest();
		request.setActionURL("/actions/lpr");
		request.setContentType("application/json");
		request.setUseSimpleHttp(true); // obligatoire car sinon on s'adresse à un serveur rpc smartclient, et ce n'est pas le cas ici
		request.setHttpMethod("GET"); // on simplifie le plus possible la requête (POST est plus complexe pour le protocole HTTP et nécessite 2 aller-retour)
		request.setWillHandleError(true); // on gère les status d'erreur nous même
		RPCManager.sendRequest(request, new RPCCallback()
		{
			@Override
			public void execute(RPCResponse response, Object rawData, RPCRequest request)
			{
				if(response.getHttpResponseCode() == 200)
				{
					processCmd(rawData.toString());
					lprTimer.schedule(100);
				}
				else if(response.getHttpResponseCode() == 401)	// Session lost => reload page
					com.google.gwt.user.client.Window.Location.reload();
				else
					lprTimer.schedule(500);
			}
		});
	}
	
	public static void sendMsg(String msg)
	{
		if (socket != null)
			socket.send(msg);
	}
	
	private native boolean canWS() /*-{
		return $wnd.jrm_no_ws !== true;
	}-*/;
	
	
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
						if(Websocket.isSupported() && canWS())
						{
							socket = new Websocket("ws://"+com.google.gwt.user.client.Window.Location.getHost());
							socket.addListener(new WebsocketListener()
							{
								@Override
								public void onMessage(String msg)
								{
									processCmd(msg);
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
						{
							(lprTimer = new Timer()
							{
								@Override
								public void run()
								{
									lpr();
								}
							}).schedule(1);
						}
					}
				}
			}
		);
	}
}
