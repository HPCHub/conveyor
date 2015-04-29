/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.command.Command;
import ru.niifhm.bioinformatics.jsub.command.QueueCommand;


/**
 * @author zeleniy
 */
public class JsubTaskProcessor <T> extends FilesProcessor<JsubTask> {


    private final static Log _logger = LogFactory.getLog(JsubTaskProcessor.class);


    /**
     * @param task
     */
    public JsubTaskProcessor(JsubTask task) {

        super(task);
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.conveyor.activemq.Processor#process()
     */
    @Override
    public void process() {

        try {

            Config config = Config.getInstance();
            for (Map.Entry<String, String> option : _task.getOptions().entrySet()) {
                config.set(option.getKey(), option.getValue());
            }

            Jsub.getInstance();
            Pipeline project = Pipeline.newInstance(
                _task.getName(), _task.getTag(), _task.getType(), _task.getDirectory()
            );

            Command.factory(Command.COMMAND_CREATE).executeCommand();

            Properties propertiesFile = new Properties();
            File file = new File(project.getBuildPropertiesFilepath());
            propertiesFile.load(new FileInputStream(file));

            Map<String, String> properties = _task.getProperties();
            for (Object key : propertiesFile.keySet()) {
                String property = (String) key;
                if (properties.containsKey(property)) {
                    propertiesFile.setProperty(property, properties.get(property));
                }
            }

            propertiesFile.store(new FileOutputStream(file), null);

            QueueCommand queueCommand = (QueueCommand) Command.factory(Command.COMMAND_QUEUE);
//            queueCommand.setWait(true);
            queueCommand.executeCommand();
            _logger.info(String.format("Pipeline \"%s\" executed", project.getLayout().getBuildDirectory().getPath()));

        } catch (Exception e) {
            _logger.error(String.format(
                "Cannot run jsub for processing \"%s\" [%s] %s",
                _task.getType(),
                e.getClass().getName(),
                e.getMessage()
            ));
        }
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.conveyor.activemq.FilesProcessor#getBlockMode()
     */
    @Override
    public long getBlockMode() {

        return FilesProcessor.BLOCK_READ;
    }
}