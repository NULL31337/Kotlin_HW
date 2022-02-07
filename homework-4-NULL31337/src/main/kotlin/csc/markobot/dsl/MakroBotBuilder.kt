package csc.markobot.dsl

import csc.markobot.api.*
import csc.markobot.dsl.Materials.*
import java.lang.IllegalArgumentException


@MakroBotDsl
interface Builder<T> {
    fun build(): T
}

@MakroBotDsl
class MakroBotBuilder(private val name: String) : Builder<MakroBot> {
    private var head: Head? = null
    private var body: Body? = null
    private var hands: Hands? = null
    var chassis: Chassis? = null

    fun head(lambda: HeadBuilder.() -> Unit) {
        head = HeadBuilder().apply(lambda).build()
    }

    fun body(lambda: BodyBuilder.() -> Unit) {
        body = BodyBuilder().apply(lambda).build()
    }

    fun hands(lambda: HandsBuilder.() -> Unit) {
        hands = HandsBuilder().apply(lambda).build()
    }

    fun wheels(lambda: WheelsBuilder.() -> Unit): Chassis {
        return WheelsBuilder().apply(lambda).build()
    }

    infix fun caterpillar.width(width: Int) = Chassis.Caterpillar(width)
    override fun build() = MakroBot(
        name,
        head ?: throw IllegalArgumentException("You forgot to make head for the robot"),
        body ?: throw IllegalArgumentException("You forgot to make body for the robot"),
        hands ?: throw IllegalArgumentException("You forgot to make hands for the robot"),
        chassis ?: throw IllegalArgumentException("You forgot to make chassis for the robot")
    )
}

fun robot(name: String, lambda: MakroBotBuilder.() -> Unit): MakroBot {
    val makroBotBuilder = MakroBotBuilder(name)
    makroBotBuilder.apply(lambda)
    return makroBotBuilder.build()
}

enum class Materials {
    metal,
    plastic
}

@MakroBotDsl
open class MaterialBuilder {
    protected var material: Material? = null
    infix fun Materials.thickness(thickness: Int) {
        material = when (this) {
            metal -> {
                Metal(thickness)
            }
            plastic -> {
                Plastic(thickness)
            }
        }
    }
}

@MakroBotDsl
class HeadBuilder : MaterialBuilder(), Builder<Head> {
    private val eyes = mutableListOf<Eye>()
    private var mouth: Mouth? = null

    fun eyes(lambda: EyesListBuilder.() -> Unit) {
        eyes += EyesListBuilder().apply(lambda).build()
    }

    fun mouth(lambda: MouthBuilder.() -> Unit) {
        mouth = MouthBuilder().apply(lambda).build()
    }

    override fun build(): Head = Head(
        material ?: throw IllegalArgumentException("You forgot to write the material and its thickness"),
        eyes,
        mouth ?: throw IllegalArgumentException("You forgot to make mouth for the robot")
    )
}

@MakroBotDsl
class MouthBuilder : Builder<Mouth> {
    private var speaker: Speaker? = null

    fun speaker(lambda: SpeakerBuilder.() -> Unit) {
        speaker = SpeakerBuilder().apply(lambda).build()
    }

    override fun build() = Mouth(speaker)
}

@MakroBotDsl
class SpeakerBuilder : Builder<Speaker> {
    var power: Int? = null
    override fun build() =
        Speaker(power ?: throw IllegalArgumentException("You forgot to write the power of the speaker"))
}

@MakroBotDsl
class EyesListBuilder : Builder<List<Eye>> {
    private val eyesList = mutableListOf<Eye>()
    private fun eyes(eyeCons: (Int) -> Eye, lambda: EyesBuilder.() -> Unit) {
        val eyes = EyesBuilder().apply(lambda)
        eyes.quantity ?: throw IllegalArgumentException("You forgot to write quantity eyes")
        repeat(eyes.quantity!!) {
            eyesList += eyeCons(
                eyes.illumination ?: throw IllegalArgumentException("You forgot to write illumination eyes")
            )
        }
    }

    fun led_eyes(lambda: EyesBuilder.() -> Unit) = eyes(::LedEye, lambda)
    fun lamp_eyes(lambda: EyesBuilder.() -> Unit) = eyes(::LampEye, lambda)
    override fun build() = eyesList
}

@MakroBotDsl
data class EyesBuilder(var quantity: Int? = null, var illumination: Int? = null)

@MakroBotDsl
class BodyBuilder : MaterialBuilder(), Builder<Body> {
    private val inscriptions = mutableListOf<String>()

    fun inscription(lambda: InscriptionBuilder.() -> Unit) {
        inscriptions += InscriptionBuilder().apply(lambda).build()
    }

    override fun build() = Body(
        material ?: throw IllegalArgumentException("You forgot to write the material and its thickness"),
        inscriptions
    )
}

@MakroBotDsl
class InscriptionBuilder : Builder<List<String>> {
    private val strings = mutableListOf<String>()

    operator fun String.unaryPlus() {
        strings += this
    }

    override fun build() = strings
}

@MakroBotDsl
class HandsBuilder : MaterialBuilder(), Builder<Hands> {
    var load_class: Pair<LoadClass, LoadClass>? = null

    operator fun LoadClass.minus(other: LoadClass) = this to other
    override fun build() = Hands(
        material ?: throw IllegalArgumentException("You forgot to write the material and its thickness"),
        load_class?.first ?: throw IllegalArgumentException("You forgot to choose the load class"),
        load_class?.second ?: throw IllegalArgumentException("You forgot to choose the load class")
    )
}

@MakroBotDsl
object caterpillar

typealias legs = Chassis.Legs

@MakroBotDsl
class WheelsBuilder : Builder<Chassis> {
    var diameter: Int? = null
    var quantity: Int? = null
    override fun build() = Chassis.Wheel(
        quantity ?: throw IllegalArgumentException("You forgot to write how many wheels are needed"),
        diameter ?: throw IllegalArgumentException("You forgot to write what diameter wheels are needed")
    )
}
