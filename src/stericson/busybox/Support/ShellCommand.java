package stericson.busybox.Support;

import com.stericson.RootTools.execution.Command;

import stericson.busybox.interfaces.CommandCallback;

/**
 * Created by Stephen Erickson on 7/9/13.
 */
public class ShellCommand extends Command {

    CommandCallback cb = null;

    public ShellCommand(CommandCallback cb, int id, String... command) {
        super(id, command);

        this.cb = cb;
    }

    public ShellCommand(CommandCallback cb, int id, boolean handlerEnabled, String... command) {
        super(id, handlerEnabled, command);

        this.cb = cb;
    }

    public ShellCommand(CommandCallback cb, int id, int timeout, String... command) {
        super(id, timeout, command);

        this.cb = cb;
    }

    @Override
    public void commandOutput(int i, String s) {
        cb.commandOutput(i, s);
    }

    @Override
    public void commandTerminated(int i, String s) {
        CommandResult r = new CommandResult();
        r.setSuccess(false);
        r.setError(s);
        r.setId(i);

        cb.commandCallback(r);
    }

    @Override
    public void commandCompleted(int i, int i2) {
        CommandResult r = new CommandResult();
        r.setSuccess(true);
        r.setExitCode(i2);
        r.setId(i);

        cb.commandCallback(r);
    }

    //pauser
    public void pause() {
        while (!isFinished()) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (Exception e) {}
            }
        }
    }
}
