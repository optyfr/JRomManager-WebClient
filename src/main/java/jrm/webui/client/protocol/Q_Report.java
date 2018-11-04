package jrm.webui.client.protocol;

public class Q_Report extends Q_
{
	protected Q_Report()
	{
		super();
	}
	
	public static class SetFilter extends Q_
	{
		protected SetFilter()
		{
			super();
		}
		
		
		final public static SetFilter instantiate(boolean lite)
		{
			return Q_.instantiateCmd(lite?"ReportLite.setFilter":"Report.setFilter").cast();
		}
		
		final public SetFilter setFilter(String name, boolean value)
		{
			getParams().set(name, value);
			return this;
		}
	}
}
