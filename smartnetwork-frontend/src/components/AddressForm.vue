<template>
  <v-form @submit.prevent="handleCrearAddress">
    
    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- TYPE -->
    <v-text-field
      v-model="type"
      label="Tipo"
      prepend-inner-icon="mdi-tag"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- IP -->
    <v-text-field
      v-model="ip"
      label="Dirección IP"
      prepend-inner-icon="mdi-ip"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- INTERFAZ (opcional) -->
    <v-select
      v-model="interfazId"
      :items="interfaces"
      item-title="name"
      item-value="id"
      label="Interfaz (opcional)"
      prepend-inner-icon="mdi-lan"
      variant="outlined"
      class="mb-3"
      clearable
    />

    <!-- COMENTARIO -->
    <v-textarea
      v-model="comentario"
      label="Comentario"
      prepend-inner-icon="mdi-comment"
      variant="outlined"
      class="mb-3"
    />

    <v-btn color="primary" size="large" block type="submit">
      Crear Address
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>
  </v-form>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useAddressStore } from '@/stores/addressStores'
import { useRoute } from "vue-router";
import { useInterfazStore } from '@/stores/interfazStore'

const route = useRoute();
const emit = defineEmits(['creada'])
const props = defineProps({
  dispositivoId: {
    type: Number,
    required: true
  }
});

const addressStore = useAddressStore()
const interfazStore = useInterfazStore()

const name = ref("");
const type = ref("");
const ip = ref("");
const interfazId = ref(null);
const comentario = ref("");

const interfaces = ref([]);
const mensaje = ref("");
const dispositivoId = Number(route.params.id);
onMounted(async () => {
  interfaces.value = (await interfazStore.cargarInterfaces(dispositivoId)) || [];
});

const handleCrearAddress = async () => {
  try {
    const payload = {
      name: name.value,
      ip: ip.value,
      type: type.value,
      interfaz: interfazId.value ? { id: interfazId.value } : null,
      comentario: comentario.value,
      dispositivoId: dispositivoId
    };

    await addressStore.crearAddress(payload);

    mensaje.value = "Address creada correctamente";

    emit("creada");
  } catch (error) {
    mensaje.value = "Error al crear la address";
  }
};
</script>
