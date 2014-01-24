package stericson.busybox.domain;

import java.util.ArrayList;
import java.util.List;

public class Result
{
	private boolean success = false;
	private String error;
	private List<Item> itemList = new ArrayList<Item>();
	private String message;
	private float space = -1;
	private String[] locations;
	
	public void setItemList(List<Item> itemList)
	{
		this.itemList = itemList;
	}
	
	public void setSuccess(boolean success)
	{
		this.success = success;
	}

	public boolean isSuccess()
	{
		return success;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	public String getError()
	{
		return error;
	}
	
	public List<Item> getItemList()
	{
		return this.itemList;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public float getSpace()
	{
		return this.space;
	}
	
	public void setSpace(float space)
	{
		this.space = space;
	}

	public String[] getLocations()
	{
		return this.locations;
	}
	
	public void setLocations(String[] locations)
	{
		this.locations = locations;
	}
}
