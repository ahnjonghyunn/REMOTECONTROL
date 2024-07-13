package com.example.remote

import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.MediaController
import android.widget.SeekBar
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var playButton: Button
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumeTextView: TextView
    private lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)

        videoView = findViewById(R.id.videoView)
        playButton = findViewById(R.id.playButton)
        volumeSeekBar = findViewById(R.id.volumeSeekBar)
        volumeTextView = findViewById(R.id.volumeTextView)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        // 비디오 파일 설정
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.sample_video)
        videoView.setVideoURI(videoUri)

        // MediaController 설정
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Play 버튼 클릭 리스너
        playButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                playButton.text = "start"
            } else {
                videoView.start()
                playButton.text = "Pause"
            }
        }

        // 볼륨 조절 시크바 설정
        volumeSeekBar.max = 100
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val volumePercentage = (currentVolume.toFloat() / maxVolume * 100).toInt()
        volumeSeekBar.progress = volumePercentage
        volumeTextView.text = "Volume: $volumePercentage"

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newVolume = (progress / 100.0 * maxVolume).toInt()
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
                volumeTextView.text = "Volume: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                adjustVolume(true)
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                adjustVolume(false)
                true
            }
            KeyEvent.KEYCODE_1 -> {
                togglePower()
                true
            }
            KeyEvent.KEYCODE_2 -> {
                adjustVolume(true)
                true
            }
            KeyEvent.KEYCODE_3 -> {
                adjustVolume(false)
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun adjustVolume(increase: Boolean) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        // 볼륨 증감 단위를 계산하여 원하는 값으로 설정
        val increment = (10 / 3)  // 3 단위로 증가할 때 10 증가를 원하는 경우 3으로 나눔

        val newVolume = if (increase) {
            (currentVolume + increment).coerceAtMost(maxVolume)
        } else {
            (currentVolume - increment).coerceAtLeast(0)
        }

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI)
        val volumePercentage = (newVolume.toFloat() / maxVolume * 100).toInt()
        volumeSeekBar.progress = volumePercentage
        volumeTextView.text = "Volume: $volumePercentage"
    }

    private fun togglePower() {
        if (videoView.isPlaying) {
            videoView.pause()
            playButton.text = "start"
        } else {
            videoView.start()
            playButton.text = "Pause"
        }
    }
}
