package stericson.busybox.interfaces;


import stericson.busybox.Support.CommandResult;

/**
 * Created by Stephen Erickson on 7/9/13.
 */
public interface CommandCallback {

    public void commandCallback(CommandResult result);

    public void commandOutput(int id, String line);
}
