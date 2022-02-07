package csc.makrobot.dsl

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import csc.markobot.dsl.Materials.*
import csc.markobot.api.LoadClass.*

class TestNegative {

    @Test
    fun testContext1() {
        assertCompilationFails("""
            import csc.markobot.api.*
            import csc.markobot.dsl.*
            
            fun badContext() {
                robot("Wall-e") {
                    head {
                        plastic thickness 2
                        
                        head {
                            plastic thickness 2
                        }
                    }
                }
            }
        """)
    }

    @Test
    fun testContext2() {
        assertCompilationFails("""
            import csc.markobot.api.*
            import csc.markobot.dsl.*
            
            fun badContext() {
                robot("Wall-e") {
                    plastic thickness 2
                }
            }
        """)
    }

    @Test
    fun testContext3() {
        assertCompilationFails("""
            import csc.markobot.api.*
            import csc.markobot.dsl.*
            
            fun badContext() {
                robot("Wall-e") {
                    eyes {
                        lamp_eyes {
                            quantity = 2
                            illumination = 10
                        }
                    }
                }
            }
        """)
    }

    @Test
    fun testContext4() {
        assertCompilationFails("""
            import csc.markobot.api.*
            import csc.markobot.dsl.*
            
            fun badContext() {
                robot("Wall-e") {
                    chassis = wheels {
                        plastic thickness 2
                        diameter = 4
                        quantity = 2
                    }
                }
            }
        """)
    }

    @Test
    fun testContext5() {
        assertCompilationFails("""
            import csc.markobot.api.*
            import csc.markobot.dsl.*
            
            fun badContext() {
                robot("Wall-e") {
                    chassis = legs {
                        diameter = 4
                        quantity = 2
                    }
                }
            }
        """)
    }

    @Test
    fun testContext6() {
        assertCompilationFails("""
            import csc.markobot.api.*
            import csc.markobot.dsl.*
            
            fun badContext() {
                robot("Wall-e") {
                    body {
                        metal thickness 1
                        caterpillar width 10
                    }
                }
            }
        """)
    }

    private fun assertCompilationFails(source: String) {
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.kotlin("BadCode.kt", source))
            inheritClassPath = true
        }.compile()

        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
    }
}