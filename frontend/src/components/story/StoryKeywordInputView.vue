<template>
  <div>
    <div id="cardGroup">
      <div style="margin-top:100px; margin-bottom: 30px; margin-left: -30px;">
        <h2>동화 키워드 입력</h2>
      </div>
      <div id="inputGroup">
        <input type="text" v-model="inputValue" ref="keywordInput"/>
        <button @click="sendInputValue" class="ae-btn btn-red">전송</button>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'StroyKeywordInputView',
  data () {
    return {
      inputValue: ''
    }
  },
  methods: {
    sendInputValue () {
      if (this.inputValue === '') {
        alert('키워드를 입력해주세요.')
        this.$refs.keywordInput.focus()
      } else {
        this.$router.push({ name: 'StoryPaintingBoard', params: { inputValue: this.inputValue } })
      }
    }
  }
}
</script>
<style scoped>
h2 {
  font-weight: 800;
}
#inputGroup input{
  width: 400px;
  height: 40px;
}

#cardGroup{
  width:700px; height: 400px; margin-left: 400px; margin-top:100px;
  padding-top:20px;
}

</style>
