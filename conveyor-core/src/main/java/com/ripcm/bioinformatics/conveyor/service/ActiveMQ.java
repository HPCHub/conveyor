/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author zeleniy
 */
public class ActiveMQ {


    public static final String PIPE_FILES = "files-processing";
    public static final String PIPE_STATS = "stats";
    /**
     * 
     */
    private Connection         _connection;
    /**
     * 
     */
    private static ActiveMQ    _instance;
    /**
     * 
     */
    private static final Log   _logger    = LogFactory.getLog(ActiveMQ.class);


    /**
     * @return
     */
    public static ActiveMQ getInstance() {

        if (_instance == null) {
            _instance = new ActiveMQ();
        }

        return _instance;
    }


    /**
     * 
     */
    private ActiveMQ() {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
            ActiveMQConnection.DEFAULT_BROKER_URL);

        try {
            _connection = connectionFactory.createConnection();
            _connection.start();
        } catch (JMSException e) {
            _logger.error(String.format("Cannot initialze AqtiveMQ connection [JMSException]: %s", e.getMessage()));
        }
    }


    public void disconnect() throws JMSException {

        _connection.close();
    }


    public Session getSession() throws JMSException {

        return _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }


    public Connection getConnection() {

        return _connection;
    }


    public void registerExceptionListener(ExceptionListener listener) throws JMSException {

        getConnection().setExceptionListener(listener);
    }


    /**
     * @param listener
     * @throws JMSException
     */
    public Session registerMessageListener(MessageListener listener, String queueName) throws JMSException {

        Session session = getSession();
        Destination destination = session.createQueue(queueName);

        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(listener);

        return session;
    }


    public Session send(String queueName, Serializable messageContent, long delay) throws JMSException {

        Session session = getSession();
        Destination destination = session.createQueue(queueName);

        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        ObjectMessage message = session.createObjectMessage(messageContent);
        message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
        producer.send(message);

        return session;
    }


    /**
     * @param queueName
     * @param message
     * @return
     * @throws JMSException
     */
    public Session send(String queueName, Serializable messageContent) throws JMSException {

        Session session = getSession();
        Destination destination = session.createQueue(queueName);

        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        ObjectMessage message = session.createObjectMessage(messageContent);
        producer.send(message);

        return session;
    }
}