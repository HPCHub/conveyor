/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web;


import java.util.List;
import org.hibernate.criterion.Order;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReference;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListReferencesAction {


    private int _start = 0;
    private int _limit = 25;


    public void setLimit(int limit) {

        _limit = limit;
    }


    public void setStart(int start) {

        _start = start;
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public long getTotalCount() {

        return DAO.getCount(SeqsReference.class);
    }


    public List<SeqsReference> getReferences() {

        return DAO.getCriteria(SeqsReference.class)
            .addOrder(Order.desc("referenceId"))
            .setFirstResult(_start)
            .setMaxResults(_limit)
            .list();
    }
}