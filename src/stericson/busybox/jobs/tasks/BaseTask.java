package stericson.busybox.jobs.tasks;

import stericson.busybox.Support.CommandResult;
import stericson.busybox.Support.ShellCommand;
import stericson.busybox.interfaces.CommandCallback;

/**
 * Created by Stephen Erickson on 7/10/13.
 */
public class BaseTask implements CommandCallback {

    protected ShellCommand command = null;

    @Override
    public void commandCallback(CommandResult result) {

    }

    @Override
    public void commandOutput(int id, String line) {

    }
}
