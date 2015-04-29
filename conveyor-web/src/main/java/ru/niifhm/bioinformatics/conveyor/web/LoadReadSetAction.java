/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReadSet;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class LoadReadSetAction {


    private long _readSetId;


    public SeqsReadSet getFile() {

        return DAO.findById(SeqsReadSet.class, _readSetId);
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public void setReadSetId(long readSetId) {

        _readSetId = readSetId;
    }
}