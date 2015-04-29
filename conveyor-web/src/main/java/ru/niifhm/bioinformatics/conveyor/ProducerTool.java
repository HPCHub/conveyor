/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor;


/**
 * @author zeleniy
 */
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.protobuf.compiler.CommandLineSupport;
import org.apache.activemq.util.IndentPrinter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ProducerTool extends Thread {


    private Destination   destination;
    private int           messageCount    = 10;
    private long          sleepTime;
    private boolean       verbose         = true;
    private int           messageSize     = 255;
    private static int    parallelThreads = 1;
    private long          timeToLive;
    private String        user            = ActiveMQConnection.DEFAULT_USER;
    private String        password        = ActiveMQConnection.DEFAULT_PASSWORD;
    private String        url             = ActiveMQConnection.DEFAULT_BROKER_URL;
    private String        subject         = "TOOL.DEFAULT";
    private boolean       topic;
    private boolean       transacted;
    private boolean       persistent;
    private static Object lockResults     = new Object();


    public static void main(String[] args) {

        ArrayList<ProducerTool> threads = new ArrayList();
        ProducerTool producerTool = new ProducerTool();
        String[] unknown = CommandLineSupport.setOptions(producerTool, args);
        if (unknown.length > 0) {
            System.out.println("Unknown options: " + Arrays.toString(unknown));
            System.exit(- 1);
        }
        producerTool.showParameters();
        for (int threadCount = 1; threadCount <= parallelThreads; threadCount ++) {
            producerTool = new ProducerTool();
            CommandLineSupport.setOptions(producerTool, args);
            producerTool.start();
            threads.add(producerTool);
        }

        while (true) {
            Iterator<ProducerTool> itr = threads.iterator();
            int running = 0;
            while (itr.hasNext()) {
                ProducerTool thread = itr.next();
                if (thread.isAlive()) {
                    running ++;
                }
            }
            if (running <= 0) {
                System.out.println("All threads completed their work");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }


    public void showParameters() {

        System.out.println("Connecting to URL: " + url + " (" + user + ":" + password + ")");
        System.out.println("Publishing a Message with size " + messageSize + " to " + (topic ? "topic" : "queue")
            + ": " + subject);
        System.out.println("Using " + (persistent ? "persistent" : "non-persistent") + " messages");
        System.out.println("Sleeping between publish " + sleepTime + " ms");
        System.out.println("Running " + parallelThreads + " parallel threads");

        if (timeToLive != 0) {
            System.out.println("Messages time to live " + timeToLive + " ms");
        }
    }


    public void run() {

        Connection connection = null;
        try {
            // Create the connection.
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
            connection = connectionFactory.createConnection();
            connection.start();

            // Create the session
            Session session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            if (topic) {
                destination = session.createTopic(subject);
            } else {
                destination = session.createQueue(subject);
            }

            // Create the producer.
            MessageProducer producer = session.createProducer(destination);
            if (persistent) {
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            } else {
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }
            if (timeToLive != 0) {
                producer.setTimeToLive(timeToLive);
            }

            // Start sending messages
            sendLoop(session, producer);

            System.out.println("[" + this.getName() + "] Done.");

            synchronized (lockResults) {
                ActiveMQConnection c = (ActiveMQConnection) connection;
                System.out.println("[" + this.getName() + "] Results:\n");
                c.getConnectionStats().dump(new IndentPrinter());
            }

        } catch (Exception e) {
            System.out.println("[" + this.getName() + "] Caught: " + e);
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (Throwable ignore) {
            }
        }
    }


    protected void sendLoop(Session session, MessageProducer producer) throws Exception {

        for (int i = 0; i < messageCount || messageCount == 0; i ++) {

            TextMessage message = session.createTextMessage(createMessageText(i));

            if (verbose) {
                String msg = message.getText();
                if (msg.length() > 50) {
                    msg = msg.substring(0, 50) + "...";
                }
                System.out.println("[" + this.getName() + "] Sending message: '" + msg + "'");
            }

            producer.send(message);

            if (transacted) {
                System.out.println("[" + this.getName() + "] Committing " + messageCount + " messages");
                session.commit();
            }
            Thread.sleep(sleepTime);
        }
    }


    private String createMessageText(int index) {

        StringBuffer buffer = new StringBuffer(messageSize);
        buffer.append("Message: " + index + " sent at: " + new Date());
        if (buffer.length() > messageSize) {
            return buffer.substring(0, messageSize);
        }
        for (int i = buffer.length(); i < messageSize; i ++) {
            buffer.append(' ');
        }
        return buffer.toString();
    }
}