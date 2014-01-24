package stericson.busybox.Support;

/**
 * Created by Stephen Erickson on 7/9/13.
 */
public class CommandResult {

    int id = 0;
    int exitCode = -1;
    boolean success = true;
    String error = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
