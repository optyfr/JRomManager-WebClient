package jrm.webui.client.protocol;

import jrm.webui.client.utils.EnhJSO;

public class A_
{
	protected EnhJSO response;

	public A_(final EnhJSO response)
	{
		this.response = response;
	}

	public String getCmd() {
		return response.getString("cmd");
	}
}
