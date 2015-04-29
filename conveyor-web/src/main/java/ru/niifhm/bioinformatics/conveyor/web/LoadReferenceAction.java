/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.Arrays;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReference;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class LoadReferenceAction extends ActionSupport {


    private long   _referenceId;
    private Logger _logger = Logger.getLogger(LoadReferenceAction.class);


    public SeqsReference getReference() {

        try {
            return DAO.findById(SeqsReference.class, _referenceId);
        } catch (Exception e) {
            _logger.error(String.format("Cannot load reference [%s] %s", e.getClass().getName(), e.getMessage()));
            setActionErrors(Arrays.asList(e.getMessage()));

            return new SeqsReference();
        }
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public void setReferenceId(long referenceId) {

        _referenceId = referenceId;
    }

}