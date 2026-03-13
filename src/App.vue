<template>
  <el-container class="app-layout">
    <el-header height="56px" class="app-header">
      <div class="app-header__title">FHIR 個案管理雛型</div>
      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        class="app-header__menu"
        @select="onSelect"
      >
        <el-menu-item index="/cases/search">個案篩選</el-menu-item>
        <el-menu-item index="/cases/intake">收案清單</el-menu-item>
      </el-menu>
    </el-header>
    <el-main class="app-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { computed } from 'vue';

const route = useRoute();
const router = useRouter();

const activeMenu = computed(() => {
  if (route.path.startsWith('/cases/intake')) return '/cases/intake';
  return '/cases/search';
});

const onSelect = (path: string) => {
  if (path !== route.path) {
    router.push(path);
  }
};
</script>

