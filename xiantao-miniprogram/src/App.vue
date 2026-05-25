<template>
  <router-view />
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* Global styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  color: #333;
}

button {
  border: none;
  outline: none;
  cursor: pointer;
}

input, textarea {
  outline: none;
  border: 1px solid #ddd;
}

input:focus, textarea:focus {
  border-color: #409eff;
}
</style>
