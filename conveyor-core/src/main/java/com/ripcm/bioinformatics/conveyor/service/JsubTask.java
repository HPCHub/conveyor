/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.ant.Property;


/**
 * @author zeleniy
 */
public class JsubTask extends Task {


    private List<String>        _clearList  = new ArrayList<String>();
    private Map<String, String> _options    = new HashMap<String, String>();
    private String              _processorClassName;
    private String              _type;
    private String              _name;
    private String              _tag;
    private String              _directory;


    /**
     * 
     */
    public JsubTask(String name, String tag, String type, String directory, String processorClassName) {

        _name = name;
        _tag = tag;
        _type = type;
        _directory = directory;
        _processorClassName = processorClassName;
    }


    public List<String> getFiles() {

//        Set<Property> files = Pipeline.newInstance(
//            getName(), getTag(), getType(), getDirectory()
//        ).init().getUserDefinedFiles();
//
        List<String> files = new ArrayList<String>();
//        for (Property file : files) {
//            result.add(file.getValue());
//        }
//
//        return result;
        for (String file : _properties.values()) {
            files.add(file);
        }

        return files;
    }



    public String getName() {

        return _name;
    }


    public String getTag() {

        return _tag;
    }


    public String getType() {

        return _type;
    }


    public String getDirectory() {

        return _directory;
    }


    /**
     * @param name
     * @param value
     */
    public void setOption(String name, String value) {

        _options.put(name, value);
    }


    /**
     * @param name
     * @param value
     */
    public void setOption(String name, Boolean value) {

        _options.put(name, value.toString());
    }


    /**
     * @param options
     */
    public void setOptions(Map<String, String> options) {

        _options.putAll(options);
    }


    /**
     * @param name
     */
    public void removeOption(String name) {

        _options.remove(name);
    }


    /**
     * @return
     */
    public Map<String, String> getOptions() {

        return _options;
    }


    /**
     * @param name
     * @param value
     */
    public void setProperty(String name, String value) {

        _properties.put(name, value);
    }


    /**
     * @param properties
     */
    public void setProperties(Map<String, String> properties) {

        _properties.putAll(properties);
    }


    /**
     * @param name
     */
    public void removeProperty(String name) {

        _properties.remove(name);
    }


    /**
     * @return
     */
    public Map<String, String> getProperties() {

        return _properties;
    }


    /**
     * @param phases
     */
    public void setClearList(List<String> phases) {

        _clearList.addAll(phases);
    }


    /**
     * @param phase
     */
    public void setClearPhase(String phase) {

        _clearList.add(phase);
    }


    /**
     * @param phase
     */
    public void removeClearPhase(String phase) {

        _clearList.remove(phase);
    }


    /**
     * @return
     */
    public List<String> getClearList() {

        return _clearList;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.conveyor.activemq.Task#getProcessorClassName()
     */
    @Override
    public String getProcessorClassName() {

        return _processorClassName;
    }
}