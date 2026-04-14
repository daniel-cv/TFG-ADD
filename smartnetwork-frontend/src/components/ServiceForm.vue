<template>
  <v-form @submit.prevent="handleSubmit">

    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre del Servicio"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
      :disabled="serviceEdit"
      required
    />

    <!-- PROTOCOLO -->
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

    <!-- IP -->
    <v-select
      v-if="protocol === 'TCP' || protocol === 'UDP'"
      v-model="ip"
      :items="direcciones"
      label="Dirección IP"
      prepend-inner-icon="mdi-lan-connect"
      variant="outlined"
      class="mb-3"
    />

    <!-- PUERTO -->
    <v-text-field
      v-if="protocol === 'TCP' || protocol === 'UDP'"
      v-model="portRange"
      label="Puerto o rango (ej: 53 o 1000-2000)"
      prepend-inner-icon="mdi-lan-connect"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- COMENTARIO -->
    <v-textarea
      v-model="comentario"
      label="Comentario"
      prepend-inner-icon="mdi-comment"
      variant="outlined"
      class="mb-3"
    />

    <!-- BOTÓN PRINCIPAL -->
    <v-btn color="primary" size="large" block type="submit">
      {{ serviceEdit ? 'Actualizar Service' : 'Crear Service' }}
    </v-btn>

    <!-- CANCELAR -->
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
import { obtenerAddressesPorDispositivo, obtenerAddressesPorUsuario } from "@/services/addressService";

const props = defineProps({
  dispositivoId: { type: Number, required: true },
  serviceEdit: { type: Object, default: null },
  modo: { type: String, default: "simple" }
});

console.log("ServiceForm props:", props.modo);

const emit = defineEmits(["creada", "cancelar"]);

const serviceStore = useServiceStore();

const name = ref("");
const protocol = ref("");
const ip = ref("");
const portRange = ref("");
const comentario = ref("");
const mensaje = ref("");

const protocolos = ["TCP", "UDP", "ICMP"];
const direcciones = ref([]);

onMounted(async () => {
  // Si estamos editando, rellenar campos
  if (props.serviceEdit) {
    name.value = props.serviceEdit.nombre;
    protocol.value = props.serviceEdit.tipoProtocolo;
    ip.value = props.serviceEdit.ip;
    portRange.value = props.serviceEdit.destinationPort;
    comentario.value = props.serviceEdit.comentario;
  }

  // Cargar direcciones del dispositivo
  try {
    if(props.modo === "simple") {
      const resUsuario = await obtenerAddressesPorUsuario();
      direcciones.value = resUsuario.data.map(addr => ({
        title: addr.name,
        value: addr.ip
      }));
    } else {
      const res = await obtenerAddressesPorDispositivo(props.dispositivoId);
      direcciones.value = res.data.map(addr => ({
        title: addr.name,
        value: addr.ip
      }));
    }
  } catch (error) {
    console.error("Error cargando direcciones", error);
  }
});

const handleSubmit = async () => {
  try {
    const payload = {
      nombre: name.value,
      tipoProtocolo: protocol.value,
      ip: ip.value,
      destinationPort: portRange.value,
      comentario: comentario.value
    };

    // 🔥 MODO FULL → crear + asignar automáticamente
    if (!props.serviceEdit && props.modo === "full") {
      payload.dispositivosId = [props.dispositivoId];
      await serviceStore.crearServiceCompleto(payload);
      mensaje.value = "Service creado y asignado correctamente";
    }

    // 🔥 MODO SIMPLE → solo crear
    else if (!props.serviceEdit) {
      await serviceStore.crearService(payload);
      mensaje.value = "Service creado correctamente";
    }

    // 🔥 EDITAR
    else {
      await serviceStore.actualizarService(
        props.serviceEdit.id,
        props.dispositivoId,
        payload
      );
      mensaje.value = "Service actualizado correctamente";
    }

    emit("creada");


  } catch (error) {
    console.error("Error creando/actualizando service:", error);
    mensaje.value = props.serviceEdit
      ? "Error al actualizar el service"
      : "Error al crear el service";
  }
};
</script>
