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
:root {
  --app-canvas: radial-gradient(900px 420px at 100% 0%, rgba(20, 184, 166, 0.20), transparent 60%), radial-gradient(700px 420px at 0% 100%, rgba(14, 165, 233, 0.16), transparent 60%), linear-gradient(150deg, #0f172a 0%, #103a3d 55%, #0f2a4d 100%);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background: #0f172a;
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
