package com.example.recipebook

import android.content.Context

class Preference(val context: Context) {
    private val preference=context.getSharedPreferences("PREFERENCE_NAME",Context.MODE_PRIVATE)
    fun setBoolean(key:String,value:Boolean){
        val editor=preference.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }
    fun getBoolean(key:String):Boolean{
        return preference.getBoolean(key,false)
    }
    fun clear(){
        preference.edit().clear().apply()
    }
    fun remove(key: String){
        preference.edit().remove(key).apply()
    }
    fun putString(key: String,value:String){
        preference.edit().putString(key,value).apply()
    }
    fun getString(key: String):String?{
        return preference.getString(key,null)
    }
    fun putInt(key: String,value:Int){
        preference.edit().putInt(key,value).apply()
    }
    fun getInt(key: String):Int {
        return preference.getInt(key, 0)
    }

}