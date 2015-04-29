/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFileType;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsProject;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReadSet;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRunsFile;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsSample;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.util.StringUtil;
import ru.niifhm.bioinformatics.util.io.FileUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.ripcm.bioinformatics.conveyor.core.XConfig;


/**
 * @author zeleniy
 */
public class SaveReadSetAction extends ActionSupport {


    /**
     * Sample id.
     */
    private long                _sampleId;
    /**
     * Quality/second file id.
     */
    private long                _qualitySetId;
    /**
     * Read/first file id.
     */
    private long                _readSetId;
    /**
     * Read/first file.
     */
    private File                _readSet;
    /**
     * Read/first file name.
     */
    private String              _readSetName;
    /**
     * Quality/second file.
     */
    private File                _qualitySet;
    /**
     * Quality/second file name.
     */
    private String              _qualitySetName;
    /**
     * Read set description.
     */
    private String              _description;
    /**
     * Is read set fixed by SAET?
     */
    private boolean             _isFixed;
    /**
     * Is read set filtered by csfasta_quality_filter.pl script?
     */
    private boolean             _isTrimmed;
    /**
     * Run id.
     */
    private long                _runId;
    private boolean             _useFileSystem;
    /**
     * Class logger.
     */
    private Logger              _logger         = Logger.getLogger(SaveReadSetAction.class);
    private Map<String, String> _jsubProperties = new HashMap<String, String>();


    public void setUseFileSystem(String value) {

        _useFileSystem = true;
    }


    public void setReadSetPath(String readSetPath) {

        _readSet = new File(readSetPath);
        _readSetName = _readSet.getName();
    }


    public void setQualitySetPath(String qualitySetPath) {

        _qualitySet = new File(qualitySetPath);
        if (! StringUtils.isEmpty(_qualitySet.getName())) {
            _qualitySetName = _qualitySet.getName();
        }
    }


    public void setSampleId(long sampleId) {

        _sampleId = sampleId;
    }


    public void setRunId(long runId) {

        _runId = runId;
    }


    public void setReadSet(File readSet) {

        _readSet = readSet;
    }


    public void setReadSetContentType(String contentType) {

    }


    public void setReadSetFileName(String readSetName) {

        _readSetName = readSetName;
    }


    public void setQualitySet(File qualitySet) {

        _qualitySet = qualitySet;
    }


    public void setQualitySetContentType(String contentType) {

    }


    public void setQualitySetFileName(String qualitySetName) {

        _qualitySetName = qualitySetName;
    }


    public void setIsTrimmed(boolean isTrimmed) {

        _isTrimmed = isTrimmed;
    }


    public void setIsFixed(boolean isFixed) {

        _isFixed = isFixed;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMessage() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), "<br />") : Action.SUCCESS;
    }


    /*
     * (non-Javadoc)
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    public String execute() {

        SeqsReadSet seqsReadSet;
        SeqsReadSet seqsQualitySet;
        File store = null;

        try {
            if (_readSet == null) {
                throw new Exception("Read file is empty (null)");
            }

            if (! _readSet.exists()) {
                throw new Exception("File not found");
            }

            if (_qualitySet != null && ! _qualitySet.exists()) {
                throw new Exception("File not found");
            }

            /*
             * Check file store availability.
             */
            String storePath = XConfig.getInstance().getProperty("filesStore");
            store = new File(storePath);
            if (! store.exists()) {
                throw new Exception(String.format("File store \"%s\" not found", storePath));
            }

            /*
             * Create and fetch from database necessary objects.
             */
            seqsReadSet = new SeqsReadSet();
            seqsQualitySet = new SeqsReadSet();

            SeqsRun run = DAO.findById(SeqsRun.class, _runId);
            SeqsProject project = run.getSeqsProject();
            SeqsSample seqsSample = DAO.findById(SeqsSample.class, _sampleId);

            SeqsFile seqsFile;

            /*
             * Save quality/second file in database if exists.
             */
            if (_qualitySetName != null) {

                seqsFile = _saveSeqsFile(
                    seqsQualitySet.getSeqsFile(), _qualitySet, _qualitySetName, store, project, run
                );

                if (seqsFile == null) {
                    throw new Exception("Cannot save sequence file in DB");
                }

                seqsQualitySet.setSeqsFile(seqsFile);
                seqsQualitySet.setSeqsSample(seqsSample);
                DAO.save(seqsQualitySet);
            }

            /*
             * Save read/first in database file.
             */
            seqsFile = _saveSeqsFile(seqsReadSet.getSeqsFile(), _readSet, _readSetName, store, project, run);
            if (seqsFile == null) {
                throw new Exception("Cannot save sequence file in DB");
            }

            seqsReadSet.setSeqsFile(seqsFile);
            seqsReadSet.setSeqsSample(seqsSample);
            seqsReadSet.setQualityFileId(seqsQualitySet.getReadSetId());
            DAO.save(seqsReadSet);

            /*
             * Save files in file system.
             */
            if (_readSetId <= 0 && ! _useFileSystem) {
                _saveUploadedFiles(store, project, run);
            }

            // String pipeline;
            // if (_jsubProperties.size() == 1) {
            // pipeline = "fastq-processing";
            // } else if (_jsubProperties.size() == 2) {
            // pipeline = "csfasta-qual-processing";
            // } else {
            // _logger.warn("Cannot find processing scenario for uploaded files");
            // return Action.SUCCESS;
            // }

            /*
             * Run upload files processing.
             */
            // _jsubProperties.put(FilesStore.DIRECTORY_PROJECT, project.getName());
            // _jsubProperties.put(FilesStore.DIRECTORY_RUN, run.getName());
            // Serializable task = new PropertiesTask(
            // pipeline,
            // _jsubProperties,
            // ru.niifhm.bioinformatics.jsub.configuration.XConfig.getInstance().getClearList("metagenome"),
            // ReadSetTaskProcessor.class.getName()
            // );
            //
            // ActiveMQ.getInstance().send(ActiveMQ.PIPE_FILES, task);

            _readSetId = seqsReadSet.getReadSetId();

        } catch (Exception e) {
            // TODO: Rollback
            String message = String.format(
                "Cannot save file \"%s\" [%s] %s", _readSetName, e.getClass().getName(), e.getMessage()
            );

            _logger.error(message);
            setActionErrors(Arrays.asList(message));
        }

        return Action.SUCCESS;
    }


    /**
     * Save additional file data in database.
     * @param seqsFile
     * @param file
     * @param fileName
     * @param store
     * @param project
     * @param run
     * @return
     */
    private SeqsFile _saveSeqsFile(SeqsFile seqsFile, File file, String fileName, File store, SeqsProject project,
        SeqsRun run) {

        try {
            if (seqsFile == null) {
                seqsFile = new SeqsFile();
            }

            SeqsFileType fileType = null;
            List<String> extensions = FileUtil.getExtensions(fileName);
            if (extensions.size() > 0) {
                List<SeqsFileType> fileTypes = DAO.getCriteria(SeqsFileType.class)
                    .add(Restrictions.in("type", extensions))
                .list();

                if (fileTypes.size() > 0) {
                    fileType = fileTypes.get(0);
                }
            }

            seqsFile.setName(fileName);
            seqsFile.setDescription(_description);
            seqsFile.setSeqsFileType(fileType);
            seqsFile.setSize(file.length());
            seqsFile.setPath(_getUploadedFilePath(file, fileName, store, project, run));

            DAO.save(seqsFile);

            SeqsRunsFile runsFile = seqsFile.getSeqsRunsFile();

            runsFile.setSeqsRun(run);
            runsFile.setSeqsFile(seqsFile);
            runsFile.setIsFixed(_isFixed);
            runsFile.setIsTrimmed(_isTrimmed);
            DAO.save(runsFile);
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot save file [%s] %s", e.getClass().getName(), e.getMessage()
            )));

            return null;
        }

        return seqsFile;
    }


    /**
     * @param store
     * @param project
     * @param run
     * @return
     */
    private String _getUploadDirectoryPath(File store, SeqsProject project, SeqsRun run) {

        return new StringBuilder(store.getAbsolutePath())
            .append(File.separator)
            .append(project.getName())
            .append(File.separator)
            .append(run.getName())
            .toString();
    }


    /**
     * @param fileName
     * @param store
     * @param project
     * @param run
     * @return
     */
    private String _getUploadedFilePath(File file, String fileName, File store, SeqsProject project, SeqsRun run) {

        if (_useFileSystem) {
            return file.getAbsolutePath();
        }

        return new StringBuilder(_getUploadDirectoryPath(store, project, run))
            .append(File.separator)
            .append(fileName)
            .toString();
    }


    /**
     * Save files in file system.
     * @param store
     * @param project
     * @param run
     * @throws Exception
     */
    private void _saveUploadedFiles(File store, SeqsProject project, SeqsRun run) throws Exception {

        File directory = new File(_getUploadDirectoryPath(store, project, run));

        /*
         * Save read set.
         */
        File readSet = new File(directory, _readSetName);
        if (! directory.exists()) {
            if (! directory.mkdirs()) {
                throw new Exception(String.format("Cannot create directory \"%s\"", directory.getPath()));
            }
        }

        if (readSet.exists()) {
            throw new Exception(String.format("File \"%s\" already exists", readSet.getPath()));
        }

        if (! readSet.createNewFile()) {
            throw new Exception(String.format("Cannot create file \"%s\"", readSet.getPath()));
        }

        FileUtil.copy(_readSet, readSet);
        _logger.debug(String.format("Save uploaded file \"%s\" as \"%s\"", _readSetName, readSet.getPath()));
        readSet.setWritable(true);

        _addSavedFile(readSet);

        /*
         * Save quality set.
         */
        if (_qualitySetName != null) {
            File qualitySet = new File(directory, _qualitySetName);

            if (! directory.exists()) {
                if (! directory.mkdirs()) {
                    throw new Exception(String.format("Cannot create directory \"%s\"", directory.getPath()));
                }
            }

            if (qualitySet.exists()) {
                throw new Exception(String.format("File \"%s\" already exists", qualitySet.getPath()));
            }

            if (! qualitySet.createNewFile()) {
                throw new Exception(String.format("Cannot create file \"%s\"", qualitySet.getPath()));
            }

            FileUtil.copy(_qualitySet, qualitySet);
            _logger.debug(String.format("Save uploaded file \"%s\" as \"%s\"", _readSetName, qualitySet.getPath()));
            qualitySet.setWritable(true);

            _addSavedFile(qualitySet);
        }
    }


    private void _addSavedFile(File file) {

        try {
            _jsubProperties.put(
                Property.getInputPropertyNameByFilename(file.getAbsolutePath()),
                file.getAbsolutePath()
                );
        } catch (Exception e) {
            _logger.error(String.format(
                "Cannot add \"%s\" to _savedList [%s] %s", file.getName(), e.getClass().getName(), e.getMessage()
                ));
        } catch (Throwable t) {
            _logger.error(String.format(
                "Cannot add \"%s\" to _savedList [%s] %s", file.getName(), t.getClass().getName(), t.getMessage()
                ));
        }
    }


    public long getReadSetId() {

        return _readSetId;
    }


    public void setReadSetId(String readSetId) {

        _readSetId = 0L;
    }


    public void setReadSetId(long readSetId) {

        _readSetId = readSetId;
    }


    public long getQualitySetId() {

        return _qualitySetId;
    }


    public void setQualitySetId(long qualitySetId) {

        _qualitySetId = qualitySetId;
    }


    public String getDescription() {

        return _description;
    }


    public void setDescription(String description) {

        _description = description;
    }
}