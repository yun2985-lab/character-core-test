package com.velcat.charactercore.wallpaper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.velcat.charactercore.character.CharacterAssetPack
import com.velcat.charactercore.character.CharacterMotionProfiles
import com.velcat.charactercore.core.CharacterState
import kotlin.math.PI
import kotlin.math.sin

class CharacterRenderer(private val context: Context, private val assets: CharacterAssetPack) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 34f; textAlign = Paint.Align.CENTER }

    fun draw(canvas: Canvas, state: CharacterState, elapsedMs: Long, progress: Float) {
        canvas.drawRGB(15, 12, 24)
        val w = canvas.width.toFloat(); val h = canvas.height.toFloat(); val t = elapsedMs / 1000f
        val m = CharacterMotionProfiles.forState(assets.id, state)
        val wave = sin((2.0 * PI * m.bobHz * t).toFloat())
        val bob = wave * m.bobAmplitude
        val bitmap = assets.frame(context, state, elapsedMs)
        val targetH = (h * if (assets.id == "starcat") .40f else .34f).coerceIn(260f, 620f)
        val targetW = targetH * bitmap.width.toFloat() / bitmap.height.toFloat()
        val range = (w - targetW).coerceAtLeast(1f)
        val x = if (state == CharacterState.WALK) ((t * m.walkSpeedPx) % (range * 2f)).let { if (it > range) range * 2f - it else it } else (w-targetW)/2f
        val bottom = h - 90f + bob
        val dst = RectF(x, bottom-targetH, x+targetW, bottom)
        val breathe = 1f + sin(t * 2.1f) * m.breatheAmplitude
        val cy = dst.centerY(); dst.top = cy + (dst.top-cy)*breathe; dst.bottom = cy + (dst.bottom-cy)*breathe
        val rotation = wave * m.swayDegrees
        paint.alpha = 255
        canvas.save(); canvas.rotate(rotation, dst.centerX(), dst.centerY()); canvas.drawBitmap(bitmap, null, dst, paint); canvas.restore()
        textPaint.setARGB(205,235,226,255); canvas.drawText("${assets.displayName} · ${state.name}", w/2f, 70f, textPaint)
        when(state){
            CharacterState.CHARGING -> badge(canvas,w,"⚡ 충전 중")
            CharacterState.BATTERY_LOW -> badge(canvas,w,"배터리 부족")
            CharacterState.MUSIC -> badge(canvas,w,"♪ MUSIC")
            else -> Unit
        }
        if(state==CharacterState.WORKING){
            paint.setARGB(90,255,255,255); canvas.drawRoundRect(RectF(w*.16f,102f,w*.84f,136f),18f,18f,paint)
            paint.setARGB(225,204,146,255); canvas.drawRoundRect(RectF(w*.16f,102f,w*(.16f+.68f*progress.coerceIn(0f,1f)),136f),18f,18f,paint)
        }
    }
    private fun badge(canvas:Canvas,w:Float,label:String){ textPaint.textSize=27f; canvas.drawText(label,w/2f,176f,textPaint); textPaint.textSize=34f }
}
