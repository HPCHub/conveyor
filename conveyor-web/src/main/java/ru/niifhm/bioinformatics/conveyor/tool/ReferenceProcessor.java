/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.tool;


import java.io.File;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.conveyor.Tools;
import ru.niifhm.bioinformatics.util.StringUtil;
import ru.niifhm.bioinformatics.util.io.DirectoryUtil;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * Reference genomes file processor.
 * Process reference file after it uploading in the separate thread.
 * @author zeleniy
 */
public class ReferenceProcessor extends Thread {


    private File   _reference;
    private Logger _logger = Logger.getLogger(ReferenceProcessor.class);


    /**
     * Set a reference file.
     * @param reference
     * @return
     */
    public ReferenceProcessor setReference(File reference) {

        _reference = reference;

        return this;
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {

        _createBowtieIndex();
        _createFaiIndex();
        _createReducedFaiIndex();
    }


    /**
     * 
     */
    private void _createBowtieIndex() {

        // TODO: use BowtieIndex class
        _createBlackWhiteBowtieIndex();
        _createColorSpaceBowtieIndex();
    }


    /**
     * 
     */
    private void _createBlackWhiteBowtieIndex() {

        _logger.info(String.format("Start create black-white bowtie index for \"%s\"", _reference.getAbsoluteFile()));

        String command = null;
        Runtime runtime = Runtime.getRuntime();
        File indexDir = new File(_reference.getParentFile(), _reference.getName().replaceAll("\\.", "_"));

        try {
            if (! indexDir.exists()) {
                DirectoryUtil.mkdir(indexDir);
            }

            command = String.format(
                "%s -f %s %s/%s",
                Tools.getInstance().get(Tools.TOOL_BOWTIE_BUILD),
                _reference.getAbsolutePath(),
                indexDir.getAbsoluteFile(),
                FileUtil.getNameWithoutExtension(_reference)
            );

            _logger.info(String.format("Execute \"%s\"", command));
            Process process = runtime.exec(command);
            int result = process.waitFor();
            if (result != 0) {
                _logger.warn("Exit code for bowtie-build not equals to 0");
            }
        } catch (Exception e) {
            _logger.warn(String.format(
                "Cannot execute \"%s\" [%s] %s",
                command,
                e.getClass().getName(),
                e.getMessage()
            ));
        } finally {
            _logger.info(String.format("Stop create black-white bowtie index for \"%s\"", _reference.getAbsoluteFile()));
        }
    }


    /**
     * 
     */
    private void _createColorSpaceBowtieIndex() {

        _logger.info(String.format("Start create color space bowtie index for \"%s\"", _reference.getAbsoluteFile()));

        String command = null;
        Runtime runtime = Runtime.getRuntime();
        File indexDir = new File(_reference.getParentFile(), _reference.getName().replaceAll("\\.", "_") + "_cs");

        try {
            if (! indexDir.exists()) {
                DirectoryUtil.mkdir(indexDir);
            }

            command = String.format(
                "%s -f --color %s %s/%s",
                Tools.getInstance().get(Tools.TOOL_BOWTIE_BUILD),
                _reference.getAbsolutePath(),
                indexDir.getAbsoluteFile(),
                FileUtil.getNameWithoutExtension(_reference)
            );

            _logger.info(String.format("Execute \"%s\"", command));
            Process process = runtime.exec(command);
            int result = process.waitFor();
            if (result != 0) {
                _logger.warn("Exit code for bowtie-build not equals to 0");
            }
        } catch (Exception e) {
            _logger.warn(String.format(
                "Cannot execute \"%s\" [%s] %s",
                command,
                e.getClass().getName(),
                e.getMessage()
            ));
        } finally {
            _logger.info(String.format("Stop create color space bowtie index for \"%s\"", _reference.getAbsoluteFile()));
        }
    }


    /**
     * 
     */
    private void _createFaiIndex() {

        _logger.info(String.format("Start create fai index for \"%s\"", _reference.getAbsoluteFile()));

        String command = null;
        Runtime runtime = Runtime.getRuntime();

        try {
            command = String.format(
                "%s faidx %s",
                Tools.getInstance().get(Tools.TOOL_SAMTOOLS),
                _reference.getAbsolutePath()
            );

            _logger.info(String.format("Execute \"%s\"", command));
            Process process = runtime.exec(command);
            int result = process.waitFor();
            if (result != 0) {
                _logger.warn("Exit code for \"bedtools faidx\" not equals to 0");
            }
        } catch (Exception e) {
            _logger.warn(String.format(
                "Cannot execute \"%s\" [%s] %s",
                command,
                e.getClass().getName(),
                e.getMessage()
            ));
        } finally {
            _logger.info(String.format("Stop create fai index for \"%s\"", _reference.getAbsoluteFile()));
        }
    }


    /**
     * 
     */
    private void _createReducedFaiIndex() {

        _logger.info(String.format("Start create reduced fai index for \"%s\"", _reference.getAbsoluteFile()));

        String[] command = new String[3];
        Runtime runtime = Runtime.getRuntime();

        try {
            command[0] = Tools.getInstance().get(Tools.TOOL_BASH);
            command[1] = "-c";
            command[2] = String.format(
                "awk -F ' ' '{print $1 \"\\t\" $2;}' \"%s.fai\" > \"%s.genome\"",
                _reference.getAbsolutePath(),
                _reference.getAbsolutePath()
            );

            for (String i : command) {
                System.out.println(i);
            }

            _logger.info(String.format("Execute \"%s\"", StringUtil.join(command, " ")));
            Process process = runtime.exec(command);
            int result = process.waitFor();
            if (result != 0) {
                _logger.warn("Exit code for \"awk\" not equals to 0");
            }
        } catch (Exception e) {
            _logger.warn(String.format(
                "Cannot execute \"%s\" [%s] %s",
                StringUtil.join(command, " "),
                e.getClass().getName(),
                e.getMessage()
            ));
        } finally {
            _logger.info(String.format("Stop create reduced fai index for \"%s\"", _reference.getAbsoluteFile()));
        }
    }
}