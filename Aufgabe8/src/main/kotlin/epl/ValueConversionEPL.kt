package epl

fun valueConversionEPL(): String = """
    @name('ValueConversionCalculation')
    select sensorId, speed
    from CleanSensorEvent
    group by sensorId
"""