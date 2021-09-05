package jrm.webui.client.protocol;

public class Q_TrntChk extends Q_	//NOSONAR
{
	protected Q_TrntChk()
	{
		super();
	}
	
	public static class Start extends Q_
	{
		protected Start()
		{
			super();
		}
		
		public static final Start instantiate()
		{
			return Q_.instantiateCmd("TrntChk.start").cast();
		}
	}
}
