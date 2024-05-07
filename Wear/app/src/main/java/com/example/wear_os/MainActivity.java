package com.example.wear_os;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AudioHelper audioHelper;
    private AudioManager audioManager;
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioHelper = new AudioHelper(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
            }

            @Override
            public void onResults(Bundle results) {
                List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    processRecognizedCommand(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        requestAudioFocus();
        startSpeechRecognition();
    }

    private void requestAudioFocus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                        @Override
                        public void onAudioFocusChange(int focusChange) {
                            // Implement your audio focus change handling here
                        }
                    })
                    .build();
            audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            // For devices with API level below Oreo
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        speechRecognizer.startListening(intent);
    }

    private void processRecognizedCommand(String command) {
        if (command != null && !command.isEmpty()) {
            textToSpeech.speak("Comando reconhecido: " + command, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak("Nenhum comando reconhecido.", TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
