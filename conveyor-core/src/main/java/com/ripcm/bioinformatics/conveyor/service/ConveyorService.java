/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import java.lang.reflect.Constructor;
import org.apache.log4j.Logger;
import com.ripcm.bioinformatics.conveyor.core.XConfig;
import ru.niifhm.bioinformatics.util.Package;


/**
 * @author zeleniy
 */
public class ConveyorService {


    private static final Logger _logger = Logger.getLogger(ConveyorService.class);


    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            String mode = XConfig.getInstance().getProperty("environmentMode");

            Class<?>[] classes;
            if (mode.equals("development")) {
                String packageName = ConveyorService.class.getPackage().getName();
                classes = Package.getPackageClasses(packageName);
            } else {
                String jarPath = ConveyorService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                String packageName = ConveyorService.class.getPackage().getName();
                classes = Package.getPackageClasses(jarPath, packageName);
            }

            if (classes.length == 0) {
                throw new Exception("No consumers available");
            }

            String className = null;
            for (Class<?> clazz : classes) {
                className = clazz.getName();
                if (! (className.endsWith("Consumer") && ! className.endsWith(".Consumer"))) {
                    continue;
                }

                Constructor<?> constructor = clazz.getConstructor();
                Consumer consumer = (Consumer) constructor.newInstance();
                consumer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            _logger.error(String.format(
                "Cannot boot services [%s] %s:", e.getClass().getName(), e.getMessage()
                ));
        }
    }
}