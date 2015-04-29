/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.tool;


import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.conveyor.web.SaveReadSetAction;


/**
 * @author zeleniy
 */
public class FileSetRegister {


    private static Logger _logger = Logger.getLogger(FileSetRegister.class);


    /**
     * @param args
     */
    public static void main(String[] args) {

        if (args.length < 2) {
            _logger.error("You must specify files set directory name input parameter");
            System.exit(0);
        }

        File directory  = new File(args[0]);
        String regexp   = args[1];
        int sample      = Integer.valueOf(args[2]);
        Pattern pattern = null;

        try {
            pattern = Pattern.compile(regexp);
        }catch (PatternSyntaxException e) {
            System.out.println(String.format("Error. Incorrect regular exception. %s", e.getMessage()));
            System.exit(0);
        }

        _logger.debug(String.format("Set input directory to \"%s\"", directory.getPath()));

        File[] files = directory.listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
                if (name.endsWith(".csfasta")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        for (File csfastaFile : files) {
            Matcher matcher = pattern.matcher(csfastaFile.getName());

            if (! matcher.matches()) {
                _logger.warn(String.format(
                    "Csfasta file \"%s\" do not satisfy for regexp pattern", csfastaFile.getName()
                )); 
                continue;
            }

            File qualFile = new File(directory, String.format("%s_QV_%s.qual", matcher.group(1), matcher.group(2)));

            if (! qualFile.exists()) {
                _logger.warn(String.format(
                    "Cannot recognize quality file \"%s\" for \"%s\"", qualFile.getPath(), csfastaFile.getName()
                ));
                continue;
            }

            _logger.debug(String.format("Get quality file \"%s\"", qualFile.getName()));

            SaveReadSetAction action = new SaveReadSetAction();
            action.setIsFixed(false);
            action.setIsTrimmed(false);
            action.setQualitySet(qualFile);
            // action.setQualitySetContentType(String);
            action.setQualitySetFileName(qualFile.getName());
            // action.setQualitySetId(long);
            action.setReadSet(csfastaFile);
            // action.setReadSetContentType(String);
            action.setReadSetFileName(csfastaFile.getName());
            // action.setReadSetId(long);
            // action.setReadSetId(String);
            // action.setRunId(long);
            // action.setSampleId(long);
        }
    }
}
