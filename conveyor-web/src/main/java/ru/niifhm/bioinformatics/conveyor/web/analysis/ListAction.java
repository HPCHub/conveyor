/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor.web.analysis;


import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.solid.ReadSet;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ListAction extends ActionSupport {


    private int                 _start  = 0;
    private int                 _limit  = 25;
    private List<ReadSet>       _analyses;
    private static final Logger _logger = Logger.getLogger(ListAction.class);


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? Action.ERROR : Action.SUCCESS;
    }


    /*
     * (non-Javadoc)
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @SuppressWarnings("unchecked")
    public String execute() {

        try {

            /*
             * SELECT MAX(read_Set_id), tag FROM read_sets
             * GROUP BY tag
             * ORDER BY MAX(read_Set_id) DESC
             */
            _analyses = DAO.getCriteria(ReadSet.class, DAO.SHEME_SOLID)
                .setProjection(Projections.projectionList()
                    .add(Projections.property("tag"), "tag")
                    .add(Projections.max("readSetId"), "readSetId")
                    .add(Projections.groupProperty("tag")))
                .addOrder(Order.desc("readSetId"))
                .setFirstResult(_start)
                .setMaxResults(_limit)
                .setResultTransformer(Transformers.aliasToBean(ReadSet.class))
            .list();

        } catch (Exception e) {
            _logger.debug(String.format("Cannot execute action [%s] %s", e.getClass().getName(), e.getMessage()));
        }

        return Action.SUCCESS;
    }


    public long getTotalCount() {

        return (Long) DAO.getCriteria(ReadSet.class, DAO.SHEME_SOLID)
            .setProjection(Projections.countDistinct("tag"))
        .uniqueResult();
    }


    public List<ReadSet> getAnalyses() {

        return _analyses;
    }


    public void setLimit(int limit) {

        _limit = limit;
    }


    public void setStart(int start) {

        _start = start;
    }
}