import { createRouter, createWebHistory } from "vue-router";

import LoginView from "../views/LoginView.vue";
import CreateUserView from "../views/CreateUserView.vue"; // <-- nueva vista
import DashboardView from "../views/DashboardView.vue";
import DevicesView from "../views/DevicesView.vue";
import NewDevicesView from "../views/AniadirDispositivo.vue";

const routes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    name: "login",
    component: LoginView,
  },
  {
    path: "/register", // ruta para crear usuario
    name: "register",
    component: CreateUserView,
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardView,
  },
  {
    path: "/devices",
    name: "devices",
    component: DevicesView,
  },
  {
    path: "/newdevice",
    name: "newdevices",
    component: NewDevicesView,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
