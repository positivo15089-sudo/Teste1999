package com.voiceaistudio.app

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var ready by remember { mutableStateOf(false) }
            var text by remember { mutableStateOf("Olá! Bem-vindo ao Voice AI Studio. Digite seu texto e escolha uma voz.") }
            var status by remember { mutableStateOf("Inicializando mecanismo de voz…") }
            var speed by remember { mutableFloatStateOf(1.0f) }
            var pitch by remember { mutableFloatStateOf(1.0f) }
            var voices by remember { mutableStateOf<List<Voice>>(emptyList()) }
            var selected by remember { mutableStateOf<Voice?>(null) }
            var expanded by remember { mutableStateOf(false) }
            var savedFile by remember { mutableStateOf<File?>(null) }
            DisposableEffect(Unit) {
                tts = TextToSpeech(context) { result ->
                    if (result == TextToSpeech.SUCCESS) {
                        val engine = tts
                        engine?.language = Locale("pt", "BR")
                        voices = engine?.voices?.filter { it.locale.language == "pt" }?.sortedBy { it.name } ?: emptyList()
                        selected = voices.firstOrNull { it.locale.country == "BR" } ?: voices.firstOrNull()
                        ready = true
                        status = "Pronto para gerar áudio"
                    } else status = "Mecanismo de voz indisponível. Instale/ative um serviço TTS no Android."
                }
                onDispose { tts?.stop(); tts?.shutdown(); tts = null }
            }
            MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFFB8A3FF), background = Color(0xFF111016), surface = Color(0xFF211E29))) {
                Scaffold { padding ->
                    Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("VOICE AI STUDIO", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        Text("Estúdio de narração e conversão de texto em voz", style = MaterialTheme.typography.bodyMedium)
                        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Seu texto") }, modifier = Modifier.fillMaxWidth().heightIn(min = 180.dp), maxLines = 12)
                        Text("Voz em português")
                        Box {
                            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(selected?.name ?: "Voz padrão do dispositivo") }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                voices.forEach { voice -> DropdownMenuItem(text = { Text("${voice.name} (${voice.locale.displayCountry})") }, onClick = { selected = voice; expanded = false }) }
                            }
                        }
                        Text("Velocidade: ${"%.1f".format(speed)}x")
                        Slider(value = speed, onValueChange = { speed = it }, valueRange = 0.5f..1.7f)
                        Text("Tom: ${"%.1f".format(pitch)}x")
                        Slider(value = pitch, onValueChange = { pitch = it }, valueRange = 0.5f..1.7f)
                        Button(enabled = ready && text.isNotBlank(), onClick = {
                            tts?.apply { selected?.let { voice = it }; setSpeechRate(speed); setPitch(pitch); speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString()) }
                            status = "Reproduzindo narração…"
                        }, modifier = Modifier.fillMaxWidth()) { Text("▶ Ouvir narração") }
                        OutlinedButton(enabled = ready, onClick = { tts?.stop(); status = "Reprodução interrompida" }, modifier = Modifier.fillMaxWidth()) { Text("■ Parar") }
                        Button(enabled = ready && text.isNotBlank(), onClick = {
                            val file = File(context.getExternalFilesDir(null), "narracao_${System.currentTimeMillis()}.wav")
                            tts?.apply { selected?.let { voice = it }; setSpeechRate(speed); setPitch(pitch) }
                            val result = tts?.synthesizeToFile(text, Bundle(), file, UUID.randomUUID().toString())
                            if (result == TextToSpeech.SUCCESS) { savedFile = file; status = "Solicitação de exportação enviada: ${file.name}. Aguarde a geração antes de compartilhar." }
                            else status = "Não foi possível iniciar a exportação."
                        }, modifier = Modifier.fillMaxWidth()) { Text("Salvar áudio (arquivo do mecanismo TTS)") }
                        Text(status, color = MaterialTheme.colorScheme.primary)
                        Text("As vozes dependem do mecanismo TTS instalado. Esta versão inicial não inclui clonagem de voz nem geração por IA em nuvem.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
