/* eslint-disable */
<template>
  <div class="hello">
    <h1>{{ listPage }}</h1>
    <b-table striped hover :items="items" v-model="items"></b-table>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  data() {
      return {
        msg: "this is hello world page",
        items: []
      };
  },
  mounted(){
    this.searchBook();
  },
  methods:{
  	searchBook: function() {
        this.$axios
          .get("/api/book")
          .then(data => {
            console.log(data);
            this.items = data.data;
          })
          .catch(error => {
            alert(error);
          })
          .finally(() => {
 		  });
      },
  }
  
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
