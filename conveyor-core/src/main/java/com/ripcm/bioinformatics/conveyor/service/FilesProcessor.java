/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Common files processor class.
 * @author zeleniy
 */
abstract public class FilesProcessor <T extends Task> extends Processor<T> {


    public static long        BLOCK_WRITE = 0;
    public static long        BLOCK_READ  = 1;
    private static final Log _logger     = LogFactory.getLog(FilesProcessor.class);
    /**
     * Task to process.
     */
    protected FilesDispatcher _dispatcher;


    /**
     * Common task constructor.
     * @param task
     */
    public FilesProcessor(T task) {

        super(task);
    }


    abstract public long getBlockMode();


    public void setDispatcher(FilesDispatcher dispatcher) {

        _dispatcher = dispatcher;
    }


    public List<String> getFiles() {

        return _task.getFiles();
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {

        try {
            process();
        } catch (Exception e) {
            _logger.error(String.format(
                "Cannot process task \"%s\" [%s] %s",
                _task.getProcessorClassName(),
                e.getClass().getName(),
                e.getMessage()
            ));
        }
    }
}