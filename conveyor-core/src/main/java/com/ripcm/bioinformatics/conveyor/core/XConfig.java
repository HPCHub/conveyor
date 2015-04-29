/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.thoughtworks.xstream.XStream;


/**
 * @author zeleniy
 */
public class XConfig {


    private ActiveMQ _activeMQ = new ActiveMQ();
    /**
     * Class instance.
     */
    private static XConfig      _instance;
    /**
     * Engines list.
     */
    private Map<String, String> _properties = new HashMap<String, String>();
    /**
     * Class logger.
     */
    private static Logger       _logger     = Logger.getLogger(XConfig.class);


    public static XConfig getInstance() {

        if (_instance == null) {
            try {
                XStream xstream = new XStream();
                xstream.registerConverter(new XstreamConverter());
                xstream.alias("configuration", XConfig.class);

                _instance = (XConfig) xstream.fromXML(XConfig.class.getResourceAsStream("/conveyor.xml"));
            } catch (Exception e) {
                _logger.error(String.format(
                    "Cannot initialize xconfig [%s] %s", e.getClass().getName(), e.getMessage()
                ));
            }
        }

        return _instance;
    }


    public XConfig() {

    }


    public ActiveMQ getActiveMQ() {

        return _activeMQ;
    }


    public Map<String, String> getProperties() {

        return _properties;
    }


    public String getProperty(String name) {

        return _properties.get(name);
    }


    public class ActiveMQ {


        private List<String> _pipes = new ArrayList<String>();


        public List<String> getPipes() {

            return _pipes;
        }
    }
}