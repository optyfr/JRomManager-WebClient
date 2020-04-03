package jrm.webui.client.utils;


public class CaseInsensitiveString
{
	private String dataString;

	public CaseInsensitiveString(String dataString)
	{
		this.dataString = dataString;
	}

	public String getDataString()
	{
		return this.dataString;
	}

	public void setDataString(String dataString)
	{
		this.dataString = dataString;
	}

	@Override
	public int hashCode()
	{
		return dataString == null?0:dataString.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof CaseInsensitiveString)) return false;
		CaseInsensitiveString is = (CaseInsensitiveString)obj;
		if(is.dataString == null) return this.dataString == null;
		return is.dataString.equalsIgnoreCase(this.dataString);
	}

	public boolean startsWith(String prefix)
	{
		if(dataString == null || prefix == null)
			return (dataString == null && prefix == null);
		if(prefix.length() > dataString.length())
			return false;
		return dataString.regionMatches(true, 0, prefix, 0, prefix.length());
	}
	
	public boolean endsWith(String suffix)
	{
		if(dataString == null || suffix == null)
			return (dataString == null && suffix == null);
		if(suffix.length() > dataString.length())
			return false;
		return dataString.regionMatches(true, dataString.length() - suffix.length(), suffix, 0, suffix.length());
	}
	
	@Override
	public String toString()
	{
		return dataString;
	}
}