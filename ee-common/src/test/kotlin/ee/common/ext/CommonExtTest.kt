package ee.common.ext

import ee.common.Label
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths
import java.util.*

class CommonExtTest {

    @Test
    fun testBuildLabel() {
        assertEquals(Label("Date", "util"), Date().buildLabel())
    }

    //Date
    @Disabled
    @Test
    fun testLongDateTime() {
        val cal = calendar()

        assertEquals("17.07.16 01:02:03.004", cal.time.longDateTime())
    }

    @Disabled
    @Test
    fun longTime() {
        val cal = calendar()

        assertEquals("01:02:03.004", cal.time.longTime())
    }

    private fun calendar(): Calendar {
        val calendar = Calendar.getInstance()

        calendar.time = Date()

        calendar.set(2016, 7, 17)
        calendar.set(Calendar.HOUR_OF_DAY, 1)
        calendar.set(Calendar.MINUTE, 2)
        calendar.set(Calendar.SECOND, 3)
        calendar.set(Calendar.MILLISECOND, 4)
        return calendar
    }

    //File
    @Test
    fun testFilePath() {
        assertTrue(
            File("d:\\folder\\childFolder\\fileName.txt").toPathString().endsWith("/folder/childFolder/fileName.txt"))
    }

    @Test
    fun testFileExt() {
        assertEquals("txt", File("fileName.txt").ext())
    }

    //Path
    @Test
    fun testPath() {
        assertEquals("txt", Paths.get("d:\\folder\\childFolder\\fileName.txt").ext())
    }

    //String
    @Test
    fun testStringFileExt() {
        assertEquals("txt", "fileName.txt".fileExt())
        assertEquals("txt", "fileName.TXT".fileExt())
    }

    @Test
    fun testStringPath() {
        assertEquals("d:/folder/childFolder/fileName.txt", "d:\\folder\\childFolder\\fileName.txt".toPathString())
        assertEquals("d:/folder/childFolder/fileName.TXT", "d:\\folder\\childFolder\\fileName.TXT".toPathString())
    }

    @Test
    fun testStringDotAsPath() {
        assertEquals("folder/childFolder/fileName", "folder.childFolder.fileName".toDotsAsPath())
    }

    @Test
    fun testStringKey() {
        assertEquals("part1_part2", "part1-part2".toKey())
    }

    @Test
    fun testStringCamelCase() {
        assertEquals("part1Part2", "part1_part2".toCamelCase())
        assertEquals("part1Part2", "part1_part2".toCamelCase())
        assertEquals("eTag", "ETag".toCamelCase())
    }

    @Test
    fun testStringUnderscoreToCamel() {
        assertEquals("part1Part2", "part1_part2".toCamelCase())
    }

    @Test
    fun testStringUnderscoreToUnderscored() {
        val toUnderscoredUpperCase = "AXLE_LOAD_A".toUnderscoredUpperCase()
        assertEquals("AXLE_LOAD_A", toUnderscoredUpperCase)
    }

    @Test
    fun testStringBigToUnderscored() {
        val toUnderscoredUpperCase = "AXLE".toUnderscoredUpperCase()
        assertEquals("AXLE", toUnderscoredUpperCase)
    }

    @Test
    fun testStringCamelCaseToUnderscored() {
        val toUnderscoredUpperCase = "AxleLoadA".toUnderscoredUpperCase()
        assertEquals("AXLE_LOAD_A", toUnderscoredUpperCase)
    }

    @Test
    fun testStringCamelCaseSmallStartToUnderscored() {
        val toUnderscoredUpperCase = "axleLoadA".toUnderscoredUpperCase()
        assertEquals("AXLE_LOAD_A", toUnderscoredUpperCase)
    }

    @Test
    fun testStringAsClass() {
        assertEquals(Date::class.java, "Date".asClass<Date>("java.util"))
    }

    @Test
    fun testStringAsClassInstance() {
        var date = "Date".asClassInstance<Date>("java.util")
        assertNotNull(date)
    }

}
