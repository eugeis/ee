package ee.task

import ee.common.StreamHandler
import ee.common.ext.isWindows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

val shell = if (isWindows) "cmd" else "sh"
val inShellOption = if (isWindows) "/c" else "-c"
val noWaitNoConsoleOption = if (isWindows) "start" else ""

fun Path.exec(config: ExecConfig, log: Logger = LoggerFactory.getLogger("Runner"), output: (String) -> Unit = {}): Int {
    var ret = 0
    output("cd $this\n${config.cmd.joinToString(" ")}")
    val command = buildCommandChain(config)
    val builder = ProcessBuilder(* command.toTypedArray()).directory(toFile())
    builder.environment().putAll(config.env)
    val process = builder.start()
    if (config.wait) {
        StreamHandler(process.inputStream, config.filter, config.filterPattern.toRegex(), log, output).start()
        if (!process.waitFor(config.timeout, config.timeoutUnit)) {
            log.warn("Timeout reached {} {}", config.timeout, config.timeoutUnit)
            ret = -1
        } else {
            ret = process.exitValue()
        }
        if (config.failOnError && (ret != 0)) {
            throw throw Exception("$ret")
        }
    }
    return ret
}

private fun buildCommandChain(config: ExecConfig): List<String> {
    val ret = arrayListOf(shell, inShellOption)
    if (!config.wait && !config.noConsole) {
        ret.add(noWaitNoConsoleOption)
    }

    if (config.wait) {
        ret.add(config.cmd.joinToString(" "))
    } else {
        ret.addAll(config.cmd)
    }
    return ret
}