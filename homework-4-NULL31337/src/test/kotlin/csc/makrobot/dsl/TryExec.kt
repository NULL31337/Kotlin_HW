@file:Suppress("LocalVariableName", "NonAsciiCharacters")

package csc.makrobot.dsl

import csc.markobot.api.*
import csc.markobot.api.WeekDay.*
import csc.markobot.dsl.*

fun main() {

    val волли = MakroBot("Wall-E",
        Head(Plastic(2), listOf(LampEye(10), LampEye(10)), Mouth(Speaker(3))),
        Body(Metal(1), listOf("I don't want to survive.", "I want live.")),
        Hands(Plastic(3), LoadClass.Light, LoadClass.Medium),
        Chassis.Caterpillar(10)
    )

/*    val воллиЧерезDSL = robot("Wall-E") {
        head {
            plastic thickness 2

            eyes {
                led_eyes {
                    quantity = 2
                    illumination = 10
                }
                lamp_eyes {
                    quantity = 1
                    illumination = 40
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

        // chassis = legs
        chassis = caterpillar width 4
        *//*chassis = wheels {
            diameter = 4
            quantity = 2
        }*//*
    }*/

    scenario {
        волли {                             // invoke operator overload
            speed = 2                       // initialization DSL
            power = 3
        }

        волли forward 3                      // infix functions
        волли pronounce {
            +"Во поле береза стояла"
            +"Во поле кудрявая стояла"
        }
        волли.turn_around()
        волли back 3

        schedule {                        // context-based high level function with context-lambda

            // волли forward 3                // control methods availability with @DslMarker

            repeat(mon at 10, tue at 12)     // typealias, infix functions, vararg
            except(13)
            repeat(wed..fri at 11)
        }

    }.start_now()
        .restart_schedule()               // calls chaining
        .schedule {
            repeat(fri at 23)
        }

    val (name, speed) = волли               // destructuring declarations
}
