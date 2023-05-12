<template>
  <div>
    <input type="text" v-model="inputValue" />
    <button @click="sendInputValue">전송</button>
    <div>{{storyResult}}</div>
    <button @click="playAudio()">오디오 재생</button>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  components: {
  },
  data () {
    return {
      inputValue: '',
      storyResult: '',
      rescode: '',
      sound: 'http://localhost:8000/static/sound/',
      sound_filename: '',
      soundBlob: {}
    }
  },
  methods: {
    playAudio () {
      if (this.soundBlob) {
        var blobURL = window.URL.createObjectURL(this.soundBlob)
        // const audio = new Audio(sound)
        const audio = new Audio(blobURL)
        audio.play()
      }
    },
    sendInputValue () {
      console.log(typeof this.inputValue)
      axios
        .post(`/fast/stories/gpt`, {
          text: this.inputValue
        })
        .then(result => {
          this.storyResult = result.data.story
          axios({
            url: `/fast/stories/sound`,
            method: 'post',
            data: {
              data: this.storyResult
            }
          })
            .then(result => {
              console.log(result)
              this.rescode = result.data.rescode
              // this.sound += result.data.sound
              this.sound_filename = result.data.sound
              axios({
                url: `/fast/file/download/${this.sound_filename}`,
                method: 'GET',
                responseType: 'blob'
              }).then((response) => {
                this.soundBlob = new Blob([response.data], {type: 'audio/mp3'})
                console.log(this.soundBlob)
              })
              // console.log(this.sound)
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(err => {
          console.log(err)
        })
    }
  }
}
</script>
