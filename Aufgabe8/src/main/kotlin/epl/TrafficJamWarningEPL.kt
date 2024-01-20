package epl

fun trafficJamWarningEPL(): String = """
    @name('TrafficJamWarningCalculation')
    insert into models.TrafficJamWarningEvent
    select sensorId, (prev(avgSpeed) - avgSpeed) as avgSpeedDrop
    from AvgSpeedSensorEvent#time(AppConfig.TIME_WINDOW_TRAFFIC_JAM seconds)
    where (prev(avgSpeed) - avgSpeed) >= AppConfig.THRESHOLD
    group by sensorId
"""