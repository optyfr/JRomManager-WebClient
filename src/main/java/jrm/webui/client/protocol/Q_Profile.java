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
	
	public static class SetProperty extends Q_
	{
		protected SetProperty()
		{
			super();
		}
		
		
		final public static SetProperty instantiate()
		{
			return Q_.instantiateCmd("Profile.setProperty").cast();
		}
		
		final public SetProperty setProperty(String name, String value)
		{
			getParams().set(name, value);
			return this;
		}
		
		final public SetProperty setProperty(String name, int value)
		{
			getParams().set(name, value);
			return this;
		}
		
		final public SetProperty setProperty(String name, boolean value)
		{
			getParams().set(name, value);
			return this;
		}
	}
}
