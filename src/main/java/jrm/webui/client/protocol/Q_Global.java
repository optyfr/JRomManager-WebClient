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
}
