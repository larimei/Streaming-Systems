import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;
import com.espertech.esper.runtime.client.EPStatement;
public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.getCommon().addEventType("SensorEvent", SensorEvent.class);

        EPCompiler compiler = EPCompilerProvider.getCompiler();
        CompilerArguments compilerArgs = new CompilerArguments(configuration);
        EPCompiled dataCleaningCompiled = compiler.compile(dataCleaningEPL, compilerArgs);
        EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        runtime.initialize();
        //String dataCleaningEPL = "insert into SensorEvent select * from SensorEvent where speed >= 0";

        runtime.getDeploymentService().deploy(dataCleaningCompiled);
        String avgSpeedEPL = "@name('AvgSpeedCalculation') " +
                "select sensorId, avg(speed) as avgSpeed " +
                "from SensorEvent#time_batch(10 seconds) " +
                "group by sensorId";
        EPCompiled avgSpeedCompiled = compiler.compile(avgSpeedEPL, compilerArgs);
        EPDeployment avgSpeedDeployment = runtime.getDeploymentService().deploy(avgSpeedCompiled);
        EPStatement avgSpeedStatement =
                runtime.getDeploymentService().getStatement(avgSpeedDeployment.getDeploymentId(), "AvgSpeedCalculation");
        avgSpeedStatement.addListener(new AvgSpeedEventListener());
        double speed = 100.0;
        for (int i = 1; i <= 100; i++) {
            SensorEvent event1 = new SensorEvent(1, speed);
            runtime.getEventService().sendEventBean(event1, "SensorEvent");
            SensorEvent event2 = new SensorEvent(1, speed + 10);
            runtime.getEventService().sendEventBean(event2, "SensorEvent");
            SensorEvent event3 = new SensorEvent(1, speed + 50);
            runtime.getEventService().sendEventBean(event3, "SensorEvent");
            // The loop is incomplete and the rest of the code is missing.
        }
    }
}
