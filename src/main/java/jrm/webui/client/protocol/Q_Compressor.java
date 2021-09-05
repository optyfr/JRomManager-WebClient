package jrm.webui.client.protocol;
//NOSONAR
public class Q_Compressor extends Q_	//NOSONAR
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
		
		public static final Start instantiate()
		{
			return Q_.instantiateCmd("Compressor.start").cast();
		}
	}
}
