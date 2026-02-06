import { createRouter, createWebHistory } from "vue-router";

import LoginView from "../views/LoginView.vue";
import CreateUserView from "../views/CreateUserView.vue"; // <-- nueva vista
import DashboardView from "../views/DashboardView.vue";
import NewDevicesView from "../views/AniadirDispositivo.vue";
import DispositivosView from "../views/DispositivosView.vue";
import ReglaFirewallView from "../views/ReglasFirewallView.vue";
import InterfazView from "@/views/InterfazView.vue";

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
    component: DispositivosView,
  },
  {
    path: "/newdevice",
    name: "newdevices",
    component: NewDevicesView,
  },
  {
    path: '/crearpolicy/:id',
    name: 'crearpolicy',
    component: ReglaFirewallView,

  },
  {
    path: "/dispositivos/:id/interfaces/crear",
    name: "CrearInterfaz",
    component: InterfazView,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
