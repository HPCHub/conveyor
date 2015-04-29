/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.niifhm.bioinformatics.jsub.ant.Property;


/**
 * @author zeleniy
 */
abstract public class Task implements Serializable {


    protected Map<String, String> _properties = new HashMap<String, String>();


    abstract public String getProcessorClassName();


    public List<String> getFiles() {

        List<String> files = new ArrayList<String>();
        for (Map.Entry<String, String> property : _properties.entrySet()) {
            if (Property.isInput(property)) {
                files.add(property.getValue());
            }
        }

        return files;
    }
}