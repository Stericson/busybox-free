package stericson.busybox.Support;

import com.stericson.RootShell.execution.Command;

import stericson.busybox.interfaces.CommandCallback;

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

        if(this.cb != null)
        {
            cb.commandOutput(i, s);
        }

        super.commandOutput(i, s);
    }

    @Override
    public void commandTerminated(int i, String s) {

        if(this.cb != null)
        {
            CommandResult r = new CommandResult();
            r.setSuccess(false);
            r.setError(s);
            r.setId(i);

            cb.commandCallback(r);
        }
    }

    @Override
    public void commandCompleted(int i, int i2) {

        if(this.cb != null)
        {
            CommandResult r = new CommandResult();
            r.setSuccess(true);
            r.setExitCode(i2);
            r.setId(i);

            cb.commandCallback(r);
        }
    }

    //pauser
    public void pause() {
        while (!isFinished()) {
            synchronized (this) {
                try {
                    while(!this.isFinished())
                    {
                        this.wait(1000);
                    }
                } catch (Exception e) {}
            }
        }
    }
}
