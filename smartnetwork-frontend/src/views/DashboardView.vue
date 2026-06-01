<template>
  <div class="dashboard">

    <h1>Panel de Control</h1>
    <p>Bienvenido al sistema de gestión de red</p>

    <router-link to="/devices">Mis dispositivos</router-link>

    <div class="tabs-container">
      <div
        v-for="tab in tabs"
        :key="tab.key"
        class="tab"
        :class="{ active: selectedTab === tab.key }"
        @click="selectedTab = tab.key"
      >
        {{ tab.label }}
      </div>
    </div>

    <div class="tab-content">
      <component :is="currentComponent" />
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import AddressDashboard from '@/components/AddressDashboard.vue'
import ServiceDashboard from '@/components/ServiceDashboard.vue'
import InterfazDashboard from '@/components/InterfazDashboard.vue'
import UsuarioFirewallDashboard from '@/components/UsuarioFirewallDashboard.vue'
import ReglaFirewallDashboard from '@/components/ReglaFirewallDashboard.vue'

const tabs = [
  { key: 'addresses', label: 'Addresses', component: AddressDashboard },
  { key: 'services', label: 'Services', component: ServiceDashboard },
  { key: 'interfaces', label: 'Interfaces', component: InterfazDashboard },
  { key: 'usuarios', label: 'Usuarios Firewall', component: UsuarioFirewallDashboard },
  { key: 'reglas', label: 'Reglas Firewall', component: ReglaFirewallDashboard }
]

const selectedTab = ref('addresses')

const currentComponent = computed(() => {
  return tabs.find(t => t.key === selectedTab.value)?.component || null
})
</script>

<style scoped>
.dashboard {
  padding: 30px;
}
.tabs-container {
  display: flex;
  gap: 20px;
  margin-top: 30px;
  margin-bottom: 20px;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 10px;
}

.tab {
  padding: 10px 20px;
  cursor: pointer;
  font-weight: 500;
  color: #64748b;
  border-radius: 6px 6px 0 0;
  transition: 0.2s;
}

.tab:hover {
  background: #f1f5f9;
  color: #0f172a;
}

.tab.active {
  background: white;
  color: #0f172a;
  border: 1px solid #e2e8f0;
  border-bottom: none;
  font-weight: 600;
}

.tab-content {
  padding-top: 20px;
}
</style>
