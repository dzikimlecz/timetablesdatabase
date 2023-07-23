package me.dzikimlecz.timetabledatabase.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LecturerKtTest {
    @Test
    fun `should remove unnecessary info chars`() {
    // given
        val code = "MN"
        val infos = listOf(
            "[4324]",
            "***",
            "[]",
            "*[*]*",
            "*",
        )
    // when
        val codes1 = infos.map { code + it }.map(::dropOmittedInfoChars).dropWhile(code::equals)
        val codes2 = infos.map { it + code }.map(::dropOmittedInfoChars).dropWhile(code::equals)
        val codes3 = infos
            .map { code.substring(0, code.length / 2) + it + code.substring(code.length / 2, code.length) }
            .map(::dropOmittedInfoChars).dropWhile(code::equals)
    // then
        assertTrue(codes1.isEmpty())
        assertTrue(codes2.isEmpty())
        assertTrue(codes3.isEmpty())
    }

    @Test
    fun `should retain not info chars`() {
    // given
        val code = "MN"
        val notInfos = listOf(
            "pimpek",
            "pimposzek",
            "][",
            "0_0",
            "j",
            "eopjfiore",
        )
    // when
        val codes1 = notInfos.map { code + it }.map(::dropOmittedInfoChars)
        val codes2 = notInfos.map { it + code }.map(::dropOmittedInfoChars)
        val codes3 = notInfos
            .map { code.substring(0, code.length / 2) + it + code.substring(code.length / 2, code.length) }
            .map(::dropOmittedInfoChars)
    // then
        for (i in notInfos.indices) {
            assertEquals(notInfos[i].length + code.length, codes1[i].length)
            assertEquals(notInfos[i].length + code.length, codes2[i].length)
            assertEquals(notInfos[i].length + code.length, codes3[i].length)
        }
    }
}