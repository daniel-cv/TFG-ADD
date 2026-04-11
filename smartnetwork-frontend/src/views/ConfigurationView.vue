<template>
  <div class="layout">
    <!-- SIDEBAR -->
    <aside class="sidebar">
      <h2>CONFIGURACIÓN</h2>
      <ul>
        <li
          :class="{ active: selectedForm === 'policy' }"
          @click="selectSection('policy')"
        >
          Policies
        </li>
        <li
          :class="{ active: selectedForm === 'address' }"
          @click="selectSection('address')"
        >
          Addresses
        </li>
        <li
          :class="{ active: selectedForm === 'service' }"
          @click="selectSection('service')"
        >
          Services
        </li>
        <li
          :class="{ active: selectedForm === 'vip' }"
          @click="selectSection('vip')"
        >
          Virtual IPs
        </li>
        <li
          :class="{ active: selectedForm === 'user' }"
          @click="selectSection('user')"
        >
          Users
        </li>
        <li
          :class="{ active: selectedForm === 'interface' }"
          @click="selectSection('interface')"
        >
          Interfaces
        </li>
        <li>
          Routing
        </li>
        <li>
          Logs
        </li>
      </ul>
    </aside>



    <!-- CONTENIDO PRINCIPAL -->
    <main class="content" v-if="seleccionadoStore.dispositivo">
      <div class="device-header">
        <h1>
          {{ seleccionadoStore.dispositivo.nombre }}
        </h1>
        <span class="device-subinfo">
          {{ seleccionadoStore.dispositivo.fabricante }}
          ·
          <span
            :class="[
            'status',
            seleccionadoStore.dispositivo.estado === 'ONLINE'
            ? 'online'
            : 'offline'
            ]"
          >
            {{ seleccionadoStore.dispositivo.estado }}
          </span>
        </span>
      </div>

      <div class="card configuration-view">
        <h2>Información del dispositivo</h2>
        <div class="info-grid">
          <div class="info-item">
            <label>IP</label>
            <span>
              {{ seleccionadoStore.dispositivo.ip }}
            </span>
          </div>

          <div class="info-item">
            <label>Fabricante</label>
            <span>
              {{ seleccionadoStore.dispositivo.fabricante }}
            </span>
          </div>

          <div class="info-item">
            <label>Estado</label>
            <span
              :class="[
              'status',
              seleccionadoStore.dispositivo.estado === 'ONLINE'
              ? 'online'
              : 'offline'
              ]"
            >
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



const route = useRoute();
const seleccionadoStore = useDispositivoSeleccionadoStore();
const dispositivoStore = useDispositivoStore();
const dispositivoId = route.params.id;

const modo ="full";

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
  }
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
  font-family: Inter, system-ui;
}

.sidebar {
  width: 250px;
  background: linear-gradient(
  180deg,
  #0f172a,
  #1e293b
  );
  color: white;
  padding-top: 20px;
}

.sidebar h2 {
  padding-left: 24px;
  font-size: 13px;
  color: #64748b;
  margin-bottom: 10px;
  letter-spacing: 1px;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar ul li {
  padding: 14px 24px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  color: #cbd5e1;
  border-left: 3px solid transparent;
}

.sidebar ul li:hover {
  background: rgba(255,255,255,0.05);
  color: white;
}

.sidebar ul li.active {
  background: rgba(59,130,246,0.15);
  border-left: 3px solid #3b82f6;
  color: white;
}

.content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
  color: black;
}

.device-header {
  margin-bottom: 20px;
}

.device-header h1 {
  margin: 0;
  font-size: 26px;
  color: #0f172a;
}

.device-subinfo {
  color: #64748b;
  font-size: 14px;
}

.card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow:
  0 4px 12px rgba(0,0,0,0.05),
  0 1px 2px rgba(0,0,0,0.05);
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 15px;
}

.info-item {
  background: #f8fafc;
  padding: 15px;
  border-radius: 8px;
}

.info-item label {
  display: block;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 5px;
}

.info-item span {
  font-weight: 600;
  color: #0f172a;
}

.status {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status.online {
  background: #dcfce7;
  color: #166534;
}

.status.offline {
  background: #fee2e2;
  color: #991b1b;
}

.form-container {
  min-height: 300px;
}
</style>
