/**
 * 
 */
package com.ripcm.bioinformatics.conveyor.service;


import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import static org.mockito.Mockito.*;


/**
 * @author zeleniy
 */
public class FilesDispatcherTest {


    private FilesDispatcher    _dispatcher;
    public final static String FILE_CSFASTA  = "/home/zeleniy/workspace/jsub/src/test/resources/test.csfasta";
    public final static String FILE_QUAL     = "/home/zeleniy/workspace/jsub/src/test/resources/test.qual";
    private final int          _PROCESS_TIME = 3000;


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }


    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        _dispatcher = FilesDispatcher.newInstance();
    }


    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {

    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.conveyor.FilesDispatcher#getInstance()}.
     */
    @Test
    public void testGetInstance() {

        assertTrue(FilesDispatcher.getInstance() instanceof FilesDispatcher);
    }


    /**
     * Test method for
     * {@link ru.niifhm.bioinformatics.conveyor.FilesDispatcher#canExecute(ru.niifhm.bioinformatics.conveyor.activemq.FilesProcessor)}
     * .
     */
    @Test
    @Ignore
    public void testCanExecute() {

//        Map<String, String> properties = new HashMap<String, String>();
//        properties.put(Property.getInputPropertyNameByFilename(FILE_CSFASTA), FILE_CSFASTA);
//        FilesProcessor processor1 = _getMockedFileProcessor(new ReadSetTaskProcessor(new PropertiesTask(
//            "scenarioType1", properties, ReadSetTaskProcessor.class.getName()
//        )));
//
//        properties = new HashMap<String, String>();
//        properties.put(Property.getInputPropertyNameByFilename(FILE_QUAL), FILE_QUAL);
//        FilesProcessor processor2 = _getMockedFileProcessor(new ReadSetTaskProcessor(new PropertiesTask(
//            "scenarioType1", properties, ReadSetTaskProcessor.class.getName()
//        )));
//
//        properties = new HashMap<String, String>();
//        properties.put(Property.getInputPropertyNameByFilename(FILE_CSFASTA), FILE_CSFASTA);
//        properties.put(Property.getInputPropertyNameByFilename(FILE_QUAL), FILE_QUAL);
//        FilesProcessor processor3 = new ReadSetTaskProcessor(new PropertiesTask(
//            "scenarioType1", properties, ReadSetTaskProcessor.class.getName()
//        ));
//
//        try {
//            assertTrue(_dispatcher.canExecute(processor1));
//            assertTrue(_dispatcher.canExecute(processor1));
//            assertTrue(_dispatcher.canExecute(processor2));
//            assertTrue(_dispatcher.canExecute(processor3));
//
//            _dispatcher.execute(processor1);
//            while (! processor1.isAlive());
//
//            assertFalse(_dispatcher.canExecute(processor1));
//            assertTrue(_dispatcher.canExecute(processor2));
//            assertFalse(_dispatcher.canExecute(processor3));
//
//            _dispatcher.execute(processor2);
//            while (! processor2.isAlive());
//
//            assertFalse(_dispatcher.canExecute(processor1));
//            assertFalse(_dispatcher.canExecute(processor2));
//            assertFalse(_dispatcher.canExecute(processor3));
//
//            processor1.join();
//            processor2.join();
//
//            assertTrue(_dispatcher.canExecute(processor1));
//            assertTrue(_dispatcher.canExecute(processor2));
//            assertTrue(_dispatcher.canExecute(processor3));
//        } catch (Exception e) {
//            fail(String.format("Somthing wrong with %s#canExecute()", _dispatcher.getClass().getName()));
//        }
    }


    private FilesProcessor _getMockedFileProcessor(FilesProcessor processor) {

        FilesProcessor stubedProcessor = spy(processor);

        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(_PROCESS_TIME);
                return null;
            }
        }).when(stubedProcessor).process();

        return stubedProcessor;
    }
}