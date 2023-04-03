package com.ajayvamsee.wifi

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.w3c.dom.Text


class FragmentA : Fragment() {

    lateinit var textListener:TextListener


    interface TextListener {
        fun sendData(text:String)
    }


    fun sendDataToActivity(){
        textListener.sendData("hello")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

      // val activity =

        textListener = (context as Activity) as TextListener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    companion object {

    }
}