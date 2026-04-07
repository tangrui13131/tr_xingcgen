<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'XingChenGit',
  data() {
    return {
      url: 'https://gitee.com/y_project/XingChen-Vue'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>