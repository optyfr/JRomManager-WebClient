package jrm.webui.client.protocol;

public class Q_Profile extends Q_
{
	protected Q_Profile()
	{
		super();
	}
	
	public static class Load extends Q_
	{
		protected Load()
		{
			super();
		}
		
		final public static Load instantiate()
		{
			return Q_.instantiateCmd("Profile.load").cast();
		}
		
		final public Load setPath(String path)
		{
			getParams().set("path",path);
			return this;
		}
	}
}
