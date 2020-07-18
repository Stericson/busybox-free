package stericson.busybox.jobs.tasks;

import stericson.busybox.support.CommandResult;
import stericson.busybox.support.ShellCommand;
import stericson.busybox.interfaces.CommandCallback;

public class BaseTask implements CommandCallback {

    protected ShellCommand command = null;

    @Override
    public void commandCallback(CommandResult result) {

    }

    @Override
    public void commandOutput(int id, String line) {

    }
}
