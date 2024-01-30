package fi.juka.library

import android.content.Context
import fi.juka.library.database.TrainingData
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DataComparerTest {

    @Mock
    lateinit var mockedContext: Context

    private lateinit var dataComparer: DataComparer

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dataComparer = DataComparer(mockedContext)
    }

    @Test
    fun testPreprocessingWithEmptyTrainingData() {
        val activityData = listOf(1L to "Activity1", 2L to "Activity2")
        val trainingData = emptyMap<Long, List<TrainingData>>()

        dataComparer.preprocessing(activityData, trainingData)

        assertEquals(0, dataComparer.activityAverageTotalAccList.size)
    }

    @Test
    fun testPreprocessingWithNonEmptyTrainingData() {
        val activityData = listOf(1L to "Activity1", 2L to "Activity2")
        val trainingData = mapOf(
            1L to listOf(
                TrainingData(1, 1.2, 0.5, 0.6, 0.0,0,1),
                TrainingData(2, 1.4, 0.6, 0.5,0.0,0,1)
            ),
            2L to listOf(
                TrainingData(1, 3.5, 2.1, 2.4,0.0,0,2),
                TrainingData(2, 4.0, 2.3, 2.4,0.0,0,2)
            )
        )

        dataComparer.preprocessing(activityData, trainingData)

        assertEquals(2, dataComparer.activityAverageTotalAccList.size)
    }

    @Test
    fun testCompareDataAveragesForStayingStill() {
        val realTimeTotAvrgAcc = 0.05
        var recognizedActivity = ""

        dataComparer.compareDataAverages(realTimeTotAvrgAcc) { activity ->
            recognizedActivity = activity
        }

        assertEquals("Still", recognizedActivity)
    }

    @Test
    fun testCompareDataAveragesForRecognizedActivity() {

        val activityData = listOf(1L to "Walking", 2L to "Running")
        val trainingData = mapOf(
            1L to listOf(
                TrainingData(1, 1.2, 0.5, 0.6, 0.0,0,1),
                TrainingData(2, 1.4, 0.6, 0.5,0.0,0,1)
            ),
            2L to listOf(
                TrainingData(1, 3.5, 2.1, 2.4,0.0,0,2),
                TrainingData(2, 4.0, 2.3, 2.4,0.0,0,2)
            )
        )

        dataComparer.preprocessing(activityData, trainingData)

        val realTimeTotAvrgAcc = 3.0
        var recognizedActivity = ""

        dataComparer.compareDataAverages(realTimeTotAvrgAcc) { activity ->
            recognizedActivity = activity
        }

        assertEquals("Running", recognizedActivity)
    }

    @Test
    fun testCompareDataAveragesForUnrecognizedActivity() {

        val activityData = listOf(1L to "Walking", 2L to "Running")
        val trainingData = mapOf(
            1L to listOf(
                TrainingData(1, 1.2, 0.5, 0.6, 0.0,0,1),
                TrainingData(2, 1.4, 0.6, 0.5,0.0,0,1)
            ),
            2L to listOf(
                TrainingData(1, 3.5, 2.1, 2.4,0.0,0,2),
                TrainingData(2, 4.0, 2.3, 2.4,0.0,0,2)
            )
        )

        dataComparer.preprocessing(activityData, trainingData)

        val realTimeTotAvrgAcc = 8.0
        var recognizedActivity = ""
        dataComparer.compareDataAverages(realTimeTotAvrgAcc) { activity ->
            recognizedActivity = activity
        }

        assertEquals("", recognizedActivity)
    }
}