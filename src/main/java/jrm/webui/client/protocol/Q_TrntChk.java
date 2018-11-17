package jrm.webui.client.protocol;

public class Q_TrntChk extends Q_
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
		
		final public static Start instantiate()
		{
			return Q_.instantiateCmd("TrntChk.start").cast();
		}
	}
}
