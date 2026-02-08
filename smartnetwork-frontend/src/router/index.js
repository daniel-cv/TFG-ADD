import { createRouter, createWebHistory } from "vue-router";

import LoginView from "../views/LoginView.vue";
import CreateUserView from "../views/CreateUserView.vue"; // <-- nueva vista
import DashboardView from "../views/DashboardView.vue";
import NewDevicesView from "../views/AniadirDispositivo.vue";
import DispositivosView from "../views/DispositivosView.vue";
import ReglaFirewallView from "../views/ReglasFirewallView.vue";
import ConfigurationView from "@/views/ConfigurationView.vue";
import InterfazView from "@/views/InterfazView.vue";
import AddressView from "@/views/AddressView.vue";
import ServiceView from "@/views/ServiceView.vue";
import VirtualIpView from "@/views/VirtualIpView.vue";
import UsuarioFirewallView from "@/views/UsuarioFirewallView.vue";

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
    path: "/interfaces/:id",
    name: "CrearInterfaz",
    component: InterfazView,
  },
  {
    path: "/crearaddress/:id",
    name: "CrearAddress",
    component: AddressView,
  },
  {
    path: '/device/:id',
    name: 'deviceConfiguration',
    component: ConfigurationView,
  },
  {
    path: "/service/:id",
    name: "CrearService",
    component: ServiceView,
  },
  {
    path: "/virtualIp/:id",
    name: "CrearVirtualIp",
    component: VirtualIpView,
  },
  {
    path: "/usuariofirewall/:id",
    name: "UsuarioFirewall",
    component: UsuarioFirewallView,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
