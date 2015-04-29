/**
 * 
 */
package ru.niifhm.bioinformatics.conveyor;


import java.io.File;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRunsFile;


/**
 * SeqsFile utility class.
 * @author zeleniy
 */
public class SeqsFileUtil {


    private SeqsFileUtil() {

    }


    /**
     * Delete SeqsFile entity with all relation dependencies.
     * @param fileId
     * @throws Exception
     */
    public static void delete(long fileId, boolean isDeleteFile) throws Exception {

        delete(DAO.findById(SeqsFile.class, fileId), isDeleteFile);
    }


    /**
     * Delete SeqsFile entity with all relation dependencies.
     * @param file
     * @throws Exception
     */
    public static void delete(SeqsFile file, boolean isDeleteFile) throws Exception {

        DAO.deleteById(SeqsRunsFile.class, file.getFileId());
        DAO.deleteById(SeqsFile.class, file.getFileId());

        if (isDeleteFile) {
            new File(file.getPath()).delete();
        }
    }
}
