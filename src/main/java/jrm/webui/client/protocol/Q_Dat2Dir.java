package jrm.webui.client.protocol;

public class Q_Dat2Dir extends Q_
{
	protected Q_Dat2Dir()
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
			return Q_.instantiateCmd("Dat2Dir.start").cast();
		}
	}
}
