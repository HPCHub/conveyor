/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.command.Command;
import ru.niifhm.bioinformatics.jsub.command.QueueCommand;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.util.RandomUtil;


/**
 * @author zeleniy
 */
public class ReadSetTaskProcessor<T> extends FilesProcessor<JsubTask> {


    private static final Log _logger = LogFactory.getLog(ReadSetTaskProcessor.class);
    protected JsubTask       _task;


    public ReadSetTaskProcessor(JsubTask task) {

        super(task);
    }


    // public List<String> getFiles() {
    //
    // List<String> files = new ArrayList<String>();
    // for (Map.Entry<String, String> property : _task.getProperties().entrySet()) {
    // if (Property.isInput(property)) {
    // files.add(property.getValue());
    // }
    // }
    //
    // return files;
    // }

    public void process() {

        try {

            // String pipelineName = RandomUtil.getRandomString();
            // Config configuration = Config.getInstance();
            // configuration.set(Config.FLAG_SKIP_COPY_PHASE, true);
            //
            // Jsub.getInstance();
            // Pipeline pipeline = Pipeline.newInstance(
            // pipelineName, "conveyor", _task.getScenarioType(), configuration.get(Config.JSUB_WEB_PROJECT_DIR)
            // );
            //
            // Command.factory(Command.COMMAND_CREATE).executeCommand();
            //
            // Properties propertiesFile = new Properties();
            // File file = new File(pipeline.getBuildPropertiesFilepath());
            // propertiesFile.load(new FileInputStream(file));
            //
            // Map<String, String> properties = _task.getProperties();
            // for (Object key : propertiesFile.keySet()) {
            // String property = (String) key;
            // if (properties.containsKey(property)) {
            // propertiesFile.setProperty(property, properties.get(property));
            // }
            // }

            // TODO: get it from config!
            // String destination = new StringBuilder("/data3/bio/reads")
            // .append(File.separator)
            // .append(properties.get(FilesStore.DIRECTORY_PROJECT))
            // .append(File.separator)
            // .append(properties.get(FilesStore.DIRECTORY_RUN))
            // .toString();

            // propertiesFile.setProperty(Property.PROPERTY_FIXED_DIR, destination);
            // propertiesFile.setProperty(Property.PROPERTY_MOVE_TO_BASEDIR, Boolean.FALSE.toString());
            //
            // propertiesFile.store(new FileOutputStream(file), null);
            //
            // QueueCommand queueCommand = (QueueCommand) Command.factory(Command.COMMAND_QUEUE);
            // queueCommand.setWait(true);
            // queueCommand.executeCommand();
            //
            // System.out.println("===========================");
            // Job job = pipeline.getLastJob();
            // List<Property> output = job.getOutputProperties();
            // for (Property property : output) {
            // System.out.println(property);
            // }
            //
            // System.out.println("----------------------------");
            // for (Map.Entry<String, String> property : properties.entrySet()) {
            // System.out.println(property.getKey() + "=" + property.getValue());
            // // if () {
            // //
            // // }
            // }
            //
            // _logger.info(String.format("Pipeline \"%s\" executed",
            // pipeline.getLayout().getBuildDirectory().getPath()));

        } catch (Exception e) {
            // _logger.error(String.format(
            // "Cannot run jsub for processing \"%s\" [%s] %s",
            // _task.getScenarioType(),
            // e.getClass().getName(),
            // e.getMessage()
            // ));
        }
    }


    public long getBlockMode() {

        return FilesProcessor.BLOCK_WRITE;
    }
}