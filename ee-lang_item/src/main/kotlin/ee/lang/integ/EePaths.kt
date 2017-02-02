package ee.lang.integ

import ee.common.ext.isMac
import java.nio.file.Paths

val eePath = Paths.get(if (isMac) "/Users/ee/git/ee" else "D:\\views\\git\\ee")
