/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.Arrays;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReadSet;
import ru.niifhm.bioinformatics.conveyor.SeqsFileUtil;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.ripcm.bioinformatics.conveyor.core.XConfig;


/**
 * @author zeleniy
 */
public class DeleteReadSetAction extends ActionSupport {


    private long    _readSetId;
    private boolean _withFile = false;
    private Logger  _logger    = Logger.getLogger(DeleteReadSetAction.class);


    public void setWithFile(boolean withFile) {

        _withFile = withFile;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMessage() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), "<br />") : Action.SUCCESS;
    }


    public String execute() {

        try {
            SeqsReadSet readSet = DAO.findById(SeqsReadSet.class, _readSetId);

            SeqsFile file = readSet.getSeqsFile();
            String fileStorePath = XConfig.getInstance().getProperty("filesStore");

            // /*
            // * Do not delete files beyond the store.
            // */
            // boolean isDeleteFile = true;
            // if (file.getPpath() != null && ! file.getPpath().startsWith(fileStorePath)) {
            // isDeleteFile = false;
            // }
            //
            // if (! file.getPath().startsWith(fileStorePath)) {
            // isDeleteFile = false;
            // }

            DAO.deleteById(SeqsReadSet.class, readSet.getReadSetId());
            SeqsFileUtil.delete(readSet.getSeqsFile(), _withFile);
        } catch (Exception e) {
            // TODO: Rollback
            String message = String.format(
                "Cannot delete file with id [%s] %s", _readSetId, e.getClass().getName(), e.getMessage()
            );

            _logger.error(message);
            setActionErrors(Arrays.asList(message));
        }

        return Action.SUCCESS;
    }


    public void setReadSetId(long readSetId) {

        _readSetId = readSetId;
    }
}