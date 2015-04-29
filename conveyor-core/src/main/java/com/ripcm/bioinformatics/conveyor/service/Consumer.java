/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author zeleniy
 */
abstract public class Consumer extends Thread implements MessageListener, ExceptionListener {


    private static final Log _logger = LogFactory.getLog(Consumer.class);


    /*
     * (non-Javadoc)
     * @see javax.jms.ExceptionListener#onException(javax.jms.JMSException)
     */
    @Override
    public void onException(JMSException e) {

        _logger.error(String.format("[%s] %s", e.getClass().getName(), e.getMessage()));
    }
}