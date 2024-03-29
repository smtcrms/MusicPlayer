/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.service.notification

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import androidx.core.app.NotificationCompat
import code.name.monkey.retromusic.Constants.ACTION_QUIT
import code.name.monkey.retromusic.Constants.ACTION_REWIND
import code.name.monkey.retromusic.Constants.ACTION_SKIP
import code.name.monkey.retromusic.Constants.ACTION_TOGGLE_PAUSE
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.glide.GlideApp
import code.name.monkey.retromusic.glide.RetroGlideExtension
import code.name.monkey.retromusic.glide.RetroSimpleTarget
import code.name.monkey.retromusic.glide.palette.BitmapPaletteWrapper
import code.name.monkey.retromusic.service.MusicService
import code.name.monkey.retromusic.activities.MainActivity
import code.name.monkey.retromusic.util.PreferenceUtil
import code.name.monkey.retromusic.util.RetroColorUtil
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

class PlayingNotificationImpl24 : PlayingNotification() {
    private var target: Target<BitmapPaletteWrapper>? = null
    @Synchronized
    override fun update() {
        stopped = false

        val song = service.currentSong
        val isPlaying = service.isPlaying

        val playButtonResId = if (isPlaying)
            R.drawable.ic_pause_white_24dp
        else
            R.drawable.ic_play_arrow_white_32dp

        val action = Intent(service, MainActivity::class.java)
        action.putExtra("expand", true)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val clickIntent = PendingIntent
                .getActivity(service, 0, action, PendingIntent.FLAG_UPDATE_CURRENT)

        val serviceName = ComponentName(service, MusicService::class.java)
        val intent = Intent(ACTION_QUIT)
        intent.component = serviceName
        val deleteIntent = PendingIntent.getService(service, 0, intent, 0)

        val bigNotificationImageSize = service.resources
                .getDimensionPixelSize(R.dimen.notification_big_image_size)
        service.runOnUiThread {
            if (target != null) {
                GlideApp.with(service).clear(target);
            }
            target = GlideApp.with(service)
                    .asBitmapPalette()
                    .load(RetroGlideExtension.getSongModel(song))
                    .transition(RetroGlideExtension.getDefaultTransition())
                    .songOptions(song)
                    .into(object : RetroSimpleTarget<BitmapPaletteWrapper>(bigNotificationImageSize, bigNotificationImageSize) {
                        override fun onResourceReady(resource: BitmapPaletteWrapper, transition: Transition<in BitmapPaletteWrapper>?) {
                            update(resource.bitmap, when {
                                PreferenceUtil.getInstance().isDominantColor -> RetroColorUtil.getDominantColor(resource.bitmap, Color.TRANSPARENT)
                                else -> RetroColorUtil.getColor(resource.palette, Color.TRANSPARENT)
                            })
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            update(null, Color.TRANSPARENT)
                        }

                        fun update(bitmap: Bitmap?, color: Int) {
                            var bitmapFinal = bitmap
                            if (bitmapFinal == null) {
                                bitmapFinal = BitmapFactory.decodeResource(service.resources, R.drawable.default_album_art)
                            }
                            val playPauseAction = NotificationCompat.Action(
                                    playButtonResId,
                                    service.getString(R.string.action_play_pause),
                                    retrievePlaybackAction(ACTION_TOGGLE_PAUSE))

                            val closeAction = NotificationCompat.Action(
                                    R.drawable.ic_close_white_24dp,
                                    service.getString(R.string.close_notification),
                                    retrievePlaybackAction(ACTION_QUIT))

                            val previousAction = NotificationCompat.Action(
                                    R.drawable.ic_skip_previous_white_24dp,
                                    service.getString(R.string.action_previous),
                                    retrievePlaybackAction(ACTION_REWIND))

                            val nextAction = NotificationCompat.Action(
                                    R.drawable.ic_skip_next_white_24dp,
                                    service.getString(R.string.action_next),
                                    retrievePlaybackAction(ACTION_SKIP))

                            val builder = NotificationCompat.Builder(service,
                                    PlayingNotification.NOTIFICATION_CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setLargeIcon(bitmapFinal)
                                    .setContentIntent(clickIntent)
                                    .setDeleteIntent(deleteIntent)
                                    .setContentTitle(Html.fromHtml("<b>" + song.title + "</b>"))
                                    .setContentText(song.artistName)
                                    .setSubText(Html.fromHtml("<b>" + song.albumName + "</b>"))
                                    .setOngoing(isPlaying)
                                    .setShowWhen(false)
                                    .addAction(previousAction)
                                    .addAction(playPauseAction)
                                    .addAction(nextAction)
                                    .addAction(closeAction)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder.setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                                        .setMediaSession(service.mediaSession.sessionToken)
                                        .setShowActionsInCompactView(0, 1, 2, 3, 4))
                                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O && PreferenceUtil.getInstance().coloredNotification()) {
                                    builder.color = color
                                }
                            }

                            if (stopped) {
                                return  // notification has been stopped before loading was finished
                            }
                            updateNotifyModeAndPostNotification(builder.build())
                        }
                    })
        }
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val serviceName = ComponentName(service, MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(service, 0, intent, 0)
    }
}