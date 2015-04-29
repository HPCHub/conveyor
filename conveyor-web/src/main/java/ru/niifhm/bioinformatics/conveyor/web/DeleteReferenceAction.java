/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.Arrays;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReference;
import ru.niifhm.bioinformatics.conveyor.SeqsFileUtil;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class DeleteReferenceAction extends ActionSupport {


    private long   _referenceId;
    private Logger _logger = Logger.getLogger(DeleteReadSetAction.class);


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), StringPool.SEMICOLON) : Action.SUCCESS;
    }


    public String execute() {

        try {
            SeqsReference reference = DAO.findById(SeqsReference.class, _referenceId);
             DAO.deleteById(SeqsReference.class, reference.getReferenceId());
             SeqsFileUtil.delete(reference.getSeqsFile(), true);
        } catch (Exception e) {
            // TODO: Rollback
            _logger.error(String.format("Cannot delete reference with id [%s] %s", _referenceId,
                e.getClass().getName(), e.getMessage()));
            setActionErrors(Arrays.asList(e.getMessage()));
        }

        return Action.SUCCESS;
    }


    public void setReferenceId(long referenceId) {

        _referenceId = referenceId;
    }
}