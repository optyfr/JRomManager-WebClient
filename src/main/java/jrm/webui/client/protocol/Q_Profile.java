package jrm.webui.client.protocol;

public class Q_Profile extends Q_	//NOSONAR
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
		
		public static final Import instantiate()
		{
			return Q_.instantiateCmd("Profile.import").cast();
		}
		
		public final Import setSL(boolean sl)
		{
			getParams().set("sl", sl);
			return this;
		}
		
		public final Import setParent(String parent)
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
		
		public static final Load instantiate()
		{
			return Q_.instantiateCmd("Profile.load").cast();
		}
		
		public final Load setPath(String parent, String file)
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
		
		public static final Scan instantiate()
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
		
		public static final Fix instantiate()
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
		
		public static final ImportSettings instantiate()
		{
			return Q_.instantiateCmd("Profile.importSettings").cast();
		}
		
		public final ImportSettings setPath(String path)
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
		
		public static final ExportSettings instantiate()
		{
			return Q_.instantiateCmd("Profile.exportSettings").cast();
		}

		public final ExportSettings setPath(String path)
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
		
		
		public static final SetProperty instantiate()
		{
			return Q_.instantiateCmd("Profile.setProperty").cast();
		}
		
		public final SetProperty setProfile(String name)
		{
			set("profile", name);
			return this;
		}

		public final SetProperty setProperty(String name, Object value)
		{
			if(value instanceof Boolean)
				return setProperty(name, (boolean)value);
			else if(value instanceof Integer)
				return setProperty(name, (int)value);
			else
				return setProperty(name, value.toString());
		}

		public final SetProperty setProperty(String name, String value)
		{
			getParams().set(name, value);
			return this;
		}
		
		public final SetProperty setProperty(String name, int value)
		{
			getParams().set(name, value);
			return this;
		}
		
		public final SetProperty setProperty(String name, boolean value)
		{
			getParams().set(name, value);
			return this;
		}
	}
}
