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
		
		final public Load setPath(String parent, String file)
		{
			getParams().set("parent",parent);
			getParams().set("file",file);
			return this;
		}
	}
	
	public static class Scan extends Q_
	{
		protected Scan()
		{
			super();
		}
		
		final public static Scan instantiate()
		{
			return Q_.instantiateCmd("Profile.scan").cast();
		}
	}
	
	public static class Fix extends Q_
	{
		protected Fix()
		{
			super();
		}
		
		final public static Fix instantiate()
		{
			return Q_.instantiateCmd("Profile.fix").cast();
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
		
		final public SetProperty setProfile(String name)
		{
			set("profile", name);
			return this;
		}

		final public SetProperty setProperty(String name, Object value)
		{
			if(value instanceof Boolean)
				return setProperty(name, (boolean)value);
			else if(value instanceof Integer)
				return setProperty(name, (int)value);
			else
				return setProperty(name, value.toString());
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
