/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor;


import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReference;


/**
 * @author zeleniy
 */
public class SeqsReferenceUtil {


    public static void delete(SeqsReference reference) throws Exception {

        DAO.deleteById(SeqsReference.class, reference.getReferenceId());
        // getFile(file).delete();
    }
}
