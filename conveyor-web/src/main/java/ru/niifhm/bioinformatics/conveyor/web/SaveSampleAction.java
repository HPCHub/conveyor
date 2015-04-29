/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.Arrays;
import java.util.Date;
import org.hibernate.exception.ConstraintViolationException;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsSample;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class SaveSampleAction extends ActionSupport {


    private long   _sampleId;
    private String _name;
    private String _description;


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMessage() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), "<br />") : Action.SUCCESS;
    }


    public String execute() {

        try {
            SeqsSample sample;
            if (_sampleId > 0) {
                sample = DAO.findById(SeqsSample.class, _sampleId);
            } else {
                sample = new SeqsSample(new Date());
            }

            sample.setName(getName());
            sample.setDescription(getDescription());

            DAO.save(sample);
            _sampleId = sample.getSampleId();
        } catch (ConstraintViolationException e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot save sample [%s] Looks like sample with name \"%s\" already exists",
                e.getClass().getName(),
                getName()
            )));
        } catch (Exception e) {
            setActionErrors(Arrays.asList(String.format(
                "Cannot save sample [%s] %s", e.getClass().getName(), e.getMessage()
            )));
        }

        return Action.SUCCESS;
    }


    public long getSampleId() {

        return _sampleId;
    }


    public String getName() {

        return _name;
    }


    public String getDescription() {

        return _description;
    }


    public void setSampleId(long sampleId) {

        _sampleId = sampleId;
    }


    public void setName(String name) {

        _name = name;
    }


    public void setDescription(String description) {

        _description = description;
    }
}