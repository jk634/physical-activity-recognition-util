package fi.juka.library

/**
 * Utility class for calculating average speed and total acceleration based on acceleration data.
 */
class AccelerationUtils {
    companion object {

        /**
         * Calculates the average speed based on acceleration data along three axes and corresponding timestamps.
         *
         * @param x     List of acceleration values along the X-axis
         * @param y     List of acceleration values along the Y-axis
         * @param z     List of acceleration values along the Z-axis
         * @param time  List of timestamps corresponding to acceleration data
         * @return      The average speed calculated from the provided acceleration data
         */
        fun calculateAverageSpeed(x: List<Double>, y: List<Double>, z: List<Double>, time: List<Long>): Double {

            var totalDistance = 0.0
            var totalTime = 0.0

            for (i in 1 until x.size) {
                val timeDifference = (time[i] - time[i - 1]) / 1000.0 // time in seconds
                val distanceX = Math.abs(x[i] - x[i - 1])
                val distanceY = Math.abs(y[i] - y[i - 1])
                val distanceZ = Math.abs(z[i] - z[i - 1])

                val distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ)
                totalDistance += distance
                totalTime += timeDifference
            }

            return totalDistance / totalTime
        }

        /**
         * Calculates the average total acceleration based on acceleration data along three axes.
         *
         * @param x     List of acceleration values along the X-axis
         * @param y     List of acceleration values along the Y-axis
         * @param z     List of acceleration values along the Z-axis
         * @return      The average total acceleration calculated from the provided acceleration data
         */
        fun calculateAverageTotalAcceleration(x: List<Double>, y: List<Double>, z: List<Double>): Double {

            var totalAcceleration = 0.0

            for (i in 1 until x.size) {
                val accX = x[i]
                val accY = y[i]
                val accZ = z[i]

                val acceleration = Math.sqrt(accX * accX + accY * accY + accZ * accZ)
                totalAcceleration += acceleration
            }

            return totalAcceleration / x.size
        }
    }
}