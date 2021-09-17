package jrm.webui.client;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.Timer;
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
	private static A_Session session = null;


	private static MainWindow mainWindow = null;
	private static Set<Window> childWindows = new HashSet<>();
	
	private static Timer lprTimer;

	public Client()
	{
		super();
	}
	
	private class EatSleepRaveRepeat implements RepeatingCommand
	{
		final String msg;
		
		private EatSleepRaveRepeat(String msg)
		{
			this.msg = msg;
		}

		@Override
		public boolean execute()
		{
			execute(new A_(JsonUtils.safeEval(msg)));
			return false;
		}
		
		private void execute(A_ a)
		{
			switch(a.getCmd())
			{
				case "Progress":
					mainWindow.update(new A_Progress(a));
					break;
				case "Progress.close":
					mainWindow.update(new A_Progress.Close(a));
					break;
				case "Progress.canCancel":
					mainWindow.update(new A_Progress.CanCancel(a));
					break;
				case "Progress.setInfos":
					mainWindow.update(new A_Progress.SetInfos(a));
					break;
				case "Progress.clearInfos":
					mainWindow.update(new A_Progress.ClearInfos(a));
					break;
				case "Progress.setFullProgress":
					mainWindow.update(new A_Progress.SetFullProgress(a));
					break;
				case "Profile.loaded":
					mainWindow.update(new A_Profile.Loaded(a));
					break;
				case "Profile.scanned":
					mainWindow.update(new A_Profile.Scanned(a));
					break;
				case "Profile.fixed":
					mainWindow.update(new A_Profile.Fixed(a));
					break;
				case "Profile.imported":
					mainWindow.update(new A_Profile.Imported(a));
					break;
				case "CatVer.loaded":
					mainWindow.update(new A_CatVer.Loaded(a));
					break;
				case "NPlayers.loaded":
					mainWindow.update(new A_NPlayers.Loaded(a));
					break;
				case "Report.applyFilters":
					mainWindow.update(new A_Report.ApplyFilter(a));
					break;
				case "ReportLite.applyFilters":
					mainWindow.update(new A_ReportLite.ApplyFilter(a));
					break;
				case "Dat2Dir.clearResults":
					mainWindow.update(new A_Dat2Dir.ClearResults(a));
					break;
				case "Dat2Dir.updateResult":
					mainWindow.update(new A_Dat2Dir.UpdateResult(a));
					break;
				case "Dat2Dir.end":
					mainWindow.update(new A_Dat2Dir.End(a));
					break;
				case "Dat2Dir.showSettings":
					mainWindow.update(new A_Dat2Dir.ShowSettings(a));
					break;
				case "TrntChk.clearResults":
					mainWindow.update(new A_TrntChk.ClearResults(a));
					break;
				case "TrntChk.updateResult":
					mainWindow.update(new A_TrntChk.UpdateResult(a));
					break;
				case "TrntChk.end":
					mainWindow.update(new A_TrntChk.End(a));
					break;
				case "Compressor.clearResults":
					mainWindow.update(new A_Compressor.ClearResults(a));
					break;
				case "Compressor.updateResult":
					mainWindow.update(new A_Compressor.UpdateResult(a));
					break;
				case "Compressor.updateFile":
					mainWindow.update(new A_Compressor.UpdateFile(a));
					break;
				case "Compressor.end":
					mainWindow.update(new A_Compressor.End(a));
					break;
				case "Global.setMemory":
					mainWindow.update(new A_Global.SetMemory(a));
					break;
				case "Global.updateProperty":
					new A_Global.UpdateProperty(a).getProperties().forEach((k, v) -> session.setSetting(k, v));
					break;
				case "Global.warn":
					SC.warn(new A_Global.Warn(a).getMsg());
					break;
				case "Global.multiCMD":
					for(final var sa : new A_Global.MultiCMD(a).getSubCMDs())
						execute(sa);
					break;
				default:
					break;
			}
		}
		
	}

	private void processCmd(String msg)
	{
		if(msg==null || msg.trim().length()==0)
			return;
		try
		{
			Scheduler.get().scheduleIncremental(new EatSleepRaveRepeat(msg));
		}
		catch(Exception e)
		{
			//Do nothing
		}
	}
	
	private void lpr(boolean init)
	{
		updateMainWindow();
		RPCRequest request = new RPCRequest();
		request.setActionURL(init?"/actions/init":"/actions/lpr");
		request.setContentType("application/json");
		request.setUseSimpleHttp(true); // obligatoire car sinon on s'adresse à un serveur rpc smartclient, et ce n'est pas le cas ici
		request.setHttpMethod("GET"); // on simplifie le plus possible la requête (POST est plus complexe pour le protocole HTTP et nécessite 2 aller-retour)
		request.setWillHandleError(true); // on gère les status d'erreur nous même
		RPCManager.sendRequest(request, (response, rawData, req) -> {
			if (response.getHttpResponseCode() == 200)
			{
				processCmd(rawData.toString());
				lprTimer.schedule(125);
			}
			else if (response.getHttpResponseCode() == 401) // Session lost => reload page
				com.google.gwt.user.client.Window.Location.reload();
			else
				lprTimer.schedule(500);
		});
	}
	
	public static void sendMsg(String msg)
	{
		RPCRequest request = new RPCRequest();
		request.setActionURL("/actions/cmd");
		SC.logWarn(request.getActionURL());
		request.setContentType("application/json");
		request.setUseSimpleHttp(true); // obligatoire car sinon on s'adresse à un serveur rpc smartclient, et ce n'est pas le cas ici
		request.setHttpMethod("POST"); // on simplifie le plus possible la requête (POST est plus complexe pour le protocole HTTP et nécessite 2 aller-retour)
		request.setData(msg);
		RPCManager.sendRequest(request);
	}
	
	@Override
	public void onModuleLoad()
	{
		
		final var title = new Label();
		title.setWidth100();
		title.setAlign(Alignment.CENTER);
		title.setContents("<span style='font:bold 24px arial'>JRomManager</span>");
		title.draw();
		Page.setTitle("JRomManager");
		
		final var rpcreq = new RPCRequest();
		rpcreq.setActionURL("/session");
		RPCManager.sendRequest(
			rpcreq,
			(RPCResponse response, Object rawData, RPCRequest request)->
				{
					if(response.getHttpResponseCode() == 200)
					{
						setSession(JsonUtils.safeEval(rawData.toString()));
						setLprTimer(new Timer()
						{
							boolean init = true;
							
							@Override
							public void run()
							{
								lpr(init);
								init = false;
							}
						}).schedule(1);
					}
				}
		);
	}

	private static synchronized void setSession(A_Session session)
	{
		Client.session = session;
	}
	
	public static synchronized A_Session getSession()
	{
		return session;
	}

	private static synchronized void updateMainWindow()
	{
		if (Client.mainWindow == null)
			Client.mainWindow = new MainWindow();
	}
	
	public static synchronized MainWindow getMainWindow()
	{
		return mainWindow;
	}

	public static Set<Window> getChildWindows()
	{
		return childWindows;
	}

	private static synchronized Timer setLprTimer(Timer lprTimer)
	{
		Client.lprTimer = lprTimer;
		return Client.lprTimer;
	}
}
