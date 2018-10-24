package jrm.webui.client.protocol;

public class Q_Progress extends Q_
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
		
		final public static Cancel instantiate()
		{
			return Q_.instantiateCmd("Progress.cancel").cast();
		}
	}
}
