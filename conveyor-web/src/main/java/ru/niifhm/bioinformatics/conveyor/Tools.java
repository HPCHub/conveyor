/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor;


import java.util.Properties;
import org.apache.log4j.Logger;


/**
 * @author zeleniy
 */
public class Tools {


    public static final String    TOOL_BOWTIE_BUILD = "bowtie-build";
    public static final String    TOOL_SAMTOOLS     = "samtools";
    public static final String    TOOL_BASH         = "bash";

    private static volatile Tools _instance;
    private Properties            _properties       = new Properties();
    private Logger                _logger           = Logger.getLogger(Tools.class);


    /**
     * Get class single instance.
     * @return
     */
    public static Tools getInstance() {

        if (_instance == null) {
            synchronized (Tools.class) {
                if (_instance == null) {
                    _instance = new Tools();
                }
            }
        }

        return _instance;
    }


    /**
     * Private constructor.
     */
    private Tools() {

        try {
            _properties.loadFromXML(this.getClass().getResourceAsStream("/tools.xml"));
        } catch (Exception e) {
            _logger.error(String.format("Cannot load tools.xml [%s] $s", e.getClass().getName(), e.getMessage()));
        }
    }


    /**
     * Get tool path.
     * @param toolName
     * @return
     */
    public String get(String tool) {

        return (String) _properties.getProperty(tool);
    }
}