package jrm.webui.client.protocol;

public class Q_NPlayers extends Q_	//NOSONAR
{
	protected Q_NPlayers()
	{
		super();
	}
	
	public static class Load extends Q_
	{
		protected Load()
		{
			super();
		}
		
		public static final Load instantiate()
		{
			return Q_.instantiateCmd("NPlayers.load").cast();
		}
		
		public final Load setPath(String path)
		{
			getParams().set("path",path);
			return this;
		}
	}
}
