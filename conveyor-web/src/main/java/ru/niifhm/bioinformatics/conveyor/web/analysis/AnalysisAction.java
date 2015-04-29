/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.analysis;


import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.fusesource.hawtbuf.ByteArrayInputStream;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import ru.niifhm.bioinformatics.biodb.solid.ReadSet;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.file.BowtieStats;
import ru.niifhm.bioinformatics.jsub.file.BowtiesStatsFactory;
import ru.niifhm.bioinformatics.util.StringPool;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class AnalysisAction extends ActionSupport {


    private static final Logger  _logger     = Logger.getLogger(AnalysisAction.class);
    private Set<Long>            _COGIdSet   = new HashSet<Long>();
    private Set<Long>            _GOIdSet    = new HashSet<Long>();
    private Set<Long>            _genusIdSet = new HashSet<Long>();
    private Set<Long>            _orgIdSet   = new HashSet<Long>();
    private List<String>         _lines      = new ArrayList<String>();
    private static Pattern       _tagPattern = Pattern.compile("(.*)_\\d+");
    private Map<String, JsubRun> _jsubRuns   = new HashMap<String, JsubRun>();
    private static String        _TYPE_ORG   = "org";
    private static String        _TYPE_COG   = "COG";
    private static String        _TYPE_GO    = "GO";
    private static String        _TYPE_GENUS = "genus";
    private String               _fileName;
    private String               _type;


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? Action.ERROR : Action.SUCCESS;
    }


    @SuppressWarnings("unchecked")
    public void prepare() {

        Set<Long> searchIdSet;
        if (_COGIdSet.size() != 0) {
            searchIdSet = _COGIdSet;
            _type = _TYPE_COG;
        } else if (_GOIdSet.size() != 0) {
            searchIdSet = _GOIdSet;
            _type = _TYPE_GO;
        } else if (_genusIdSet.size() != 0) {
            searchIdSet = _genusIdSet;
            _type = _TYPE_GENUS;
        } else {
            searchIdSet = _orgIdSet;
            _type = _TYPE_ORG;
        }

        List<ReadSet> readSets = DAO.getCriteria(ReadSet.class, DAO.SHEME_SOLID)
            .add(Restrictions.in("readSetId", searchIdSet))
        .list();

        for (ReadSet readSet : readSets) {
            String tag = _getTag(readSet.getTag());

            /*
             * Try to get exactly those file, which corresponds to tag.
             */
            SeqsFile seqsFile = (SeqsFile) DAO.getCriteria(SeqsFile.class, "seqsFile", DAO.SHEME_MSDBA)
                .createAlias("seqsFile.seqsReadSet", "seqsReadSet", CriteriaSpecification.INNER_JOIN)
                .createAlias("seqsReadSet.seqsSample", "seqsSample", CriteriaSpecification.INNER_JOIN)
                .add(Restrictions.eq("path", readSet.getLocation()))
                .add(Restrictions.eq("seqsSample.name", tag))
            .uniqueResult();

            if (seqsFile == null) {
                continue;
            }

            JsubRun jsubRun = (JsubRun) DAO.getCriteria(JsubRun.class)
                .createAlias("jsubRunFiles", "jsubRunFiles", CriteriaSpecification.INNER_JOIN)
                .add(Restrictions.eq("jsubRunFiles.fileId", seqsFile.getFileId()))
            .uniqueResult();

            if (jsubRun == null) {
                continue;
            }

            _jsubRuns.put(tag, jsubRun);
        }
    }


    public String stats() {

        try {

            prepare();
            _fileName = "stats.txt";

            /*
             * Traverse through jsub project directories.
             */
            _lines.add("name\tmapped-to-human\tmapped-to-reference");
            for (Map.Entry<String, JsubRun> entry : _jsubRuns.entrySet()) {

                JsubRun jsubRun = entry.getValue();

                File directory = Pipeline.newInstance(
                    jsubRun.getProjectName(), jsubRun.getProjectTag(), jsubRun.getProjectType(),
                    jsubRun.getProjectDirectory()
                ).getLogDirectory();

                /*
                 * Read mapping stats files.
                 */
                BowtieStats bowtieStats = BowtiesStatsFactory.getInstance(directory);
                _lines.add(String.format(
                    "%s\t%s\t%s",
                    entry.getKey(),
                    bowtieStats.getHumanMappingPercent(),
                    bowtieStats.getReferenceMappingPercent()
                ));
            }

        } catch (Exception e) {
            _logger.error(String.format("Cannot execute action [%s] %s", e.getClass().getName(), e.getMessage()));
        }

        return Action.SUCCESS;
    }


    public String coverage() {

        try {

            prepare();
            _fileName = String.format("coverage.%s.txt", _type);

            /*
             * Traverse through jsub project directories.
             */
            for (Map.Entry<String, JsubRun> entry : _jsubRuns.entrySet()) {

                JsubRun jsubRun = entry.getValue();

                File directory = Pipeline.newInstance(
                    jsubRun.getProjectName(), jsubRun.getProjectTag(), jsubRun.getProjectType(),
                    jsubRun.getProjectDirectory()
                ).getOutputDir();

                /*
                 * Get coverage files.
                 */
                File[] files = directory.listFiles(new CoverageFileFilter(_type));
                if (files == null) {
                    continue;
                }

                /*
                 * Read coverage file.
                 */
                List<String> lines = IOUtils.readLines(new FileInputStream(files[0]));
                if (_lines.size() == 0) {
                    _lines.add(lines.get(0));
                }

                _lines.add(lines.get(1));
            }

        } catch (Exception e) {
            _logger.error(String.format("Cannot execute action [%s] %s", e.getClass().getName(), e.getMessage()));
        }

        return Action.SUCCESS;
    }


    private static String _getTag(String string) {

        Matcher matcher = _tagPattern.matcher(string);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return string;
        }
    }


    public String getFilename() {

        return _fileName;
    }


    public InputStream getFileInputStream() {

        return new ByteArrayInputStream(StringUtils.join(_lines, StringPool.NEW_LINE).getBytes());
    }


    public void setCogReadSet(String[] readSetId) {

        for (String id : readSetId) {
            _COGIdSet.add(Long.parseLong(id));
        }
    }


    public void setGoReadSet(String[] readSetId) {

        for (String id : readSetId) {
            _GOIdSet.add(Long.parseLong(id));
        }
    }


    public void setGenusReadSet(String[] readSetId) {

        for (String id : readSetId) {
            _genusIdSet.add(Long.parseLong(id));
        }
    }


    public void setOrgReadSet(String[] readSetId) {

        for (String id : readSetId) {
            _orgIdSet.add(Long.parseLong(id));
        }
    }


    private class CoverageFileFilter implements FilenameFilter {


        private Pattern _pattern;


        public CoverageFileFilter(String type) {

            super();
            _pattern = Pattern.compile(".*\\." + type + "\\.txt$");
        }


        public boolean accept(File dir, String name) {

            if (_pattern.matcher(name).matches()) {
                return true;
            } else {
                return false;
            }
        }
    }
}