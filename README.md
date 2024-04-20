<div align="center">

  <img src="https://github.com/ris5266/chatbot/blob/master/src/main/resources/icon.jpeg" alt="logo" width="500px" height="500px"/>
  
# Customizable Text-To-Speech Chatbot
</div>

### A fast customizable Text-To-Speech Chatbot built using Java, JavaFX, [Ollama API](https://github.com/ollama/ollama) and [Silero-TTS](https://github.com/twirapp/silero-tts-api-server)

> still under development

<div align="center">

  ---
  [**Features**](#features) | [**Install**](#install) | [**Upcoming**](#upcoming)

  ---

</div>

## Features

🎨 **Customization**: Customize your character's name, description, and gender for a unique experience

👥 **Multiple Characters**: Create and switch between multiple characters seamlessly

🤖 **AI Integration**: Select various models through [OllamaAPI](https://github.com/ollama/ollama) for dynamic chat experiences

🔊 **Text-to-Speech**: The [Silero-TTS](https://github.com/twirapp/silero-tts-api-server) ensures that the bot responses are spoken in accordance with the character's gender

<div align="center">

 https://github.com/ris5266/chatbot/assets/86254687/0ec8c932-3bf5-4a7e-85cd-199af3f93b57
  
</div>

## Install

1. **Clone the repository**
```
git clone https://github.com/ris5266/chatbot.git
```



2. **Install [OllamaAPI](https://github.com/ollama/ollama) using Docker**
   
      Run in CPU:
      ```
      docker run -it -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama
      ```
      Run in GPU:
      ```
      docker run -it --gpus=all -v ~/ollama:/root/.ollama -p 11434:11434 ollama/ollama
      ```



3. **Download your desired [chat model](https://ollama.com/library)**
   
      For example:
      ```
      ollama run llama3
      ```



4. **Install [Silero-TTS-Api-Server](https://github.com/twirapp/silero-tts-api-server) using Docker**
```
docker run --rm -p 8000:8000 twirapp/silero-tts-api-server
```




## Upcoming
- save & request previous chat from database
- upload custom voice models (for example [rvc v2](https://github.com/RVC-Project/Retrieval-based-Voice-Conversion-WebUI/tree/main) models)
- ... and more!




