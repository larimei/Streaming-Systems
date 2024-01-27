package epl

fun avgSpeedEPL(): String = """
    @name('AvgSpeedCalculation') 
    insert into AvgSpeedSensorEvent
    select sensorId, avg(speed) as avgSpeed
    from ValueConversionEvent#time_batch(AppConfig.TIME_WINDOW_AVERAGE seconds)
    group by sensorId
"""