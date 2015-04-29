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
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFileType;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReference;
import ru.niifhm.bioinformatics.conveyor.tool.ReferenceProcessor;
import ru.niifhm.bioinformatics.util.StringUtil;
import ru.niifhm.bioinformatics.util.io.FileUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.ripcm.bioinformatics.conveyor.core.XConfig;


/**
 * @author zeleniy
 */
public class SaveReferenceAction extends ActionSupport {


    private long   _referenceId;
    private File   _file;
    private String _fileName;
    private String _organism;
    private String _description;
    private String _type;
    private Logger _logger = Logger.getLogger(SaveReferenceAction.class);


    public void setType(String type) {

        _type = type;
    }


    public void setUpload(File file) {

        _file = file;
    }


    public void setUploadContentType(String contentType) {

    }


    public void setUploadFileName(String fileName) {

        _fileName = fileName;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), ";") : Action.SUCCESS;
    }


    public String execute() {

        SeqsFile seqsFile;
        SeqsReference reference;
        File file = null;

        try {
            if (_referenceId > 0) {
                reference = DAO.findById(SeqsReference.class, _referenceId);
                seqsFile = reference.getSeqsFile();
            } else {
                if (_file == null) {
                    throw new Exception("File is empty (null)");
                }

                reference = new SeqsReference();
                String extension = FileUtil.getExtension(_fileName);
                SeqsFileType fileType = (SeqsFileType) DAO.getCriteria(SeqsFileType.class)
                    .add(Restrictions.eq("type", extension))
                    .uniqueResult();

                if (fileType == null) {
                    throw new Exception(String.format("Unexpected file extension \"%s\"", extension));
                }

                seqsFile = new SeqsFile();
                seqsFile.setName(_fileName);
                seqsFile.setSeqsFileType(fileType);
                seqsFile.setSize(_file.length());
                seqsFile.setPath(_getUploadedFilePath());

                _saveUploadedFile();
            }

            seqsFile.setDescription(_description);
            DAO.save(seqsFile);

            reference.setOrganism(_organism);
            reference.setSeqsFile(seqsFile);
            reference.setType(_type);

            DAO.save(reference);

        } catch (Exception e) {
            // TODO: Rollback

            if (_file != null && file != null) {
                file.delete();
            }

            _logger.error(String.format(
                "Cannot save reference [%s] %s", e.getClass().getName(), e.getMessage()));
            setActionErrors(Arrays.asList(e.getMessage()));
        }

        return Action.SUCCESS;
    }


    private String _getUploadedFilePath() {

        return new StringBuilder(XConfig.getInstance().getProperty("referencesStore"))
            .append(File.separator)
            .append(_fileName)
            .toString();
    }


    private File _saveUploadedFile() throws Exception {

        String fileDirectoryPath = XConfig.getInstance().getProperty("referencesStore");

        File directory = new File(fileDirectoryPath);
        if (! directory.exists()) {
            if (! directory.mkdirs()) {
                throw new Exception(String.format("Cannot create directory \"%s\"", fileDirectoryPath));
            }
        }

        String filePath = new StringBuilder(fileDirectoryPath)
            .append(File.separator)
            .append(_fileName)
            .toString();

        File file = new File(filePath);
        if (file.exists()) {
            throw new Exception(String.format("File \"%s\" already exists", filePath));
        }

        if (! file.createNewFile()) {
            throw new Exception(String.format("Cannot create file \"%s\"", filePath));
        }

        FileUtil.copy(_file, file);
        file.setWritable(true);

        new ReferenceProcessor()
            .setReference(file)
            .start();

        return file;
    }


    public long getReferenceId() {

        return _referenceId;
    }


    public void setReferenceId(long referenceId) {

        _referenceId = referenceId;
    }


    public void setOrganism(String organism) {

        _organism = organism;
    }


    public void setDescription(String description) {

        _description = description;
    }
}