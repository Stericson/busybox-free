package stericson.busybox.domain;

public class JobResult<E>
{	
	private E obj = null;
	private String error = null;
	private Exception exception = null;
	
	public E getObj()
    {
    	return obj;
    }
	public void setObj(E obj)
    {
    	this.obj = obj;
    }
	public String getError()
    {
    	return error;
    }
	public void setError(String error)
    {
    	this.error = error;
    }
	public void setException(Exception exception)
    {
	    this.exception = exception;
    }
	public Exception getException()
    {
	    return exception;
    }
}
