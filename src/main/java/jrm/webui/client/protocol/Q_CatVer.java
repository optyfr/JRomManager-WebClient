package jrm.webui.client.protocol;

public class Q_CatVer extends Q_
{
	protected Q_CatVer()
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
			return Q_.instantiateCmd("CatVer.load").cast();
		}
		
		final public Load setPath(String path)
		{
			getParams().set("path",path);
			return this;
		}
	}
}
