package jrm.webui.client.protocol;

public class Q_Global extends Q_	//NOSONAR
{
	protected Q_Global()
	{
		super();
	}

	public static class SetProperty extends Q_
	{
		protected SetProperty()
		{
			super();
		}
		
		
		public static final SetProperty instantiate()
		{
			return Q_.instantiateCmd("Global.setProperty").cast();
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
	
	public static class GC extends Q_
	{
		protected GC()
		{
			super();
		}

		public static final GC instantiate()
		{
			return Q_.instantiateCmd("Global.GC").cast();
		}
		
	}
	
	public static class GetMemory extends Q_
	{
		protected GetMemory()
		{
			super();
		}

		public static final GetMemory instantiate()
		{
			return Q_.instantiateCmd("Global.getMemory").cast();
		}
		
	}
}
