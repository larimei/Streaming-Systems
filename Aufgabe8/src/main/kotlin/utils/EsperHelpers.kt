package utils

import com.espertech.esper.compiler.client.CompilerArguments
import com.espertech.esper.compiler.client.EPCompilerProvider
import com.espertech.esper.runtime.client.EPDeployment
import com.espertech.esper.runtime.client.EPRuntime
import com.espertech.esper.runtime.client.UpdateListener

fun compileAndDeploy(runtime: EPRuntime, epl: String): EPDeployment {
    val compiler = EPCompilerProvider.getCompiler()
    val compiled = compiler.compile(epl, CompilerArguments(runtime.configurationDeepCopy))
    return runtime.deploymentService.deploy(compiled)
}

fun setupListener(runtime: EPRuntime, deployment: EPDeployment, statementName: String, listener: UpdateListener) {
    val statement = runtime.deploymentService.getStatement(deployment.deploymentId, statementName)
    statement.addListener(listener)
}