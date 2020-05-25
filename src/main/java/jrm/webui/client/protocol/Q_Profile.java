package jrm.webui.client.protocol;

public class Q_Profile extends Q_
{
	protected Q_Profile()
	{
		super();
	}
	
	public static class Import extends Q_
	{
		protected Import()
		{
			super();
		}
		
		final public static Import instantiate()
		{
			return Q_.instantiateCmd("Profile.import").cast();
		}
		
		final public Import setSL(boolean sl)
		{
			getParams().set("sl", sl);
			return this;
		}
		
		final public Import setParent(String parent)
		{
			getParams().set("parent", parent);
			return this;
		}
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
	
	public static class ImportSettings extends Q_
	{
		protected ImportSettings()
		{
			super();
		}
		
		final public static ImportSettings instantiate()
		{
			return Q_.instantiateCmd("Profile.importSettings").cast();
		}
		
		final public ImportSettings setPath(String path)
		{
			getParams().set("path",path);
			return this;
		}
	}
	
	public static class ExportSettings extends Q_
	{
		protected ExportSettings()
		{
			super();
		}
		
		final public static ExportSettings instantiate()
		{
			return Q_.instantiateCmd("Profile.exportSettings").cast();
		}

		final public ExportSettings setPath(String path)
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
