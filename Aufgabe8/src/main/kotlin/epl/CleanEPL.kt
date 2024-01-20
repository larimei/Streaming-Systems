package epl

fun cleanEPL(): String = """
    @name('CleanEvents') 
    select sensorId, speeds
    from SensorEvent
    group by sensorId
"""