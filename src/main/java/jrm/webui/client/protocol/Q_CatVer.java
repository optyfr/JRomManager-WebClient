package jrm.webui.client.protocol;

public class Q_CatVer extends Q_	//NOSONAR
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
		
		public static final Load instantiate()
		{
			return Q_.instantiateCmd("CatVer.load").cast();
		}
		
		public final Load setPath(String path)
		{
			getParams().set("path",path);
			return this;
		}
	}
}
