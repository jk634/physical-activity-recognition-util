package fi.juka.library

import android.content.Context
import fi.juka.library.database.TrainingData
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DataCompareTest {

    @Mock
    lateinit var mockedContext: Context

    private lateinit var dataCompare: DataCompare

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        dataCompare = DataCompare(mockedContext)
    }

    @Test
    fun testPreprocessingWithEmptyTrainingData() {
        val activityData = listOf(1L to "Activity1", 2L to "Activity2")
        val trainingData = emptyMap<Long, List<TrainingData>>()

        dataCompare.preprocessing(activityData, trainingData)

        assertEquals(0, dataCompare.activityAverageTotalAccList.size)
    }

    @Test
    fun testPreprocessingWithNonEmptyTrainingData() {
        val activityData = listOf(1L to "Activity1", 2L to "Activity2")
        val trainingData = mapOf(
            1L to listOf(
                TrainingData(1, 1.2F, 0.5F, 0.6F, 0.0F,0,1),
                TrainingData(2, 1.4F, 0.6F, 0.5F,0.0F,0,1)
            ),
            2L to listOf(
                TrainingData(1, 3.5F, 2.1F, 2.4F,0.0F,0,2),
                TrainingData(2, 4.0F, 2.3F, 2.4F,0.0F,0,2)
            )
        )

        dataCompare.preprocessing(activityData, trainingData)

        assertEquals(2, dataCompare.activityAverageTotalAccList.size)
    }

    @Test
    fun testCompareDataAveragesForStayingStill() {
        val realTimeTotAvrgAcc = 0.05
        var recognizedActivity = ""

        dataCompare.compareDataAverages(realTimeTotAvrgAcc) { activity ->
            recognizedActivity = activity
        }

        assertEquals("Still", recognizedActivity)
    }

    @Test
    fun testCompareDataAveragesForRecognizedActivity() {

        val activityData = listOf(1L to "Walking", 2L to "Running")
        val trainingData = mapOf(
            1L to listOf(
                TrainingData(1, 1.2F, 0.5F, 0.6F, 0.0F,0,1),
                TrainingData(2, 1.4F, 0.6F, 0.5F,0.0F,0,1)
            ),
            2L to listOf(
                TrainingData(1, 3.5F, 2.1F, 2.4F,0.0F,0,2),
                TrainingData(2, 4.0F, 2.3F, 2.4F,0.0F,0,2)
            )
        )

        dataCompare.preprocessing(activityData, trainingData)

        val realTimeTotAvrgAcc = 3.0
        var recognizedActivity = ""

        dataCompare.compareDataAverages(realTimeTotAvrgAcc) { activity ->
            recognizedActivity = activity
        }

        assertEquals("Running", recognizedActivity)
    }

    @Test
    fun testCompareDataAveragesForUnrecognizedActivity() {

        val activityData = listOf(1L to "Walking", 2L to "Running")
        val trainingData = mapOf(
            1L to listOf(
                TrainingData(1, 1.2F, 0.5F, 0.6F, 0.0F,0,1),
                TrainingData(2, 1.4F, 0.6F, 0.5F,0.0F,0,1)
            ),
            2L to listOf(
                TrainingData(1, 3.5F, 2.1F, 2.4F,0.0F,0,2),
                TrainingData(2, 4.0F, 2.3F, 2.4F,0.0F,0,2)
            )
        )

        dataCompare.preprocessing(activityData, trainingData)

        val realTimeTotAvrgAcc = 8.0
        var recognizedActivity = ""
        dataCompare.compareDataAverages(realTimeTotAvrgAcc) { activity ->
            recognizedActivity = activity
        }

        assertEquals("", recognizedActivity)
    }
}