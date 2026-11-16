# Voice AI Studio — Android

Aplicativo Android nativo de narração de texto, com interface escura, seleção de vozes em português, ajuste de velocidade e tom, reprodução e solicitação de exportação de áudio pelo mecanismo TTS do dispositivo.

## Gerar APK pelo GitHub (sem conectar GitHub ao ChatGPT)

1. Crie um repositório novo no GitHub (por exemplo, `VoiceAIStudio`).
2. Extraia este ZIP e envie **o conteúdo da pasta VoiceAIStudio**, incluindo a pasta oculta `.github`, para a raiz do repositório. Pelo navegador, use **Add file > Upload files**. Verifique se `.github/workflows/android.yml` foi incluído.
3. Faça commit na branch `main`.
4. Abra **Actions > Build Android APK**. Se necessário, habilite Actions ou clique **Run workflow**.
5. Após o build terminar, abra a execução e baixe **VoiceAIStudio-APK** em **Artifacts**. Extraia o ZIP do artifact e instale `app-debug.apk`.

## Requisitos
- Java 17, Gradle 8.9, Android SDK 35 (configurados automaticamente pelo workflow).
- Android 8.0 ou superior com mecanismo de texto para fala (TTS) instalado.

## Limitações honestas
- Esta versão **não** usa API do ElevenLabs e **não** tem clonagem de voz, login Google ou IA generativa online. Não é uma cópia do ElevenLabs.
- A qualidade, os idiomas e o funcionamento offline dependem do mecanismo TTS do Android e das vozes instaladas.
- A exportação depende do mecanismo TTS; a solicitação de arquivo não confirma a finalização, e alguns mecanismos podem produzir formatos diferentes da extensão usada.
- O APK é um **debug APK** para teste, não um pacote assinado para publicação na Play Store.
- O código não foi compilado neste ambiente; o GitHub Actions fará a primeira validação.
