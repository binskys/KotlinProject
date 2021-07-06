package com.ruiec.zxing_library.camera

import android.content.SharedPreferences
import com.ruiec.zxing_library.PreferencesActivity

enum class FrontLightMode {

    /** Always on.  */
    ON,
    /** On only when ambient light is low.  */
    AUTO,
    /** Always off.  */
    OFF;


    companion object {

        private fun parse(modeString: String?): FrontLightMode {
            return if (modeString == null) OFF else valueOf(modeString)
        }

        fun readPref(sharedPrefs: SharedPreferences): FrontLightMode {
            return parse(sharedPrefs.getString(PreferencesActivity().KEY_FRONT_LIGHT_MODE, OFF.toString()))
        }
    }

}