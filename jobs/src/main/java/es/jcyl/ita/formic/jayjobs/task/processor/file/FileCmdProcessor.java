package es.jcyl.ita.formic.jayjobs.task.processor.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.processor.AbstractProcessor;
import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;

/**
 * Processor to add file operations as job tasks
 *
 * @author gustavo.rio@itacyl.es
 */

public class FileCmdProcessor extends AbstractProcessor implements NonIterProcessor {

    protected static final Log LOGGER = LogFactory.getLog(FileCmdProcessor.class);
    /**
     * Input file list
     */
    private List<String> commands;

    @Override
    public void process() throws TaskException {
        if (CollectionUtils.isEmpty(commands)) {
            LOGGER.info("No commands defined, task skipped");
            return;
        }

        String currentCommand = null;
        for (String cmdLine : commands) {
            try {
                currentCommand = cmdLine;
                FileCommand command = CommandParser.parseCommand(cmdLine);
                LOGGER.info("Executing command " + cmdLine);
                command.execute(new BasicContext("t1"));
            } catch (Exception e) {
                String msg = "An error occurred while executing command " + currentCommand;
                LOGGER.error(msg, e);
                if (isFailOnError()) {
                    throw new TaskException(msg, e);
                }
            }
        }
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
