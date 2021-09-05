package jrm.webui.client.protocol;

public class Q_Progress extends Q_	//NOSONAR
{
	protected Q_Progress()
	{
		super();
	}
	
	public static class Cancel extends Q_
	{
		protected Cancel()
		{
			super();
		}
		
		public static final Cancel instantiate()
		{
			return Q_.instantiateCmd("Progress.cancel").cast();
		}
	}
}
