/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.List;
import org.hibernate.criterion.Order;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFileType;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsSequencer;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListSequencersAction {


    public String execute() {

        return Action.SUCCESS;
    }


    public List<SeqsFileType> getTypes() {

        return DAO.getCriteria(SeqsSequencer.class)
            .addOrder(Order.desc("name"))
            .list();
    }
}