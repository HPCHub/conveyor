/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * @author zeleniy
 */
public class XstreamConverter implements Converter {


    /*
     * (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
     */
    @Override
    public boolean canConvert(Class clazz) {

        return clazz.equals(XConfig.class);
    }


    /*
     * (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
     * com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }


    /*
     * (non-Javadoc)
     * @see
     * com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
     * com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        XConfig xconfig = new XConfig();

        Map<String, String> properties = xconfig.getProperties();
        reader.moveDown(); // properties
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // property
            String name  = reader.getAttribute("name");
            String value = reader.getAttribute("value");

            properties.put(name, value);
            reader.moveUp();
        }
        reader.moveUp();

//        List<String> pipes = xconfig.getActiveMQ().getPipes();
//
//        reader.moveDown(); // activemq
//        reader.moveDown(); // pipes
//        while (reader.hasMoreChildren()) {
//            reader.moveDown(); // pipe
//            pipes.add(reader.getValue());
//            reader.moveUp();
//        }
//        reader.moveUp();

        return xconfig;
    }
}