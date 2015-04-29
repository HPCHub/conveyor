/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;


/**
 * @author zeleniy
 */
public class FilesDispatcher {


    private static final Log                _logger = LogFactory.getLog(FilesDispatcher.class);
    // private List<FilesProcessor> _processes = new ArrayList<FilesProcessor>();
    private static volatile FilesDispatcher _instance;


    /**
     * @return
     */
    public static FilesDispatcher getInstance() {

        if (_instance == null) {
            synchronized (FilesDispatcher.class) {
                if (_instance == null) {
                    _instance = new FilesDispatcher();
                }
            }
        }

        return _instance;
    }


    public static FilesDispatcher newInstance() {

        return new FilesDispatcher();
    }


    // public void remove(FilesProcessor processor) {
    //
    // synchronized(this) {
    // _processes.remove(processor);
    // }
    // }

    public void execute(FilesProcessor processor) {

        try {
            // _processes.add(processor);
            // processor.setDispatcher(this);
            processor.start();
        } catch (Exception e) {
            // _processes.remove(processor);
            _logger.error(String.format("Cannot execute processor [%s] %s", e.getClass().getName(), e.getMessage()));
        }
    }


    public boolean canExecute(FilesProcessor processor) throws Exception {

        List<String> files = processor.getFiles();

        // for (String file : files) {
        // System.out.println("FILE: " + file);
        // }

        List<Long> filesIds = DAO.getCriteria(SeqsFile.class)
            .setProjection(Projections.property("fileId"))
            .add(Restrictions.disjunction()
                .add(Restrictions.in("path", files))
                .add(Restrictions.in("ppath", files))
            ).list();

//        for (long id : filesIds) {
//            System.out.println("ID: " + id);
//        }

        // SELECT * FROM jsub$runs JOIN jsub$run_files USING(run_id)
        // WHERE (file_id IN(52484567) OR pathname IN('/')) AND
        // stop_date IS NULL AND
        // files_lock_mode = 1

        Criterion criterion;
        if (filesIds.size() == 0) {
            criterion = Restrictions.in("jsubRunFile.pathname", files);
        } else {
            criterion = Restrictions.disjunction()
                .add(Restrictions.in("jsubRunFile.fileId", filesIds))
                .add(Restrictions.in("jsubRunFile.pathname", files));
        }

        Criteria criteria = DAO.getCriteria(JsubRun.class)
            .createAlias("jsubRunFiles", "jsubRunFile", CriteriaSpecification.INNER_JOIN)
            .add(criterion)
            .add(Restrictions.isNull("startDate"))
            .add(Restrictions.eq("filesLockMode", FilesProcessor.BLOCK_WRITE));

        // if (filesIds.size() == 0) {
        // criteria.add(Restrictions.in("jsubRunFile.pathname", files));
        // } else {
        // criteria.add(Restrictions.disjunction()
        // .add(Restrictions.in("jsubRunFile.fileId", filesIds))
        // .add(Restrictions.in("jsubRunFile.pathname", files)));
        // }

        List<JsubRun> jsubRuns = criteria.list();

        if (jsubRuns.size() > 0) {
            return false;
        }

        long requstedBlockMode = processor.getBlockMode();

        /*
         * if request to read
         */
        if (requstedBlockMode == FilesProcessor.BLOCK_READ) {
            return jsubRuns.size() == 0;
        }

        /*
         * if request to write
         */
        jsubRuns = DAO.getCriteria(JsubRun.class)
            .createAlias("jsubRunFiles", "jsubRunFile", CriteriaSpecification.INNER_JOIN)
            .add(criterion)
            .add(Restrictions.isNull("startDate"))
            .list();

        return jsubRuns.size() == 0;
        // if (blockMode == FilesProcessor.BLOCK_READ) {
        // List<String> filesInUse = new ArrayList<String>();
        // for (FilesProcessor fp : _getFilesProcessorsByBlockMode(FilesProcessor.BLOCK_WRITE)) {
        // filesInUse.addAll(fp.getFiles());
        // }
        //
        // return _canExecute(filesInUse, files);
        // }
        //
        // if (blockMode == FilesProcessor.BLOCK_WRITE) {
        // List<String> filesInUse = new ArrayList<String>();
        // for (FilesProcessor fp : _processes) {
        // filesInUse.addAll(fp.getFiles());
        // }
        //
        // return _canExecute(filesInUse, files);
        // }
        //
        // throw new Exception(String.format("Unexpected block mode [%d]", blockMode));
    }

    // public List<String> getFiles() {

    // List<String> files = new ArrayList<String>();
    // for (FilesProcessor processor : _processes) {
    // files.addAll(processor.getFiles());
    // }
    //
    // return files;
    // }

    // private boolean _canExecute(List<String> filesInUse, List<String> newFiles) {
    //
    // for (String file : newFiles) {
    // if (filesInUse.contains(file)) {
    // return false;
    // }
    // }
    //
    // return true;
    // }
    //
    //
    // private List<FilesProcessor> _getFilesProcessorsByBlockMode(int blockMode) {
    //
    // List<FilesProcessor> result = new ArrayList<FilesProcessor>();
    // for (FilesProcessor processor : _processes) {
    // if (processor.getBlockMode() == blockMode) {
    // result.add(processor);
    // }
    // }
    //
    // return result;
    // }
}