<template>
  <v-form @submit.prevent="handleCrearInterfaz">

    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre"
      prepend-inner-icon="mdi-lan"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- TIPO -->
    <v-select
      v-model="tipo"
      :items="['fisica', 'vlan']"
      label="Tipo"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- INTERFAZ PADRE (solo VLAN) -->
    <v-select
      v-if="tipo === 'vlan'"
      v-model="interfacePadre"
      :items="['port1']"
      item-title="name"
      item-value="name"
      label="Interfaz padre"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- VLAN ID (solo VLAN) -->
    <v-text-field
      v-if="tipo === 'vlan'"
      v-model="vlanid"
      label="VLAN ID"
      type="number"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- VDOM (fijo) -->
    <v-text-field
      v-model="vdom"
      label="VDOM"
      variant="outlined"
      class="mb-3"
      disabled
    />

    <!-- MODE -->
    <v-select
      v-model="mode"
      :items="['static', 'dhcp']"
      label="Modo IP"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- IP (solo static) -->
    <v-text-field
      v-if="mode === 'static'"
      v-model="ip"
      label="IP / Máscara (ej: 192.168.1.1/24)"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- ALLOW ACCESS -->
    <v-text-field
      v-model="allowaccess"
      label="Allow Access (ping https ssh)"
      variant="outlined"
      class="mb-3"
    />

    <!-- ROLE -->
    <v-select
      v-model="role"
      :items="['lan', 'wan', 'dmz']"
      label="Rol"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- DESCRIPTION -->
    <v-textarea
      v-model="description"
      label="Descripción"
      variant="outlined"
      class="mb-3"
    />

    <v-btn color="primary" size="large" block type="submit">
      Crear Interfaz
    </v-btn>

    <v-btn
      variant="outlined"
      size="large"
      block
      class="mt-2"
      @click="cancelar()"
    >
      Cancelar
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>

  </v-form>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { useInterfazStore } from "@/stores/interfazStore";

const route = useRoute();
const emit = defineEmits(['creada', 'cancelar'])

const interfazStore = useInterfazStore();

const name = ref("");
const tipo = ref("fisica");
const interfacePadre = ref(null);
const vlanid = ref(null);
const vdom = ref("root");
const mode = ref("dhcp");
const ip = ref(null);
const allowaccess = ref("ping");
const role = ref("lan");
const description = ref("");

const mensaje = ref("");
const interfaces = ref([]);
const dispositivoId = Number(route.params.id);



// Limpiar campos si cambian tipo o modo
watch(tipo, (t) => {
  if (t !== "vlan") {
    interfacePadre.value = null;
    vlanid.value = null;
  }
});
watch(mode, (m) => {
  if (m !== "static") {
    ip.value = null;
  }
});

const handleCrearInterfaz = async () => {
  try {
    const payload = {
      name: name.value,
      tipo: tipo.value,
      interfacePadre: interfacePadre.value,
      vlanid: vlanid.value,
      vdom: vdom.value,
      mode: mode.value,
      ip: ip.value,
      allowaccess: allowaccess.value,
      role: role.value,
      description: description.value,
      dispositivoId: dispositivoId
    };

    await interfazStore.crearInterfaz(payload);
    mensaje.value = "Interfaz creada correctamente";
    emit("creada");
  } catch (error) {
    mensaje.value = "Error al crear la interfaz";
  }
};

function cancelar() {
  emit('cancelar')
}
</script>
