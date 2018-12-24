package jrm.webui.client.protocol;

public class Q_Global extends Q_
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
		
		
		final public static SetProperty instantiate()
		{
			return Q_.instantiateCmd("Global.setProperty").cast();
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
	
	public static class GC extends Q_
	{
		protected GC()
		{
			super();
		}

		final public static GC instantiate()
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

		final public static GetMemory instantiate()
		{
			return Q_.instantiateCmd("Global.getMemory").cast();
		}
		
	}
}
