package stericson.busybox.interfaces;


import stericson.busybox.support.CommandResult;

public interface CommandCallback {

    public void commandCallback(CommandResult result);

    public void commandOutput(int id, String line);
}
