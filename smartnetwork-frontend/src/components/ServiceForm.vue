<template>
  <v-form @submit.prevent="handleSubmit">
    <v-text-field
      v-model="name"
      label="Nombre del Servicio"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
      :disabled="serviceEdit"
      required
    />
    <v-select
      v-model="protocol"
      :items="protocolos"
      label="Protocolo"
      prepend-inner-icon="mdi-swap-horizontal"
      variant="outlined"
      class="mb-3"
      :disabled="serviceEdit"
      required
    />

    <v-select
      v-if="protocol === 'TCP' || protocol === 'UDP'"
      v-model="ip"
      :items="direcciones"
      label="Dirección IP"
      prepend-inner-icon="mdi-lan-connect"
      variant="outlined"
      class="mb-3"
    />

    <v-text-field
      v-if="protocol === 'TCP' || protocol === 'UDP'"
      v-model="portRange"
      label="Puerto o rango (ej: 53 o 1000-2000)"
      prepend-inner-icon="mdi-lan-connect"
      variant="outlined"
      class="mb-3"
      required
    />

    <v-textarea
      v-model="comentario"
      label="Comentario"
      prepend-inner-icon="mdi-comment"
      variant="outlined"
      class="mb-3"
    />

    <v-btn color="primary" size="large" block type="submit">
      {{ serviceEdit ? 'Actualizar Service' : 'Crear Service' }}
    </v-btn>
    <v-btn
      variant="outlined"
      size="large"
      block
      class="mt-2"
      @click="emit('cancelar')"
    >
      Cancelar
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>
  </v-form>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useServiceStore } from "@/stores/serviceStore";
import {
  obtenerAddressesPorDispositivo,
  obtenerAddressesPorUsuario
} from "@/services/addressService";

const props = defineProps({
  dispositivoId: { type: Number, required: true },
  serviceEdit: { type: Object, default: null },
  modo: { type: String, default: "simple" }
});

const emit = defineEmits(["creada", "cancelar"]);

const serviceStore = useServiceStore();

const name = ref("");
const protocol = ref("");
const ip = ref(null);
const portRange = ref("");
const comentario = ref("");
const mensaje = ref("");

const protocolos = ["TCP", "UDP", "ICMP"];

const direcciones = ref([]);

onMounted(async () => {

  if (props.serviceEdit) {
    name.value = props.serviceEdit.nombre;
    protocol.value = props.serviceEdit.tipoProtocolo;
    portRange.value = props.serviceEdit.destinationPort;
    comentario.value = props.serviceEdit.comentario;
  }
  try {
    let addresses = [];
    if (props.modo === "simple") {
      const resUsuario = await obtenerAddressesPorUsuario();
      addresses = resUsuario.data;
    } else {
      const res = await obtenerAddressesPorDispositivo(props.dispositivoId);
      addresses = res.data;
    }
    direcciones.value = addresses.map(addr => ({
      title: `${addr.name} (${addr.ip})`,
      value: addr.id,
      ip: addr.ip
    }));
    if (props.serviceEdit?.ip) {
      const encontrada = direcciones.value.find( d => d.ip === props.serviceEdit.ip);
      if (encontrada) {
        ip.value = encontrada.value;
      }
    }
  } catch (error) {
    console.error("Error cargando direcciones", error);
  }
});

const handleSubmit = async () => {
  try {
    const selectedAddress = direcciones.value.find(
      d => d.value === ip.value
    );
    const payload = {
      nombre: name.value,
      tipoProtocolo: protocol.value,
      ip: selectedAddress?.ip || "",
      destinationPort: portRange.value,
      comentario: comentario.value
    };
    if (props.serviceEdit) {
      if (props.serviceEdit.dispositivosIds?.length) {
        payload.dispositivosIds =props.serviceEdit.dispositivosIds;
      }
      await serviceStore.actualizarService(
        props.serviceEdit.id,
        payload
      );
      mensaje.value = "Service actualizado correctamente";
    }
    else {
      if (props.modo === "full") {
        payload.dispositivosId = [props.dispositivoId];
        await serviceStore.crearServiceCompleto(payload);
      } else {
        await serviceStore.crearService(payload);
      }
      mensaje.value = "Service creado correctamente";
    }
    emit("creada");
  } catch (error) {
    console.error(error);
    mensaje.value = props.serviceEdit
      ? "Error actualizando"
      : "Error creando";
  }
};
</script>
