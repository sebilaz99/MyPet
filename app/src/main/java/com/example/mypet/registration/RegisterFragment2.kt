package com.example.mypet.registration

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mypet.R
import com.example.mypet.model.Colour
import com.example.mypet.model.Sex
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*


class RegisterFragment2 : Fragment(R.layout.fragment_register2) {

    lateinit var adapter: ArrayAdapter<Pair<String, Int>>
    private lateinit var adapter2: ArrayAdapter<String>

    private lateinit var petName: String
    private lateinit var petBreed: String

    private val args: RegisterFragment2Args by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToReg3Btn = view.findViewById<AppCompatButton>(R.id.goToReg3Button)
        val goToReg1 = view.findViewById<AppCompatButton>(R.id.backToRegister1Button)
        val cactv = view.findViewById<AutoCompleteTextView>(R.id.colorsAutoCompleteTextView)
        val rg = view.findViewById<RadioGroup>(R.id.sexRadioGroup)
        val chooseDob = view.findViewById<AppCompatButton>(R.id.chooseBdayButton)

        val colorsList = mutableListOf<Pair<String, Int>>()

        val colorsStrList = mutableListOf<String>()
        for (color in Colour.values()) {
            colorsStrList.add(color.toString())
        }
        colorsStrList.sort()

        val circlesList = mutableListOf<Int>()
        circlesList.add(R.drawable.circle_black)
        circlesList.add(R.drawable.circle_blonde)
        circlesList.add(R.drawable.circle_blue)
        circlesList.add(R.drawable.circle_brown)
        circlesList.add(R.drawable.circle_green)
        circlesList.add(R.drawable.circle_grey)
        circlesList.add(R.drawable.circle_multi)
        circlesList.add(R.drawable.circle_red)
        circlesList.add(R.drawable.circle_white)
        circlesList.add(R.drawable.circle_yellow)

        for (index in Colour.values().indices) {
            colorsList.add(Pair(colorsStrList[index], circlesList[index]))
        }

        adapter2 = ArrayAdapter(requireContext(), R.layout.species_dropdown_item, colorsStrList)
        cactv.setAdapter(adapter2)

        petName = args.name
        petBreed = args.breed

        val calendar = Calendar.getInstance()

        val cDay = calendar.get(Calendar.DAY_OF_MONTH)
        val cMonth = calendar.get(Calendar.MONTH)
        val cYear = Year.now().value

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val format = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(format, Locale.UK)
            val dateStr = sdf.format(calendar.time)
            chooseDob.text = dateStr
        }

        var selectedSex = ""
        rg.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.mRadioButton -> selectedSex = Sex.M.toString()
                R.id.fRadioButton -> selectedSex = Sex.F.toString()
            }
        }

        goToReg3Btn.setOnClickListener {

            val sex = selectedSex
            val dob = chooseDob.text.toString()
            val color = cactv.text.toString()
            val action = RegisterFragment2Directions.actionRegisterFragment2ToRegisterFragment3(
                dob,
                color,
                sex,
                petName,
                petBreed
            )
            view.findNavController().navigate(action)
        }


        goToReg1.setOnClickListener {
            val action = RegisterFragment2Directions.actionRegisterFragment2ToRegisterFragment1()
            view.findNavController().navigate(action)
        }

        chooseDob.setOnClickListener {
            DatePickerDialog(view.context, datePicker, cYear, cMonth, cDay).show()
        }
    }
}


