package com.velcat.charactercore.wallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.velcat.charactercore.core.CharacterBrain
import com.velcat.charactercore.character.CharacterAssetFactory
import com.velcat.charactercore.power.PowerProfiler

class CharacterWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = CharacterEngine()

    inner class CharacterEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val brain = CharacterBrain()
        private val renderer = CharacterRenderer(this@CharacterWallpaperService, CharacterAssetFactory.create())
        private val profiler by lazy { PowerProfiler(this@CharacterWallpaperService) }

        private var visible = false
        private var surfaceReady = false
        private var frameCount = 0L
        private var batteryPercent = 100
        private var startedAtMs = SystemClock.elapsedRealtime()
        private var lastProfileMs = 0L

        private val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level >= 0 && scale > 0) {
                    batteryPercent = (level * 100f / scale).toInt()
                    brain.setBatteryPercent(batteryPercent)
                }
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                brain.setCharging(
                    status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
                )
            }
        }

        private val drawRunnable = object : Runnable {
            override fun run() {
                if (!visible || !surfaceReady) return
                drawFrame()
                if (visible && surfaceReady) handler.postDelayed(this, frameDelayMs())
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                registerReceiver(batteryReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("DEPRECATION")
                registerReceiver(batteryReceiver, filter)
            }
        }

        override fun onDestroy() {
            handler.removeCallbacksAndMessages(null)
            runCatching { unregisterReceiver(batteryReceiver) }
            super.onDestroy()
        }

        override fun onVisibilityChanged(isVisible: Boolean) {
            visible = isVisible
            restartDrawing()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            surfaceReady = true
            restartDrawing()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            surfaceReady = true
            restartDrawing()
        }

        private fun restartDrawing() {
            handler.removeCallbacks(drawRunnable)
            if (visible && surfaceReady) handler.post(drawRunnable)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            surfaceReady = false
            handler.removeCallbacks(drawRunnable)
            super.onSurfaceDestroyed(holder)
        }

        private fun drawFrame() {
            if (!surfaceHolder.surface.isValid) return
            val state = brain.currentState()
            var canvas: android.graphics.Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    renderer.draw(
                        canvas,
                        state,
                        SystemClock.elapsedRealtime() - startedAtMs,
                        brain.taskProgress()
                    )
                    frameCount++
                }
            } catch (error: IllegalArgumentException) {
                Log.w("CharacterWallpaper", "Surface unavailable while drawing", error)
            } catch (error: IllegalStateException) {
                Log.w("CharacterWallpaper", "Surface changed while drawing", error)
            } finally {
                canvas?.let { drawnCanvas ->
                    runCatching { surfaceHolder.unlockCanvasAndPost(drawnCanvas) }
                        .onFailure { Log.w("CharacterWallpaper", "Unable to post frame", it) }
                }
            }

            val now = SystemClock.elapsedRealtime()
            if (now - lastProfileMs >= 5_000L) {
                lastProfileMs = now
                runCatching { profiler.sample(state, frameCount, visible, batteryPercent) }
                    .onFailure { Log.w("CharacterWallpaper", "Power sample unavailable", it) }
            }
        }

        private fun frameDelayMs(): Long = when (brain.currentState()) {
            com.velcat.charactercore.core.CharacterState.SLEEP -> 250L
            com.velcat.charactercore.core.CharacterState.IDLE -> 66L
            else -> 33L
        }
    }
}
