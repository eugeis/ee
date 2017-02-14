package ee.lang.integ

import ee.common.ext.isMac
import java.nio.file.Paths

val dPath = Paths.get(if (isMac) "/Users/ee/d" else "D:\\views\\d")
val eePath = Paths.get(if (isMac) "$dPath/ee" else "$dPath\\ee")
