/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.io.File;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReadSet;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRunsFile;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsSample;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class EditReadSetAction extends ActionSupport {

    /**
     * Sample id.
     */
    private long  _sampleId;
    /**
     * Read file id.
     */
    private long   _readSetId;
    /**
     * Read set description.
     */
    private String _description;
    /**
     * Run id.
     */
    private long _runId;
    /**
     * Class logger.
     */
    private Logger _logger = Logger.getLogger(SaveReadSetAction.class);
    /**
     * Path to file.
     */
    private File _file;


    public void setPath(String path) {

        _file = new File(path);
    }


    public void setSampleId(long sampleId) {

        _sampleId = sampleId;
    }


    public void setReadSetId(long readSetId) {

        _readSetId = readSetId;
    }


    public void setDescription(String description) {

        _description = description;
    }


    public String execute() {

        try {

            if (_readSetId <= 0) {
                throw new Exception("Read set id not initialized");
            }

            SeqsReadSet readSet = DAO.findById(SeqsReadSet.class, _readSetId);

            /*
             * Change sample id if necessary.
             */
            SeqsSample sample   = readSet.getSeqsSample();
            if (sample.getSampleId() != _sampleId) {
                SeqsSample newSample = DAO.findById(SeqsSample.class, _sampleId);
                readSet.setSeqsSample(newSample);
                DAO.save(readSet);

                /*
                 * Change sample id on paired csfasta file.
                 */
                if (readSet.getQualityFileId() == null) {
                    SeqsReadSet pairedSet = (SeqsReadSet) DAO.getCriteria(SeqsReadSet.class)
                        .add(Restrictions.eq("qualityFileId", readSet.getReadSetId()))
                    .uniqueResult();
                    if (pairedSet != null && sample.getSampleId() != _sampleId) {
                        pairedSet.setSeqsSample(newSample);
                        DAO.save(pairedSet);
                    }
                } else {
                    /*
                     * Change sample id on paired quality file.
                     */
                    SeqsReadSet qualitySet = DAO.findById(SeqsReadSet.class, readSet.getQualityFileId());
                    if (qualitySet != null && sample.getSampleId() != _sampleId) {
                        qualitySet.setSeqsSample(newSample);
                        DAO.save(qualitySet);
                    }
                }
            }

            /*
             * Change sample id if necessary.
             */
            SeqsRunsFile runsFile = readSet.getSeqsFile().getSeqsRunsFile();
            if (runsFile.getSeqsRun().getRunId() != _runId) {
                SeqsRun newRun = DAO.findById(SeqsRun.class, _runId);
                runsFile.setSeqsRun(newRun);
                DAO.save(runsFile);

                /*
                 * Change sample id on paired csfasta file.
                 */
                if (readSet.getQualityFileId() == null) {
                    SeqsReadSet pairedSet = (SeqsReadSet) DAO.getCriteria(SeqsReadSet.class)
                        .add(Restrictions.eq("qualityFileId", readSet.getReadSetId()))
                    .uniqueResult();
                    if (pairedSet != null) {
                        SeqsRunsFile pairedSetRunsFile = pairedSet.getSeqsFile().getSeqsRunsFile();
                        if (pairedSetRunsFile.getSeqsRun().getRunId() != _runId) {
                            pairedSetRunsFile.setSeqsRun(newRun);
                            DAO.save(pairedSetRunsFile);
                        }
                    }
                } else {
                    /*
                     * Change sample id on paired quality file.
                     */
                    SeqsReadSet qualitySet = DAO.findById(SeqsReadSet.class, readSet.getQualityFileId());
                    if (qualitySet != null) {
                        SeqsRunsFile qualitySetRunsFile = qualitySet.getSeqsFile().getSeqsRunsFile();
                        if (qualitySetRunsFile.getSeqsRun().getRunId() != _runId) {
                            qualitySetRunsFile.setSeqsRun(newRun);
                            DAO.save(qualitySetRunsFile);
                        }
                    }
                }
            }

            SeqsFile seqsFile = readSet.getSeqsFile();
            seqsFile.setName(_file.getName());
            seqsFile.setDescription(_description);
            seqsFile.setPath(_file.getPath());
            seqsFile.setSize(_file.length());
            DAO.save(seqsFile);

        } catch (Exception e) {
            setActionErrors(Arrays.asList(e.getMessage()));
            _logger.error(String.format(
                "Cannot update read set with id \"%s\" [%s] %s", _readSetId, e.getClass().getName(), e.getMessage())
            );

            return Action.ERROR;
        }

        return Action.SUCCESS;
    }


    public void setRunId(long runId) {

        _runId = runId;
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), ";") : Action.SUCCESS;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }
}