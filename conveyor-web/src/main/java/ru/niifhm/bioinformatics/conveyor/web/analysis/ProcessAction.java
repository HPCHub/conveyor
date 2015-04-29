/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.analysis;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.util.StringPool;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ProcessAction extends ActionSupport {


    private File                _metafile;
    private String[]            _columns;
    private boolean             _mds                  = false;
    private String              _mdsColor;
    private String              _mdsShape;
    private boolean             _heatplot             = false;
    private boolean             _randomForest         = false;
    private String[]            _randomForestValues;
    private boolean             _utest                = false;
    private String[]            _utestValues;
    private boolean             _isMetafileProcessing = false;
    private boolean             _cluster              = false;
    private static final Logger _logger               = Logger.getLogger(ProcessAction.class);


    /**
     * @return
     */
    private boolean _isMetafileProcessing() {

        return _isMetafileProcessing;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? Action.ERROR : Action.SUCCESS;
    }


    public void setMetafile(File metafile) {

        _metafile = metafile;
    }


    public void setHeatplot(Object heatplot) {

        _heatplot = true;
    }


    public void setMDS(Object mds) {

        _mds = true;
    }


    public void setMDSColor(String color) {

        _mdsColor = color;
    }


    public void setMDSShape(String shape) {

        _mdsShape = shape;
    }


    public void setRandomForest(Object randomForest) {

        _randomForest = true;
    }


    public void setRandomForestValues(String[] values) {

        _randomForestValues = values;
    }


    public void setUtest(Object utest) {

        _utest = true;
    }


    public void setUtestValues(String[] values) {

        _utestValues = values;
    }


    public void setCluster(Object cluster) {

        _cluster = true;
    }


    public void setMetafileProcessing(Object value) {

        _isMetafileProcessing = true;
    }


    private String _processMetafile() throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader(_metafile));
        String line = reader.readLine();
        reader.close();

        _columns = StringUtils.split(line, StringPool.TAB);

        return Action.SUCCESS;
    }


    /*
     * (non-Javadoc)
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    public String execute() {

        try {

            if (_isMetafileProcessing()) {
                return _processMetafile();
            }

        } catch (Exception e) {
            _logger.debug(String.format("Cannot execute action [%s] %s", e.getClass().getName(), e.getMessage()));
        }

        return Action.SUCCESS;
    }


    public String[] getColumns() {

        return _columns;
    }
}