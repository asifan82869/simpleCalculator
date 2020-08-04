package shock.com.mycalculatorapp

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private var calculation: TextView? = null
    var answer: TextView? = null
    var sCalculation = ""
    var sAnswer  = ""
    var number_one = ""
    var number_two = ""
    var current_oprator = ""
    var prev_ans: String? = ""
    var Result = 0.0
    var numberOne = 0.0
    var numberTwo = 0.0
    var temp = 0.0
    var dot_present = false
    var value_inverted = false
    var number_allow = true
    //we need to reformat answer
    var format: NumberFormat? = null
    var longformate: NumberFormat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        calculation = findViewById(R.id.calculation)
        //set movement to the text view
        calculation?.movementMethod = ScrollingMovementMethod()
        //initialize answer
        answer = findViewById(R.id.answer)

        //we set the answer upto four decimal
        format = DecimalFormat("#.####")
        longformate = DecimalFormat("#.####")
    }
    fun onClickNumber(v: View) {
        //we need to find which button is pressed
        if (number_allow) {
            val bn = v as Button
            sCalculation += bn.text
            number_one += bn.text
            numberOne = number_one.toDouble()
            //check root is present
            when (current_oprator) {
                "", "+" -> {
                    temp =Result + numberOne
                }
                "-" -> {
                    temp =Result - numberOne
                }
                "x" -> {
                    temp = Result * numberOne
                }
                "/" -> try {
                    // divided by 0 cause execption
                    temp=Result / numberOne
                } catch (e: Exception) {
                    sAnswer = e.message.toString()
                }
            }
            sAnswer = format!!.format(temp).toString()
            updateCalculation()
        }
    }

    fun onClickOprator(v: View) {
        val ob = v as Button
        //if sAnswer is null means no calculation needed
        if (sAnswer !== "") {
            //we check last char is operator or not
            if (current_oprator !== "") {
                val c = getcharfromLast(
                        sCalculation,
                        2
                ) // 2 is the char from last because our las char is " "
                if (c == '+' || c == '-' || c == 'x' || c == '/') {
                    sCalculation = sCalculation.substring(0, sCalculation.length - 3)
                }
            }
            sCalculation = """$sCalculation
${ob.text} """
            number_one = ""
            Result = temp
            current_oprator = ob.text.toString()
            updateCalculation()
            //when operator click dot is not present in number_one
            number_two = ""
            numberTwo = 0.0
            dot_present = false
            value_inverted = false
        }
    }

    private fun getcharfromLast(s: String, i: Int): Char {
        return s[s.length - i]
    }

    fun onClickClear(v: View?) {
        cleardata()
    }

    fun cleardata() {
        sCalculation = ""
        sAnswer = ""
        current_oprator = ""
        number_one = ""
        number_two = ""
        prev_ans = ""
        Result = 0.0
        numberOne = 0.0
        numberTwo = 0.0
        temp = 0.0
        updateCalculation()
        dot_present = false
        number_allow = true
        value_inverted = false
    }

    fun updateCalculation() {
        calculation!!.text = sCalculation
        answer!!.text = sAnswer
    }

    fun onDotClick(view: View?) {
        //create boolean dot_present check if dot is present or not.
        if (!dot_present) {
            //check length of numberone
            if (number_one.length == 0) {
                number_one = "0."
                sCalculation += "0."
                sAnswer = "0."
                dot_present = true
                updateCalculation()
            } else {
                number_one += "."
                sCalculation += "."
                sAnswer += "."
                dot_present = true
                updateCalculation()
            }
        }
    }

    fun onClickEqual(view: View?) {
        showresult()
    }

    fun showresult() {
        if (sAnswer !== "" && sAnswer !== prev_ans) {
            sCalculation += "\n= $sAnswer\n----------\n$sAnswer "
            number_one = ""
            number_two = ""
            numberTwo = 0.0
            numberOne = 0.0
            Result = temp
            prev_ans = sAnswer
            updateCalculation()
            //we  don't allow to edit our ans so
            dot_present = true
            number_allow = false
            value_inverted = false
        }
    }

    fun onModuloClick(view: View?) {
        if (sAnswer !== "" && getcharfromLast(sCalculation, 1) != ' ') {
            sCalculation += "% "
            when (current_oprator) {
                "" -> temp = temp / 100
                "+" -> temp = Result + Result * numberOne / 100
                "-" -> temp = Result - Result * numberOne / 100
                "x" -> temp = Result * (numberOne / 100)
                "/" -> try {
                    temp = Result / (numberOne / 100)
                } catch (e: Exception) {
                    sAnswer = e.message.toString()
                }
            }
            sAnswer = format!!.format(temp).toString()
            if (sAnswer!!.length > 9) {

                sAnswer = longformate!!.format(temp).toString()
            }
            Result = temp
            //now we show the result
            showresult()
        }
    }

    fun removechar(str: String, i: Int): String {
        val c = str[str.length - i]
        //we need to check if dot is removed or not
        if (c == '.' && !dot_present) {
            dot_present = false
        }
        return if (c == ' ') {
            str.substring(0, str.length - (i - 1))
        } else str.substring(0, str.length - i)
    }
    fun removeuntilchar(str: String, chr: Char) {
        var str = str
        val c = getcharfromLast(str, 1)
        if (c != chr) {
            //remove last char
            str = removechar(str, 1)
            sCalculation = str
            updateCalculation()
            removeuntilchar(str, chr)
        }
    }

    fun onClickDelete(view: View?) {
        if (sAnswer !== "") {
            if (getcharfromLast(sCalculation, 1) != ' ') {
                if (number_one.length < 2 && current_oprator !== "") {
                    number_one = ""
                    temp = Result
                    sAnswer = format!!.format(kotlin.Result).toString()
                    sCalculation = removechar(sCalculation, 1)
                    updateCalculation()
                } else {
                    when (current_oprator) {
                        "" -> {
                            if (value_inverted) {
                                sAnswer = sAnswer.substring(1, sAnswer.length)
                                sCalculation = sCalculation.substring(1, sAnswer.length)
                                updateCalculation()
                            }
                            if (sCalculation.length < 2) {
                                cleardata()
                            } else {
                                if (getcharfromLast(sCalculation, 1) == '.') {
                                    dot_present = false
                                }
                                number_one = removechar(number_one, 1)
                                numberOne = number_one.toDouble()
                                temp = numberOne
                                sCalculation = number_one
                                sAnswer = number_one
                                updateCalculation()
                            }
                        }
                        "+" -> {
                            if (value_inverted) {
                                numberOne = numberOne * -1
                                number_one = format!!.format(numberOne).toString()
                                temp = Result + numberOne
                                sAnswer = format!!.format(temp).toString()
                                removeuntilchar(sCalculation, ' ')
                                sCalculation += number_one
                                updateCalculation()
                                value_inverted = if (value_inverted) false else true
                            }
                            if (getcharfromLast(sCalculation, 1) == '.') {
                                dot_present = false
                            }
                            number_one = removechar(number_one, 1)
                            if (number_one.length == 1 && number_one === ".") {
                                numberOne = number_one.toDouble()
                            }
                            numberOne = number_one.toDouble()
                            temp = Result + numberOne
                            sAnswer = format!!.format(temp).toString()
                            sCalculation = removechar(sCalculation, 1)
                            updateCalculation()
                        }
                        "-" -> {
                            if (value_inverted) {
                                numberOne = numberOne * -1
                                number_one = format!!.format(numberOne).toString()
                                temp = Result - numberOne
                                sAnswer = format!!.format(temp).toString()
                                removeuntilchar(sCalculation, ' ')
                                sCalculation += number_one
                                updateCalculation()
                                value_inverted = if (value_inverted) false else true
                            }
                            if (getcharfromLast(sCalculation, 1) == '.') {
                                dot_present = false
                            }
                            number_one = removechar(number_one, 1)
                            if (number_one.length == 1 && number_one === ".") {
                                numberOne = number_one.toDouble()
                            }
                            numberOne = number_one.toDouble()
                            temp = Result - numberOne
                            sAnswer = format!!.format(temp).toString()
                            sCalculation = removechar(sCalculation, 1)
                            updateCalculation()
                        }
                        "x" -> {
                            if (value_inverted) {
                                numberOne = numberOne * -1
                                number_one = format!!.format(numberOne).toString()
                                temp = Result * numberOne
                                sAnswer = format!!.format(temp).toString()
                                removeuntilchar(sCalculation, ' ')
                                sCalculation += number_one
                                updateCalculation()
                                value_inverted = if (value_inverted) false else true
                            }
                            if (getcharfromLast(sCalculation, 1) == '.') {
                                dot_present = false
                            }
                            number_one = removechar(number_one, 1)
                            if (number_one.length == 1 && number_one === ".") {
                                numberOne = number_one.toDouble()
                            }
                            numberOne = number_one.toDouble()
                            temp = Result * numberOne
                            sAnswer = format!!.format(temp).toString()
                            sCalculation = removechar(sCalculation, 1)
                            updateCalculation()
                        }
                        "/" -> {
                            try {
                                if (value_inverted) {
                                    numberOne = numberOne * -1
                                    number_one = format!!.format(numberOne).toString()
                                    temp = Result / numberOne
                                    sAnswer = format!!.format(temp).toString()
                                    removeuntilchar(sCalculation, ' ')
                                    sCalculation += number_one
                                    updateCalculation()
                                    value_inverted = if (value_inverted) false else true
                                }
                                if (getcharfromLast(sCalculation, 1) == '.') {
                                    dot_present = false
                                }
                                number_one = removechar(number_one, 1)
                                if (number_one.length == 1 && number_one === ".") {
                                    numberOne = number_one.toDouble()
                                }
                                numberOne = number_one.toDouble()
                                temp = Result / numberOne
                                sAnswer = format!!.format(temp).toString()
                                sCalculation = removechar(sCalculation, 1)
                            } catch (e: java.lang.Exception) {
                                sAnswer = e.message!!
                            }
                            updateCalculation()
                        }
                    }
                }
            }
        }
    }

}