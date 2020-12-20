package com.rn1.gogoyo.statistic.total

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.rn1.gogoyo.R
import com.rn1.gogoyo.databinding.FragmentTotalWalkBinding
import com.rn1.gogoyo.ext.getVmFactory
import com.rn1.gogoyo.util.Logger
import java.text.DecimalFormat

class TotalWalkFragment : Fragment() {

    private lateinit var binding: FragmentTotalWalkBinding
    private val viewModel by viewModels<TotalWalkViewModel> { getVmFactory() }
    private val pieValueTime = mutableListOf<PieEntry>()
    private val pieValueDistance = mutableListOf<PieEntry>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_total_walk, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.pet.observe(viewLifecycleOwner, Observer {
            it?.let {
                for (pet in it) {
                    Logger.d("pet.divTotalTime = ${pet.divTotalTime}, pet.divTotalDistance = ${pet.divTotalDistance}")

                    val valueTime = PieEntry(pet.divTotalTime, pet.name)
                    val valueDistance = PieEntry(pet.divTotalDistance.toFloat(), pet.name)
//                    Logger.d("valueTime = $valueTime, valueDistance = $valueDistance")
                    pieValueTime.add(valueTime)
                    pieValueDistance.add(valueDistance)
                }

                val toggle = binding.toggleButtonGroup
                toggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
                    if (isChecked) {
                        when (checkedId) {
                            R.id.timeBt -> {
                                val pieChart = binding.pie
//                                pieChart.setUsePercentValues(true)
                                pieChart.holeRadius = 60f
                                pieChart.transparentCircleRadius = 65f
                                pieChart.description.isEnabled = false
                                pieChart.setCenterTextSize(20f)
                                pieChart.animateXY(500, 500)

                                val pieDataSet = PieDataSet(pieValueTime, "")
                                pieDataSet.valueTextSize = 16f
                                pieDataSet.valueTextColor = Color.YELLOW
                                pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                                val pieData = PieData(pieDataSet)

                                pieChart.data = pieData
                                pieChart.centerText = "總時數\n ${formatTime(viewModel.totalTime.toInt())}"
                            }

                            R.id.distanceBt -> {
                                val pieChart = binding.pie
                                pieChart.setUsePercentValues(true)
                                pieChart.holeRadius = 60f
                                pieChart.transparentCircleRadius = 65f
                                pieChart.description.isEnabled = false
                                pieChart.setCenterTextSize(20f)
                                pieChart.animateXY(500, 500)

                                val pieDataSet = PieDataSet(pieValueDistance, "")
                                pieDataSet.valueTextSize = 16f
                                pieDataSet.valueTextColor = Color.YELLOW
                                pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

                                val pieData = PieData(pieDataSet)

                                pieChart.data = pieData
                                pieChart.centerText = "總路程\n ${formatFloat(viewModel.totalDistance.toFloat())}公里"
                            }
                        }
                    }
                }
                toggle.check(R.id.timeBt)
            }
        })

        return binding.root
    }

    companion object{
        val JOYFUL_COLORS2 = intArrayOf(
            Color.rgb(217, 80, 138), Color.rgb(254, 149, 7), Color.rgb(128, 128, 255),
            Color.rgb(106, 167, 134), Color.rgb(53, 194, 209), Color.rgb(254, 247, 120)
        )
    }

    private fun formatTime(second: Int): String{

        val hour = second / 3600
        var secondTime = second % 3600
        val minute = secondTime / 60
        secondTime %= 60

        return "${addZero(hour)}時${addZero(minute)}分${addZero(secondTime)}秒"
    }

    private fun addZero(number: Int): String{
        return if(number.toString().length == 1){
            "0$number"
        } else {
            "$number"
        }
    }

    private fun formatFloat(float: Float): String{
        val decimalFormat = DecimalFormat("#.00")
        return decimalFormat.format(float)
    }
}