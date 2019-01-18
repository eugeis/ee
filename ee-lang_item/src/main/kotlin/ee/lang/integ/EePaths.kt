package ee.lang.integ

import ee.common.ext.isLinux
import ee.common.ext.isWindows
import java.nio.file.Paths

val dPath = Paths.get(if (isWindows) "D:\\TC_CACHE\\d" else if(isLinux) "/home/ee/d" else "/Users/ee/d")
val eePath = Paths.get(if (isWindows) "$dPath\\ee" else "$dPath/ee")
