<template>
  <v-form @submit.prevent="handleSubmit">

    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
      :disabled="isEdit"
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

    <!-- IP DESTINO / MÁSCARA -->
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
      item-title="title"
      item-value="value"
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
      {{ isEdit ? 'Actualizar Address' : 'Crear Address' }}
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
import { ref, onMounted, computed, nextTick } from "vue";
import { useAddressStore } from '@/stores/addressStores'
import { obtenerInterfacesPorDispositivo, obtenerInterfacesUsuario } from '@/services/interfazService'

const props = defineProps({
  dispositivoId: { type: Number, required: false },
  addressEdit: { type: Object, default: null },
  modo: { type: String, default: 'simple' }
})

const emit = defineEmits(['creada', 'cancelar'])

const addressStore = useAddressStore()

const name = ref("")
const type = ref("")
const ip = ref("")
const ipdestino = ref("")
const interfazId = ref(null)
const comentario = ref("")
const interfaces = ref([])
const mensaje = ref("")

// ✅ modo edición limpio
const isEdit = computed(() => !!props.addressEdit)

onMounted(async () => {
  try {
    let res = null;

    if (props.modo === "full") {
      res = await obtenerInterfacesPorDispositivo(props.dispositivoId);
    } else {
      res = await obtenerInterfacesUsuario();
    }

    const apiData = res.data;

    // 🔹 Generar puertos base
    const puertosBase = [1, 2, 3, 4].map(num => {
      const nombreBuscado = `port${num}`;
      const coincidencia = apiData.find(
        inter => (inter.name ?? '').toLowerCase() === nombreBuscado
      );

      return {
        title: `Port${num}`,
        value: coincidencia ? Number(coincidencia.id) : -num // ⚠️ quitamos negativos
      };
    });

    const idsUsados = puertosBase
      .filter(p => p.value > 0)
      .map(p => p.value);

    interfaces.value = [
      { title: 'Vacío', value: null },
      ...puertosBase,
      ...apiData
        .filter(inter => !idsUsados.includes(Number(inter.id)))
        .map(inter => ({
          title: inter.name ?? `Interfaz ${inter.id}`,
          value: Number(inter.id)
        }))
    ];

    await nextTick();

    if (props.addressEdit) {
      name.value = props.addressEdit.name;
      type.value = props.addressEdit.type;
      ip.value = props.addressEdit.ip;
      ipdestino.value = props.addressEdit.ipdestino;

      const id = props.addressEdit.interfazId
        ? Number(props.addressEdit.interfazId)
        : null;

      interfazId.value = id;

      // 🔥 CLAVE: asegurar que el select tiene ese valor
      if (id && !interfaces.value.find(i => i.value === id)) {
        interfaces.value.push({
          title: `Port${id}`,
          value: id
        });
      }

      comentario.value = props.addressEdit.comentario;
    }

  } catch (error) {
    console.error('Error cargando interfaces', error);
  }
});

const handleSubmit = async () => {
  try {
    const payload = {
      name: name.value,
      ip: ip.value,
      type: type.value,
      ipdestino:
        (type.value === 'iprange' || type.value === 'ipmask' || type.value === 'subnet')
          ? ipdestino.value
          : null,
      interfazId: interfazId.value || null,
      comentario: comentario.value
    };

    // EDIT
    if (props.addressEdit) {

      if (props.addressEdit.dispositivosIds?.length) {
        payload.dispositivosIds = props.addressEdit.dispositivosIds
      }
      else if (props.modo === 'full' && props.dispositivoId) {
        payload.dispositivosIds = [props.dispositivoId]
      }

      await addressStore.actualizarAddress(
        props.addressEdit.id,
        payload
      )

      mensaje.value = "Address actualizada correctamente"
    }

    // CREATE
    else {
      if (props.modo === 'full' && props.dispositivoId) {
        payload.dispositivosIds = [props.dispositivoId]
        await addressStore.crearAddressCompleto(payload)
      } else {
        await addressStore.crearAddress(payload)
      }

      mensaje.value = "Address creada correctamente"
    }

    emit("creada")

  } catch (error) {
    console.error(error)
    mensaje.value = props.addressEdit
      ? "Error al actualizar la address"
      : "Error al crear la address"
  }
}
</script>
