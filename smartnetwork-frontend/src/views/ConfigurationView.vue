<template>
  <div class="layout">

    <!-- SIDEBAR -->
    <aside class="sidebar">
      <h2>CONFIGURACIÓN</h2>

      <ul>

        <!-- FIREWALL -->
        <template v-if="esFirewall">
          <li :class="{ active: selectedForm === 'policy' }" @click="selectSection('policy')">
            Policies
          </li>
          <li :class="{ active: selectedForm === 'address' }" @click="selectSection('address')">
            Addresses

          </li>
          <li :class="{ active: selectedForm === 'service' }" @click="selectSection('service')">
            Services</li>
          <li :class="{ active: selectedForm === 'vip' }" @click="selectSection('vip')">Virtual IPs

          </li>
          <li :class="{ active: selectedForm === 'user' }" @click="selectSection('user')">
            Users
          </li>
        </template>

        <!-- 🔥 SWITCH -->
        <template v-if="esSwitch">
          <li class="menu-title">Interfaces</li>
          <li :class="{ active: selectedForm === 'interfaces' }" @click="selectSection('interfaces')">
            Lista de interfaces
          </li>
          <li :class="{ active: selectedForm === 'interface-config' }" @click="selectSection('interface-config')">
            Configurar puerto
          </li>

          <li class="menu-title">VLANs</li>
          <li :class="{ active: selectedForm === 'vlans' }" @click="selectSection('vlans')">
            VLANs
          </li>

          <li class="menu-title">Routing</li>
          <li :class="{ active: selectedForm === 'routing' }" @click="selectSection('routing')">
            Routing básico
          </li>

          <li class="menu-title">Seguridad</li>
          <li :class="{ active: selectedForm === 'security' }" @click="selectSection('security')">
            ACLs
          </li>
        </template>
      </ul>
    </aside>

    <!-- CONTENIDO -->
    <main class="content" v-if="seleccionadoStore.dispositivo">

      <div class="device-header">
        <h1>{{ seleccionadoStore.dispositivo.nombre }}</h1>
        <span class="device-subinfo">
          {{ seleccionadoStore.dispositivo.fabricante }} ·
          <span :class="['status',
            seleccionadoStore.dispositivo.estado === 'ONLINE' ? 'online' : 'offline'
          ]">
            {{ seleccionadoStore.dispositivo.estado }}
          </span>
        </span>
      </div>

      <div class="card configuration-view">
        <h2>Información del dispositivo</h2>
        <div class="info-grid">
          <div class="info-item">
            <label>IP: </label>
            <span>{{ seleccionadoStore.dispositivo.ip }}</span>
          </div>
          <div class="info-item">
            <label>Fabricante: </label>
            <span>{{ seleccionadoStore.dispositivo.fabricante }}</span>
          </div>
          <div class="info-item">
            <label>Estado: </label>
            <span :class="['status',
              seleccionadoStore.dispositivo.estado === 'ONLINE' ? 'online' : 'offline'
            ]">
              {{ seleccionadoStore.dispositivo.estado }}
            </span>
          </div>
        </div>
      </div>

      <div class="card form-container">
        <component
          :is="currentComponent"
          :device-id="dispositivoId"
          :modo="modo"
          @crear="currentMode = 'create'"
          @creada="currentMode = 'list'"
          @cancelar="currentMode = 'list'"
        />
      </div>

    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore";
import { useRoute } from "vue-router";
import { useDispositivoStore } from "@/stores/dispositivoStore";

import ReglaFirewallList from "@/components/ReglaFirewallList.vue"
import ReglaFirewallForm from "@/components/ReglaFirewallForm.vue"

import AddressList from "@/components/AddressList.vue"
import AddressForm from "@/components/AddressForm.vue"

import ServiceList from "@/components/ServiceList.vue"
import ServiceForm from "@/components/ServiceForm.vue"

import UsuarioFirewallList from "@/components/UsuarioFirewallList.vue"
import UsuarioFirewallForm from "@/components/UsuarioFirewallForm.vue"

import VirtualIpList from "@/components/VirtualIpList.vue"
import VirtualIpForm from "@/components/VirtualIpForm.vue"

import InterfazList from "@/components/InterfazList.vue"
import InterfazForm from "@/components/InterfazForm.vue"

import InterfazFormS from "@/components/Switch/InterfazForm.vue"
import InterfazListS from "@/components/Switch/InterfazList.vue"

import VlanList from "@/components/Switch/VlanList.vue"

import RoutingForm from "@/components/Switch/RoutingForm.vue";
import RoutingList from "@/components/Switch/IpRouteList.vue";
import AclList from "@/components/Switch/AclsList.vue";


const route = useRoute();
const seleccionadoStore = useDispositivoSeleccionadoStore();
const dispositivoStore = useDispositivoStore();
const dispositivoId = route.params.id;

const modo ="full";

const esFirewall = computed(() =>
  seleccionadoStore.dispositivo?.tipo === "FIREWALL"
);

const esSwitch = computed(() =>
  seleccionadoStore.dispositivo?.tipo === "SWITCH"
);

onMounted(async () => {
  const dispositivo = await dispositivoStore.getDispositivo(dispositivoId);
  seleccionadoStore.seleccionar(dispositivo);
});

const selectedForm = ref<string | null>(null)
const currentMode = ref<'list' | 'create'>('list')

const currentComponent = computed(() => {
  if (!selectedForm.value) return null
  return componentMap[selectedForm.value]?.[currentMode.value] || null
})

const componentMap: Record<string, any> = {

  // FIREWALL
  policy: {
    list: ReglaFirewallList,
    create: ReglaFirewallForm
  },
  address: {
    list: AddressList,
    create: AddressForm
  },
  service: {
    list: ServiceList,
    create: ServiceForm
  },
  user: {
    list: UsuarioFirewallList,
    create: UsuarioFirewallForm
  },
  vip: {
    list: VirtualIpList,
    create: VirtualIpForm
  },
  interface: {
    list: InterfazList,
    create: InterfazForm
  },
  vlans: { list: VlanList },

  routing: { list: RoutingList },

  interfaces:{ list: InterfazListS},
  "interface-config": { list: InterfazFormS },

  security: { list: AclList }
}

function selectSection(section: string) {
  selectedForm.value = section
  currentMode.value = 'list'
}
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  background: #f1f5f9;
}

.sidebar {
  position: fixed;
  top: 1;
  left: 0;
  width: 250px;
  height: 100vh;
  background: linear-gradient(180deg, #0f172a, #1e293b);
  color: white;
  padding-top: 20px;
}

.sidebar h2 {
  padding-left: 24px;
  font-size: 13px;
  color: #64748b;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar ul li {
  padding: 12px 24px;
  cursor: pointer;
  color: #cbd5e1;
}

.sidebar ul li:hover {
  background: rgba(255, 255, 255, 0.05);
}

.sidebar ul li.active {
  background: rgba(59, 130, 246, 0.15);
  border-left: 3px solid #3b82f6;
  color: white;
}

.menu-title {
  padding: 10px 24px;
  font-size: 12px;
  color: #64748b;
  font-weight: bold;
}

.submenu-title {
  padding: 8px 24px;
  font-size: 13px;
  color: #94a3b8;
}

.content {
  flex: 1;
  margin-left: 260px;
  color: black;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  color: black;
}
</style>
