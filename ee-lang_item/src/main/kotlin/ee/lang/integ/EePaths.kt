package ee.lang.integ

import ee.common.ext.isWindows
import java.nio.file.Paths

val dPath = Paths.get(if (isWindows) "D:\\TC_CACHE\\d" else "/Users/ee/d")
val eePath = Paths.get(if (isWindows) "$dPath\\ee" else "$dPath/ee")
