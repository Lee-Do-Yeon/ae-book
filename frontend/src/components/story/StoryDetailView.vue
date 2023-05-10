<template>
  <div>
    <h3 v-if="story">
      <b-form-input v-model="story.title" placeholder="Enter your name"></b-form-input>
    </h3>
    <p v-if="story">{{ story.content }}</p>
    <img v-bind:src="story.imgUrl" alt="story image" v-if="story" />
    <p v-if="story">작성자: {{ story.storyAuthorNickname }}</p>
    <p v-if="story">작성일: {{ story.createdAt }}</p>
    <StoryModalButton :title="story.title" :voiceUrl="story.voiceUrl" @close="closeModal"></StoryModalButton>
  </div>
</template>

<script>
import { searchDetailStory } from '@/api/story'
import { mapGetters } from 'vuex'
import StoryModalButton from '@/components/story/StoryModalButton'
const storyStore = 'storyStore'

export default {
  name: 'StoryDetailView',
  components: {
    StoryModalButton
  },
  computed: {
    ...mapGetters(storyStore, ['getStoryId'])
  },

  data () {
    return {
      story: null
    }
  },
  mounted () {
    this.storyId = this.getStoryId

    searchDetailStory(this.storyId)
      .then(response => {
        this.story = response.data.result
      })
      .catch(error => {
        alert('정상적으로 조회하지 못했습니다. ' + error)
      })
  },
  methods: {
    closeModal () {
      this.$emit('close')
    }
  }

}
</script>

<style scoped>
</style>
