import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import CaseSearchView from '../views/CaseSearchView.vue';
import CaseIntakeListView from '../views/CaseIntakeListView.vue';
import CaseProfileView from '../views/CaseProfileView.vue';
import CaseVisitsView from '../views/CaseVisitsView.vue';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/cases/search'
  },
  {
    path: '/cases/search',
    component: CaseSearchView
  },
  {
    path: '/cases/intake',
    component: CaseIntakeListView
  },
  {
    path: '/cases/:patientId/profile',
    component: CaseProfileView,
    props: true
  },
  {
    path: '/cases/:patientId/visits',
    component: CaseVisitsView,
    props: true
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;

