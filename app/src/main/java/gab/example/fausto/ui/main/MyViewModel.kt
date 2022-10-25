package gab.example.fausto.ui.main

import android.app.Application
import android.content.Context
import android.icu.text.Transliterator
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import gab.example.fausto.R

class MyViewModel(application: Application) : AndroidViewModel(application) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val codes = MutableLiveData<MutableList<Code>>()
    val position = MutableLiveData<Location?>()

    init {
        codes.value = mutableListOf()
        position.value = null
    }

    fun addCode(code: String) {
        val id = codes.value?.size.toString()
        codes.value?.add(Code(id, code))
    }

    fun deleteCode(code: Code) {
        codes.value?.remove(code)
    }

    fun send(ctx: Context, next: () -> Unit) {
        /*db
            .collection("Deliveries")
            .add(
                mapOf(
                    "codes" to codes.value!!,
                    "position" to position.value
                )
            )
            .addOnSuccessListener { */
                Toast.makeText(ctx, ctx.getString(R.string.Inviati), Toast.LENGTH_LONG).show()
                codes.value = mutableListOf()
                position.value = null
                next()
            /*}
            .addOnFailureListener { println("error") }*/
    }
}