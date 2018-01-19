package ee.system.dev

open class BuildRequest : BuildRequestBase, Cloneable {
    companion object {
        val EMPTY = BuildRequestBase.EMPTY
    }

    constructor(tasks: MutableList<String> = arrayListOf(), params: MutableMap<String, String> = hashMapOf(),
        flags: MutableList<String> = arrayListOf(), profiles: MutableList<String> = arrayListOf()) : super(tasks,
        params, flags, profiles) {
    }

    override fun build(): BuildRequest {
        return task("build")
    }

    override fun clean(): BuildRequest {
        return task("clean")
    }

    override fun test(): BuildRequest {
        return task("test")
    }

    override fun integTest(): BuildRequest {
        return task("integTest")
    }

    override fun acceptanceTest(): BuildRequest {
        return task("acceptanceTest")
    }

    override fun install(): BuildRequest {
        return task("install")
    }

    override fun publish(): BuildRequest {
        return task("publish")
    }

    override fun flag(flag: String): BuildRequest {
        flags.add(flag)
        return this
    }

    override fun task(task: String): BuildRequest {
        tasks.add(task)
        return this
    }

    override fun profile(task: String): BuildRequest {
        profiles.add(task)
        return this
    }

    override fun param(name: String, value: String): BuildRequest {
        params[name] = value
        return this
    }

    override public fun clone(): BuildRequest {
        return super.clone() as BuildRequest
    }
}

