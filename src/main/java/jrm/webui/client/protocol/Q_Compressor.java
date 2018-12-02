package jrm.webui.client.protocol;

public class Q_Compressor extends Q_
{
	protected Q_Compressor()
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
			return Q_.instantiateCmd("Compressor.start").cast();
		}
	}
}
