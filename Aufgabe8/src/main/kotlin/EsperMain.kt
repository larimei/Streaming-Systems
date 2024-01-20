import com.espertech.esper.common.client.configuration.Configuration
import com.espertech.esper.compiler.client.CompilerArguments
import com.espertech.esper.compiler.client.EPCompilerProvider
import com.espertech.esper.runtime.client.EPDeployment
import com.espertech.esper.runtime.client.EPRuntime
import com.espertech.esper.runtime.client.EPRuntimeProvider.getDefaultRuntime
import com.espertech.esper.runtime.client.UpdateListener
import generator.DataGenerator
import generator.GeneratorConfig
import kotlin.random.Random

fun main() {
    val runtime = setupEsperRuntime()

    val cleanDeployment = compileAndDeploy(runtime, cleanEPL())
    setupListener(runtime, cleanDeployment, "CleanEvents", CleanSensorEventListener())

    val avgSpeedDeployment = compileAndDeploy(runtime, avgSpeedEPL())
    setupListener(runtime, avgSpeedDeployment, "AvgSpeedCalculation", AvgSpeedEventListener())

    val trafficJamWarningDeployment = compileAndDeploy(runtime, trafficJamWarningEPL())
    setupListener(runtime, trafficJamWarningDeployment, "TrafficJamWarningCalculation", TrafficJamWarningEventListener())

    val generator = DataGenerator(
        GeneratorConfig.SENSOR_COUNT,
        GeneratorConfig.VALUE_COUNT,
        GeneratorConfig.MIN_SPEED,
        GeneratorConfig.MAX_SPEED
    )

    generateAndSendEvents(runtime, generator)
}

fun setupEsperRuntime(): EPRuntime {
    val configuration = Configuration().apply {
        common.addEventType("SensorEvent", SensorEvent::class.java)
        common.addEventType("SensorEvent", SensorEvent::class.java)
        common.addEventType("CleanSensorEvent", CleanSensorEvent::class.java)
        common.addEventType("AvgSpeedSensorEvent", AvgSpeedSensorEvent::class.java)
        common.addEventType("TrafficJamWarningEvent", TrafficJamWarningEvent::class.java)
    }

    val runtime = getDefaultRuntime(configuration)
    runtime.initialize()
    return runtime
}

fun compileAndDeploy(runtime: EPRuntime, epl: String): EPDeployment {
    val compiler = EPCompilerProvider.getCompiler()
    val compiled = compiler.compile(epl, CompilerArguments(runtime.configurationDeepCopy))
    return runtime.deploymentService.deploy(compiled)
}

fun setupListener(runtime: EPRuntime, deployment: EPDeployment, statementName: String, listener: UpdateListener) {
    val statement = runtime.deploymentService.getStatement(deployment.deploymentId, statementName)
    statement.addListener(listener)
}

fun cleanEPL(): String = """
    @name('CleanEvents') 
    select sensorId, speeds
    from SensorEvent
    group by sensorId
"""

fun avgSpeedEPL(): String = """
    @name('AvgSpeedCalculation') 
    insert into AvgSpeedSensorEvent
    select sensorId, avg(speed) as avgSpeed
    from CleanSensorEvent#time_batch(AppConfig.TIME_WINDOW_AVERAGE seconds)
    group by sensorId
"""

fun trafficJamWarningEPL(): String = """
    @name('TrafficJamWarningCalculation')
    insert into TrafficJamWarningEvent
    select sensorId, (prev(avgSpeed) - avgSpeed) as avgSpeedDrop
    from AvgSpeedSensorEvent#time(AppConfig.TIME_WINDOW_TRAFFIC_JAM seconds)
    where (prev(avgSpeed) - avgSpeed) >= AppConfig.THRESHOLD
    group by sensorId
"""



fun generateAndSendEvents(runtime: EPRuntime, generator: DataGenerator) {
    while (true) {
        val data = generator.generateData()
        println(data)
        runtime.eventService.sendEventBean(data, "SensorEvent")
        Thread.sleep(Random.nextLong(GeneratorConfig.MIN_INTERVAL.toLong(), GeneratorConfig.MAX_INTERVAL.toLong()))
    }
}


/*fun main() {




    val compiler: EPCompiler = EPCompilerProvider.getCompiler()
    val args = CompilerArguments(configuration)

    val runtime = getDefaultRuntime(configuration)
    runtime.initialize()

    val cleanEPL = """
        @name('CleanEvents') 
        select sensorId, speeds
        from SensorEvent
        group by sensorId
    """

    val cleanCompiled: EPCompiled = compiler.compile(cleanEPL, args)
    val cleanDeployment = runtime.deploymentService.deploy(cleanCompiled)
    val cleanStatement =
        runtime.deploymentService.getStatement(cleanDeployment.deploymentId, "CleanEvents")
    cleanStatement.addListener(CleanSensorEventListener())

    val avgSpeedEPL = """
        @name('AvgSpeedCalculation') 
        insert into AvgSpeedSensorEvent
        select sensorId, avg(speed) as avgSpeed
        from CleanSensorEvent#time_batch(AppConfig.TIME_WINDOW_AVERAGE seconds)
        group by sensorId
    """
    val avgSpeedCompiled: EPCompiled = compiler.compile(avgSpeedEPL, args)
    val avgSpeedDeployment = runtime.deploymentService.deploy(avgSpeedCompiled)
    val avgSpeedStatement =
        runtime.deploymentService.getStatement(avgSpeedDeployment.deploymentId, "AvgSpeedCalculation")
    avgSpeedStatement.addListener(AvgSpeedEventListener())


    val trafficJamWarningEPL = """
    @name('TrafficJamWarningCalculation')
    insert into TrafficJamWarningEvent
    select sensorId, (prev(avgSpeed) - avgSpeed) as avgSpeedDrop
    from AvgSpeedSensorEvent#time(AppConfig.TIME_WINDOW_TRAFFIC_JAM seconds)
    where (prev(avgSpeed) - avgSpeed) >= AppConfig.THRESHOLD
    group by sensorId
"""
    val trafficJamWarningCompiled: EPCompiled = compiler.compile(trafficJamWarningEPL, args)
    val trafficJamWarningDeployment = runtime.deploymentService.deploy(trafficJamWarningCompiled)
    val trafficJamWarningStatement =
        runtime.deploymentService.getStatement(trafficJamWarningDeployment.deploymentId, "TrafficJamWarningCalculation")
    trafficJamWarningStatement.addListener(TrafficJamWarningEventListener())

    val generator = DataGenerator(
        GeneratorConfig.SENSOR_COUNT,
        GeneratorConfig.VALUE_COUNT,
        GeneratorConfig.MIN_SPEED,
        GeneratorConfig.MAX_SPEED
    )

    /*while (true) {
        val data = generator.generateData()
        //println(data)
        runtime.eventService.sendEventBean(data, "SensorEvent")

        Thread.sleep(Random.nextLong(GeneratorConfig.MIN_INTERVAL.toLong(), GeneratorConfig.MAX_INTERVAL.toLong()))


    }*/
    var speedReducer = 30.0
    while (true) {
        val speeds =
        //SensorEvent(1,speeds ).let { runtime.eventService.sendEventBean(it, "SensorEvent") }
        SensorEvent(2, listOf(80.0, 90.0, 100.0 , 110.0, 120.0 )).let { runtime.eventService.sendEventBean(it, "SensorEvent") }
//        SensorEvent(3, speeds).let { runtime.eventService.sendEventBean(it, "SensorEvent") }
        speedReducer += 3.0
        println("Speed reduced to $speedReducer")
        Thread.sleep(4000)
    }





    /*Thread.sleep(8000)

    SensorEvent(2, listOf(10.0, 20.0, 30.0, 40.0, 50.0)).let { runtime.eventService.sendEventBean(it, "SensorEvent") }
    SensorEvent(1, listOf(10.0, 20.0, 30.0, 40.0, 50.0)).let { runtime.eventService.sendEventBean(it, "SensorEvent") }
    SensorEvent(3, listOf(10.0, 20.0, 30.0, 40.0, 50.0)).let { runtime.eventService.sendEventBean(it, "SensorEvent") }

    Thread.sleep(8000)


    SensorEvent(2, listOf(1.0, 2.0, 3.0, 4.0, 5.0)).let { runtime.eventService.sendEventBean(it, "SensorEvent") }
    SensorEvent(1, listOf(1.0, 2.0, 3.0, 4.0, 5.0)).let { runtime.eventService.sendEventBean(it, "SensorEvent") }
    SensorEvent(3, listOf(1.0, 2.0, 3.0, 4.0, 5.0)).let { runtime.eventService.sendEventBean(it, "SensorEvent") }



    while (true) {
        println("still alive")
        Thread.sleep(10000)
    }*/
}*/