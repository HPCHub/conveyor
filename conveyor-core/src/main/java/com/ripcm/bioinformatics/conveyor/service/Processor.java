/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import java.lang.reflect.Constructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Top level processor class with no functionality.
 * @author zeleniy
 */
abstract class Processor <T extends Task> extends Thread {


    /**
     * Task to process.
     */
    protected T _task;
    /**
     * Class logger.
     */
    private static final Log _logger = LogFactory.getLog(Processor.class);


    /**
     * Factory method.
     * @param task
     * @return
     */
    public static <T extends Task> Processor<T> factory(T task) {

        Processor<T> processor = null;
        String className = task.getProcessorClassName();

        try {
            Class<? extends Processor> clazz = Class.forName(className).asSubclass(Processor.class);
            Constructor<? extends Processor> constructor = clazz.getConstructor(task.getClass());
            processor = constructor.newInstance(task);
        } catch (Exception e) {
            _logger.error(String.format(
                "Cannot create instance of %s for %s [%s] %s",
                className,
                task.getClass().getName(),
                e.getClass().getName(),
                e.getMessage()
            ));
        }

        return processor;
    }


    /**
     * Constructor.
     * @param task
     */
    public Processor(T task) {

        _task = task;
    }


    /**
     * Process task.
     */
    abstract public void process();
}