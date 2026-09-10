package com.velcat.charactercore

import android.app.Activity
import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ScrollView
import android.widget.Toast
import com.velcat.charactercore.power.PowerProfiler
import com.velcat.charactercore.wallpaper.CharacterWallpaperService

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 72, 48, 48)
        }

        root.addView(TextView(this).apply {
            text = "Character Core ${BuildConfig.VERSION_NAME} · ${getString(R.string.character_name)}"
            textSize = 25f
            setTypeface(typeface, Typeface.BOLD)
        })

        root.addView(TextView(this).apply {
            text = "Dual Character Asset Pack + Live Wallpaper + Power Profiler\n\n이 빌드는 ${getString(R.string.character_name)} 전용 버전입니다."
            textSize = 16f
            setPadding(0, 32, 0, 32)
        })

        root.addView(Button(this).apply {
            text = "라이브 배경화면 설정"
            setOnClickListener {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                    putExtra(
                        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(this@MainActivity, CharacterWallpaperService::class.java)
                    )
                }
                try {
                    startActivity(intent)
                } catch (_: ActivityNotFoundException) {
                    try {
                        startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(this@MainActivity, "이 기기에서는 라이브 배경화면 설정 화면을 열 수 없습니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        root.addView(TextView(this).apply {
            val path = PowerProfiler(this@MainActivity).logPath()
            text = "\n전력 로그 저장 위치(앱 내부):\n$path\n\n5초마다 배터리 전류/평균전류/charge/energy counter를 기록합니다. 기기 미지원 값은 빈칸으로 남습니다."
            textSize = 14f
        })

        val scroll = ScrollView(this).apply { addView(root) }
        scroll.setOnApplyWindowInsetsListener { view, insets ->
            val bars = if (android.os.Build.VERSION.SDK_INT >= 30) {
                insets.getInsets(android.view.WindowInsets.Type.systemBars() or android.view.WindowInsets.Type.displayCutout())
            } else null
            if (bars != null) {
                view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            } else {
                @Suppress("DEPRECATION")
                view.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            }
            insets
        }
        setContentView(scroll)
        scroll.requestApplyInsets()
    }
}
