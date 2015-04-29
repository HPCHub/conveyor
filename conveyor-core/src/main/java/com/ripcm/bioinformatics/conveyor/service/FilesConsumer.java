/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author zeleniy
 */
public class FilesConsumer extends Consumer {


    private static final Log _logger = LogFactory.getLog(FilesConsumer.class);
    private static final long _MESSAGE_DELAY = 60 * 1000 * 5;


    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {

        try {
            ActiveMQ.getInstance().registerMessageListener(this, ActiveMQ.PIPE_FILES);
        } catch (JMSException e) {
            _logger.error(String.format("Cannot register self as message listener: %s", e.getMessage()));
        }
    }


    /* (non-Javadoc)
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @Override
    public void onMessage(Message message) {

        try {

            _logger.debug("Receive files message");

            ObjectMessage objectMessage = (ObjectMessage) message;
            Task task = (Task) objectMessage.getObject();

            FilesDispatcher dispatcher = FilesDispatcher.getInstance();
            synchronized (dispatcher) {

                FilesProcessor processor = (FilesProcessor) Processor.factory(task);
                if (! dispatcher.canExecute(processor)) {
                    ActiveMQ.getInstance().send(ActiveMQ.PIPE_FILES, task, _MESSAGE_DELAY);
                    _logger.info(String.format("Postpone task for \"%s\"", task.getProcessorClassName()));
                    return;
                }

                dispatcher.execute(processor);
            }

        } catch (JMSException e) {
            _logger.error(String.format("Cannot handle message [JMSException] %s", e.getMessage()));
        } catch (Exception e) {
            _logger.error(String.format("Cannot process message [%s] %s", e.getClass().getName(), e.getMessage()));
        }
    }
}