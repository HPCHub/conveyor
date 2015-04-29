/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsProject;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsSample;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsSequencer;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class MassUploadAction extends ActionSupport {


    private File           _importFile;
    private List<Object[]> _table  = new ArrayList<Object[]>();
    private Logger         _logger = Logger.getLogger(MassUploadAction.class);


    public String execute() {

        try {
            /*
             * Create indexes for projects, runs and samples.
             */
            List<SeqsProject> projectsList = DAO.getCriteria(SeqsProject.class).list();
            Map<String, Long> projects = new HashMap<String, Long>(projectsList.size());
            for (SeqsProject project : projectsList) {
                projects.put(project.getName(), project.getProjectId());
            }

            List<SeqsRun> runsList = DAO.getCriteria(SeqsRun.class).list();
            Map<String, Long> runs = new HashMap<String, Long>(runsList.size());
            for (SeqsRun run : runsList) {
                runs.put(run.getName(), run.getRunId());
            }

            List<SeqsSample> samplesList = DAO.getCriteria(SeqsSample.class).list();
            Map<String, Long> samples = new HashMap<String, Long>(samplesList.size());
            for (SeqsSample sample : samplesList) {
                samples.put(sample.getName(), sample.getSampleId());
            }

            List<SeqsSequencer> sequencersList = DAO.getCriteria(SeqsSequencer.class).list();
            Map<String, Long> sequencers = new HashMap<String, Long>(sequencersList.size());
            for (SeqsSequencer sequencer : sequencersList) {
                sequencers.put(sequencer.getName(), sequencer.getSequencerId());
            }

            /*
             * Parse input xslt file.
             * See http://viralpatel.net/blogs/java-read-write-excel-file-apache-poi
             */
            FileInputStream inputStream = new FileInputStream(_importFile);
            XSSFWorkbook book = new XSSFWorkbook(inputStream);
            XSSFSheet sheet;
            int j = 1;
            for (int i = 0; i < book.getNumberOfSheets(); i ++) {
                sheet = book.getSheetAt(i);
                for (Row row : sheet) {
                    Cell cell0 = row.getCell(0);
                    Cell cell1 = row.getCell(1);
                    Cell cell2 = row.getCell(2);
                    Cell cell3 = row.getCell(3);
                    Cell cell4 = row.getCell(4);
                    if (cell0 == null || cell1 == null || cell2 == null || cell3 == null || cell4 == null) {
                        break;
                    }

                    String fileName = cell0.toString();
                    String readSetId = cell1.toString();
                    String sampleName = cell2.toString();
                    String runName = cell3.toString();
                    String projectName = cell4.toString();

                    long count = (Long) DAO.getCriteria(SeqsFile.class)
                        .setProjection(Projections.rowCount())
                        .add(Restrictions.eq("path", fileName))
                    .uniqueResult();

                    _logger.info(String.format("%s. [%s][%s] %s", j, count, new File(fileName).exists(), fileName));
                    j ++;

                    _table.add(new Object[] {
                        fileName,
                        new File(fileName).exists(),
                        count > 0,
                        readSetId,
                        sampleName,
                        samples.get(sampleName),
                        runName,
                        runs.get(runName),
                        projectName,
                        projects.get(projectName),
                        sheet.getSheetName(),
                        sequencers.get(sheet.getSheetName())
                    });
                }
            }
        } catch (FileNotFoundException e) {
            _logger.error(String.format("Cannot find importing file [%s] %s", e.getClass().getName(), e.getMessage()));
        } catch (IOException e) {
            _logger.error(String.format("Cannot read importing file [%s] %s", e.getClass().getName(), e.getMessage()));
        } catch (Exception e) {
            _logger.error(String.format("Cannot process importing file [%s] %s", e.getClass().getName(), e.getMessage()));
        }

        return Action.SUCCESS;
    }


    public void setImportFile(File importFile) {

        _importFile = importFile;
    }


    public List<Object[]> getTable() {

        return _table;
    }
}