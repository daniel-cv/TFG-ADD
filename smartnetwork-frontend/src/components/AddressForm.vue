<template>
  <v-form @submit.prevent="handleSubmit">

    <!-- NAME -->

    <v-text-field
      v-model="name"
      label="Nombre"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
      :disabled="addressEdit"  
      required
    />

    <!-- TYPE -->
    <v-select
      v-model="type"
      :items="['subnet', 'iprange', 'ipmask']"
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

    <!-- IP DESTINO / MASCARA -->
    <v-text-field
      v-if="type === 'iprange'"
      v-model="ipdestino"
      label="IP Final"
      prepend-inner-icon="mdi-ip"
      variant="outlined"
      class="mb-3"
      required
    />
    <v-text-field
      v-if="type === 'ipmask' || type === 'subnet'"
      v-model="ipdestino"
      label="Máscara"
      prepend-inner-icon="mdi-ip"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- INTERFAZ -->
    <v-select
      v-model="interfazId"
      :items="interfaces"
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
      {{ addressEdit ? 'Actualizar Address' : 'Crear Address' }}
    </v-btn>

    <v-btn
      variant="outlined"
      size="large"
      block
      class="mt-2"
      @click="$emit('cancelar')"
    >
      Cancelar
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>

  </v-form>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useAddressStore } from '@/stores/addressStores'
import { useRoute } from "vue-router";
import { useInterfazStore } from '@/stores/interfazStore'
import { obtenerInterfacesPorDispositivo, obtenerInterfacesUsuario } from '@/services/interfazService'

const props = defineProps({
  dispositivoId: { type: Number, required: true },
  addressEdit: { type: Object, default: null },
  modo: { type: String, default: 'simple' }
})
console.log("MODO:", props.modo)

const emit = defineEmits(['creada', 'cancelar'])

const addressStore = useAddressStore()
const route = useRoute()

const name = ref("")
const type = ref("")
const ip = ref("")
const ipdestino = ref("")
const interfazId = ref("")
const comentario = ref("")
const interfaces = ref([])
const mensaje = ref("")
const dispositivoId = Number(route.params.id)

onMounted(async () => {
  if (props.addressEdit) {
    name.value = props.addressEdit.name
    type.value = props.addressEdit.type
    ip.value = props.addressEdit.ip
    ipdestino.value = props.addressEdit.ipdestino
    interfazId.value = props.addressEdit.interfazId
    comentario.value = props.addressEdit.comentario
  }
 try {
  var res=null;
  if(props.modo=="full"){res = await obtenerInterfacesPorDispositivo(props.dispositivoId);}
  else{res= await obtenerInterfacesUsuario()};
  console.log(props.modo)
  const apiData = res.data;
  const puertosBase = [1, 2, 3, 4].map(num => {
    const nombreBuscado = `port${num}`;
    const coincidencia = apiData.find(inter => inter.name.toLowerCase() === nombreBuscado);
    
    return {
      title: `Port${num}`,
      value: coincidencia ? coincidencia.id : -num 
    };
  });

  const idsProcesados = puertosBase.map(p => p.value);

  interfaces.value = [
  {
    title: 'Vacío',
    value: null
  },
    ...puertosBase,
    ...apiData
      .filter(inter => {
        const name = inter.name.toLowerCase();
        return !['port1', 'port2', 'port3', 'port4'].includes(name);
      })
      .map(inter => ({
        title: inter.name,
        value: inter.id
      }))
  ];
} catch (error) {
  console.error('Error cargando interfaces', error);
}
})

const handleSubmit = async () => {
  try {
    const payload = {
      name: name.value,
      ip: ip.value,
      type: type.value,
      ipdestino:
        type.value === 'iprange' || type.value === 'ipmask' || type.value === 'subnet'
          ? ipdestino.value
          : null,
      interfazId: interfazId.value || null,
      comentario: comentario.value
    };

    // 🔥 MODO FULL (desde dispositivos)
    if (props.modo === 'full' && props.dispositivoId) {
      payload.dispositivosIds = [props.dispositivoId]
    }

    if (props.addressEdit) {
      await addressStore.actualizarAddress(props.addressEdit.id, payload)
      mensaje.value = "Address actualizada correctamente"
    } else {
      if (props.modo === 'full') {
        await addressStore.crearAddressCompleto(payload)
      } else {
        await addressStore.crearAddress(payload)
      }
      mensaje.value = "Address creada correctamente"
    }

    emit("creada")

  } catch (error) {
    mensaje.value = props.addressEdit
      ? "Error al actualizar la address"
      : "Error al crear la address"
  }
}
</script>