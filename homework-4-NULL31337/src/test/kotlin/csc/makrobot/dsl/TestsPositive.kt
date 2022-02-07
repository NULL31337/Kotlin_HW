@file:Suppress("LocalVariableName", "NonAsciiCharacters")

package csc.makrobot.dsl

import csc.markobot.api.*
import csc.markobot.dsl.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import csc.markobot.dsl.Materials.*
import csc.markobot.api.LoadClass.*

class TestsPositive {

    @Test
    fun testNonDSL() {
        val robot = MakroBot("Wall-E",
            Head(Plastic(2), listOf(LampEye(10), LampEye(10), LedEye(3)), Mouth(Speaker(3))),
            Body(Metal(1), listOf("I don't want to survive.", "I want live.")),
            Hands(Plastic(3), LoadClass.Light, LoadClass.Medium),
            Chassis.Caterpillar(10)
        )
        verify(robot)
    }

    @Test
    fun testDSL() {
        val robot = robot("Wall-E") {
            head {
                plastic thickness 2

                eyes {
                    lamp_eyes {
                        quantity = 2
                        illumination = 10
                    }
                    led_eyes {
                        quantity = 1
                        illumination = 3
                    }
                }

                mouth {
                    speaker {
                        power = 3
                    }
                }
            }

            body {
                metal thickness 1

                inscription {
                    +"I don't want to survive."
                    +"I want live."
                }
            }

            hands {
                plastic thickness 3
                load_class = Light - Medium
            }

            chassis = caterpillar width 10
        }

        verify(robot)
    }

    @Test
    fun testDSLOtherChassis() {
        val robotНаwheelsх = robot("Wall-E") {
            head {
                plastic thickness 2

                eyes {
                    lamp_eyes {
                        quantity = 2
                        illumination = 10
                    }
                }

                mouth {
                    speaker {
                        power = 3
                    }
                }
            }

            body {
                metal thickness 1
            }

            hands {
                plastic thickness 3
                load_class = Light - Medium
            }
            chassis = wheels {
                diameter = 4
                quantity = 2
            }
        }

        Assertions.assertEquals(Chassis.Wheel(2, 4), robotНаwheelsх.chassis)

        val robotНаНогах = robot("Wall-E") {
            head {
                plastic thickness 2

                eyes {
                    lamp_eyes {
                        quantity = 2
                        illumination = 10
                    }
                }

                mouth {
                    speaker {
                        power = 3
                    }
                }
            }

            body {
                metal thickness 1
            }

            hands {
                plastic thickness 3
                load_class = Light - Medium
            }
            chassis = legs
        }

        Assertions.assertEquals(Chassis.Legs, robotНаНогах.chassis)
    }

    private fun verify(robot: MakroBot) {
        Assertions.assertEquals("Wall-E", robot.name)
        Assertions.assertEquals(Plastic(2), robot.head.material)
        Assertions.assertArrayEquals(arrayOf(LampEye(10), LampEye(10), LedEye(3)), robot.head.eyes.toTypedArray())
        Assertions.assertEquals(Mouth(Speaker(3)), robot.head.mouth)

        Assertions.assertEquals(Metal(1), robot.body.material)
        Assertions.assertArrayEquals(arrayOf("I don't want to survive.", "I want live."), robot.body.strings.toTypedArray())

        Assertions.assertEquals(Plastic(3), robot.hands.material)
        Assertions.assertEquals(LoadClass.Light, robot.hands.minLoad)
        Assertions.assertEquals(LoadClass.Medium, robot.hands.maxLoad)

        Assertions.assertEquals(Chassis.Caterpillar(10), robot.chassis)
    }
}
