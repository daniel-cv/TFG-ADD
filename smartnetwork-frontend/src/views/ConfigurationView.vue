<template>
  <div class="layout">
    <aside class="sidebar">
      <h2>CONFIGURACIÓN</h2>
      <ul>
        <div class="device-type">
          <div :class="['device-badge', esFirewall ? 'firewall' : 'switch']">
            {{ esFirewall ? 'FIREWALL' : 'SWITCH' }}
          </div>
        </div>
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
          <li :class="{ active: selectedForm === 'interfaces' }" @click="selectSection('interface')">
            Interfaces
          </li>
          <li :class="{ active: selectedForm === 'user' }" @click="selectSection('user')">
            Users
          </li>
          <li :class="{ active: selectedForm === 'logs' }" @click="selectSection('logs')">
            Logs
          </li>
        </template>

        <template v-if="esSwitch">
          <li :class="{ active: selectedForm === 'interfaces' }" @click="selectSection('interfaces')">
            Interfaces
          </li>
          <li :class="{ active: selectedForm === 'vlans' }" @click="selectSection('vlans')">
            VLANs
          </li>
          <li :class="{ active: selectedForm === 'iproutes' }" @click="selectSection('iproutes')">
            IP Routes
          </li>
          <li :class="{ active: selectedForm === 'security' }" @click="selectSection('security')">
            ACLs
          </li>
          <li :class="{ active: selectedForm === 'logs' }" @click="selectSection('logs')">
            Logs
          </li>
        </template>
      </ul>
    </aside>
    <main class="content" v-if="seleccionadoStore.dispositivo">
      <div class="device-header">
        <div class="device-title-row">
          <h1 class="device-name">
            {{ seleccionadoStore.dispositivo.nombre }}
          </h1>

          <span :class="['status status-pill',
            seleccionadoStore.dispositivo.estado === 'ONLINE' ? 'online' : 'offline']">
            {{ seleccionadoStore.dispositivo.estado }}
          </span>
        </div>

        <div class="device-meta">
          <span class="device-brand">
            {{ seleccionadoStore.dispositivo.fabricante }}
          </span>

          <span class="device-ip">
            {{ seleccionadoStore.dispositivo.ip }}
          </span>
        </div>
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
        <component :is="currentComponent" :device-id="dispositivoId" :modo="modo" @crear="currentMode = 'create'"
          @creada="currentMode = 'list'" @cancelar="currentMode = 'list'" />
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
import AclList from "@/components/Switch/AclsList.vue";
import LogList from "@/components/LogList.vue"
import IpRouteList from "@/components/Switch/IpRouteList.vue"


const route = useRoute();
const seleccionadoStore = useDispositivoSeleccionadoStore();
const dispositivoStore = useDispositivoStore();
const dispositivoId = route.params.id;

const modo = "full";

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
  logs: { list: LogList },
  interfaces: { list: InterfazListS },
  "interface-config": { list: InterfazFormS },
  iproutes: { list: IpRouteList },
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
  min-height: 100vh;
  background: #f6f7fb;
  font-family: Inter, system-ui, sans-serif;
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 260px;
  height: 100vh;
  background: linear-gradient(180deg, #0b1220, #111a2e);
  color: white;
  padding: 24px 0;
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.25);
  overflow-y: auto;
}

.sidebar h2 {
  font-size: 11px;
  letter-spacing: 1.5px;
  color: #64748b;
  padding: 0 24px 12px;
}

.sidebar ul {
  list-style: none;
  padding: 0 0 20px;
  margin: 0;
}

.sidebar ul li {
  padding: 14px 24px;
  margin: 4px 12px;
  cursor: pointer;
  color: #cbd5e1;
  font-size: 14px;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
  border-radius: 10px;
}

.sidebar ul li:hover {
  background: rgba(255, 255, 255, 0.06);
  color: white;
  padding-left: 28px;
}

.sidebar ul li.active {
  background: rgba(59, 130, 246, 0.15);
  border-left: 3px solid #3b82f6;
  color: white;
  font-weight: 500;
}

.menu-title {
  margin-top: 24px;
  padding: 14px 24px 6px;
  font-size: 11px;
  text-transform: uppercase;
  color: #64748b;
  letter-spacing: 1px;
}

.content {
  margin-left: 260px;
  padding: 32px;
  width: 100%;
  color: #0f172a;
}

.device-header {
  margin-bottom: 20px;
}

.device-name {
  font-size: 48px !important;
  font-weight: 600;
  letter-spacing: -1px;
  margin: 0;
  line-height: 1.1;
  color: #0f172a;
}

.device-subinfo {
  font-size: 13px;
  color: #64748b;
}

.status {
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.status.online {
  background: rgba(34, 197, 94, 0.15);
  color: #22c55e;
}

.status.offline {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.card {
  background: white;
  border-radius: 14px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  border: 1px solid #eef2f7;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.1);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.info-item label {
  display: block;
  font-size: 12px;
  color: #64748b;
}

.info-item span {
  font-size: 14px;
  font-weight: 500;
}

.device-type {
  padding: 10px 24px 24px;
}

.device-badge {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 2px;
  text-transform: uppercase;
  padding: 14px 16px;
  border-radius: 12px;
  text-align: center;
}

.device-badge.firewall {
  background: rgba(37, 99, 235, 0.10);
  color: #3b82f6;
  box-shadow: 0 0 0 1px rgba(37, 99, 235, 0.25);
}

.device-badge.switch {
  background: rgba(79, 70, 229, 0.10);
  color: #6366f1;
  box-shadow: 0 0 0 1px rgba(79, 70, 229, 0.25);
}

.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
}

.badge.firewall {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.badge.switch {
  background: rgba(59, 130, 246, 0.15);
  color: #3b82f6;
}
</style>
