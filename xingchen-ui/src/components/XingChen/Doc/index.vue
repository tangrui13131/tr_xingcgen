<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'XingChenDoc',
  data() {
    return {
      url: 'http://doc.xingchen.vip/xingchen-vue'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>